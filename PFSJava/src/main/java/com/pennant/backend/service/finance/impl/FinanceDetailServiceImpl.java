/********************************************************************************************************************
 * Copyright 2011 - Pennant Technologies * * This file is part of Pennant Java Application Framework and related
 * Products. All * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of
 * Pennant * Technologies. * * Copyright and other intellectual property laws protect these materials. Reproduction or
 * retransmission of the * materials, in whole or in part, in any manner, without the prior written consent of the
 * copyright holder, is a * violation of copyright law. *
 ******************************************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************************************** 
 * * FileName : FinanceDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011 *
 * Modified Date : 15-11-2011 * * Description : * * *
 ******************************************************************************************************************** 
 * Date Author Version Comments *
 ********************************************************************************************************************
 * 15-11-2011 Pennant 0.1 * 08-05-2018 Vinay 0.2 As per mail from Raju ,subject : Daily status call : 19 April * added
 * validations in Disbursement and Covenant types *
 ********************************************************************************************************************
 * 16-05-2018 Madhu Babu 0.3****************** * As per mail from Raju added the validations to proceed with loan
 * approval and disbursement basd on PDD/OTC * * 13-06-2018 Siva 0.4 Stage Accounting Modifications * * 22-06-2018
 * Srinivas Varma 0.5 Post Hook Validation Implementation * * * * * * * * * * *
 ********************************************************************************************************************
 */
package com.pennant.backend.service.finance.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

import javax.security.auth.login.AccountNotFoundException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaxen.JaxenException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.zkoss.util.resource.Labels;

import com.pennant.Interface.service.CustomerLimitIntefaceService;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.finance.limits.LimitCheckDetails;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FeeCalculator;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.NotesDAO;
import com.pennant.backend.dao.QueueAssignmentDAO;
import com.pennant.backend.dao.TATDetailDAO;
import com.pennant.backend.dao.TaskOwnersDAO;
import com.pennant.backend.dao.UserActivityLogDAO;
import com.pennant.backend.dao.applicationmaster.BaseRateCodeDAO;
import com.pennant.backend.dao.applicationmaster.FinIRRDetailsDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.customermasters.CustomerIncomeDAO;
import com.pennant.backend.dao.finance.CreditReviewDetailDAO;
import com.pennant.backend.dao.finance.ExtendedFieldMaintenanceDAO;
import com.pennant.backend.dao.finance.FinExpenseDetailsDAO;
import com.pennant.backend.dao.finance.FinFlagsHeaderDAO;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.dao.finance.GuarantorDetailDAO;
import com.pennant.backend.dao.finance.HoldDisbursementDAO;
import com.pennant.backend.dao.finance.IRRScheduleDetailDAO;
import com.pennant.backend.dao.finance.JointAccountDetailDAO;
import com.pennant.backend.dao.finance.LowerTaxDeductionDAO;
import com.pennant.backend.dao.finance.financialSummary.DealRecommendationMeritsDAO;
import com.pennant.backend.dao.finance.financialSummary.DueDiligenceDetailsDAO;
import com.pennant.backend.dao.finance.financialSummary.RecommendationNotesDetailsDAO;
import com.pennant.backend.dao.finance.financialSummary.RisksAndMitigantsDAO;
import com.pennant.backend.dao.finance.financialSummary.SanctionConditionsDAO;
import com.pennant.backend.dao.limits.LimitInterfaceDAO;
import com.pennant.backend.dao.payorderissue.PayOrderIssueHeaderDAO;
import com.pennant.backend.dao.reason.deatil.ReasonDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.AccountTypeDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeExpenseDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.dao.rmtmasters.PromotionDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.dao.solutionfactory.ExtendedFieldDetailDAO;
import com.pennant.backend.dao.systemmasters.IncomeTypeDAO;
import com.pennant.backend.delegationdeviation.DeviationHelper;
import com.pennant.backend.model.QueueAssignment;
import com.pennant.backend.model.TaskOwners;
import com.pennant.backend.model.UserActivityLog;
import com.pennant.backend.model.WSReturnStatus;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.blacklist.FinBlacklistCustomer;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.expenses.FinExpenseDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.AdvancePaymentDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinCustomerDetails;
import com.pennant.backend.model.finance.FinFeeConfig;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinOCRHeader;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceMainExtension;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.LMSServiceLog;
import com.pennant.backend.model.finance.LinkedFinances;
import com.pennant.backend.model.finance.LowerTaxDeduction;
import com.pennant.backend.model.finance.OverdraftMovements;
import com.pennant.backend.model.finance.PricingDetail;
import com.pennant.backend.model.finance.ProspectCustomer;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.finance.TATDetail;
import com.pennant.backend.model.finance.TaxHeader;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.finance.financialsummary.DealRecommendationMerits;
import com.pennant.backend.model.finance.financialsummary.DueDiligenceDetails;
import com.pennant.backend.model.finance.financialsummary.RecommendationNotes;
import com.pennant.backend.model.finance.financialsummary.RisksAndMitigants;
import com.pennant.backend.model.finance.financialsummary.SanctionConditions;
import com.pennant.backend.model.finance.financialsummary.SynopsisDetails;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.financemanagement.FinanceFlag;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennant.backend.model.rmtmasters.FinTypeExpense;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.service.UpdateAttributeServiceTask;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.backend.service.authorization.AuthorizationLimitService;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.collateral.impl.FlagDetailValidation;
import com.pennant.backend.service.configuration.impl.VasRecordingValidation;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.service.dms.DMSIdentificationService;
import com.pennant.backend.service.drawingpower.DrawingPowerService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.CashBackProcessService;
import com.pennant.backend.service.finance.CustomServiceTask;
import com.pennant.backend.service.finance.FinChequeHeaderService;
import com.pennant.backend.service.finance.FinFeeConfigService;
import com.pennant.backend.service.finance.FinOCRHeaderService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceTaxDetailService;
import com.pennant.backend.service.finance.GenericFinanceDetailService;
import com.pennant.backend.service.finance.ISRADetailService;
import com.pennant.backend.service.finance.LinkedFinancesService;
import com.pennant.backend.service.finance.PSLDetailService;
import com.pennant.backend.service.finance.TaxHeaderDetailsService;
import com.pennant.backend.service.finance.financialsummary.DealRecommendationMeritsService;
import com.pennant.backend.service.finance.financialsummary.DueDiligenceDetailsService;
import com.pennant.backend.service.finance.financialsummary.RecommendationNotesDetailsService;
import com.pennant.backend.service.finance.financialsummary.RisksAndMitigantsService;
import com.pennant.backend.service.finance.financialsummary.SanctionConditionsService;
import com.pennant.backend.service.finance.financialsummary.SynopsisDetailsService;
import com.pennant.backend.service.finance.manual.schedule.ManualScheduleService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditFinancialService;
import com.pennant.backend.service.legal.LegalDetailService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.service.lmtmasters.FinanceWorkFlowService;
import com.pennant.backend.service.payment.PaymentsProcessService;
import com.pennant.backend.service.systemmasters.PMAYService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.InsuranceConstants;
import com.pennant.backend.util.LimitConstants;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.pff.accounting.model.PostingDTO;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.pff.extension.FeeExtension;
import com.pennant.pff.holdrefund.dao.HoldRefundUploadDAO;
import com.pennant.pff.holdrefund.model.FinanceHoldDetail;
import com.pennanttech.finance.tds.cerificate.model.TanAssignment;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.engine.workflow.Action;
import com.pennanttech.pennapps.core.engine.workflow.Operation;
import com.pennanttech.pennapps.core.engine.workflow.ProcessUtil;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine.Flow;
import com.pennanttech.pennapps.core.engine.workflow.model.ServiceTask;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.pff.finsampling.service.FinSamplingService;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pennapps.pff.service.hook.PostExteranalServiceHook;
import com.pennanttech.pennapps.pff.service.hook.PostValidationHook;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.model.AdvancePayment;
import com.pennanttech.pff.advancepayment.service.AdvancePaymentService;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.ProductUtil;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;
import com.pennanttech.pff.external.BreService;
import com.pennanttech.pff.external.CreditInformation;
import com.pennanttech.pff.external.Crm;
import com.pennanttech.pff.external.DocumentVerificationService;
import com.pennanttech.pff.external.DomainCheckService;
import com.pennanttech.pff.external.HunterService;
import com.pennanttech.pff.external.InitiateHunterService;
import com.pennanttech.pff.external.LoanDataSyncService;
import com.pennanttech.pff.external.ProfectusHunterBreService;
import com.pennanttech.pff.external.PushNotificationsService;
import com.pennanttech.pff.external.service.ExternalFinanceSystemService;
import com.pennanttech.pff.notifications.service.NotificationService;
import com.pennanttech.pff.overdraft.OverdraftConstants;
import com.pennanttech.pff.overdraft.dao.OverdraftScheduleDetailDAO;
import com.pennanttech.pff.overdraft.model.OverdraftScheduleDetail;
import com.pennanttech.pff.overdraft.service.OverdrafLoanService;
import com.pennanttech.pff.overdraft.service.VariableOverdraftSchdService;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.service.sampling.SamplingService;
import com.pennattech.pff.receipt.model.ReceiptDTO;
import com.rits.cloning.Cloner;

/**
 * Service implementation for methods that depends on <b>FinanceMain</b>.<br>
 * 
 */
public class FinanceDetailServiceImpl extends GenericFinanceDetailService implements FinanceDetailService {
	private static final Logger logger = LogManager.getLogger(FinanceDetailServiceImpl.class);

	private CustomerIncomeDAO customerIncomeDAO;
	private IncomeTypeDAO incomeTypeDAO;
	private AccountTypeDAO accountTypeDAO;
	private CustomerLimitIntefaceService custLimitIntefaceService;
	private FinanceWriteoffDAO financeWriteoffDAO;
	private NotesDAO notesDAO;
	private QueueAssignmentDAO queueAssignmentDAO;
	private UserActivityLogDAO userActivityLogDAO;
	private TaskOwnersDAO taskOwnersDAO;
	private LimitInterfaceDAO limitInterfaceDAO;
	private DedupParmService dedupParmService;
	private PayOrderIssueHeaderDAO payOrderIssueHeaderDAO;
	private TATDetailDAO tatDetailDAO;
	private IRRScheduleDetailDAO irrScheduleDetailDAO;
	private LinkedFinancesService linkedFinancesService;
	private ISRADetailService israDetailService;
	private LimitManagement limitManagement;
	private LimitCheckDetails limitCheckDetails;
	private OverdraftScheduleDetailDAO overdraftScheduleDetailDAO;
	private FlagDetailValidation flagDetailValidation;
	private FinChequeHeaderService finChequeHeaderService;
	private VASRecordingDAO vasRecordingDAO;
	private FinTypeFeesDAO finTypeFeesDAO;
	private VasRecordingValidation vasRecordingValidation;
	private PromotionDAO promotionDAO;
	private FinanceTaxDetailService financeTaxDetailService;
	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	private ExtendedFieldDetailDAO extendedFieldDetailDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;
	private CustomServiceTask customServiceTask;
	private CustomerService customerService;
	@Autowired(required = false)
	private CreditInformation creditInformation;
	private ReasonDetailDAO reasonDetailDAO;
	private FinTypeExpenseDAO finTypeExpenseDAO;
	private FinExpenseDetailsDAO finExpenseDetailsDAO;
	private PSLDetailService pSLDetailService;
	private CollateralSetupService collateralSetupService;
	private HoldDisbursementDAO holdDisbursementDAO;
	private PaymentsProcessService paymentsProcessService;
	private HoldRefundUploadDAO holdRefundUploadDAO;
	private FinFlagsHeaderDAO finFlagsHeaderDAO;

	@Autowired(required = false)
	private Crm crm;

	@Autowired(required = false)
	private VerificationService verificationService;
	@Autowired(required = false)
	private DeviationHelper deviationHelper;
	@Autowired(required = false)
	private AuthorizationLimitService authorizationLimitService;

	@Autowired(required = false)
	private DMSIdentificationService dmsIdentificationService;

	@Autowired(required = false)
	private ProfectusHunterBreService profectusHunterBreService;
	@Autowired(required = false)
	private SamplingService samplingService;
	@Autowired(required = false)
	private FinSamplingService finSamplingService;

	@Autowired(required = false)
	private LegalDetailService legalDetailService;

	@Autowired(required = false)
	@Qualifier("financeDetailPostValidationHook")
	private PostValidationHook postValidationHook;

	@Autowired(required = false)
	private NotificationService notificationService;
	private LowerTaxDeductionDAO lowerTaxDeductionDAO;
	@Autowired(required = false)
	private HunterService hunterService;
	@Autowired(required = false)
	private BreService extBreService;

	@Autowired(required = false)
	private InitiateHunterService initiateHunterService;
	@Autowired(required = false)
	private DomainCheckService domainCheckService;
	@Autowired(required = false)
	private DocumentVerificationService documentVerificationService;
	@Autowired(required = false)
	private LoanDataSyncService loanDataSyncService;
	private transient BaseRateCodeDAO baseRateCodeDAO;

	private DrawingPowerService drawingPowerService;
	@Autowired(required = false)
	private CreditFinancialService creditFinancialService;
	private CreditReviewDetailDAO creditReviewDetailDAO;
	@Autowired(required = false)
	private PushNotificationsService pushNotificationsService;
	private RisksAndMitigantsDAO risksAndMitigantsDAO;
	private SanctionConditionsDAO sanctionConditionsDAO;
	private DealRecommendationMeritsDAO dealRecommendationMeritsDAO;
	private DueDiligenceDetailsDAO dueDiligenceDetailsDAO;
	private RecommendationNotesDetailsDAO recommendationNotesDetailsDAO;
	@Autowired(required = false)
	private RisksAndMitigantsService risksAndMitigantsService;
	@Autowired(required = false)
	private SanctionConditionsService sanctionConditionsService;
	@Autowired(required = false)
	private DealRecommendationMeritsService dealRecommendationMeritsService;
	@Autowired(required = false)
	private DueDiligenceDetailsService dueDiligenceDetailsService;

	@Autowired(required = false)
	private RecommendationNotesDetailsService recommendationNotesDetailsService;

	@Autowired(required = false)
	private SynopsisDetailsService synopsisDetailsService;

	private FinanceWorkFlowService financeWorkFlowService;

	@Autowired(required = false)
	private CashBackProcessService cashBackProcessService;
	@Autowired(required = false)
	private FinOCRHeaderService finOCRHeaderService;
	private PMAYService pmayService;
	private JointAccountDetailDAO jointAccountDetailDAO;
	private GuarantorDetailDAO guarantorDetailDAO;
	private FinFeeConfigService finFeeConfigService;
	private FeeCalculator feeCalculator;
	@Autowired(required = false)
	@Qualifier("verificationPostExteranalServiceHook")
	private PostExteranalServiceHook postExteranalServiceHook;

	private TaxHeaderDetailsService taxHeaderDetailsService;
	private ExtendedFieldMaintenanceDAO extendedFieldMaintenanceDAO;
	private ExternalFinanceSystemService externalFinanceSystemService;
	private ManualScheduleService manualScheduleService;
	private VariableOverdraftSchdService variableOverdraftSchdService;
	private OverdrafLoanService overdrafLoanService;

	public FinanceDetailServiceImpl() {
		super();
	}

	@Override
	public FinanceDetail getFinanceDetail(boolean isWIF) {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.getFinScheduleData().setFinanceMain(financeMainDAO.getFinanceMain(isWIF));
		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	@Override
	public FinanceDetail getNewFinanceDetail(boolean isWIF) {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = getFinanceDetail(isWIF);
		financeDetail.setNewRecord(true);
		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * Method for Check Have to Creating New Finance Accessibility for User or not
	 */
	@Override
	public boolean checkFirstTaskOwnerAccess(Set<String> userroles, String event, String moduleName) {
		logger.debug(Literal.ENTERING);

		if (userroles == null || userroles.isEmpty()) {
			return false;
		}

		List<String> listFirsttaskOwners = financeMainDAO.getFinanceWorlflowFirstTaskOwners(event, moduleName);

		for (String firsttaskonwer : listFirsttaskOwners) {

			if (firsttaskonwer.contains(PennantConstants.DELIMITER_COMMA)) {

				String[] firstTaskOners = firsttaskonwer.split(PennantConstants.DELIMITER_COMMA);

				for (String firsttask : firstTaskOners) {

					if (userroles.contains(firsttask)) {
						return true;
					}
				}

			} else {

				if (userroles.contains(firsttaskonwer)) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public FinanceDetail getOriginationFinance(long finID, String nextRoleCode, String procEdtEvent, String userrole) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = financeMainDAO.getFinanceMain(finID, nextRoleCode, "_TView");
		if (fm == null) {
			return null;
		}

		String finReference = fm.getFinReference();
		long custID = fm.getCustID();
		boolean tdsApplicable = fm.istDSApplicable();
		Long mandateID = fm.getMandateID();
		String offerId = fm.getOfferId();
		String moduleName = FinanceConstants.MODULE_NAME;

		setDasAndDmaData(fm);

		FinanceDetail fd = getFinanceOrgDetails(fm, "_TView");
		FinScheduleData schdData = fd.getFinScheduleData();
		fm = schdData.getFinanceMain();

		/* IRR codes tab */
		schdData.setiRRDetails(finIRRDetailsDAO.getFinIRRList(finID, "_View"));

		if (tdsApplicable && SysParamUtil.isAllowed("ALLOW_LOWER_TAX_DED_REQ")) {
			schdData.setLowerTaxDeductionDetails(lowerTaxDeductionDAO.getLowerTaxDeductionDetails(finID, "_Temp"));
		}

		/* Customer Details */
		fd.setCustomerDetails(customerDetailsService.getCustomerDetailsById(custID, true, "_AView"));

		/* Guaranteer Details */
		fd.setGurantorsDetailList(guarantorDetailService.getGuarantorDetail(finID, "_TView"));

		/* Joint Account Details */
		fd.setJointAccountDetailList(jointAccountDetailService.getJoinAccountDetail(finID, "_TView"));

		/* Finance Fee Details */
		schdData.setFinFeeDetailList(finFeeDetailService.getFinFeeDetailById(finID, false, "_TView"));

		/* Finance Receipt Details */
		schdData.setImdReceipts(finFeeDetailService.getUpfrontReceipts(finID, String.valueOf(custID)));

		/* Loading Up-front Fee Details by LeadId */
		if (StringUtils.isNotEmpty(offerId)) {
			/* Finance Fee Details */
			schdData.getImdReceipts().addAll(finFeeDetailService.getUpfrontReceipts(finID, offerId));

			/* Finance Fee Details by leadID */
			List<FinFeeDetail> feeDetails = finFeeDetailService.getFinFeeDetailById(offerId, false, "_View");
			schdData.setFinFeeDetailList(feeDetails);
		}

		List<Long> feeIds = new ArrayList<Long>();

		for (FinFeeDetail finFeeDetail : schdData.getFinFeeDetailList()) {
			feeIds.add(finFeeDetail.getFeeID());
		}

		if (!feeIds.isEmpty()) {
			schdData.setFinFeeReceipts(finFeeDetailService.getFinFeeReceiptsById(feeIds, "_View"));
		}

		getFinanceReferenceDetails(fd, userrole, "DDE", "", procEdtEvent, true);

		// Document Details
		List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(finReference, moduleName,
				procEdtEvent, "_TView");
		if (fd.getDocumentDetailsList() != null && !fd.getDocumentDetailsList().isEmpty()) {
			fd.getDocumentDetailsList().addAll(documentList);
		} else {
			fd.setDocumentDetailsList(documentList);
		}

		// TAN Assignments Details
		List<TanAssignment> assignments = tanAssignmentService.getTanDetails(fm.getCustID(), finReference);
		if (CollectionUtils.isNotEmpty(fd.getTanAssignments())) {
			fd.getTanAssignments().addAll(assignments);
		} else {
			fd.setTanAssignments(assignments);
		}

		/* Deviations */
		if (ImplementationConstants.ALLOW_DEVIATIONS) {
			List<FinanceDeviations> deviations = deviationDetailsService.getFinanceDeviations(finID);
			List<FinanceDeviations> apprDeviations = deviationDetailsService.getApprovedFinanceDeviations(finID);
			deviationHelper.setDeviationDetails(fd, deviations, apprDeviations);
		}

		if (mandateID != null && mandateID > 0) {
			fd.setMandate(finMandateService.getMnadateByID(mandateID));
		}
		/* Mandate */
		fd.setSecurityMandate(finMandateService.getSecurityMandate(finReference));

		/* Finance Tax Detail */
		fd.setFinanceTaxDetail(financeTaxDetailDAO.getFinanceTaxDetail(finID, "_TView"));

		/* Multiple Party Disbursement Details */
		fd.setAdvancePaymentsList(finAdvancePaymentsService.getFinAdvancePaymentsById(finID, "_View"));

		/* Covenant Type Details */
		if (ImplementationConstants.ALLOW_COVENANT_TYPES) {
			fd.setCovenantTypeList(finCovenantTypeService.getFinCovenantTypeById(finReference, "_View", false));
			if (ImplementationConstants.COVENANT_MODULE_NEW) {
				fd.setCovenants(covenantsService.getCovenants(finReference, "Loan", TableType.VIEW));
			}
		}

		/* Put-call */
		fd.setFinOptions(finOptionService.getFinOptions(finID, TableType.VIEW));

		/* Asset Type Details */
		fd.setFinAssetTypesList(finAssetTypeDAO.getFinAssetTypesByFinRef(finReference, "_TView"));

		/* Extended Field Details for Assets */
		fd.setExtendedFieldRenderList(getExtendedAssetDetails(finReference, fd.getFinAssetTypesList()));

		/* VAS Recording Details */
		if (ImplementationConstants.ALLOW_VAS) {
			schdData.setVasRecordingList(getVasRecordings(finReference, "_TView"));
		}

		/* Cheque Header and Cheque Details getting */
		fd.setChequeHeader(finChequeHeaderService.getChequeHeaderByRef(finID));

		/* Collateral Details */
		List<CollateralAssignment> assignmentListMain = null;
		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			assignmentListMain = collateralAssignmentDAO.getCollateralAssignmentByFinRef(finReference, moduleName,
					"_TView");
		} else {
			fd.setFinanceCollaterals(finCollateralService.getFinCollateralsByRef(finID, "_TView"));
		}

		/* Collateral setup details and assignment details */
		List<CollateralSetup> collateralSetupList = null;
		if (StringUtils.isNotBlank(fm.getParentRef())) {
			collateralSetupList = collateralSetupService.getCollateralDetails(fm.getParentRef(), false);
		} else {
			collateralSetupList = collateralSetupService.getCollateralDetails(finReference, false);
		}

		List<CollateralAssignment> assignmentListTemp = null;
		if (CollectionUtils.isNotEmpty(collateralSetupList)) {
			if (ImplementationConstants.COLLATERAL_INTERNAL) {
				assignmentListTemp = collateralAssignmentDAO.getCollateralAssignmentByFinRef(finReference, moduleName,
						"_CTView");
			}
		}

		fd.setCollaterals(collateralSetupList);
		fd = setCollateralAssignments(fd, assignmentListMain, assignmentListTemp);

		fd.setSampling(samplingService.getSampling(finReference, "_aview"));

		if (fd.isSamplingApprover()) {
			fd.setSampling(finSamplingService.getSamplingDetails(finReference, "_aview"));
		}

		/* PSL details */
		fd.setPslDetail(pSLDetailService.getPSLDetail((finID)));

		/* Legal details */
		if (fm.isLegalRequired()) {
			fd.getLegalDetailsList().addAll(legalDetailService.getLegalDetailByFinreference(finReference));
		}

		/* Linked Finances */
		fd.setLinkedFinancesList(linkedFinancesService.getLinkedFinancesByRef(finReference, "_TView"));

		// Manual Schedule Detail
		fd.getFinScheduleData().setManualScheduleHeader(
				this.manualScheduleService.getManualScheduleDetails(finID, FinServiceEvent.ORG, TableType.TEMP_TAB));

		// Variable OD Detail
		fd.getFinScheduleData().setVariableOverdraftSchdHeader(
				this.variableOverdraftSchdService.getHeader(finReference, FinServiceEvent.ORG, TableType.TEMP_TAB));

		// ISRA Details
		fd.setIsraDetail(this.israDetailService.getIsraDetailsByRef(finReference, "_TView"));

		/* Financial Summary RisksAndMitigants Details */
		fd.getRisksAndMitigantsList().addAll(risksAndMitigantsDAO.getRisksAndMitigants(finReference));

		/* Financial Summary SanctionConditions Details */
		fd.getSanctionDetailsList().addAll(sanctionConditionsDAO.getSanctionConditions(finID));

		/* Financial Summary DealRecommendationMerits Details */
		fd.getDealRecommendationMeritsDetailsList()
				.addAll(dealRecommendationMeritsDAO.getDealRecommendationMerits(finReference));

		/* Financial Summary DueDiligences Details */
		List<DueDiligenceDetails> dueDiligenceDetailsList = dueDiligenceDetailsDAO.getDueDiligenceDetails(finID);
		fd.getDueDiligenceDetailsList().addAll(dueDiligenceDetailsList);

		/* Financial Summary DueDiligences Details */
		fd.getRecommendationNoteList().addAll(recommendationNotesDetailsDAO.getRecommendationNotesDetails(finID));

		/* SynopsisDetails details */
		fd.setSynopsisDetails(synopsisDetailsService.getSynopsisDetails(finID));

		/* Finance OCR Details */
		fd.setFinOCRHeader(finOCRHeaderService.getFinOCRHeaderByRef(finID, "_View"));

		/* PMAY */
		fd.setPmay(pmayService.getPMAY(finID, "_View"));

		if (subventionService != null) {
			subventionService.setSubventionDetails(schdData, "_View");
		}

		logger.debug(Literal.LEAVING);
		return fd;
	}

	private FinanceDetail setCollateralAssignments(FinanceDetail fd, List<CollateralAssignment> assignmentListMain,
			List<CollateralAssignment> assignmentListTemp) {
		if (CollectionUtils.isEmpty(assignmentListMain) && CollectionUtils.isEmpty(assignmentListTemp)) {
			return fd;
		}

		if (CollectionUtils.isEmpty(assignmentListMain)) {
			fd.getCollateralAssignmentList().addAll(assignmentListTemp);
			return fd;
		}

		if (CollectionUtils.isEmpty(assignmentListTemp)) {
			fd.getCollateralAssignmentList().addAll(assignmentListMain);
			return fd;
		}

		List<CollateralAssignment> resultantList = new ArrayList<>();
		resultantList.addAll(assignmentListMain);

		for (CollateralAssignment assignmentTemp : assignmentListTemp) {
			boolean equal = false;
			for (CollateralAssignment assignmentMain : assignmentListMain) {
				if (assignmentTemp.getCollateralRef().equals(assignmentMain.getCollateralRef())) {
					equal = true;
					break;
				}
			}
			if (!equal) {
				resultantList.add(assignmentTemp);
			}
		}

		fd.getCollateralAssignmentList().addAll(resultantList);

		return fd;
	}

	private void setDasAndDmaData(FinanceMain fm) {
		logger.debug(Literal.ENTERING);

		List<Long> dealerIds = new ArrayList<>();
		long dsaID = 0;
		long dmaID = 0;
		long connectorID = 0;

		if (StringUtils.isNotBlank(fm.getDsaCode()) && StringUtils.isNumeric(fm.getDsaCode())) {
			dsaID = Long.parseLong(fm.getDsaCode());
			dealerIds.add(dsaID);
		}

		if (StringUtils.isNotBlank(fm.getDmaCode()) && StringUtils.isNumeric(fm.getDmaCode())) {
			dmaID = Long.parseLong(fm.getDmaCode());
			dealerIds.add(dmaID);
		}

		if (fm.getConnector() > 0) {
			connectorID = fm.getConnector();
			dealerIds.add(fm.getConnector());
		}

		if (dealerIds.isEmpty()) {
			return;
		}

		List<VehicleDealer> dealers = vehicleDealerService.getVehicleDealerById(dealerIds);
		for (VehicleDealer dealer : dealers) {
			if (dealer.getDealerId() == dmaID) {
				fm.setDmaName(dealer.getDealerName());
				fm.setDmaCodeDesc(dealer.getCode());
			} else if (dealer.getDealerId() == dsaID) {
				fm.setDsaName(dealer.getDealerName());
				fm.setDsaCodeDesc(dealer.getCode());
			} else if (dealer.getDealerId() == connectorID) {
				fm.setConnectorCode(dealer.getDealerName());
				fm.setConnectorDesc(dealer.getCode());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private List<ExtendedFieldRender> getExtendedAssetDetails(String finReference, List<FinAssetTypes> assetTypes) {
		List<ExtendedFieldRender> extendedFieldRenderList = new ArrayList<ExtendedFieldRender>();
		if (assetTypes == null || assetTypes.isEmpty()) {
			return extendedFieldRenderList;
		}

		List<String> renderedAssetTypes = new ArrayList<>();

		// Extended FieldDetails
		for (FinAssetTypes assetType : assetTypes) {

			// If Duplicate asset types with multiple record , to avoid multiple
			// DB callings
			if (renderedAssetTypes.contains(assetType.getAssetType())) {
				continue;
			}

			StringBuilder tableName = new StringBuilder();
			tableName.append(AssetConstants.EXTENDEDFIELDS_MODULE);
			tableName.append("_");
			tableName.append(assetType.getAssetType());
			tableName.append("_ED");
			renderedAssetTypes.add(assetType.getAssetType());

			List<Map<String, Object>> renderMapList = extendedFieldRenderDAO.getExtendedFieldMap(finReference,
					tableName.toString(), "_View");
			for (int i = 0; i < renderMapList.size(); i++) {

				ExtendedFieldRender aExetendedFieldRender = new ExtendedFieldRender();
				aExetendedFieldRender.setTypeCode(assetType.getAssetType());
				aExetendedFieldRender.setTypeCodeDesc(assetType.getAssetType());

				Map<String, Object> extFieldMap = renderMapList.get(i);

				aExetendedFieldRender.setReference((String) extFieldMap.get("Reference"));
				extFieldMap.remove("Reference");
				aExetendedFieldRender.setSeqNo(Integer.valueOf(extFieldMap.get("SeqNo").toString()));
				extFieldMap.remove("SeqNo");
				aExetendedFieldRender.setVersion(Integer.valueOf(extFieldMap.get("Version").toString()));
				extFieldMap.remove("Version");
				aExetendedFieldRender.setLastMntOn((Timestamp) extFieldMap.get("LastMntOn"));
				extFieldMap.remove("LastMntOn");
				aExetendedFieldRender.setLastMntBy(Long.valueOf(extFieldMap.get("LastMntBy").toString()));
				extFieldMap.remove("LastMntBy");
				aExetendedFieldRender.setRecordStatus(getFieldValue("RecordStatus", extFieldMap));
				extFieldMap.remove("RecordStatus");
				aExetendedFieldRender.setRoleCode(getFieldValue("RoleCode", extFieldMap));
				extFieldMap.remove("RoleCode");
				aExetendedFieldRender.setNextRoleCode(getFieldValue("NextRoleCode", extFieldMap));
				extFieldMap.remove("NextRoleCode");
				aExetendedFieldRender.setTaskId(getFieldValue("TaskId", extFieldMap));
				extFieldMap.remove("TaskId");
				aExetendedFieldRender.setNextTaskId(getFieldValue("NextTaskId", extFieldMap));
				extFieldMap.remove("NextTaskId");
				aExetendedFieldRender.setRecordType(getFieldValue("RecordType", extFieldMap));
				extFieldMap.remove("RecordType");
				aExetendedFieldRender.setWorkflowId(Long.valueOf(extFieldMap.get("WorkflowId").toString()));
				extFieldMap.remove("WorkflowId");

				aExetendedFieldRender.setMapValues(extFieldMap);
				aExetendedFieldRender.setTableName(tableName.toString());
				extendedFieldRenderList.add(aExetendedFieldRender);
			}
		}
		return extendedFieldRenderList;
	}

	private List<VASRecording> getVasRecordings(String finReference, String tableType) {
		List<VASRecording> vasRecordingsList = new ArrayList<VASRecording>();

		vasRecordingsList = vasRecordingDAO.getVASRecordingsByLinkRef(finReference, tableType);

		if (vasRecordingsList.isEmpty()) {
			return vasRecordingsList;
		}

		List<String> renderedVasProducts = new ArrayList<>();

		for (VASRecording recording : vasRecordingsList) {
			if (renderedVasProducts.contains(recording.getProductCode())) {
				continue;
			}

			StringBuilder tableName = new StringBuilder();
			tableName.append(VASConsatnts.MODULE_NAME);
			tableName.append("_");
			tableName.append(recording.getProductCode());
			tableName.append("_ED");

			renderedVasProducts.add(recording.getProductCode());

			Map<String, Object> extFieldMap = extendedFieldRenderDAO.getExtendedField(recording.getVasReference(),
					tableName.toString(), "_View");

			if (extFieldMap == null) {
				continue;
			}

			ExtendedFieldRender aExetendedFieldRender = new ExtendedFieldRender();
			aExetendedFieldRender.setTypeCode(recording.getProductCode());
			aExetendedFieldRender.setTypeCodeDesc(recording.getProductDesc());

			aExetendedFieldRender.setReference((String) extFieldMap.get("Reference"));
			extFieldMap.remove("Reference");
			aExetendedFieldRender.setSeqNo(Integer.valueOf(extFieldMap.get("SeqNo").toString()));
			extFieldMap.remove("SeqNo");
			aExetendedFieldRender.setVersion(Integer.valueOf(extFieldMap.get("Version").toString()));
			extFieldMap.remove("Version");
			aExetendedFieldRender.setLastMntOn((Timestamp) extFieldMap.get("LastMntOn"));
			extFieldMap.remove("LastMntOn");
			aExetendedFieldRender.setLastMntBy(Long.valueOf(extFieldMap.get("LastMntBy").toString()));
			extFieldMap.remove("LastMntBy");
			aExetendedFieldRender.setRecordStatus(getFieldValue("RecordStatus", extFieldMap));
			extFieldMap.remove("RecordStatus");
			aExetendedFieldRender.setRoleCode(getFieldValue("RoleCode", extFieldMap));
			extFieldMap.remove("RoleCode");
			aExetendedFieldRender.setNextRoleCode(getFieldValue("NextRoleCode", extFieldMap));
			extFieldMap.remove("NextRoleCode");
			aExetendedFieldRender.setTaskId(getFieldValue("TaskId", extFieldMap));
			extFieldMap.remove("TaskId");
			aExetendedFieldRender.setNextTaskId(getFieldValue("NextTaskId", extFieldMap));
			extFieldMap.remove("NextTaskId");
			aExetendedFieldRender.setRecordType(getFieldValue("RecordType", extFieldMap));
			extFieldMap.remove("RecordType");
			aExetendedFieldRender.setWorkflowId(Long.valueOf(extFieldMap.get("WorkflowId").toString()));
			extFieldMap.remove("WorkflowId");

			aExetendedFieldRender.setMapValues(extFieldMap);
			recording.setExtendedFieldRender(aExetendedFieldRender);
		}
		return vasRecordingsList;
	}

	private String getFieldValue(String columnName, Map<String, Object> extFieldMap) {
		String value = String.valueOf(extFieldMap.get(columnName));
		return StringUtils.equals(value, "null") ? "" : value;
	}

	@Override
	public FinanceDetail getServicingFinanceForQDP(long finID, String eventCode, String procEdtEvent, String userrole) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = getServicingFinance(finID, eventCode, procEdtEvent, userrole);
		fd.getFinScheduleData()
				.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false));

		logger.debug(Literal.LEAVING);

		return fd;
	}

	@Override
	public FinanceDetail getServicingFinance(long finID, String eventCodeRef, String serviceEvent, String userrole) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = getFinSchdDetailById(finID, "_View", false);
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		String finReference = fm.getFinReference();

		List<FinServiceInstruction> siList = finServiceInstructionDAO.getFinServiceInstructions(finID, "_Temp",
				serviceEvent);

		schdData.setFinServiceInstructions(siList);

		BigDecimal pftChg = BigDecimal.ZERO;
		for (FinServiceInstruction si : siList) {
			pftChg = pftChg.add(si.getPftChg());
		}

		schdData.setPftChg(pftChg);

		String tableType = "";
		if (StringUtils.isNotBlank(fm.getRecordType())) {
			tableType = "_Temp";
		}

		if (fm.isPlanEMIHAlw()) {
			if (FinanceConstants.PLANEMIHMETHOD_FRQ.equals(fm.getPlanEMIHMethod())) {
				schdData.setPlanEMIHmonths(finPlanEmiHolidayDAO.getPlanEMIHMonthsByRef(finID, tableType));
			} else if (FinanceConstants.PLANEMIHMETHOD_ADHOC.equals(fm.getPlanEMIHMethod())) {
				schdData.setPlanEMIHDates(finPlanEmiHolidayDAO.getPlanEMIHDatesByRef(finID, tableType));
			}
		}

		// Finance Customer Details
		if (fm.getCustID() != 0 && fm.getCustID() != Long.MIN_VALUE) {
			fd.setCustomerDetails(customerDetailsService.getCustomerDetailsById(fm.getCustID(), true, "_View"));
		}

		// Finance Reference Details List
		fd = getFinanceReferenceDetails(fd, userrole, "DDE", eventCodeRef, serviceEvent, true);

		// Finance Document Details
		List<DocumentDetails> documents = documentDetailsDAO.getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, serviceEvent, "_View");
		fd.getDocumentDetailsList().addAll(documents);

		if (FinServiceEvent.BASICMAINTAIN.equals(serviceEvent) || FinServiceEvent.ADDDISB.equals(serviceEvent)) {
			// Finance Guaranteer Details
			fd.setGurantorsDetailList(guarantorDetailService.getGuarantorDetail(finID, "_View"));
			// Finance Joint Account Details
			fd.setJointAccountDetailList(jointAccountDetailService.getJoinAccountDetail(finID, "_View"));
		}

		if (StringUtils.equals(serviceEvent, FinServiceEvent.ADDDISB)
				|| StringUtils.equals(serviceEvent, FinServiceEvent.CANCELDISB)) {
			// Advance Payment Details
			fd.setAdvancePaymentsList(finAdvancePaymentsService.getFinAdvancePaymentsById(finID, "_View"));
		}

		if (StringUtils.isNotBlank(fm.getPromotionCode())) {
			fd.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesList(fm.getPromotionCode(), eventCodeRef, "_AView",
					false, FinanceConstants.MODULEID_PROMOTION));
		} else {
			fd.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesList(fm.getFinType(), eventCodeRef, "_AView", false,
					FinanceConstants.MODULEID_FINTYPE));
		}

		// Finance Fee Details
		schdData.setFinFeeDetailList(finFeeDetailService.getFinFeeDetailById(finID, false, "_TView", eventCodeRef));

		List<FinanceDisbursement> deductDisbFeeList = financeDisbursementDAO.getDeductDisbFeeDetails(finID);

		if (CollectionUtils.isNotEmpty(deductDisbFeeList)) {
			for (FinanceDisbursement disbursement : deductDisbFeeList) {
				for (FinanceDisbursement finDisb : schdData.getDisbursementDetails()) {
					if (finDisb.getDisbSeq() == disbursement.getDisbSeq()) {
						finDisb.setDeductFeeDisb(disbursement.getDeductFeeDisb());
						break;
					}
				}
			}
		}

		// Restructure Details
		if (FinServiceEvent.RESTRUCTURE.equals(serviceEvent)) {
			RestructureDetail restructure = restructureService.getRestructureDetailByRef(finID, "_View");
			schdData.setRestructureDetail(restructure);
			schdData.getFinanceMain().setOldSchedules(financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false));
			if (CollectionUtils.isNotEmpty(fd.getFinTypeFeesList())) {
				fd.setFinTypeFeesList(new ArrayList<FinTypeFees>());
			}
		}

		// Collateral Details
		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			fd.setCollateralAssignmentList(collateralAssignmentDAO.getCollateralAssignmentByFinRef(finReference,
					FinanceConstants.MODULE_NAME, "_View"));
			if (CollectionUtils.isEmpty(fd.getCollateralAssignmentList())) {
				fd.setCollateralAssignmentList(collateralSetupService.getCollateralAssignmentByFinRef(
						fm.getFinReference(), FinanceConstants.MODULE_NAME, "_CTView"));
			}
		} else {
			fd.setFinanceCollaterals(finCollateralService.getFinCollateralsByRef(finID, "_TView"));
		}

		if (!AccountingEvent.RESTRUCTURE.equals(eventCodeRef)) {
			fd.setCovenantTypeList(finCovenantTypeService.getFinCovenantTypeById(finReference, "_View", false));
		}

		if (ImplementationConstants.COVENANT_MODULE_NEW) {
			fd.setCovenants(covenantsService.getCovenants(finReference, "Loan", TableType.VIEW));
		}

		if (fm.isFinOcrRequired()) {
			fd.setFinOCRHeader(finOCRHeaderService.getApprovedFinOCRHeaderByRef(finID, "_View"));
		}

		if (schdData.getFinanceMain().isStepFinance()) {
			schdData.setStepPolicyDetails(financeStepDetailDAO.getFinStepDetailListByFinRef(finID, "_View", false));
		}

		// Manual Schedule Details
		if (fm.isManualSchedule()) {
			schdData.setManualScheduleHeader(
					this.manualScheduleService.getManualScheduleDetails(finID, serviceEvent, TableType.TEMP_TAB));
		}

		// Variable OD Schedule Details
		if (OverdraftConstants.DROPING_METHOD_VARIABLE.equals(fm.getDroppingMethod())) {
			schdData.setVariableOverdraftSchdHeader(
					this.variableOverdraftSchdService.getHeader(finReference, serviceEvent, TableType.TEMP_TAB));
		}

		// Manual Schedule Details
		if (fm.isManualSchedule()) {
			schdData.setManualScheduleHeader(
					this.manualScheduleService.getManualScheduleDetails(finID, serviceEvent, TableType.TEMP_TAB));
		}

		logger.debug(Literal.LEAVING);

		return fd;
	}

	@Override
	public FinanceDetail getWIFFinance(long finID, boolean reqCustDetail, String procEdtEvent) {
		logger.debug(Literal.ENTERING);

		// Finance Details
		FinanceDetail fd = getFinSchdDetailById(finID, "_View", true);
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		// Finance Fee Details
		schdData.setFinFeeDetailList(finFeeDetailService.getFinFeeDetailById(finID, true, "_View"));

		long custId = fm.getCustID();
		if (custId != 0) {
			fd.setCustomer(customerDAO.getWIFCustomerByID(custId, null, "_AView"));

			if (fd.getCustomer() == null) {
				Customer customer = customerDAO.getCustomerByID(custId, "");
				WIFCustomer wifcustomer = new WIFCustomer();
				BeanUtils.copyProperties(customer, wifcustomer);
				wifcustomer.setExistCustID(wifcustomer.getCustID());
				wifcustomer.setCustID(0);
				wifcustomer.setNewRecord(true);
				fd.setCustomer(wifcustomer);
			} else {
				fd.getCustomer().setNewRecord(false);
			}
		}

		if (reqCustDetail) {

			if (StringUtils.equals(schdData.getFinanceType().getFinDivision(), FinanceConstants.FIN_DIVISION_RETAIL)) {
				if (custId != 0) {
					fd.setCustomer(customerDAO.getWIFCustomerByID(custId, null, "_AView"));

					if (fd.getCustomer() == null) {
						Customer customer = customerDAO.getCustomerByID(custId, "");
						WIFCustomer wifcustomer = new WIFCustomer();
						BeanUtils.copyProperties(customer, wifcustomer);
						wifcustomer.setExistCustID(wifcustomer.getCustID());
						wifcustomer.setCustID(0);
						wifcustomer.setNewRecord(true);
						fd.setCustomer(wifcustomer);
					}
				} else {
					WIFCustomer wifcustomer = new WIFCustomer();
					wifcustomer.setNewRecord(true);
					wifcustomer.setCustBaseCcy(SysParamUtil.getAppCurrency());

					Country defaultCountry = PennantApplicationUtil.getDefaultCounty();

					wifcustomer.setCustNationality(defaultCountry.getCountryCode());
					wifcustomer.setLovDescCustNationalityName(defaultCountry.getCountryDesc());
					wifcustomer.setCustTypeCode("EA");
					wifcustomer.setLovDescCustTypeCodeName("Individual");
					wifcustomer.setCustCtgCode("INDV");
					wifcustomer.setLovDescCustCtgCodeName("Individual");
					fd.setCustomer(wifcustomer);
				}

				fd.getCustomer().setCustomerIncomeList(prepareIncomeDetails());
				String finType = schdData.getFinanceType().getFinType();

				fd.setElgRuleList(eligibilityDetailService.setFinanceEligibilityDetails(finID, fm.getFinCcy(),
						fm.getFinAmount(), fm.isNewRecord(), finType, null, procEdtEvent));

				fd = scoringDetailService.setFinanceScoringDetails(fd, finType, null,
						PennantConstants.PFF_CUSTCTG_INDIV, procEdtEvent);
			}
		} else if (StringUtils.equals(schdData.getFinanceType().getFinDivision(),
				FinanceConstants.FIN_DIVISION_RETAIL)) {

			ProspectCustomer propCustomer = customerDAO.getProspectCustomer(finID, "_View");
			if (propCustomer != null) {
				fm.setCustID(propCustomer.getCustId());
				fm.setLovDescCustCIF(propCustomer.getCustCIF());
				fm.setLovDescCustShrtName(propCustomer.getCustShrtName());
				fm.setLovDescCustCtgCode(propCustomer.getCustCtgCode());
				fm.setFinBranch(propCustomer.getCustDftBranch());
			} else {
				fm.setCustID(0);
				fm.setLovDescCustCIF("");
				fm.setLovDescCustShrtName("");
				fm.setLovDescCustCtgCode("");
				fm.setFinBranch("");
			}
		}

		logger.debug(Literal.LEAVING);
		return fd;
	}

	private FinanceDetail getWIFFinanceDetail(long finID, String eventCodeRef, boolean reqCustDetail,
			String procEdtEvent, String userRole) {

		FinanceDetail fd = getFinSchdDetailById(finID, "_View", true);
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		schdData.setFinServiceInstructions(
				finServiceInstructionDAO.getFinServiceInstructions(finID, "_Temp", procEdtEvent));

		FinanceType financeType = schdData.getFinanceType();

		if (reqCustDetail && fm != null) {

			if (FinanceConstants.FIN_DIVISION_RETAIL.equals(financeType.getFinDivision())) {

				long custId = fm.getCustID();
				if (custId != 0) {
					fd.setCustomer(customerDAO.getWIFCustomerByID(custId, null, "_AView"));
					if (fd.getCustomer() == null) {
						Customer customer = customerDAO.getCustomerByID(custId, "");
						WIFCustomer wifcustomer = new WIFCustomer();
						BeanUtils.copyProperties(customer, wifcustomer);
						wifcustomer.setExistCustID(wifcustomer.getCustID());
						wifcustomer.setCustID(0);
						wifcustomer.setNewRecord(true);
						fd.setCustomer(wifcustomer);
					}
				} else {
					WIFCustomer wifcustomer = new WIFCustomer();
					wifcustomer.setNewRecord(true);
					wifcustomer.setCustBaseCcy(SysParamUtil.getAppCurrency());

					Country defaultCountry = PennantApplicationUtil.getDefaultCounty();
					wifcustomer.setCustNationality(defaultCountry.getCountryCode());
					wifcustomer.setLovDescCustNationalityName(defaultCountry.getCountryDesc());

					wifcustomer.setCustTypeCode("EA");
					wifcustomer.setLovDescCustTypeCodeName("Individual");
					wifcustomer.setCustCtgCode("INDV");
					wifcustomer.setLovDescCustCtgCodeName("Individual");
					fd.setCustomer(wifcustomer);
				}

				fd.getCustomer().setCustomerIncomeList(prepareIncomeDetails());
				String finType = financeType.getFinType();

				fd.setElgRuleList(eligibilityDetailService.setFinanceEligibilityDetails(finID, fm.getFinCcy(),
						fm.getFinAmount(), fm.isNewRecord(), finType, null, procEdtEvent));

				fd = scoringDetailService.setFinanceScoringDetails(fd, finType, null,
						PennantConstants.PFF_CUSTCTG_INDIV, procEdtEvent);
			}

		} else if (FinanceConstants.FIN_DIVISION_RETAIL.equals(financeType.getFinDivision())) {
			ProspectCustomer propCustomer = customerDAO.getProspectCustomer(finID, "_View");
			if (propCustomer != null) {
				fm.setCustID(propCustomer.getCustId());
				fm.setLovDescCustCIF(propCustomer.getCustCIF());
				fm.setLovDescCustShrtName(propCustomer.getCustShrtName());
				fm.setLovDescCustCtgCode(propCustomer.getCustCtgCode());
				fm.setFinBranch(propCustomer.getCustDftBranch());
			} else {
				fm.setCustID(0);
				fm.setLovDescCustCIF("");
				fm.setLovDescCustShrtName("");
				fm.setLovDescCustCtgCode("");
				fm.setFinBranch("");
			}
		}

		schdData.setFinFeeDetailList(finFeeDetailService.getFinFeeDetailById(finID, true, "_View"));

		logger.debug(Literal.LEAVING);
		return fd;

	}

	@Override
	public FinanceDetail getFinanceDetailById(long finID, boolean isWIF, String eventCodeRef, boolean reqCustDetail,
			String procEdtEvent, String userRole) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = null;
		if (isWIF) {
			fd = getWIFFinanceDetail(finID, eventCodeRef, reqCustDetail, procEdtEvent, userRole);

			logger.debug(Literal.LEAVING);

			return fd;
		}

		fd = getFinSchdDetailById(finID, "_View", false);
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		schdData.setFinServiceInstructions(
				finServiceInstructionDAO.getFinServiceInstructions(finID, "_Temp", procEdtEvent));

		if (fm.getCustID() != 0 && fm.getCustID() != Long.MIN_VALUE) {
			fd.setCustomerDetails(customerDetailsService.getCustomerDetailsById(fm.getCustID(), true, "_View"));
		}

		if (ImplementationConstants.ALLOW_DEVIATIONS) {
			if (StringUtils.equals(procEdtEvent, FinServiceEvent.ORG)) {
				List<FinanceDeviations> finDeviations = deviationDetailsService.getFinanceDeviations(finID);
				List<FinanceDeviations> apprFinDeviations = deviationDetailsService.getApprovedFinanceDeviations(finID);
				deviationHelper.setDeviationDetails(fd, finDeviations, apprFinDeviations);
			}
		}

		fd.setMandate(finMandateService.getMnadateByID(fm.getMandateID()));

		String finReference = fm.getFinReference();

		fd = getFinanceReferenceDetails(fd, userRole, "DDE", eventCodeRef, procEdtEvent, true);

		List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, procEdtEvent, "_View");
		if (fd.getDocumentDetailsList() != null && !fd.getDocumentDetailsList().isEmpty()) {
			fd.getDocumentDetailsList().addAll(documentList);
		} else {
			fd.setDocumentDetailsList(documentList);
		}

		if (FinServiceEvent.ORG.equals(procEdtEvent) || FinServiceEvent.BASICMAINTAIN.equals(procEdtEvent)) {
			fd.setGurantorsDetailList(guarantorDetailService.getGuarantorDetail(finID, "_View"));
			fd.setJointAccountDetailList(jointAccountDetailService.getJoinAccountDetail(finID, "_View"));
			fd.setAdvancePaymentsList(finAdvancePaymentsService.getFinAdvancePaymentsById(finID, "_View"));

			if (ImplementationConstants.ALLOW_COVENANT_TYPES) {
				fd.setCovenantTypeList(finCovenantTypeService.getFinCovenantTypeById(finReference, "_View", false));
			}

			if (ImplementationConstants.COVENANT_MODULE_NEW) {
				fd.setCovenants(covenantsService.getCovenants(finReference, "Loan", TableType.VIEW));
			}

			fd.setFinOptions(finOptionService.getFinOptions(finID, TableType.VIEW));
			fd.setFinAssetTypesList(finAssetTypeDAO.getFinAssetTypesByFinRef(finReference, "_Temp"));
			fd.setExtendedFieldRenderList(getExtendedAssetDetails(finReference, fd.getFinAssetTypesList()));

			if (ImplementationConstants.COLLATERAL_INTERNAL) {
				fd.setCollateralAssignmentList(collateralAssignmentDAO.getCollateralAssignmentByFinRef(finReference,
						FinanceConstants.MODULE_NAME, "_View"));
			} else {
				fd.setFinanceCollaterals(finCollateralService.getFinCollateralsByRef(finID, "_View"));
			}
		}

		schdData.setFinFeeDetailList(finFeeDetailService.getFinFeeDetailById(finID, false, "_View"));

		logger.debug(Literal.LEAVING);

		return fd;
	}

	/**
	 * getFinanceDetailById fetch the details by using FinanceMainDAO's getFinanceDetailById method.
	 * 
	 * @param finReference (String)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getWIFFinanceDetailById(long finID, String procEdtEvent) {
		logger.debug(Literal.ENTERING);

		// Finance Details
		FinanceDetail financeDetail = getFinSchdDetailById(finID, "_View", true);
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();

		// Finance Service Instructions
		financeDetail.getFinScheduleData().setFinServiceInstructions(
				finServiceInstructionDAO.getFinServiceInstructions(finID, "_Temp", procEdtEvent));

		// Finance Fee Details
		financeDetail.getFinScheduleData()
				.setFinFeeDetailList(finFeeDetailService.getFinFeeDetailById(finID, true, "_View"));

		if (StringUtils.equals(scheduleData.getFinanceType().getFinDivision(), FinanceConstants.FIN_DIVISION_RETAIL)) {
			ProspectCustomer propCustomer = customerDAO.getProspectCustomer(finID, "_View");
			if (propCustomer != null) {
				financeMain.setCustID(propCustomer.getCustId());
				financeMain.setLovDescCustCIF(propCustomer.getCustCIF());
				financeMain.setLovDescCustShrtName(propCustomer.getCustShrtName());
				financeMain.setLovDescCustCtgCode(propCustomer.getCustCtgCode());
				financeMain.setFinBranch(propCustomer.getCustDftBranch());
			} else {
				financeMain.setCustID(0);
				financeMain.setLovDescCustCIF("");
				financeMain.setLovDescCustShrtName("");
				financeMain.setLovDescCustCtgCode("");
				financeMain.setFinBranch("");
			}
		}

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	@Override
	public FinanceMain getFinanceMain(long finID, String type) {
		return financeMainDAO.getFinanceMainById(finID, type, false);
	}

	/**
	 * Method for Fetching List of Fee Charge Details depends on Event Code
	 * 
	 * @param finType
	 * @param startDate
	 * @param isWIF
	 * @return
	 */
	@Override
	public List<Rule> getFeeRuleDetails(FinanceType finType, Date startDate, boolean isWIF) {
		logger.debug(Literal.ENTERING);

		// Finance Accounting Fee Charge Details
		String eventCode = PennantApplicationUtil.getEventCode(startDate);
		Long accSetId = finTypeAccountingDAO.getAccountSetID(finType.getFinType(), eventCode,
				FinanceConstants.MODULEID_FINTYPE);

		// Fetch Stage Accounting AccountingSetId List
		List<Long> accSetIdList = new ArrayList<Long>();
		accSetIdList.addAll(financeReferenceDetailDAO.getRefIdListByFinType(finType.getFinType(), FinServiceEvent.ORG,
				null, "_ACView"));
		if (accSetId != Long.MIN_VALUE) {
			accSetIdList.add(Long.valueOf(accSetId));
		}

		// Finance Fee Charge Details
		List<Rule> feeChargeList = new ArrayList<Rule>();
		if (!accSetIdList.isEmpty()) {
			feeChargeList = transactionEntryDAO.getListFeeChargeRules(accSetIdList,
					eventCode.startsWith(AccountingEvent.ADDDBS) ? AccountingEvent.ADDDBS : eventCode, "", 0);
		}

		logger.debug(Literal.LEAVING);
		return feeChargeList;
	}

	/**
	 * Method for Fetching List of Fee Rules From Approved Finance
	 */
	@Override
	public List<FeeRule> getApprovedFeeRules(long finID, String finEvent, boolean isWIF) {
		logger.debug(Literal.ENTERING);
		List<FeeRule> feeRuleList = finFeeChargesDAO.getFeeChargesByFinRef(finID, finEvent, isWIF, "");
		logger.debug(Literal.LEAVING);
		return feeRuleList;
	}

	/**
	 * getApprovedFinanceDetailById fetch the details by using FinanceMainDAO's getFinanceDetailById method . with
	 * parameter id and type as blank. it fetches the approved records from the FinanceMain.
	 * 
	 * @param finReference (String)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getApprovedFinanceDetailById(long finID, boolean isWIF) {
		return getFinSchdDetailById(finID, "_AView", isWIF);

	}

	@Override
	public FinanceDetail getFinanceOrgDetails(FinanceMain fm, String type) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		schdData.setFinanceMain(fm);

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		String finType = fm.getFinType();

		schdData.setFinID(finID);
		schdData.setFinReference(finReference);

		FinanceType financeType = financeTypeDAO.getOrgFinanceTypeByID(finType, "_ORGView");
		schdData.setFinanceType(financeType);
		schdData.getFinanceMain().setOverdraftTxnChrgFeeType(financeType.getOverdraftTxnChrgFeeType());

		if (StringUtils.isNotBlank(fm.getPromotionCode())) {
			Promotion promotion = this.promotionDAO.getPromotionByReferenceId(fm.getPromotionSeqId(), "_AView");
			financeType.setFInTypeFromPromotiion(promotion);
		}

		// Step Policy Details List
		if (fm.isStepFinance()) {
			schdData.setStepPolicyDetails(financeStepDetailDAO.getFinStepDetailListByFinRef(finID, type, false));
		}

		// Overdraft Details
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())) {
			List<OverdraftScheduleDetail> odSchedules = null;
			odSchedules = overdraftScheduleDetailDAO.getOverdraftScheduleDetails(finID, "_Temp", false);
			schdData.setOverdraftScheduleDetails(odSchedules);
		}

		// Finance Flag Details
		fd.setFinFlagsDetails(
				finFlagDetailsDAO.getFinFlagsByFinRef(finReference, FinanceConstants.MODULE_NAME, "_Temp"));

		// Finance Schedule Details
		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false));

		// Finance Disbursement Details
		schdData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, type, false));

		if (subventionService != null) {
			subventionService.setSubventionScheduleDetails(schdData, type);
		}

		// Finance Repayments Instruction Details
		schdData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, type, false));

		String tableType = "";
		if (StringUtils.isNotBlank(fm.getRecordType())) {
			tableType = "_Temp";
		}

		schdData.setFinServiceInstructions(finServiceInstructionDAO.getOrgFinServiceInstructions(finID, tableType));

		// Finance Overdue Penalty Rate Details
		schdData.setFinODPenaltyRate(finODPenaltyRateDAO.getEffectivePenaltyRate(finID, type));

		// Plan EMI Holiday Details
		int sdSize = schdData.getFinanceScheduleDetails().size();

		if (fm.isPlanEMIHAlw() && sdSize > 0) {
			if (FinanceConstants.PLANEMIHMETHOD_FRQ.equals(fm.getPlanEMIHMethod())) {
				schdData.setPlanEMIHmonths(finPlanEmiHolidayDAO.getPlanEMIHMonthsByRef(finID, tableType));
			} else if (FinanceConstants.PLANEMIHMETHOD_ADHOC.equals(fm.getPlanEMIHMethod())) {
				schdData.setPlanEMIHDates(finPlanEmiHolidayDAO.getPlanEMIHDatesByRef(finID, tableType));
			}
		}

		logger.debug(Literal.LEAVING);

		return fd;
	}

	@Override
	public FinanceDetail getFinSchdDetailByRef(String finReference, String type, boolean isWIF) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = new FinanceDetail();
		FinanceMain fm = financeMainDAO.getFinanceMainByRef(finReference, type, isWIF);

		if (fm == null) {
			logger.debug(Literal.LEAVING);
			return fd;
		}

		fd.getFinScheduleData().setFinanceMain(fm);

		setFinanceDetails(fd, type, isWIF);

		logger.debug(Literal.LEAVING);
		return fd;
	}

	@Override
	public FinanceDetail getFinSchdDetailById(long finID, String type, boolean isWIF) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = new FinanceDetail();

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, type, isWIF);

		if (fm == null) {
			logger.debug(Literal.LEAVING);
			return fd;
		}

		fd.getFinScheduleData().setFinanceMain(fm);

		setFinanceDetails(fd, type, isWIF);

		logger.debug(Literal.LEAVING);
		return fd;
	}

	private void setFinanceDetails(FinanceDetail fd, String type, boolean isWIF) {
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		FinScheduleData schdData = fd.getFinScheduleData();
		schdData.setFinID(finID);
		schdData.setFinReference(finReference);

		schdData.setFinanceMain(fm);

		setDasAndDmaData(fm);

		// Finance Type Details
		FinanceType financeType = financeTypeDAO.getOrgFinanceTypeByID(fm.getFinType(), "_ORGView");
		if (StringUtils.isNotBlank(fm.getPromotionCode())) {
			// Fetching Promotion Details
			Promotion promotion = this.promotionDAO.getPromotionByReferenceId(fm.getPromotionSeqId(), "_AView");
			financeType.setFInTypeFromPromotiion(promotion);
		}
		schdData.setFinanceType(financeType);

		// Step Policy Details List
		if (fm.isStepFinance()) {
			schdData.setStepPolicyDetails(
					financeStepDetailDAO.getFinStepDetailListByFinRef(finID, isWIF ? "_View" : "_TView", isWIF));
		}

		// Finance Schedule Details
		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, isWIF));

		// Finance Disbursement Details
		schdData.setDisbursementDetails(
				financeDisbursementDAO.getFinanceDisbursementDetails(finID, isWIF ? "_View" : type, isWIF));

		if (subventionService != null) {
			subventionService.setSubventionDetails(schdData, "_View");
			subventionService.setSubventionScheduleDetails(schdData, type);
		}

		// Finance Repayments Instruction Details
		schdData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, type, isWIF));

		// Fee Details
		List<FinFeeDetail> finOriginationFeeList = finFeeDetailDAO.getFinScheduleFees(finID, false, "_View");
		schdData.setFinFeeDetailList(finOriginationFeeList);

		// Finance Fee Schedule Details
		List<Long> feeIDList = new ArrayList<>();
		for (FinFeeDetail feeDetail : finOriginationFeeList) {
			feeIDList.add(feeDetail.getFeeID());
			feeDetail.setRcdVisible(false);
		}

		List<FinFeeScheduleDetail> feeScheduleList = new ArrayList<>();

		if (CollectionUtils.isNotEmpty(feeIDList)) {
			feeScheduleList.addAll(finFeeScheduleDetailDAO.getFeeScheduleByFinID(feeIDList, false, ""));
		}

		Map<Long, List<FinFeeScheduleDetail>> schFeeMap = new HashMap<>();
		for (FinFeeScheduleDetail schdFee : feeScheduleList) {
			List<FinFeeScheduleDetail> schList = new ArrayList<>();

			if (schFeeMap.containsKey(schdFee.getFeeID())) {
				schList = schFeeMap.get(schdFee.getFeeID());
				schFeeMap.remove(schdFee.getFeeID());
			}

			schList.add(schdFee);
			schFeeMap.put(schdFee.getFeeID(), schList);

		}

		for (FinFeeDetail feeDetail : finOriginationFeeList) {
			if (schFeeMap.containsKey(feeDetail.getFeeID())) {
				feeDetail.setFinFeeScheduleDetailList(schFeeMap.get(feeDetail.getFeeID()));
			}
		}

		if (!isWIF) {
			fd.setFinFlagsDetails(
					finFlagDetailsDAO.getFinFlagsByFinRef(finReference, FinanceConstants.MODULE_NAME, type));

		}

		if (fm.istDSApplicable()) {
			schdData.setLowerTaxDeductionDetails(lowerTaxDeductionDAO.getLowerTaxDeductionDetails(finID, ""));
		}

		if (!isWIF) {

			String tableType = "";
			if (StringUtils.isNotBlank(fm.getRecordType())) {
				tableType = "_Temp";
			}

			// Overdue Penalty Rates
			schdData.setFinODPenaltyRate(finODPenaltyRateDAO.getEffectivePenaltyRate(finID,
					StringUtils.equals(tableType, "") ? type : tableType));

			// Overdraft Schedule Detail
			if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())) {
				schdData.setOverdraftScheduleDetails(
						overdraftScheduleDetailDAO.getOverdraftScheduleDetails(finID, tableType, isWIF));
			}

			// Get Collateral Details if any
			List<CollateralAssignment> assignmentListMain = null;
			if (ImplementationConstants.COLLATERAL_INTERNAL) {
				assignmentListMain = collateralAssignmentDAO.getCollateralAssignmentByFinRef(finReference,
						FinanceConstants.MODULE_NAME, "_TView");
			} else {
				fd.setFinanceCollaterals(finCollateralService.getFinCollateralsByRef(finID, "_TView"));
			}

			// Collateral setup details and assignment details
			List<CollateralSetup> collateralSetupList = collateralSetupService.getCollateralDetails(finReference);
			List<CollateralAssignment> assignmentListTemp = null;
			if (CollectionUtils.isNotEmpty(collateralSetupList)) {
				if (ImplementationConstants.COLLATERAL_INTERNAL) {
					assignmentListTemp = collateralAssignmentDAO.getCollateralAssignmentByFinRef(finReference,
							FinanceConstants.MODULE_NAME, "_CTView");
				}
			}
			fd.setCollaterals(collateralSetupList);
			fd = setCollateralAssignments(fd, assignmentListMain, assignmentListTemp);
		}
	}

	@Override
	public FinanceDetail getFinanceReferenceDetails(FinanceDetail fd, String nextRoleCode, String screenCode,
			String eventCode, String procEdtEvent, boolean extFieldsReq) {
		logger.debug(Literal.ENTERING);

		List<Long> accSetIdList = new ArrayList<Long>();
		boolean isCustExist = true;
		FinanceType financeType = fd.getFinScheduleData().getFinanceType();
		FinanceMain financeMain = fd.getFinScheduleData().getFinanceMain();

		String ctgType = "";
		if (financeMain.getCustID() <= 0) {
			isCustExist = false;
		}
		if (fd.getCustomerDetails() != null && fd.getCustomerDetails().getCustomer() != null) {
			ctgType = fd.getCustomerDetails().getCustomer().getCustCtgCode();
		}

		List<FinanceReferenceDetail> aggrementList = new ArrayList<>(1);
		List<FinanceReferenceDetail> eligibilityList = new ArrayList<>(1);
		List<FinanceReferenceDetail> retScoringGroupList = new ArrayList<>(1);
		List<FinanceReferenceDetail> corpScoringGroupList = new ArrayList<>(1);
		List<FinanceReferenceDetail> checkListdetails = new ArrayList<>(1);
		List<FinanceReferenceDetail> finRefDetails;
		Map<String, String> showTabMap = new HashMap<>();

		String event = procEdtEvent;
		String tempNextRoleCode = StringUtils.isNotBlank(nextRoleCode) ? nextRoleCode.concat(",") : nextRoleCode;
		String finType = financeType.getFinType();

		if (StringUtils.isEmpty(event)) {
			event = FinServiceEvent.ORG;
		}

		finRefDetails = financeReferenceDetailDAO.getFinanceProcessEditorDetails(finType, event, "_FINVIEW");
		for (FinanceReferenceDetail finrefDetail : finRefDetails) {
			String reference = finrefDetail.getLovDescRefDesc();
			int finRefType = finrefDetail.getFinRefType();
			String showInStage = StringUtils.trimToEmpty(finrefDetail.getShowInStage());
			String mandInputInStage = StringUtils.trimToEmpty(finrefDetail.getMandInputInStage());
			String allowInputInStage = StringUtils.trimToEmpty(finrefDetail.getAllowInputInStage());
			String ruleReturnType = StringUtils.trimToEmpty(finrefDetail.getLovDescRuleReturnType());
			// fix in mandate tab show only OPRAPPROVER stage
			if ((!finrefDetail.isIsActive())
					|| ((FinanceConstants.PROCEDT_FINANCETABS != finRefType) && (StringUtils.isEmpty(reference)))) {
				continue;
			}

			switch (finRefType) {
			case FinanceConstants.PROCEDT_CHECKLIST:
				if (showInStage.contains(tempNextRoleCode)) {
					checkListdetails.add(finrefDetail);
				}
				break;
			case FinanceConstants.PROCEDT_AGREEMENT:
				if (mandInputInStage.contains((tempNextRoleCode))) {
					aggrementList.add(finrefDetail);
				}
				break;
			case FinanceConstants.PROCEDT_ELIGIBILITY:
				if (StringUtils.isNotEmpty(ruleReturnType) && allowInputInStage.contains(tempNextRoleCode)) {
					eligibilityList.add(finrefDetail);
				}
				break;
			case FinanceConstants.PROCEDT_RTLSCORE:
				if (mandInputInStage.contains(tempNextRoleCode)) {
					retScoringGroupList.add(finrefDetail);
				}
				break;
			case FinanceConstants.PROCEDT_CORPSCORE:
				if (mandInputInStage.contains(tempNextRoleCode)) {
					corpScoringGroupList.add(finrefDetail);
					continue;
				}
				break;
			case FinanceConstants.PROCEDT_STAGEACC:
				accSetIdList.add(finrefDetail.getFinRefId());
				break;
			case FinanceConstants.PROCEDT_LIMIT:
				if (mandInputInStage.contains(tempNextRoleCode)) {
					setMiscellaneousTabs(fd, reference);
				}
				break;
			case FinanceConstants.PROCEDT_FINANCETABS:
				showTabMap.put(StringUtils.leftPad(String.valueOf(finrefDetail.getTabCode()), 3, "0"),
						mandInputInStage);
				break;
			default:
				break;
			}
		}

		// Finance Agreement Details
		fd.setAggrementList(aggrementList);
		fd.setShowTabDetailMap(showTabMap);

		if (isCustExist) {
			if (financeMain.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())) {

				// Eligibility Details
				fd.setElgRuleList(eligibilityDetailService.fetchEligibilityDetails(financeMain, eligibilityList));

				// Scoring Details
				if (PennantConstants.PFF_CUSTCTG_INDIV.equals(ctgType)) {
					scoringDetailService.fetchFinScoringDetails(fd, retScoringGroupList, ctgType);
				} else {
					scoringDetailService.fetchFinScoringDetails(fd, corpScoringGroupList, ctgType);
				}
			}

			// Checklist Details
			checkListDetailService.fetchFinCheckListDetails(fd, checkListdetails);

			// Finance Stage Accounting Posting Details
			// =======================================
			List<TransactionEntry> stageEntries = new ArrayList<>();
			for (int i = 0; i < accSetIdList.size(); i++) {
				stageEntries.addAll(AccountingConfigCache.getTransactionEntry(accSetIdList.get(i)));
			}
			fd.setStageTransactionEntries(stageEntries);
		}

		// Accounting Set Details
		if (StringUtils.isBlank(eventCode)) {

			if (StringUtils.equalsIgnoreCase(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
				eventCode = AccountingEvent.CMTDISB;
			} else {
				eventCode = PennantApplicationUtil.getEventCode(financeMain.getFinStartDate());
			}
		}

		// Finance Commitment Accounting Posting Details
		// =======================================
		if (PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())) {
			if (financeType.isFinCommitmentReq() && StringUtils.isNotBlank(financeMain.getFinCommitmentRef())) {

				long accountingSetId = accountingSetDAO.getAccountingSetId(AccountingEvent.CMTDISB,
						AccountingEvent.CMTDISB);// TODO :

				if (accountingSetId != 0) {
					fd.setCmtFinanceEntries(AccountingConfigCache.getTransactionEntry(accountingSetId));
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return fd;
	}

	private void setMiscellaneousTabs(FinanceDetail financeDetail, String reference) {
		switch (reference) {
		case FinanceConstants.PROCEDT_VERIFICATION_FI_INIT:
			financeDetail.setFiInitTab(true);
			break;
		case FinanceConstants.PROCEDT_VERIFICATION_FI_APPR:
			financeDetail.setFiApprovalTab(true);
			break;
		case FinanceConstants.PROCEDT_VERIFICATION_TV_INIT:
			financeDetail.setTvInitTab(true);
			break;
		case FinanceConstants.PROCEDT_VERIFICATION_TV_APPR:
			financeDetail.setTvApprovalTab(true);
			break;
		case FinanceConstants.PROCEDT_VERIFICATION_LV_INIT:
			financeDetail.setLvInitTab(true);
			break;
		case FinanceConstants.PROCEDT_VERIFICATION_LV_APPR:
			financeDetail.setLvApprovalTab(true);
			break;
		case FinanceConstants.PROCEDT_VERIFICATION_RCU_INIT:
			financeDetail.setRcuInitTab(true);
			break;
		case FinanceConstants.PROCEDT_VERIFICATION_RCU_APPR:
			financeDetail.setRcuApprovalTab(true);
			break;
		case FinanceConstants.PROCEDT_SAMPLING_INIT:
			financeDetail.setSamplingInitiator(true);
			break;
		case FinanceConstants.PROCEDT_SAMPLING_APPR:
			financeDetail.setSamplingApprover(true);
			break;
		case FinanceConstants.PROCEDT_LEGAL_INIT:
			financeDetail.setLegalInitiator(true);
			break;
		case FinanceConstants.FEE_UPFRONT_REQ:
			financeDetail.setUpFrentFee(true);
			break;
		case FinanceConstants.PROCEDT_VERIFICATION_PD_INIT:
			financeDetail.setPdInitTab(true);
			break;
		case FinanceConstants.PROCEDT_VERIFICATION_PD_APPR:
			financeDetail.setPdApprovalTab(true);
			break;
		case FinanceConstants.PROCEDT_VERIFICATION_LVETTING_INIT:
			financeDetail.setVettingInitTab(true);
			break;
		case FinanceConstants.PROCEDT_VERIFICATION_LVETTING_APPR:
			financeDetail.setVettingApprovalTab(true);
			break;
		default:
			break;
		}
	}

	/**
	 * Method for testing Finance Reference is Already Exist or not
	 */
	@Override
	public boolean isFinReferenceExits(String finReference, String tableType, boolean isWIF) {
		logger.debug(Literal.ENTERING);
		if (isWIF) {
			tableType = "";
		}
		logger.debug(Literal.LEAVING);
		return financeMainDAO.isFinReferenceExists(finReference, tableType, isWIF);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table FinanceMain/FinanceMain_Temp by
	 * using FinanceMainDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using FinanceMainDAO's update method 3) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws DatatypeConfigurationException
	 * @throws AccountNotFoundException
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader, boolean isWIF) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		Date curBDay = SysParamUtil.getAppDate();

		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(fd);
		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction fsi : serviceInstructions) {
			serviceUID = fsi.getInstructionUID();
			fm.setInstructionUID(serviceUID);
			if (ObjectUtils.isEmpty(fsi.getInitiatedDate())) {
				fsi.setInitiatedDate(curBDay);
			}
		}

		fd.setValidateUpfrontFees(true);
		aAuditHeader = businessValidation(aAuditHeader, "saveOrUpdate", isWIF, false);
		List<AuditDetail> auditDetails = new ArrayList<>();
		if (!isWIF) {
			aAuditHeader = processLimitSaveOrUpdate(aAuditHeader, true);
		}
		if (!aAuditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return aAuditHeader;
		}

		/*
		 * Cloner cloner = new Cloner(); AuditHeader auditHeader = cloner.deepClone(aAuditHeader);
		 */
		AuditHeader auditHeader = null;
		try {
			auditHeader = aAuditHeader.getNewCopyInstance();
		} catch (Exception e) {
			logger.error("Error Occured {}", e);
		}

		TableType tableType = TableType.MAIN_TAB;
		if (fm.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		// Accounting (Stage/Posting) Execution Process
		// =======================================
		if (!isWIF) {

			// Finance Stage Accounting Process
			// =======================================

			PostingDTO postingDTO = new PostingDTO();
			postingDTO.setFinanceMain(fm);
			postingDTO.setFinanceDetail(fd);
			postingDTO.setValueDate(curBDay);
			postingDTO.setUserBranch(auditHeader.getAuditBranchCode());

			AccountingEngine.post(AccountingEvent.STAGE, postingDTO);
			if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
				return auditHeader;
			}

			// Accounting Execution Process on Maintenance
			// =======================================
			if (tableType == TableType.MAIN_TAB && fm.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {

				auditHeader = executeAccountingProcess(auditHeader, curBDay);
				if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
					return auditHeader;
				}
			}
		}

		String table = tableType.getSuffix();
		String moduleDefiner = fd.getModuleDefiner();
		if (isWIF) {

			// Customer Basic Details Maintenance
			// =======================================
			WIFCustomer customer = fd.getCustomer();
			if (customer != null) {
				long custId = customer.getCustID();
				if (customer.isNewRecord()) {
					custId = customerDAO.saveWIFCustomer(customer);
				} else {
					customerDAO.updateWIFCustomer(customer);
					getCustomerIncomeDAO().deleteByCustomer(customer.getCustID(), "", true);
				}

				if (customer.getCustomerIncomeList() != null && !customer.getCustomerIncomeList().isEmpty()) {
					for (CustomerIncome income : customer.getCustomerIncomeList()) {
						income.setCustId(custId);
					}
					getCustomerIncomeDAO().saveBatch(customer.getCustomerIncomeList(), "", true);
				}
				fm.setCustID(custId);
			}
		} else {
			// set Customer Details Audit
			if (!StringUtils.equals(fm.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)
					|| auditHeader.getApiHeader() == null) {
				if (fd.getCustomerDetails() != null && StringUtils.equals(moduleDefiner, FinServiceEvent.ORG)) {
					auditDetails.addAll(customerDetailsService.saveOrUpdate(fd, ""));
				}
			}
		}

		// Finance Main Details Save And Update
		// =======================================
		// Update Task_log and Task_Owners tables
		if (!isWIF) {
			updateTaskLog(fm, true);
		}

		if (fd.getMandate() != null) {
			Mandate mandate = fd.getMandate();
			finMandateService.saveOrUpdate(fm, mandate, auditHeader, table);
		}

		if (fd.getSecurityMandate() != null) {
			Mandate mandate = fd.getSecurityMandate();
			finMandateService.saveOrUpdate(fm, mandate, auditHeader, table);
		}

		if (fm.isNewRecord()) {
			// Lock Functionality not required while Creating loan From API
			if (StringUtils.equals(fm.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)
					&& auditHeader.getApiHeader() != null) {
				fm.setNextUserId(null);
			}
			financeMainDAO.save(fm, tableType, isWIF);

			// Overdraft Details
			if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())
					&& !schdData.getOverdraftScheduleDetails().isEmpty()) {

				for (int i = 0; i < schdData.getOverdraftScheduleDetails().size(); i++) {
					schdData.getOverdraftScheduleDetails().get(i).setFinID(finID);
					schdData.getOverdraftScheduleDetails().get(i).setFinReference(finReference);
				}
				overdraftScheduleDetailDAO.saveList(schdData.getOverdraftScheduleDetails(), table);
			}
		} else {
			financeMainDAO.update(fm, tableType, isWIF);

			// Overdraft Details
			if (fm.isLovDescIsSchdGenerated()
					&& StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())
					&& !schdData.getOverdraftScheduleDetails().isEmpty()) {

				// Existing Data deletion
				overdraftScheduleDetailDAO.deleteByFinReference(finID, "_Temp", isWIF);

				// Save New list of records
				for (int i = 0; i < schdData.getOverdraftScheduleDetails().size(); i++) {
					schdData.getOverdraftScheduleDetails().get(i).setFinID(finID);
					schdData.getOverdraftScheduleDetails().get(i).setFinReference(finReference);
				}
				overdraftScheduleDetailDAO.saveList(schdData.getOverdraftScheduleDetails(), table);
			}
		}

		// Save or Update FInance Tax Details
		FinanceTaxDetail taxDetail = fd.getFinanceTaxDetail();
		if (taxDetail != null) {
			FinanceTaxDetail tempTaxDetail = financeTaxDetailDAO.getFinanceTaxDetail(finID, "_View");
			if (tempTaxDetail != null) {
				financeTaxDetailDAO.delete(taxDetail, tableType);
			}
			if (PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER.equals(taxDetail.getApplicableFor())
					|| PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT.equals(taxDetail.getApplicableFor())
					|| PennantConstants.TAXAPPLICABLEFOR_GUARANTOR.equals(taxDetail.getApplicableFor())) {

				// if we take new customer case
				if (taxDetail.getTaxCustId() <= 0
						&& PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER.equals(taxDetail.getApplicableFor())) {
					taxDetail.setTaxCustId(fm.getCustID());
				}

				taxDetail.setFinID(finID);
				taxDetail.setFinReference(finReference);
				taxDetail.setTaskId(fm.getTaskId());
				taxDetail.setNextTaskId(fm.getNextTaskId());
				taxDetail.setRoleCode(fm.getRoleCode());
				taxDetail.setNextRoleCode(fm.getNextRoleCode());
				taxDetail.setRecordStatus(fm.getRecordStatus());
				taxDetail.setWorkflowId(fm.getWorkflowId());
				financeTaxDetailDAO.save(taxDetail, tableType);
			}
		}

		// =======================================
		String auditTranType = auditHeader.getAuditTranType();
		// Save Cheque Header Details
		// =======================================
		if (fd.getChequeHeader() != null) {
			String[] fields = PennantJavaUtil.getFieldDetails(new ChequeHeader(),
					fd.getChequeHeader().getExcludeFields());
			finChequeHeaderService.saveOrUpdate(auditHeader, tableType);
			auditDetails.addAll(auditHeader.getAuditDetails());
			auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], fd.getChequeHeader().getBefImage(),
					fd.getChequeHeader()));
		}

		// Save schedule details
		// =======================================
		if (!fd.isNewRecord()) {

			if (!isWIF && tableType == TableType.MAIN_TAB
					&& fm.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
				// Fetch Existing data before Modification

				FinScheduleData oldFinSchdData = null;
				if (fm.isScheduleRegenerated()) {
					oldFinSchdData = getFinSchDataByFinRef(finID, "", -1);
					oldFinSchdData.setFinID(schdData.getFinID());
					oldFinSchdData.setFinReference(schdData.getFinReference());
				}

				// Create log entry for Action for Schedule Modification
				FinLogEntryDetail entryDetail = new FinLogEntryDetail();
				entryDetail.setFinID(schdData.getFinID());
				entryDetail.setFinReference(schdData.getFinReference());
				entryDetail.setEventAction(fd.getAccountingEventCode());
				entryDetail.setSchdlRecal(fm.isScheduleRegenerated());
				entryDetail.setPostDate(curBDay);
				entryDetail.setReversalCompleted(false);
				long logKey = finLogEntryDetailDAO.save(entryDetail);

				// Save Schedule Details For Future Modifications
				if (fm.isScheduleRegenerated()) {
					listSave(oldFinSchdData, "_Log", false, logKey, serviceUID);
				}
			}

			listDeletion(schdData, moduleDefiner, table, isWIF);
			finServiceInstructionDAO.deleteList(finID, moduleDefiner, "_Temp");
			listSave(schdData, table, isWIF, 0, serviceUID);
			saveFeeChargeList(schdData, moduleDefiner, isWIF, table);

			// Finance IRR Values
			// =======================================
			deleteFinIRR(finID, tableType);
			saveFinIRR(schdData.getiRRDetails(), finID, finReference, tableType);

			// Plan EMI Holiday Details Deletion, if exists on Old image
			// =======================================
			FinanceMain befFinMain = fm.getBefImage();
			if (befFinMain != null && befFinMain.isPlanEMIHAlw()) {
				if (FinanceConstants.PLANEMIHMETHOD_FRQ.equals(befFinMain.getPlanEMIHMethod())) {
					finPlanEmiHolidayDAO.deletePlanEMIHMonths(finID, table);
				} else if (FinanceConstants.PLANEMIHMETHOD_ADHOC.equals(befFinMain.getPlanEMIHMethod())) {
					finPlanEmiHolidayDAO.deletePlanEMIHDates(finID, table);
				}
			}

		} else {
			listSave(schdData, table, isWIF, 0, serviceUID);
			saveFeeChargeList(schdData, moduleDefiner, isWIF, table);

			// Finance IRR Values
			// =======================================
			deleteFinIRR(finID, tableType);
			saveFinIRR(schdData.getiRRDetails(), finID, finReference, tableType);
		}

		// Plan EMI Holiday Details
		// =======================================
		boolean planEMIHAlw = fm.isPlanEMIHAlw();
		String planEMIHMethod = fm.getPlanEMIHMethod();
		if (planEMIHAlw) {
			List<FinPlanEmiHoliday> holidayList = new ArrayList<>();
			int planEMIHMonth = 0;
			Date planEMIHDate = null;

			for (int i = 0; i < schdData.getPlanEMIHmonths().size(); i++) {
				if (FinanceConstants.PLANEMIHMETHOD_FRQ.equals(planEMIHMethod)) {
					planEMIHMonth = schdData.getPlanEMIHmonths().get(i);
				} else if (FinanceConstants.PLANEMIHMETHOD_ADHOC.equals(planEMIHMethod)) {
					planEMIHDate = schdData.getPlanEMIHDates().get(i);
				}

				FinPlanEmiHoliday emiHoliday = new FinPlanEmiHoliday();
				emiHoliday.setFinID(finID);
				emiHoliday.setFinReference(finReference);
				emiHoliday.setPlanEMIHMonth(planEMIHMonth);
				emiHoliday.setPlanEMIHDate(planEMIHDate);
				holidayList.add(emiHoliday);
			}

			for (int i = 0; i < schdData.getPlanEMIHDates().size(); i++) {
				if (FinanceConstants.PLANEMIHMETHOD_FRQ.equals(planEMIHMethod)) {
					planEMIHMonth = schdData.getPlanEMIHmonths().get(i);
				} else if (FinanceConstants.PLANEMIHMETHOD_ADHOC.equals(planEMIHMethod)) {
					planEMIHDate = schdData.getPlanEMIHDates().get(i);
				}

				FinPlanEmiHoliday emiHoliday = new FinPlanEmiHoliday();
				emiHoliday.setFinID(finID);
				emiHoliday.setFinReference(finReference);
				emiHoliday.setPlanEMIHMonth(planEMIHMonth);
				emiHoliday.setPlanEMIHDate(planEMIHDate);
				holidayList.add(emiHoliday);
			}

			if (!holidayList.isEmpty()) {
				if (FinanceConstants.PLANEMIHMETHOD_FRQ.equals(planEMIHMethod)) {
					finPlanEmiHolidayDAO.savePlanEMIHMonths(holidayList, table);
				} else if (FinanceConstants.PLANEMIHMETHOD_ADHOC.equals(planEMIHMethod)) {
					finPlanEmiHolidayDAO.savePlanEMIHDates(holidayList, table);
				}

			}
		}

		// Save Finance Step Policy Details
		// =======================================
		if (fm.isStepFinance() && (isWIF || PennantConstants.RECORD_TYPE_NEW.equals(fm.getRecordType())
				&& FinServiceEvent.ORG.equals(moduleDefiner))) {
			financeStepDetailDAO.deleteList(finID, isWIF, tableType.getSuffix());
			saveStepDetailList(schdData, isWIF, tableType.getSuffix());
			if (!isWIF) {
				auditDetails.addAll(fd.getAuditDetailMap().get("FinanceStepPolicyDetail"));
			}
		} else if (!fm.isStepFinance() && (isWIF || PennantConstants.RECORD_TYPE_NEW.equals(fm.getRecordType())
				&& FinServiceEvent.ORG.equals(moduleDefiner))) {
			financeStepDetailDAO.deleteList(finID, isWIF, tableType.getSuffix());
		} else if (fm.isStepFinance()
				&& (FinServiceEvent.CHGGRCEND.equals(moduleDefiner) || FinServiceEvent.RESCHD.equals(moduleDefiner)
						|| FinServiceEvent.ADDDISB.equals(moduleDefiner))
				|| FinServiceEvent.RESTRUCTURE.equals(moduleDefiner)
						&& CollectionUtils.isNotEmpty(schdData.getStepPolicyDetails())) {
			financeStepDetailDAO.deleteList(finID, isWIF, tableType.getSuffix());
			saveStepDetailList(schdData, isWIF, tableType.getSuffix());
			if (!isWIF) {
				auditDetails.addAll(fd.getAuditDetailMap().get("FinanceStepPolicyDetail"));
			}
		}

		if (!isWIF) {

			// Finance Eligibility Rule Details
			// =======================================
			auditDetails.addAll(eligibilityDetailService.saveOrUpdate(fd));

			// Finance Scoring Module Details List Saving
			// =======================================
			auditDetails.addAll(scoringDetailService.saveOrUpdate(fd));
		}

		// Save asset details
		// =======================================
		if (!isWIF) {
			// =======================================
			List<DocumentDetails> documentDetails = fd.getDocumentDetailsList();
			if (CollectionUtils.isNotEmpty(documentDetails)) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
				details = processingDocumentDetailsList(details, table, fm, moduleDefiner, serviceUID);
				auditDetails.addAll(details);
			}

			// set Finance Check List audit details to auditDetails
			// =======================================
			if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
				auditDetails.addAll(checkListDetailService.saveOrUpdate(fd, table, serviceUID));
			}

			// set Guaranteer Details Audit
			// =======================================
			if (fd.getGurantorsDetailList() != null && !fd.getGurantorsDetailList().isEmpty()) {
				// 10-Jul-2018 BUG FIX related to TktNo:127415
				List<AuditDetail> details = fd.getAuditDetailMap().get("Guarantors");
				auditDetails.addAll(guarantorDetailService.processingGuarantorsList(details, table));
			}

			// set JointAccount Details Audit
			// =======================================
			if (fd.getJointAccountDetailList() != null && !fd.getJointAccountDetailList().isEmpty()) {
				// 10-Jul-2018 BUG FIX related to TktNo:127415
				List<AuditDetail> details = fd.getAuditDetailMap().get("JointAccountDetails");
				auditDetails
						.addAll(jointAccountDetailService.processingJointAccountDetail(details, table, auditTranType));
			}

			if (CollectionUtils.isNotEmpty(fd.getTanAssignments())) {
				fd.getTanAssignments().forEach(ta -> ta.setCustID(fm.getCustID()));
				List<AuditDetail> details = fd.getAuditDetailMap().get("TanAssignments");
				auditDetails.addAll(tanAssignmentService.processingTanAssignemts(details, tableType));
			}

			// set Finance Collateral Details Audit
			// =======================================
			if (fd.getFinanceCollaterals() != null && !fd.getFinanceCollaterals().isEmpty()) {
				auditDetails
						.addAll(finCollateralService.saveOrUpdate(fd.getFinanceCollaterals(), table, auditTranType));
			}

			// Deviation details
			List<FinanceDeviations> deviations = new ArrayList<FinanceDeviations>();
			if (fd.getFinanceDeviations() != null) {
				deviations.addAll(fd.getFinanceDeviations());
			}

			if (fd.getManualDeviations() != null) {
				deviations.addAll(fd.getManualDeviations());
			}

			if (ImplementationConstants.ALLOW_DEVIATIONS) {
				deviationDetailsService.processDevaitions(finID, deviations, auditHeader);
				deviationDetailsService.processApprovedDevaitions(finID, fd.getApprovedFinanceDeviations(),
						auditHeader);
			}

			// Dedup Details
			// =======================================
			auditDetails.addAll(saveDedupDetails(fd));

			// Additional Field Details Save / Update
			// =======================================
			doSaveAddlFieldDetails(fd, table);

			// Advance Payment Details
			// =======================================
			// Payment Order Issue Details
			// =======================================
			// Quick disbursement
			auditDetails.addAll(processAdvancePayments(fd, auditHeader, table, auditTranType));

			// Covenant Type Details
			// =======================================
			if (fd.getCovenantTypeList() != null && !fd.getCovenantTypeList().isEmpty()) {
				for (FinCovenantType finCovenantType : fd.getCovenantTypeList()) {
					finCovenantType.setFinReference(finReference);
					finCovenantType.setTaskId(fm.getTaskId());
					finCovenantType.setNextTaskId(fm.getNextTaskId());
					finCovenantType.setRoleCode(fm.getRoleCode());
					finCovenantType.setNextRoleCode(fm.getNextRoleCode());
					finCovenantType.setRecordStatus(fm.getRecordStatus());
					finCovenantType.setWorkflowId(fm.getWorkflowId());
					finCovenantType.setLastMntOn(fm.getLastMntOn());
				}

				List<AuditDetail> covenantAuditDetails = null;
				covenantAuditDetails = finCovenantTypeService.saveOrUpdate(fd.getCovenantTypeList(), table,
						auditTranType);

				auditDetails.addAll(covenantAuditDetails);
			}

			List<Covenant> covenants = fd.getCovenants();
			if (CollectionUtils.isNotEmpty(covenants)) {
				int docSize = 0;
				for (Covenant covenant : fd.getCovenants()) {
					covenant.setKeyReference(finReference);
					covenant.setTaskId(fm.getTaskId());
					covenant.setNextTaskId(fm.getNextTaskId());
					covenant.setRoleCode(fm.getRoleCode());
					covenant.setNextRoleCode(fm.getNextRoleCode());
					covenant.setRecordStatus(fm.getRecordStatus());
					covenant.setWorkflowId(fm.getWorkflowId());
					covenant.setLastMntOn(fm.getLastMntOn());
				}
				if (CollectionUtils.isNotEmpty(fd.getDocumentDetailsList())) {
					docSize = fd.getDocumentDetailsList().size();
				}
				auditDetails.addAll(covenantsService.doProcess(covenants, tableType, auditTranType, false, docSize));
			}

			// set Finance Collateral Details Audit
			// =======================================
			if (fd.getCollateralAssignmentList() != null && !fd.getCollateralAssignmentList().isEmpty()) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("CollateralAssignments");
				details = processingCollateralAssignmentList(details, table, fm);
				auditDetails.addAll(details);
			}
			// Save Collateral setup Details
			List<CollateralSetup> collateralSetupList = null;
			collateralSetupList = fd.getCollaterals();

			// When collateral created from outside menu then documents need to synch DMS added
			if (CollectionUtils.isEmpty(collateralSetupList)) {
				collateralSetupList = fd.getDmsCollateralDocuments();
			}
			if (CollectionUtils.isNotEmpty(collateralSetupList)) {
				List<AuditDetail> details = collateralSetupService.processCollateralSetupList(aAuditHeader,
						"saveOrUpdate");
				auditDetails.addAll(details);
			}

			// Save Legal details
			List<LegalDetail> legalDetails = fd.getLegalDetailsList();
			if (CollectionUtils.isNotEmpty(legalDetails)) {
				List<AuditDetail> details = legalDetailService.processLegalDetails(aAuditHeader, "saveOrUpdate");
				auditDetails.addAll(details);
			}

			// FinAssetTypes Audit
			if (fd.getFinAssetTypesList() != null && !fd.getFinAssetTypesList().isEmpty()) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("FinAssetTypes");
				details = processingFinAssetTypesList(details, table, fm);
				auditDetails.addAll(details);
			}

			// Vas Recording Details
			if (schdData.getVasRecordingList() != null && !schdData.getVasRecordingList().isEmpty()) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("VasRecordings");
				details = processingVasRecordngList(fd, details, table);
				auditDetails.addAll(details);
			}

			// Vas Recording Extended Field Details
			if (schdData.getVasRecordingList() != null && !schdData.getVasRecordingList().isEmpty()) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("VasExtendedDetails");

				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
						VASConsatnts.MODULE_NAME, null, table, serviceUID);
				auditDetails.addAll(details);
			}

			// LowerTax Deductions
			// ===============
			if (CollectionUtils.isNotEmpty(fd.getFinScheduleData().getLowerTaxDeductionDetails())
					&& FinServiceEvent.ORG.equals(moduleDefiner)) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("LowerTaxDeductionDetails");
				details = processLowerTaxDeductionDetails(details, tableType, fd);
				auditDetails.addAll(details);
			}
			// AssetType Extended field Details
			if (fd.getExtendedFieldRenderList() != null && fd.getExtendedFieldRenderList().size() > 0) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldDetails");

				for (int i = 0; i < details.size(); i++) {
					ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) details.get(i).getModelData();
					extendedFieldRender.setReference(finReference);
				}
				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
						AssetConstants.EXTENDEDFIELDS_MODULE, null, table, serviceUID);
				auditDetails.addAll(details);
			}

			// Extended field Details
			String event = null;
			if (fd.getExtendedFieldHeader() != null) {
				event = fd.getExtendedFieldHeader().getEvent();
			}
			if (fd.getExtendedFieldRender() != null) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("LoanExtendedFieldDetails");
				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
						ExtendedFieldConstants.MODULE_LOAN, event, table, serviceUID);
				if (details != null) {
					auditDetails.addAll(details);
				}
			}

			// Flag Details
			// =======================================
			if (CollectionUtils.isNotEmpty(fd.getFinFlagsDetails())) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("FinFlagsHeader");
				details = processingFinFlagHeader(details, table);
				auditDetails.addAll(details);

				details = fd.getAuditDetailMap().get("FinFlagsDetail");
				details = processingFinFlagDetailList(details, table);
				auditDetails.addAll(details);
			}

			// psl Details
			// =======================================
			if (fd.getPslDetail() != null) {
				fd.getPslDetail().setRecordStatus(fm.getRecordStatus());
				fd.getPslDetail().setRoleCode(fm.getRoleCode());
				fd.getPslDetail().setNextRoleCode(fm.getNextRoleCode());
				fd.getPslDetail().setTaskId((fm.getTaskId()));
				fd.getPslDetail().setNextTaskId((fm.getNextTaskId()));
				fd.getPslDetail().setWorkflowId((fm.getWorkflowId()));
				auditDetails.add(pSLDetailService.saveOrUpdate(fd.getPslDetail(), tableType, auditTranType));
			}

			// Verifications
			saveOrUpdateVerifications(auditDetails, fd, fm, auditTranType);

			// calling post hoot
			if (postExteranalServiceHook != null) {
				postExteranalServiceHook.doProcess(aAuditHeader, "saveOrUpdate");
			}

			/**
			 * save sampling details
			 */

			if (fm.isSamplingRequired() && !fd.isActionSave()
					&& !samplingService.isExist(schdData.getFinReference(), "_view")) {
				Sampling sampling = new Sampling();
				sampling.setKeyReference(fm.getFinReference());
				sampling.setLastMntBy(fm.getLastMntBy());
				sampling.setCreatedBy(fm.getLastMntBy());
				samplingService.save(sampling);
			}

			/**
			 * save Legal details
			 */
			if (fm.isLegalRequired() && (!fd.isActionSave() || auditHeader.getApiHeader() != null)
					&& CollectionUtils.isNotEmpty(fd.getCollateralAssignmentList())) {
				legalDetailService.saveLegalDetails(fd, auditHeader.getApiHeader());
			}

		}

		// Finance Fee Details
		// =======================================
		List<FinFeeDetail> finFeeDetails = schdData.getFinFeeDetailList();
		if (CollectionUtils.isNotEmpty(finFeeDetails)) {
			for (FinFeeDetail fee : finFeeDetails) {
				fee.setInstructionUID(serviceUID);
			}
			auditDetails.addAll(finFeeDetailService.saveOrUpdate(finFeeDetails, table, auditTranType, isWIF));
		}

		// Finance Fee Receipts
		// =======================================
		/*
		 * //TODO:GANESH NEED TO REMOVE if (financeDetail.getFinScheduleData().getFinFeeReceipts() != null &&
		 * !financeDetail.getFinScheduleData().getFinFeeReceipts().isEmpty()) { for (FinFeeReceipt finFeeReceipt :
		 * financeDetail.getFinScheduleData().getFinFeeReceipts()) { for (FinFeeDetail finFeeDetail :
		 * financeDetail.getFinScheduleData().getFinFeeDetailActualList()) {
		 * 
		 * if (finFeeReceipt.getFeeTypeId() != 0) { if (finFeeReceipt.getFeeTypeId() == finFeeDetail.getFeeTypeID()) {
		 * finFeeReceipt.setFeeID(finFeeDetail.getFeeID()); break; }
		 * 
		 * } else { if (StringUtils.equals(finFeeReceipt.getFeeTypeCode(), finFeeDetail.getFeeTypeCode())) {
		 * finFeeReceipt.setFeeID(finFeeDetail.getFeeID()); break; } } } }
		 * 
		 * 
		 * auditDetails.addAll(finFeeDetailService.saveOrUpdateFinFeeReceipts(
		 * financeDetail.getFinScheduleData().getFinFeeReceipts(), tableType.getSuffix(),
		 * auditHeader.getAuditTranType()));
		 * 
		 * }
		 */

		// Put-Call
		List<FinOption> finOptions = fd.getFinOptions();
		if (CollectionUtils.isNotEmpty(finOptions)) {
			for (FinOption finOption : fd.getFinOptions()) {
				finOption.setFinID(finID);
				finOption.setFinReference(finReference);
				finOption.setTaskId(fm.getTaskId());
				finOption.setNextTaskId(fm.getNextTaskId());
				finOption.setRoleCode(fm.getRoleCode());
				finOption.setNextRoleCode(fm.getNextRoleCode());
				finOption.setRecordStatus(fm.getRecordStatus());
				finOption.setWorkflowId(fm.getWorkflowId());
				finOption.setLastMntOn(fm.getLastMntOn());
			}
			auditDetails.addAll(finOptionService.doProcess(finOptions, tableType, auditTranType, false));
		}

		// SubventionDetails
		if (fd.getFinScheduleData().getSubventionDetail() != null
				&& (StringUtils.isBlank(moduleDefiner) || FinServiceEvent.ORG.equals(moduleDefiner))) {
			if (subventionService != null) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("SubventionDetails");
				details = subventionService.processSubventionDetails(details, tableType, fd);
				auditDetails.addAll(details);
			}
		}

		if (!isWIF) {
			processLimitSaveOrUpdate(aAuditHeader, false);
		}

		sendMailNotification(fd);

		// Saving the reasons
		saveReasonDetails(fd);

		if (fd.getCreditReviewData() != null && creditFinancialService != null) {
			creditFinancialService.saveOrUpdate(fd, auditHeader, table);
		}

		// Restructure Details
		if (fd.getFinScheduleData().getRestructureDetail() != null) {
			auditDetails.addAll(
					restructureService.saveOrUpdateRestructureDetail(fd, table, auditHeader.getAuditTranType()));
		}

		// LinkedFinances
		List<LinkedFinances> list = fd.getLinkedFinancesList();
		if (CollectionUtils.isNotEmpty(list)) {
			auditDetails.addAll(linkedFinancesService.saveOrUpdateLinkedFinanceList(fd, tableType.getSuffix()));
		}

		// ISRA Details
		if (fd.getIsraDetail() != null) {
			auditDetails
					.addAll(israDetailService.saveOrUpdate(fd, tableType.getSuffix(), auditHeader.getAuditTranType()));
		}

		if (!isWIF) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
			auditHeader.setAuditDetail(new AuditDetail(auditTranType, 1, fields[0], fields[1], fm.getBefImage(), fm));

			auditHeader.setAuditDetails(auditDetails);
			auditHeaderDAO.addAudit(auditHeader);
		}

		// Reset Finance Detail Object for Service Task Verifications
		// =======================================
		auditHeader.getAuditDetail().setModelData(fd);

		// Manual Schedule details
		this.manualScheduleService.saveOrUpdate(fd, fd.getModuleDefiner(), tableType);

		// Variable OD Schedule details
		this.variableOverdraftSchdService.saveOrUpdate(fd, fd.getModuleDefiner(), tableType);

		// Push Notification API
		if (pushNotificationsService != null && SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_PUSH_NOTIFICATION)) {
			pushNotificationsService.sendPushNotification(auditHeader);
		}

		// FinancialSummary Risks and Mitigants Details
		// =======================================
		if (fd.getRisksAndMitigantsList() != null && risksAndMitigantsService != null) {
			for (RisksAndMitigants risksAndMitigants : fd.getRisksAndMitigantsList()) {
				risksAndMitigants.setFinReference(finReference);
				risksAndMitigants.setTaskId(fm.getTaskId());
				risksAndMitigants.setNextTaskId(fm.getNextTaskId());
				risksAndMitigants.setRoleCode(fm.getRoleCode());
				risksAndMitigants.setNextRoleCode(fm.getNextRoleCode());
				risksAndMitigants.setRecordStatus(fm.getRecordStatus());
				risksAndMitigants.setWorkflowId(fm.getWorkflowId());
				risksAndMitigants.setLastMntOn(fm.getLastMntOn());
			}
			auditDetails.addAll(
					risksAndMitigantsService.doProcess(fd.getRisksAndMitigantsList(), tableType, auditTranType, false));
		}
		// FinancialSummary Sanction Condition Details
		// =======================================
		if (fd.getSanctionDetailsList() != null && sanctionConditionsService != null) {
			for (SanctionConditions sc : fd.getSanctionDetailsList()) {
				sc.setFinID(fm.getFinID());
				sc.setFinReference(finReference);
				sc.setTaskId(fm.getTaskId());
				sc.setNextTaskId(fm.getNextTaskId());
				sc.setRoleCode(fm.getRoleCode());
				sc.setNextRoleCode(fm.getNextRoleCode());
				sc.setRecordStatus(fm.getRecordStatus());
				sc.setWorkflowId(fm.getWorkflowId());
				sc.setLastMntOn(fm.getLastMntOn());
			}
			auditDetails.addAll(
					sanctionConditionsService.doProcess(fd.getSanctionDetailsList(), tableType, auditTranType, false));
		}
		// FinancialSummary Deal Recommendation Merits Details
		// =======================================
		if (fd.getDealRecommendationMeritsDetailsList() != null && dealRecommendationMeritsService != null) {
			for (DealRecommendationMerits dealRecommendationMerits : fd.getDealRecommendationMeritsDetailsList()) {
				dealRecommendationMerits.setFinReference(finReference);
				dealRecommendationMerits.setTaskId(fm.getTaskId());
				dealRecommendationMerits.setNextTaskId(fm.getNextTaskId());
				dealRecommendationMerits.setRoleCode(fm.getRoleCode());
				dealRecommendationMerits.setNextRoleCode(fm.getNextRoleCode());
				dealRecommendationMerits.setRecordStatus(fm.getRecordStatus());
				dealRecommendationMerits.setWorkflowId(fm.getWorkflowId());
				dealRecommendationMerits.setLastMntOn(fm.getLastMntOn());
			}
			auditDetails.addAll(dealRecommendationMeritsService.doProcess(fd.getDealRecommendationMeritsDetailsList(),
					tableType, auditTranType, false));
		}
		// FinancialSummary DueDiligence Details
		// =======================================
		if (fd.getDueDiligenceDetailsList() != null && dueDiligenceDetailsService != null) {
			for (DueDiligenceDetails dueDiligenceDetails : fd.getDueDiligenceDetailsList()) {
				dueDiligenceDetails.setFinReference(finReference);
				dueDiligenceDetails.setTaskId(fm.getTaskId());
				dueDiligenceDetails.setNextTaskId(fm.getNextTaskId());
				dueDiligenceDetails.setRoleCode(fm.getRoleCode());
				dueDiligenceDetails.setNextRoleCode(fm.getNextRoleCode());
				dueDiligenceDetails.setRecordStatus(fm.getRecordStatus());
				dueDiligenceDetails.setWorkflowId(fm.getWorkflowId());
				dueDiligenceDetails.setLastMntOn(fm.getLastMntOn());
			}
			auditDetails.addAll(dueDiligenceDetailsService.doProcess(fd.getDueDiligenceDetailsList(), tableType,
					auditTranType, false));
		}
		// FinancialSummary DueDiligence Details
		// =======================================
		if (fd.getRecommendationNoteList() != null && recommendationNotesDetailsService != null) {
			for (RecommendationNotes recommendationNotesDetails : fd.getRecommendationNoteList()) {
				recommendationNotesDetails.setFinReference(finReference);
				recommendationNotesDetails.setTaskId(fm.getTaskId());
				recommendationNotesDetails.setNextTaskId(fm.getNextTaskId());
				recommendationNotesDetails.setRoleCode(fm.getRoleCode());
				recommendationNotesDetails.setNextRoleCode(fm.getNextRoleCode());
				recommendationNotesDetails.setRecordStatus(fm.getRecordStatus());
				recommendationNotesDetails.setWorkflowId(fm.getWorkflowId());
				recommendationNotesDetails.setLastMntOn(fm.getLastMntOn());
			}
			auditDetails.addAll(recommendationNotesDetailsService.doProcess(fd.getRecommendationNoteList(), tableType,
					auditTranType, false));
		}

		// Synoposis Details
		// =======================================
		SynopsisDetails synopsisDetails = fd.getSynopsisDetails();
		if (synopsisDetails != null && synopsisDetailsService != null) {
			synopsisDetails.setRecordStatus(fm.getRecordStatus());
			synopsisDetails.setRoleCode(fm.getRoleCode());
			synopsisDetails.setNextRoleCode(fm.getNextRoleCode());
			synopsisDetails.setTaskId((fm.getTaskId()));
			synopsisDetails.setNextTaskId((fm.getNextTaskId()));
			synopsisDetails.setWorkflowId((fm.getWorkflowId()));
			synopsisDetails.setFinID((fm.getFinID()));
			synopsisDetails.setFinReference((fm.getFinReference()));
			auditDetails.add(synopsisDetailsService.saveOrUpdate(synopsisDetails, tableType, auditTranType));
		}
		// Fin OCR Details
		// =========================
		if (fd.getFinOCRHeader() != null && finOCRHeaderService != null) {
			auditDetails.addAll(finOCRHeaderService.processFinOCRHeader(aAuditHeader, "saveOrUpdate"));
		}
		// PMAY
		// =========================
		if (fd.getPmay() != null && pmayService != null) {
			auditDetails.add(pmayService.saveOrUpdate(fd.getPmay(), tableType, auditTranType));
		}
		auditHeader.setAuditDetails(auditDetails);

		List<Long> splittedLoans = financeMainDAO.getParentRefifAny(fm.getFinReference(), "_view", false);
		if (CollectionUtils.isEmpty(splittedLoans)) {
			processPricingLoans(fd, table, auditTranType);
		}
		logger.debug(Literal.LEAVING);

		return auditHeader;

	}

	private List<AuditDetail> processAdvancePayments(FinanceDetail fd, AuditHeader auditHeader, String table,
			String trantype) {
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		if (fm.isQuickDisb()) {
			boolean apiProcess = isAPIProcess(fm.getFinSourceID(), auditHeader);
			if (apiProcess) {
				return finAdvancePaymentsService.processAPIQuickDisbursment(fd, table, trantype);
			} else {
				return finAdvancePaymentsService.processQuickDisbursment(fd, table, trantype);
			}
		} else {
			List<FinAdvancePayments> payments = fd.getAdvancePaymentsList();
			return finAdvancePaymentsService.saveOrUpdate(payments, table, trantype, fd.isDisbStp());
		}
	}

	private boolean isAPIProcess(String finSourceID, AuditHeader apiHeader) {
		return PennantConstants.FINSOURCE_ID_API.equals(finSourceID) && apiHeader.getApiHeader() != null;
	}

	private List<AuditDetail> processLowerTaxDeductionDetails(List<AuditDetail> auditDetails, TableType tableType,
			FinanceDetail financeDetail) {
		logger.debug("Entering");
		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		String type = tableType.getSuffix();

		for (int i = 0; i < auditDetails.size(); i++) {
			LowerTaxDeduction lowerTaxDeduction = (LowerTaxDeduction) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				lowerTaxDeduction.setRoleCode("");
				lowerTaxDeduction.setNextRoleCode("");
				lowerTaxDeduction.setTaskId("");
				lowerTaxDeduction.setNextTaskId("");
				lowerTaxDeduction.setWorkflowId(0);
				lowerTaxDeduction.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			}

			if (lowerTaxDeduction.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (lowerTaxDeduction.isNewRecord()) {
				saveRecord = true;
				if (lowerTaxDeduction.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					lowerTaxDeduction.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (lowerTaxDeduction.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					lowerTaxDeduction.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (lowerTaxDeduction.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					lowerTaxDeduction.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (lowerTaxDeduction.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (lowerTaxDeduction.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (lowerTaxDeduction.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (lowerTaxDeduction.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = lowerTaxDeduction.getRecordType();
				recordStatus = lowerTaxDeduction.getRecordStatus();
				lowerTaxDeduction.setRecordType("");
				lowerTaxDeduction.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				lowerTaxDeductionDAO.save(lowerTaxDeduction, type);
			}

			if (updateRecord) {
				lowerTaxDeductionDAO.update(lowerTaxDeduction, type);
			}

			if (deleteRecord) {
				lowerTaxDeductionDAO.delete(lowerTaxDeduction, type);
			}

			if (approveRec) {
				lowerTaxDeduction.setRecordType(rcdType);
				lowerTaxDeduction.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(lowerTaxDeduction);
		}

		logger.debug("Leaving");
		return auditDetails;

	}

	protected void sendMailNotification(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		Notification notification = new Notification();
		notification.setKeyReference(financeMain.getFinReference());
		notification.setModule("LOAN");
		notification.setSubModule(FinServiceEvent.ORG);
		notification.setTemplateCode(NotificationConstants.CREATE_LOAN_API_MAIL_NOTIFICATION);

		CustomerDetails customerDetails = financeDetail.getCustomerDetails();
		if (customerDetails == null) {
			return;
		}

		if (customerDetails.getCustomer() == null) {
			return;
		}
		// For Customers marked as DND true are not allow to Trigger a Mail.
		if (customerDetails.getCustomer().isDnd()) {
			return;
		}

		// Customer E-mails
		List<CustomerEMail> emailList = customerDetails.getCustomerEMailList();
		if (CollectionUtils.isEmpty(emailList)) {
			return;
		}

		String emailId = null;
		for (CustomerEMail email : emailList) {
			if (Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH) == email.getCustEMailPriority()) {
				emailId = email.getCustEMail();
				break;
			}
		}

		if (StringUtils.isEmpty(emailId)) {
			return;
		}

		List<String> emails = new ArrayList<>();
		emails.add(emailId);
		notification.setEmails(emails);

		// Customer Contact Number
		String mobileNumber = null;
		List<CustomerPhoneNumber> customerPhoneNumbers = customerDetails.getCustomerPhoneNumList();
		for (CustomerPhoneNumber customerPhoneNumber : customerPhoneNumbers) {
			if (Integer.valueOf(PennantConstants.KYC_PRIORITY_VERY_HIGH) == customerPhoneNumber
					.getPhoneTypePriority()) {
				mobileNumber = customerPhoneNumber.getPhoneNumber();
				break;
			}
		}

		List<String> mobileNumberList = new ArrayList<>();
		mobileNumberList.add(mobileNumber);
		notification.setMobileNumbers(mobileNumberList);

		if (notificationService != null) {
			notificationService.sendNotification(notification, financeDetail);
		}

		logger.debug(Literal.LEAVING);
	}

	private void saveOrUpdateVerifications(List<AuditDetail> auditDetails, FinanceDetail fd, FinanceMain fm,
			String auditTranType) {
		// FI Verification details
		if ((!ImplementationConstants.VER_INIT_FROM_OUTSIDE && fd.isFiInitTab()) || fd.isFiApprovalTab()) {
			setVerificationWorkflowDetails(fd.getFiVerification(), fm);
		}

		// Technical Verification details
		if ((!ImplementationConstants.VER_INIT_FROM_OUTSIDE && fd.isTvInitTab()) || fd.isTvApprovalTab()) {
			setVerificationWorkflowDetails(fd.getTvVerification(), fm);
		}

		// Legal Verification details
		if ((!ImplementationConstants.VER_INIT_FROM_OUTSIDE && fd.isLvInitTab()) || fd.isLvApprovalTab()) {
			setVerificationWorkflowDetails(fd.getLvVerification(), fm);
		}

		// Legal Vetting details
		if ((!ImplementationConstants.VER_INIT_FROM_OUTSIDE && fd.isVettingInitTab()) || fd.isVettingApprovalTab()) {
			setVerificationWorkflowDetails(fd.getLegalVetting(), fm);
		}

		// RCU Verification details
		if ((!ImplementationConstants.VER_INIT_FROM_OUTSIDE && fd.isRcuInitTab()) || fd.isRcuApprovalTab()) {
			setVerificationWorkflowDetails(fd.getRcuVerification(), fm);
		}
		// PD Verification details
		if (fd.isPdInitTab() || fd.isPdApprovalTab()) {
			setVerificationWorkflowDetails(fd.getPdVerification(), fm);
		}

		List<AuditDetail> adtVerifications = new ArrayList<>();

		// save FI Initiation details
		// =======================================
		if (!ImplementationConstants.VER_INIT_FROM_OUTSIDE) {
			if (fd.isFiInitTab() && fd.getFiVerification() != null) {
				adtVerifications.addAll(verificationService.saveOrUpdate(fd, VerificationType.FI, auditTranType, true));
			}
		}

		// save FI Approval details
		// =======================================
		if (fd.isFiApprovalTab() && fd.getFiVerification() != null) {
			adtVerifications.addAll(verificationService.saveOrUpdate(fd, VerificationType.FI, auditTranType, false));
		}

		// save TV Initiation details
		// TO-DO
		// FIXME - To be uncommented while merging
		// =======================================
		if (!ImplementationConstants.VER_INIT_FROM_OUTSIDE) {
			if (fd.isTvInitTab() && fd.getTvVerification() != null) {
				adtVerifications.addAll(verificationService.saveOrUpdate(fd, VerificationType.TV, auditTranType, true));
			}
		}

		// save TV Approval details
		// =======================================
		if (fd.isTvApprovalTab() && fd.getTvVerification() != null) {
			adtVerifications.addAll(verificationService.saveOrUpdate(fd, VerificationType.TV, auditTranType, false));
		}

		// save LV Initiation details
		// =======================================
		if (!ImplementationConstants.VER_INIT_FROM_OUTSIDE) {
			if (fd.isLvInitTab() && fd.getLvVerification() != null) {
				Verification verification = fd.getLvVerification();
				verification.setVerificationType(VerificationType.LV.getKey());
				adtVerifications.addAll(verificationService.saveOrUpdate(fd, VerificationType.LV, auditTranType, true));
			}
		}

		// save LV Approval details
		// =======================================
		if (fd.isLvApprovalTab() && fd.getLvVerification() != null) {
			Verification verification = fd.getLvVerification();
			verification.setVerificationType(VerificationType.LV.getKey());
			adtVerifications.addAll(verificationService.saveOrUpdate(fd, VerificationType.LV, auditTranType, false));
		}

		// save Legal Vetting Initiation details
		// =======================================
		if (!ImplementationConstants.VER_INIT_FROM_OUTSIDE) {
			if (fd.isVettingInitTab() && fd.getLegalVetting() != null) {
				Verification verification = fd.getLegalVetting();
				verification.setVerificationType(VerificationType.VETTING.getKey());
				adtVerifications
						.addAll(verificationService.saveOrUpdate(fd, VerificationType.VETTING, auditTranType, true));
			}
		}

		// save Legal Vetting Approval details
		// =======================================
		if (fd.isVettingApprovalTab() && fd.getLegalVetting() != null) {
			Verification verification = fd.getLegalVetting();
			verification.setVerificationType(VerificationType.VETTING.getKey());
			adtVerifications
					.addAll(verificationService.saveOrUpdate(fd, VerificationType.VETTING, auditTranType, false));
		}

		// save RCU Initiation details
		// =======================================
		if (!ImplementationConstants.VER_INIT_FROM_OUTSIDE) {
			if (fd.isRcuInitTab() && fd.getRcuVerification() != null) {
				adtVerifications
						.addAll(verificationService.saveOrUpdate(fd, VerificationType.RCU, auditTranType, true));
			}
		}

		// save RCU Approval details
		// =======================================
		if (fd.isRcuApprovalTab() && fd.getRcuVerification() != null) {
			adtVerifications.addAll(verificationService.saveOrUpdate(fd, VerificationType.RCU, auditTranType, false));
		}

		// Update Sampling details
		// =======================================
		if (fd.isSamplingApprover() && fd.getSampling() != null) {
			adtVerifications.add(finSamplingService.saveOrUpdate(fd, auditTranType));
		}

		// save PD Initiation details
		// =======================================
		if (fd.isPdInitTab() && fd.getPdVerification() != null) {
			adtVerifications.addAll(verificationService.saveOrUpdate(fd, VerificationType.PD, auditTranType, true));
		}
		// save pd Approval details
		// =======================================
		if (fd.isPdApprovalTab() && fd.getPdVerification() != null) {
			adtVerifications.addAll(verificationService.saveOrUpdate(fd, VerificationType.PD, auditTranType, false));
		}

		// preparing audit seqno for same table(adtverifications)
		int i = 0;
		for (AuditDetail auditDetail : adtVerifications) {
			auditDetail.setAuditSeq(++i);
		}

		// Update verification stage tables document id's after saving to
		// database and reference of RCU documntes
		verificationService.updateReferenceIds(fd);

		auditDetails.addAll(adtVerifications);
	}

	private void setVerificationWorkflowDetails(Verification verification, FinanceMain financeMain) {
		if (verification != null) {
			verification.setLastMntBy(financeMain.getLastMntBy());
			verification.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			verification.setRecordType(financeMain.getRecordType());
			verification.setVersion(financeMain.getVersion());
			verification.setWorkflowId(financeMain.getWorkflowId());
			verification.setRoleCode(financeMain.getRoleCode());
			verification.setNextRoleCode(financeMain.getNextRoleCode());
			verification.setTaskId(financeMain.getTaskId());
			verification.setNextTaskId(financeMain.getNextTaskId());
			verification.setRecordStatus(financeMain.getRecordStatus());
			if (PennantConstants.RECORD_TYPE_DEL.equals(financeMain.getRecordType())) {
				if (StringUtils.trimToNull(verification.getRecordType()) == null) {
					verification.setRecordType(financeMain.getRecordType());
					verification.setNewRecord(true);
				}
			}
		}
	}

	private List<AuditDetail> processingFinFlagDetailList(List<AuditDetail> auditDetails, String type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FinFlagsDetail finFlagsDetail = (FinFlagsDetail) auditDetails.get(i).getModelData();

			if (StringUtils.isEmpty(finFlagsDetail.getRecordType())) {
				continue;
			}

			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				finFlagsDetail.setRoleCode("");
				finFlagsDetail.setNextRoleCode("");
				finFlagsDetail.setTaskId("");
				finFlagsDetail.setNextTaskId("");
				finFlagsDetail.setWorkflowId(0);
			}

			if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (finFlagsDetail.isNewRecord()) {
				saveRecord = true;
				if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (finFlagsDetail.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = finFlagsDetail.getRecordType();
				recordStatus = finFlagsDetail.getRecordStatus();
				finFlagsDetail.setRecordType("");
				finFlagsDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				finFlagDetailsDAO.save(finFlagsDetail, type);
			}

			if (updateRecord) {
				finFlagDetailsDAO.update(finFlagsDetail, type);
			}

			if (deleteRecord) {
				finFlagDetailsDAO.delete(finFlagsDetail.getReference(), finFlagsDetail.getFlagCode(),
						finFlagsDetail.getModuleName(), type);
			}

			if (approveRec) {
				finFlagsDetail.setRecordType(rcdType);
				finFlagsDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(finFlagsDetail);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;

	}

	/**
	 * Method For Preparing List of AuditDetails for Check List for Fin Flag Details
	 * 
	 * @param financeDetail
	 * 
	 * @param auditDetails
	 * @param financeDetail
	 * @param type
	 * @return
	 */
	private List<AuditDetail> processingVasRecordngList(FinanceDetail financeDetail, List<AuditDetail> auditDetails,
			String type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			VASRecording recording = (VASRecording) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				recording.setRoleCode("");
				recording.setNextRoleCode("");
				recording.setTaskId("");
				recording.setNextTaskId("");
				recording.setWorkflowId(0);
			}

			if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (recording.isNewRecord()) {
				saveRecord = true;
				if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					recording.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					recording.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					recording.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (recording.isNewRecord()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = recording.getRecordType();
				recordStatus = recording.getRecordStatus();
				recording.setRecordType("");
				recording.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
				if (StringUtils.isBlank(financeDetail.getModuleDefiner())
						|| FinServiceEvent.ORG.equals(financeDetail.getModuleDefiner())) {
					recording.setStatus(InsuranceConstants.PENDING);
				}
			}
			if (saveRecord) {
				vasRecordingDAO.save(recording, type);
			}

			if (updateRecord) {
				vasRecordingDAO.update(recording, type);
			}

			if (deleteRecord) {
				vasRecordingDAO.delete(recording, type);
			}

			if (approveRec) {
				recording.setRecordType(rcdType);
				recording.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(recording);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;

	}

	private List<AuditDetail> saveDedupDetails(FinanceDetail financeDetail) {

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		// Save Black List Customer Data
		// =======================================

		long lastmntby = financeMain.getLastMntBy();
		String roleCode = financeMain.getRoleCode();
		String recordSts = financeMain.getRecordStatus();

		if (financeDetail.getFinBlacklistCustomer() != null && !financeDetail.getFinBlacklistCustomer().isEmpty()) {

			List<FinBlacklistCustomer> insertList = new ArrayList<FinBlacklistCustomer>();
			List<FinBlacklistCustomer> updateList = new ArrayList<FinBlacklistCustomer>();

			FinBlacklistCustomer blCustomer = new FinBlacklistCustomer();
			String[] blFields = PennantJavaUtil.getFieldDetails(blCustomer, blCustomer.getExcludeFields());

			for (int i = 0; i < financeDetail.getFinBlacklistCustomer().size(); i++) {

				blCustomer = financeDetail.getFinBlacklistCustomer().get(i);
				blCustomer.setLastMntBy(lastmntby);
				blCustomer.setRoleCode(roleCode);
				blCustomer.setRecordStatus(recordSts);
				if (!blCustomer.isNewBlacklistRecord()) {
					updateList.add(blCustomer);
				} else {
					insertList.add(blCustomer);
				}

				auditDetails.add(
						new AuditDetail(PennantConstants.TRAN_WF, i + 1, blFields[0], blFields[1], null, blCustomer));
			}

			if (!insertList.isEmpty()) {
				blacklistCustomerDAO.saveList(insertList, "");
			}
			if (!updateList.isEmpty()) {
				blacklistCustomerDAO.updateList(updateList);
			}

			blFields = null;
			blCustomer = null;
			insertList = null;
			updateList = null;
		}

		// Save Finance DeDup List Data
		// =======================================
		if (financeDetail.getFinDedupDetails() != null && !financeDetail.getFinDedupDetails().isEmpty()) {

			List<FinanceDedup> insertList = new ArrayList<FinanceDedup>();
			List<FinanceDedup> updateList = new ArrayList<FinanceDedup>();

			FinanceDedup dedup = new FinanceDedup();
			String[] dedupFields = PennantJavaUtil.getFieldDetails(dedup, dedup.getExcludeFields());
			dedup = null;
			for (int i = 0; i < financeDetail.getFinDedupDetails().size(); i++) {

				dedup = financeDetail.getFinDedupDetails().get(i);
				dedup.setLastMntBy(lastmntby);
				dedup.setRoleCode(roleCode);
				dedup.setRecordStatus(recordSts);
				if (!dedup.isNewRecord()) {
					updateList.add(dedup);
				} else {
					insertList.add(dedup);
				}

				auditDetails.add(
						new AuditDetail(PennantConstants.TRAN_WF, i + 1, dedupFields[0], dedupFields[1], null, dedup));
			}
			if (!insertList.isEmpty()) {
				financeDedupeDAO.saveList(insertList, "");
			}
			if (!updateList.isEmpty()) {
				financeDedupeDAO.updateList(updateList);
			}

			insertList = null;
			updateList = null;
			dedupFields = null;
			dedup = null;
		}

		// Save Customer Dedup Data
		// =======================================

		if (financeDetail.getCustomerDedupList() != null && !financeDetail.getCustomerDedupList().isEmpty()) {

			List<CustomerDedup> insertList = new ArrayList<CustomerDedup>();
			List<CustomerDedup> updateList = new ArrayList<CustomerDedup>();

			CustomerDedup deDupCustomer = new CustomerDedup();
			String[] blFields = PennantJavaUtil.getFieldDetails(deDupCustomer, deDupCustomer.getExcludeFields());

			for (int i = 0; i < financeDetail.getCustomerDedupList().size(); i++) {

				deDupCustomer = financeDetail.getCustomerDedupList().get(i);
				deDupCustomer.setFinReference(financeDetail.getFinScheduleData().getFinReference());
				deDupCustomer.setLastMntBy(lastmntby);
				deDupCustomer.setRoleCode(roleCode);
				deDupCustomer.setRecordStatus(recordSts);
				if (!deDupCustomer.isNewCustDedupRecord()) {
					updateList.add(deDupCustomer);
				} else {
					insertList.add(deDupCustomer);
				}

				auditDetails.add(new AuditDetail(PennantConstants.TRAN_WF, i + 1, blFields[0], blFields[1], null,
						deDupCustomer));
			}

			if (!insertList.isEmpty()) {
				customerDedupDAO.saveList(insertList, "");
			}
			if (!updateList.isEmpty()) {
				customerDedupDAO.updateList(updateList);
			}

			deDupCustomer = null;
			insertList = null;
			updateList = null;
		}
		return auditDetails;
	}

	/**
	 * delete method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) delete Record for the DB table
	 * FinanceMain by using FinanceMainDAO's delete method with type as Blank 3) Audit the record in to AuditHeader and
	 * AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader, boolean isWIF) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		fd.setValidateUpfrontFees(false);
		auditHeader = businessValidation(auditHeader, "delete", isWIF, false);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		// Finance SubChild List And Reference Details List Deletion
		listDeletion(schdData, fd.getModuleDefiner(), "", isWIF);

		finServiceInstructionDAO.deleteList(finID, fd.getModuleDefiner(), "");

		if (!isWIF) {
			// Additional Field Details Deletion
			doDeleteAddlFieldDetails(fd, "");
		}

		String auditTranType = auditHeader.getAuditTranType();
		if (!isWIF) {
			auditDetails.addAll(jointGuarantorDeletion(fd, "", auditTranType));
			auditDetails.addAll(checkListDetailService.delete(fd, "", auditTranType));

			if (fd.getAdvancePaymentsList() != null) {
				auditDetails.addAll(finAdvancePaymentsService.delete(fd.getAdvancePaymentsList(), "", auditTranType));
			}

			List<FinCovenantType> finCovenantTypes = fd.getCovenantTypeList();
			if (CollectionUtils.isNotEmpty(finCovenantTypes)) {
				auditDetails.addAll(finCovenantTypeService.delete(finCovenantTypes, "", auditTranType));
			}

			List<Covenant> covenants = fd.getCovenants();
			if (CollectionUtils.isNotEmpty(covenants)) {
				auditDetails.addAll(covenantsService.delete(covenants, TableType.MAIN_TAB, auditTranType));
			}

			// Collateral assignment Details
			if (fd.getCollateralAssignmentList() != null && !fd.getCollateralAssignmentList().isEmpty()) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("CollateralAssignments");
				auditDetails.addAll(details);
				collateralAssignmentDAO.deleteByReference(finReference, "");
			}

			// Save Collateral setup Details
			List<CollateralSetup> collateralSetupList = fd.getCollaterals();
			if (collateralSetupList != null && !collateralSetupList.isEmpty()) {
				List<AuditDetail> details = collateralSetupService.processCollateralSetupList(auditHeader, "delete");
				auditDetails.addAll(details);
			}
			// Fin OCR Details
			// =========================
			if (fd.getFinOCRHeader() != null && finOCRHeaderService != null) {
				auditDetails.addAll(finOCRHeaderService.processFinOCRHeader(auditHeader, "delete"));
			}

			// Legal details
			List<LegalDetail> legalDetails = fd.getLegalDetailsList();
			if (CollectionUtils.isNotEmpty(legalDetails)) {
				List<AuditDetail> details = legalDetailService.processLegalDetails(auditHeader, "delete");
				auditDetails.addAll(details);
			}

			// FinAssetTypes details
			if (fd.getFinAssetTypesList() != null && !fd.getFinAssetTypesList().isEmpty()) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("FinAssetTypes");
				finAssetTypeDAO.deleteByReference(finReference, "");
				auditDetails.addAll(details);
			}

			legalDetailService.deleteList(finReference, TableType.TEMP_TAB);

			// Cheque details
			if (fd.getChequeHeader() != null) {
				String[] fields = PennantJavaUtil.getFieldDetails(new ChequeHeader());
				finChequeHeaderService.delete(auditHeader);
				auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1],
						fd.getChequeHeader().getBefImage(), fd.getChequeHeader()));
			}

			// AssetType Extended field Details
			if (fd.getExtendedFieldRenderList() != null && fd.getExtendedFieldRenderList().size() > 0) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldDetails");
				auditDetails.addAll(extendedFieldDetailsService.delete(details, AssetConstants.EXTENDEDFIELDS_MODULE,
						finReference, null, "_Temp"));
			}

			// Vas Recording Details details Prasad
			if (schdData.getVasRecordingList() != null && !schdData.getVasRecordingList().isEmpty()) {

				List<AuditDetail> details = fd.getAuditDetailMap().get("VasRecordings");
				vasRecordingDAO.deleteByPrimaryLinkRef(finReference, "_Temp");
				auditDetails.addAll(details);

				// Vas Recording Extended field Details
				List<AuditDetail> vasExtDetails = fd.getAuditDetailMap().get("VasExtendedDetails");
				if (CollectionUtils.isNotEmpty(vasExtDetails)) {
					for (AuditDetail auditDetail : vasExtDetails) {
						ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) auditDetail.getModelData();
						extendedFieldDetailsService.delete(vasExtDetails, VASConsatnts.MODULE_NAME,
								extendedFieldRender.getReference(), null, "_Temp");
					}
				}
			}
		}

		List<FinFeeDetail> fees = schdData.getFinFeeDetailList();
		if (fees != null) {
			auditDetails.addAll(finFeeDetailService.delete(fees, "", auditTranType, isWIF));
		}

		creditReviewDetailDAO.delete(finID, TableType.MAIN_TAB);

		// Finance Deletion
		financeMainDAO.delete(fm, TableType.MAIN_TAB, isWIF, true);

		// Step Details Deletion
		financeStepDetailDAO.deleteList(finID, isWIF, "");

		// Saving the reasons
		saveReasonDetails(fd);

		// Extended field Render Details.
		List<AuditDetail> extendedDetails = fd.getAuditDetailMap().get("ExtendedFieldDetails");
		if (extendedDetails != null && extendedDetails.size() > 0) {
			auditDetails.addAll(extendedFieldDetailsService.delete(fd.getExtendedFieldHeader(), fd.getFinReference(),
					"_Temp", auditHeader.getAuditTranType(), extendedDetails));
		}

		// Loan Extended field Render Details.
		List<AuditDetail> details = fd.getAuditDetailMap().get("LoanExtendedFieldDetails");
		if (details != null && details.size() > 0) {
			auditDetails.addAll(extendedFieldDetailsService.delete(fd.getExtendedFieldHeader(), fd.getFinReference(),
					fd.getExtendedFieldRender().getSeqNo(), "_Temp", auditHeader.getAuditTranType(), details));
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));
		auditHeader.setAuditDetails(auditDetails);

		if (!isWIF) {
			auditHeaderDAO.addAudit(auditHeader);
		}

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using financeMainDAO.delete with parameters
	 * financeMain,"" b) NEW Add new record in to main table by using financeMainDAO.save with parameters financeMain,""
	 * c) EDIT Update record in the main table by using financeMainDAO.update with parameters financeMain,"" 3) Delete
	 * the record from the workFlow table by using financeMainDAO.delete with parameters financeMain,"_Temp" 4) Audit
	 * the record in to AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5)
	 * Audit the record in to AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) based on the
	 * transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 * @throws InterfaceException
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader, boolean isWIF) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		String roleCode = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FinanceDetail fd = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
		fd.setValidateUpfrontFees(true);
		aAuditHeader = businessValidation(aAuditHeader, "doApprove", isWIF, false);

		if (!isWIF) {
			aAuditHeader = processLimitApprove(aAuditHeader, true);
		}
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		// process to send FIN-one request and create or update the cust data.
		String moduleDefiner = fd.getModuleDefiner();
		if (!isWIF && FinServiceEvent.ORG.equals(moduleDefiner)) {
			createOrUpdateCrmCustomer(fd);
		}

		if (!aAuditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return aAuditHeader;
		}

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		Date curBDay = SysParamUtil.getAppDate();
		// Review dates reset for backdated loans.
		if (!isWIF && FinServiceEvent.ORG.equals(moduleDefiner)) {
			resetNextFrqDates(fd, curBDay);
		}

		// gCDCustomerService.processGcdCustomer(financeDetail, "insert"); //
		// inserting gcdcustomer.
		// Execute Accounting Details Process
		// =======================================
		boolean isSanctionBasedSchd = fm.isSanBsdSchdle();

		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(fd);
		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : serviceInstructions) {
			serviceUID = finServInst.getInstructionUID();
			if (!FinServiceEvent.ORG.equalsIgnoreCase(finServInst.getFinEvent())) {
				finServInst.setApprovedDate(SysParamUtil.getAppDate());
			}
		}

		if (fm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			fm.setFinApprovedDate(curBDay);

			if (fm.getFinContractDate() == null) {
				fm.setFinContractDate(fm.getFinStartDate());
			}

			if (fm.getFeeChargeAmt() == null) {
				fm.setFeeChargeAmt(BigDecimal.ZERO);
			}

		}
		// To maintain record Maintained status for furthur process
		String recordMainStatus = StringUtils.trimToEmpty(fm.getRcdMaintainSts());

		boolean restructReceiptReq = false;

		List<FinAdvancePayments> payments = fd.getAdvancePaymentsList();
		if (!isWIF) {
			String recordType = fm.getRecordType();
			if (CollectionUtils.isNotEmpty(payments)
					&& !StringUtils.trimToEmpty(recordMainStatus).equals(FinServiceEvent.CANCELDISB)
					&& !PennantConstants.RECORD_TYPE_DEL.equals(recordType) && !fd.isExtSource()
					&& ((FinServiceEvent.ORG.equals(fd.getModuleDefiner())
							&& PennantConstants.RECORD_TYPE_NEW.equals(recordType))
							|| (FinServiceEvent.ADDDISB.equals(fd.getModuleDefiner())
									&& PennantConstants.RECORD_TYPE_UPD.equals(recordType)))) {
				fd.setAdvancePaymentsList(finAdvancePaymentsService.splitRequest(payments));
				auditHeader.getAuditDetail().setModelData(fd);
			}
			// Receipt Creation Required with RESTRUCT Accounting Set
			if (FinServiceEvent.RESTRUCTURE.equals(recordMainStatus)
					&& ImplementationConstants.RESTRUCTURE_DFT_APP_DATE) {
				restructReceiptReq = true;
			}

			if (!restructReceiptReq) {
				auditHeader = executeAccountingProcess(auditHeader, curBDay);
			}
		}

		// Validation Checking for All Finance Detail data
		// =======================================
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		// send ConfirmReservation Request to ACP Interface and save log details
		// ======================================================================
		if (!ImplementationConstants.LIMIT_INTERNAL) {
			if (!isWIF) {
				if (fm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
					getLimitCheckDetails().doProcessLimits(fm, FinanceConstants.CONFIRM);
				}

			}
		}

		// Validation Checking for All Finance Detail data
		// =======================================
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		// Re-Prepare of Finance Overdue Details with Existing Data
		// =======================================

		if (StringUtils.trimToEmpty(fm.getRcdMaintainSts()).equals(FinServiceEvent.CHGFRQ)) {

			List<FinanceScheduleDetail> schdList = schdData.getFinanceScheduleDetails();
			for (int i = 1; i < schdList.size(); i++) {

				FinanceScheduleDetail curSchd = schdList.get(i);
				if (!(curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))) {
					continue;
				}

				if (curSchd.isSchPftPaid() || curSchd.isSchPriPaid()) {
					continue;
				}

				if (curSchd.getSchDate().compareTo(curBDay) > 0) {
					continue;
				}

				FinRepayQueue finRepayQueue = new FinRepayQueue();

				finRepayQueue.setFinID(finID);
				finRepayQueue.setFinReference(finReference);
				finRepayQueue.setBranch(fm.getFinBranch());
				finRepayQueue.setFinType(fm.getFinType());
				finRepayQueue.setCustomerID(fm.getCustID());
				finRepayQueue.setRpyDate(curSchd.getSchDate());
				finRepayQueue.setFinPriority(9999);
				finRepayQueue.setFinRpyFor("S");
				finRepayQueue.setSchdPft(curSchd.getProfitSchd());
				finRepayQueue.setSchdPri(curSchd.getPrincipalSchd());
				finRepayQueue.setSchdPftPaid(curSchd.getSchdPftPaid());
				finRepayQueue.setSchdPriPaid(curSchd.getSchdPriPaid());
				finRepayQueue.setSchdPftBal(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
				finRepayQueue.setSchdPriBal(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
				finRepayQueue.setSchdIsPftPaid(false);
				finRepayQueue.setSchdIsPriPaid(false);

				try {
					recoveryPostingsUtil.overDueDetailPreparation(finRepayQueue, fm.getProfitDaysBasis(), curBDay,
							false, false);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}

			// Recalculate Status of Finance using Overdue
			String curFinStatus = customerStatusCodeDAO.getFinanceStatus(finReference, true);
			fm.setFinStatus(curFinStatus);

			frequencyDatesUpdation(schdData, fm, serviceInstructions);
			// GHF 166278 - Frequency and dates update in finance main - START
			List<FinanceScheduleDetail> modfsd = schdData.getFinanceScheduleDetails();

			for (FinanceScheduleDetail currSchd : modfsd) {
				Date schDate = currSchd.getSchDate();

				if (schDate.compareTo(fm.getGrcPeriodEndDate()) <= 0) {

					if (currSchd.isCpzOnSchDate()) {
						fm.setGrcCpzFrq(serviceInstructions.get(0).getRepayFrq());
						fm.setNextGrcCpzDate(schDate);
					}

					if (currSchd.isPftOnSchDate() || currSchd.isRepayOnSchDate()) {
						fm.setGrcPftFrq(serviceInstructions.get(0).getRepayFrq());
						fm.setNextGrcPftDate(schDate);
					}

					if (currSchd.isRvwOnSchDate() || currSchd.getSchDate().compareTo(fm.getGrcPeriodEndDate()) == 0) {
						fm.setGrcPftRvwFrq(serviceInstructions.get(0).getRepayFrq());
						fm.setNextGrcPftRvwDate(schDate);
					}

				}

				if (currSchd.isCpzOnSchDate()) {
					fm.setRepayCpzFrq(serviceInstructions.get(0).getRepayFrq());
					fm.setNextRepayCpzDate(schDate);
				}

				if (currSchd.isPftOnSchDate() || currSchd.isRepayOnSchDate()
						|| currSchd.getSchDate().compareTo(fm.getMaturityDate()) == 0) {
					fm.setRepayFrq(serviceInstructions.get(0).getRepayFrq());
					fm.setNextRepayDate(schDate);
				}

				if (currSchd.isPftOnSchDate() || currSchd.getSchDate().compareTo(fm.getMaturityDate()) == 0) {
					fm.setRepayPftFrq(serviceInstructions.get(0).getRepayFrq());
					fm.setNextRepayPftDate(schDate);
				}

				if (currSchd.isRvwOnSchDate()) {
					fm.setRepayRvwFrq(serviceInstructions.get(0).getRepayFrq());
					fm.setNextRepayRvwDate(schDate);
				}

				if (schDate.compareTo(SysParamUtil.getAppDate()) >= 0) {
					break;
				}
			}
			// GHF 166278 - Frequency and dates update in finance main - END

			// Suspense Process Check after Overdue Details Recalculation
			suspenseCheckProcess(fm, FinServiceEvent.CHGFRQ, curBDay, fm.getFinStatus(), 0);

		} else if (StringUtils.trimToEmpty(fm.getRcdMaintainSts()).equals(FinServiceEvent.RESCHD)) {
			frequencyDatesUpdation(schdData, fm, serviceInstructions);
		}

		// LinkedFinances
		List<LinkedFinances> list = fd.getLinkedFinancesList();
		if (CollectionUtils.isNotEmpty(list)) {
			auditDetails.addAll(linkedFinancesService.doApproveLinkedFinanceList(fd));
		}

		String recordType = fm.getRecordType();

		if (!isWIF) {
			processLimitApprove(aAuditHeader, false);
		}

		long tempWorkflowId = fm.getWorkflowId();

		List<FinFeeDetail> finFeeDetails = schdData.getFinFeeDetailList();
		String auditTranType = auditHeader.getAuditTranType();

		String planEMIHMethod = fm.getPlanEMIHMethod();
		if (recordType.equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			creditReviewDetailDAO.delete(finID, TableType.MAIN_TAB);
			financeMainDAO.delete(fm, TableType.MAIN_TAB, isWIF, true);
			listDeletion(schdData, moduleDefiner, "", isWIF);
			finServiceInstructionDAO.deleteList(finID, moduleDefiner, "");

			// Delete overdraft Details
			if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())
					&& schdData.getOverdraftScheduleDetails().size() > 0) {
				overdraftScheduleDetailDAO.deleteByFinReference(finID, "", isWIF);
			}

			// Finance Flag Details
			if (CollectionUtils.isNotEmpty(fd.getFinFlagsDetails())) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("FinFlagsHeader");
				finFlagsHeaderDAO.delete(((FinanceFlag) details.get(0).getModelData()), "");
				auditDetails.addAll(details);

				details = fd.getAuditDetailMap().get("FinFlagsDetail");
				finFlagDetailsDAO.deleteList(finReference, FinanceConstants.MODULE_NAME, "");
				auditDetails.addAll(details);
			}

			// Step Details Deletion
			// =======================================
			financeStepDetailDAO.deleteList(finID, isWIF, "");

			if (!isWIF && (!StringUtils.equals(fm.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)
					|| (auditHeader.getApiHeader() == null) || StringUtils.isNotBlank(fm.getServiceName()))) {
				// Additional Field Details Deletion
				// =======================================
				doDeleteAddlFieldDetails(fd, "");
				auditDetails.addAll(jointGuarantorDeletion(fd, "", tranType));
				auditDetails.addAll(checkListDetailService.delete(fd, "", auditTranType));
			}
		} else {
			roleCode = fm.getRoleCode();
			fm.setRcdMaintainSts("");
			fm.setRoleCode("");
			fm.setNextRoleCode("");
			fm.setTaskId("");
			fm.setNextTaskId("");
			fm.setNextUserId(null);
			fm.setWorkflowId(0);

			// Resetting Maturity Terms & Summary details rendering in case of
			// Reduce maturity cases
			if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())
					&& fm.getAdvTerms() == 0 && !isSanctionBasedSchd) {
				int size = schdData.getFinanceScheduleDetails().size();
				for (int i = size - 1; i >= 0; i--) {
					FinanceScheduleDetail curSchd = schdData.getFinanceScheduleDetails().get(i);
					if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
							&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0 && fm.getAdvTerms() == 0) {
						fm.setMaturityDate(curSchd.getSchDate());
						break;
					} else if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
							&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
						schdData.getFinanceScheduleDetails().remove(i);
					}
				}
			}

			if (recordType.equals(PennantConstants.RECORD_TYPE_NEW)) {

				// Mandate Should be processed first for changes since the
				// mandate id will be updated in the finance
				// main.
				// finMandateService.doApprove(fd, auditHeader, "");
				if (fd.getMandate() != null) {
					Mandate mandate = fd.getMandate();
					finMandateService.doApprove(fd, mandate, auditHeader, "");
				}

				if (fd.getSecurityMandate() != null) {
					Mandate mandate = fd.getSecurityMandate();
					finMandateService.doApprove(fd, mandate, auditHeader, "");
				}

				tranType = PennantConstants.TRAN_ADD;
				fm.setRecordType("");

				financeMainDAO.save(fm, TableType.MAIN_TAB, isWIF);

				if (fm.getOldFinReference() != null && auditHeader.getApiHeader() != null
						&& StringUtils.equals(moduleDefiner, FinServiceEvent.ORG)) {
					FinanceMainExtension finExtension = new FinanceMainExtension();
					finExtension.setFinId(finID);
					finExtension.setFinreference(finReference);
					finExtension.setHostreference(fm.getOldFinReference());
					finExtension.setOldhostreference(fm.getExtReference());
					financeMainDAO.saveHostRef(finExtension);
				}
				// Credit Review Details Saving
				if (fd.getCreditReviewData() != null && creditFinancialService != null) {
					creditFinancialService.doApprove(fd, auditHeader, "");
				}

				// Setting BPI Paid amount to Schedule details
				// =======================================
				FinanceRepayments repayment = null;
				if (fm.isAlwBPI() && StringUtils.equals(FinanceConstants.BPI_DISBURSMENT, fm.getBpiTreatment())) {
					for (int i = 0; i < schdData.getFinanceScheduleDetails().size(); i++) {
						FinanceScheduleDetail curSchd = schdData.getFinanceScheduleDetails().get(i);

						if (StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday())) {

							// Update Amount on FinExcessAmount as Amount Type :
							// "ADVANCE INTEREST"
							if (SysParamUtil.isAllowed(SMTParameterConstants.BPI_PAID_ON_INSTDATE)) {
								this.advancePaymentService.processBpiAmount(schdData, curSchd);
							} else {
								// Default to PAID
								curSchd.setSchdPftPaid(curSchd.getProfitSchd());
								curSchd.setSchPftPaid(true);
								long linkedTranId = schdData.getDisbursementDetails().get(0).getLinkedTranId();
								repayment = prepareBpiRepayData(fm, curSchd.getSchDate(), linkedTranId,
										curSchd.getProfitSchd(), curSchd.getTDSAmount());
								break;
							}
							if (curSchd.getInstNumber() > 1) {
								break;
							}
						}
					}
				}

				// Schedule Details
				// =======================================
				finServiceInstructionDAO.deleteList(finID, moduleDefiner, "_Temp");
				listSave(schdData, "", isWIF, 0, serviceUID);
				finServiceInstructionDAO.deleteList(finID, moduleDefiner, "_Temp");

				// IRR Schedule Details
				if (FinanceConstants.PRODUCT_CD.equals(fm.getProductCategory())) {
					irrScheduleDetailDAO.saveList(schdData.getIrrSDList());
				}

				// Finance IRR Details
				// =======================================
				deleteFinIRR(finID, TableType.MAIN_TAB);
				saveFinIRR(schdData.getiRRDetails(), finID, finReference, TableType.MAIN_TAB);

				// BPI Repayment details saving
				if (repayment != null) {
					financeRepaymentsDAO.save(repayment, "");
				}

				// Save Finance Step Policy Details
				// =======================================
				if (FinServiceEvent.ORG.equals(fd.getModuleDefiner())) {
					if (fm.isStepFinance()) {
						saveStepDetailList(schdData, isWIF, "");
						List<AuditDetail> fspd = fd.getAuditDetailMap().get("FinanceStepPolicyDetail");
						if (CollectionUtils.isNotEmpty(fspd)) {
							auditDetails.addAll(fspd);
						}
					}
				}

				// Fee Charge Details
				// =======================================
				saveFeeChargeList(schdData, moduleDefiner, isWIF, "");

				// update finreferece in finReceiptheader table for upfront
				// receipts case
				if (schdData.getExternalReference() != null && !schdData.getExternalReference().isEmpty()) {
					finReceiptHeaderDAO.updateReference(schdData.getExternalReference(), fd.getFinReference(), "");
					financeRepaymentsDAO.updateFinReference(fd.getFinReference(), schdData.getExternalReference(), "");
				}

				// Save FInance Tax Details
				FinanceTaxDetail financeTaxDetail = fd.getFinanceTaxDetail();
				if (financeTaxDetail != null) {
					FinanceTaxDetail tempTaxDetail = financeTaxDetailDAO.getFinanceTaxDetail(finID, "_AView");
					if (tempTaxDetail != null) {
						financeTaxDetailDAO.delete(financeTaxDetail, TableType.MAIN_TAB);
					}
					if (PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER.equals(financeTaxDetail.getApplicableFor())
							|| PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT.equals(financeTaxDetail.getApplicableFor())
							|| PennantConstants.TAXAPPLICABLEFOR_GUARANTOR
									.equals(financeTaxDetail.getApplicableFor())) {
						financeTaxDetail.setRecordType(" ");
						financeTaxDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
						financeTaxDetail.setTaskId("");
						financeTaxDetail.setNextTaskId("");
						financeTaxDetail.setRoleCode("");
						financeTaxDetail.setNextRoleCode("");
						financeTaxDetail.setWorkflowId(0);
						financeTaxDetailDAO.save(financeTaxDetail, TableType.MAIN_TAB);
					}
				}

				// Save Finance Premium Details
				// =======================================
				if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())
						&& !schdData.getOverdraftScheduleDetails().isEmpty()) {

					// Save New list of records
					for (int i = 0; i < schdData.getOverdraftScheduleDetails().size(); i++) {
						schdData.getOverdraftScheduleDetails().get(i).setFinID(finID);
						schdData.getOverdraftScheduleDetails().get(i).setFinReference(finReference);
					}
					overdraftScheduleDetailDAO.saveList(schdData.getOverdraftScheduleDetails(), "");
				}

				/*
				 * // Save Finance Flag details if(financeDetail.getFinFlagsDetails() != null &&
				 * !financeDetail.getFinFlagsDetails().isEmpty()) { finFlagDetailsDAO.savefinFlagList(financeDetail.
				 * getFinFlagsDetails(), ""); }
				 */

				// Vas Recording Details
				if (schdData.getVasRecordingList() != null && !schdData.getVasRecordingList().isEmpty()) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("VasRecordings");
					details = processingVasRecordngList(fd, details, "");
					auditDetails.addAll(details);

					// Vas Recording Extended Field Details
					List<AuditDetail> exdDetails = fd.getAuditDetailMap().get("VasExtendedDetails");
					exdDetails = extendedFieldDetailsService.processingExtendedFieldDetailList(exdDetails,
							VASConsatnts.MODULE_NAME, null, "", serviceUID);
					auditDetails.addAll(exdDetails);
				}

				saveFinExpenseDetails(fm);

			} else {

				tranType = PennantConstants.TRAN_UPD;
				fm.setRecordType("");

				if (!isWIF) {

					// Fetch Existing data before Modification
					FinScheduleData oldFinSchdData = null;
					if (fm.isScheduleRegenerated()) {
						oldFinSchdData = getFinSchDataByFinRef(finID, "", -1);
						oldFinSchdData.setFinID(schdData.getFinID());
						oldFinSchdData.setFinReference(schdData.getFinReference());
					}

					// Overdraft Movements are set for every Overdraft Schedule
					// Built,and limit change is calculated by
					// sub old fin asset and current Fin asset
					// =======================================
					if (StringUtils.equals(moduleDefiner, FinServiceEvent.OVERDRAFTSCHD)
							&& fm.isScheduleRegenerated()) {

						BigDecimal limitChange = fm.getFinAssetValue()
								.subtract(oldFinSchdData.getFinanceMain().getFinAssetValue());

						OverdraftMovements odMovements = new OverdraftMovements();
						odMovements.setFinReference(finReference);
						odMovements.setDroplineDate(fm.getFirstDroplineDate());
						odMovements.setDroplineFrq(fm.getDroplineFrq());
						odMovements.setTenor(fm.getNumberOfTerms());
						odMovements.setODExpiryDate(fm.getMaturityDate());
						odMovements.setODLimit(fm.getFinAssetValue());
						odMovements.setLimitChange(limitChange);
						odMovements.setValueDate(Calendar.getInstance().getTime());

						// Saving the OverdraftMovements in table
						overdraftScheduleDetailDAO.saveOverdraftMovement(odMovements);

					}

					// Create log entry for Action for Schedule Modification
					// =======================================
					FinLogEntryDetail entryDetail = new FinLogEntryDetail();
					entryDetail.setFinID(schdData.getFinID());
					entryDetail.setFinReference(schdData.getFinReference());
					entryDetail
							.setEventAction(StringUtils.isBlank(fd.getAccountingEventCode()) ? AccountingEvent.ADDDBSN
									: fd.getAccountingEventCode());
					entryDetail.setSchdlRecal(fm.isScheduleRegenerated());
					entryDetail.setPostDate(curBDay);
					entryDetail.setReversalCompleted(false);
					long logKey = finLogEntryDetailDAO.save(entryDetail);

					// Save Schedule Details For Future Modifications
					if (fm.isScheduleRegenerated()) {
						listSave(oldFinSchdData, "_Log", isWIF, logKey, serviceUID);
					}
				}

				// Save Finance Main after Saving the oldFinSchdData
				// =======================================
				financeMainDAO.update(fm, TableType.MAIN_TAB, isWIF);

				// Save Finance Premium Details
				// =======================================
				if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())
						&& !schdData.getOverdraftScheduleDetails().isEmpty()) {
					overdraftScheduleDetailDAO.deleteByFinReference(finID, "", isWIF);

					// Save New list of records
					for (int i = 0; i < schdData.getOverdraftScheduleDetails().size(); i++) {
						schdData.getOverdraftScheduleDetails().get(i).setFinReference(finReference);
					}
					overdraftScheduleDetailDAO.saveList(schdData.getOverdraftScheduleDetails(), "");
				}

				// ScheduleDetails delete and save
				// =======================================

				saveLMSServiceLogs(schdData, "");

				if (FinServiceEvent.ADDDISB.equals(fd.getModuleDefiner())) {
					schdData.setFinODPenaltyRate(finODPenaltyRateDAO.getEffectivePenaltyRate(finID, ""));
				}

				listDeletion(schdData, moduleDefiner, "", isWIF);
				finServiceInstructionDAO.deleteList(finID, moduleDefiner, "_Temp");
				listSave(schdData, "", isWIF, 0, serviceUID);

				// Fee Charge Details
				// =======================================
				saveFeeChargeList(schdData, moduleDefiner, isWIF, "");

				if (fm.isStepFinance() && (FinServiceEvent.CHGGRCEND.equals(moduleDefiner)
						|| FinServiceEvent.RESCHD.equals(moduleDefiner) || FinServiceEvent.ADDDISB.equals(moduleDefiner)
						|| FinServiceEvent.RESTRUCTURE.equals(moduleDefiner))
						&& CollectionUtils.isNotEmpty(fd.getFinScheduleData().getStepPolicyDetails())) {
					financeStepDetailDAO.deleteList(fm.getFinID(), isWIF, "");
					saveStepDetailList(fd.getFinScheduleData(), isWIF, "");
					// Handled Exception in CreateLoan Schedule
					if (!isWIF) {
						auditDetails.addAll(fd.getAuditDetailMap().get("FinanceStepPolicyDetail"));
					}
				}
			}

			// LowerTax Deductions
			// ===============
			if (CollectionUtils.isNotEmpty(fd.getFinScheduleData().getLowerTaxDeductionDetails())
					&& FinServiceEvent.ORG.equals(fd.getModuleDefiner())) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("LowerTaxDeductionDetails");
				details = processLowerTaxDeductionDetails(details, TableType.MAIN_TAB, fd);
				auditDetails.addAll(details);
			}
			if (fd.getFinScheduleData().getLowerTaxDeductionDetails() != null
					&& fd.getFinScheduleData().getLowerTaxDeductionDetails().size() >= 1) {
				lowerTaxDeductionDAO.delete(fd.getFinScheduleData().getLowerTaxDeductionDetails().get(0), "_temp");
			}
			// Save Cheque Header Details
			// =======================================
			if (fd.getChequeHeader() != null) {
				String[] fields = PennantJavaUtil.getFieldDetails(new ChequeHeader());
				finChequeHeaderService.doApprove(auditHeader);
				auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1],
						fd.getChequeHeader().getBefImage(), fd.getChequeHeader()));
			}

			// ISRA Details
			if (fd.getIsraDetail() != null) {
				auditDetails.addAll(israDetailService.doApprove(fd, "", tranType));
			}

			// set Customer Details Audit
			if (!StringUtils.equals(fm.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)) {
				if (fd.getCustomerDetails() != null && StringUtils.equals(moduleDefiner, FinServiceEvent.ORG)) {
					auditDetails.addAll(customerDetailsService.saveOrUpdate(fd, ""));
				}
			}

			if (!fd.isExtSource() && !isWIF) {
				List<DocumentDetails> documents = fd.getDocumentDetailsList();
				if (CollectionUtils.isNotEmpty(documents)) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
					details = processingDocumentDetailsList(details, "", fm, moduleDefiner, serviceUID);
					auditDetails.addAll(details);
				}

				// set Check list details Audit
				// =======================================
				if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
					auditDetails.addAll(checkListDetailService.doApprove(fd, "", serviceUID));
				}

				// set the Audit Details & Save / Update Guarantor Details
				// =======================================
				if (fd.getGurantorsDetailList() != null && !fd.getGurantorsDetailList().isEmpty()) {
					auditDetails.addAll(guarantorDetailService.doApprove(fd.getGurantorsDetailList(), "", tranType,
							fm.getFinSourceID(), auditHeader.getApiHeader(), fm.getServiceName()));
				}

				// set the Audit Details & Save / Update JointAccount Details
				// =======================================
				if (fd.getJointAccountDetailList() != null && !fd.getJointAccountDetailList().isEmpty()) {
					auditDetails.addAll(jointAccountDetailService.doApprove(fd.getJointAccountDetailList(), "",
							tranType, fm.getFinSourceID(), auditHeader.getApiHeader(), fm.getServiceName()));
				}

				List<TanAssignment> tanAssignments = fd.getTanAssignments();
				if (CollectionUtils.isNotEmpty(tanAssignments)) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("TanAssignments");
					auditDetails.addAll(tanAssignmentService.processingTanAssignemts(details, TableType.MAIN_TAB));
				}

				// set Finance Collateral Details Audit
				// =======================================
				if (fd.getFinanceCollaterals() != null && !fd.getFinanceCollaterals().isEmpty()) {
					auditDetails.addAll(finCollateralService.doApprove(fd.getFinanceCollaterals(), "", tranType,
							fm.getFinSourceID(), auditHeader.getApiHeader(), fm.getServiceName()));
				}

				// Finance Eligibility Rule Details
				// =======================================
				auditDetails.addAll(eligibilityDetailService.saveOrUpdate(fd));

				// Finance Scoring Module Details List Saving
				// =======================================
				auditDetails.addAll(scoringDetailService.saveOrUpdate(fd));

				// Dedup Details
				// =======================================
				auditDetails.addAll(saveDedupDetails(fd));

				// Additional Field Details Save / Update
				// =======================================
				doSaveAddlFieldDetails(fd, "");

				// Advance Payment Details
				// =======================================
				// Payment Order Issue Details
				// =======================================
				// Quick disbursement
				if (FinServiceEvent.ORG.equals(moduleDefiner)) {
					auditDetails.addAll(processAdvancePayments(fd, auditHeader, "", auditTranType));
				} else {
					if (PennantConstants.RCD_STATUS_SUBMITTED.equals(fm.getRecordStatus())) {
						List<FinServiceInstruction> fsi = fd.getFinScheduleData().getFinServiceInstructions();
						if (fsi.size() > 0 && FinServiceEvent.ADDDISB.equals(fsi.get(0).getFinEvent())) {
							List<FinanceDisbursement> dd = fd.getFinScheduleData().getDisbursementDetails();
							for (FinanceDisbursement disb : dd) {
								for (FinAdvancePayments adv : payments) {
									if (adv.getLlDate().compareTo(disb.getDisbDate()) == 0) {
										if (disb.isQuickDisb()
												&& PennantConstants.RECORD_TYPE_NEW.equals(adv.getStatus())) {
											auditDetails.addAll(finAdvancePaymentsService.processQuickDisbursment(fd,
													"", auditTranType));
										}
									}
								}
							}
						}
					}
					auditDetails.addAll(
							finAdvancePaymentsService.saveOrUpdate(payments, "", auditTranType, fd.isDisbStp()));
				}

				// Save for Loan Extended fields Maintenance
				if (FinServiceEvent.ORG.equals(moduleDefiner)) {
					extendedFieldMaintenanceDAO.save(fm);
				}

				// PSL details
				if (fd.getPslDetail() != null) {
					pSLDetailService.doApprove(fd.getPslDetail(), TableType.MAIN_TAB, tranType);
				}
				List<RisksAndMitigants> risksAndMitigants = fd.getRisksAndMitigantsList();
				if (CollectionUtils.isNotEmpty(risksAndMitigants)) {
					auditDetails.addAll(
							risksAndMitigantsService.doApprove(risksAndMitigants, TableType.MAIN_TAB, tranType));
				}
				List<SanctionConditions> sanctionConditions = fd.getSanctionDetailsList();
				if (CollectionUtils.isNotEmpty(sanctionConditions)) {
					auditDetails.addAll(
							sanctionConditionsService.doApprove(sanctionConditions, TableType.MAIN_TAB, tranType));
				}
				List<DealRecommendationMerits> dealRecommendationMerits = fd.getDealRecommendationMeritsDetailsList();
				if (CollectionUtils.isNotEmpty(dealRecommendationMerits)) {
					auditDetails.addAll(dealRecommendationMeritsService.doApprove(dealRecommendationMerits,
							TableType.MAIN_TAB, tranType));
				}
				List<DueDiligenceDetails> dueDiligenceDetails = fd.getDueDiligenceDetailsList();
				if (CollectionUtils.isNotEmpty(dueDiligenceDetails)) {
					auditDetails.addAll(
							dueDiligenceDetailsService.doApprove(dueDiligenceDetails, TableType.MAIN_TAB, tranType));
				}
				List<RecommendationNotes> recommendationNotesList = fd.getRecommendationNoteList();

				if (CollectionUtils.isNotEmpty(recommendationNotesList)) {
					auditDetails.addAll(recommendationNotesDetailsService.doApprove(recommendationNotesList,
							TableType.MAIN_TAB, tranType));
				}
				// Synopsis Details
				if (fd.getSynopsisDetails() != null) {
					synopsisDetailsService.doApprove(fd.getSynopsisDetails(), TableType.MAIN_TAB, tranType);
				}

				// Fin OCR Details
				// =========================
				if (fd.getFinOCRHeader() != null && finOCRHeaderService != null) {
					auditDetails.addAll(finOCRHeaderService.processFinOCRHeader(aAuditHeader, "doApprove"));
				}

				// PMAY
				// =========================
				if (fd.getPmay() != null && pmayService != null) {
					auditDetails.add(pmayService.doApprove(fd.getPmay(), TableType.MAIN_TAB, auditTranType));
				}

				// Verifications
				saveOrUpdateVerifications(auditDetails, fd, fm, tranType);

				// calling post hoot
				if (postExteranalServiceHook != null) {
					postExteranalServiceHook.doProcess(aAuditHeader, "doApprove");
				}

				// Advance Payment Details
				// =======================================
				if (payments != null) {
					if (StringUtils.trimToEmpty(recordMainStatus).equals(FinServiceEvent.CANCELDISB)) {
						finAdvancePaymentsService.doCancel(fd);
					} else {
						finAdvancePaymentsService.doApprove(payments, "", tranType, fd.isDisbStp());
					}

					finAdvancePaymentsService.processDisbursments(fd);
				}

				if (CollectionUtils.isNotEmpty(fd.getCovenantTypeList())) {
					finCovenantTypeService.doApprove(fd.getCovenantTypeList(), "", tranType);
				}

				List<Covenant> covenants = fd.getCovenants();
				int docSize = 0;
				if (CollectionUtils.isNotEmpty(fd.getDocumentDetailsList())) {
					docSize = fd.getDocumentDetailsList().size();
				}
				if (CollectionUtils.isNotEmpty(covenants)) {
					auditDetails.addAll(covenantsService.doApprove(covenants, TableType.MAIN_TAB, tranType, docSize));
				}

				List<FinOption> finOptions = fd.getFinOptions();
				if (CollectionUtils.isNotEmpty(finOptions)) {
					auditDetails.addAll(finOptionService.doApprove(finOptions, TableType.MAIN_TAB, tranType));
				}

				boolean isRegCol = false;

				// Collateral Assignments Details
				// =======================================
				if (fd.getCollateralAssignmentList() != null && !fd.getCollateralAssignmentList().isEmpty()) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("CollateralAssignments");
					details = processingCollateralAssignmentList(details, "", fm);
					for (CollateralAssignment ca : fd.getCollateralAssignmentList()) {
						int count = collateralAssignmentDAO.getAssignedCollateralCount(ca.getCollateralRef(), "_AView");
						Date regDate = collateralSetupService.getRegistrationDate(ca.getCollateralRef());
						if (PennantConstants.RECORD_TYPE_NEW.equals(ca.getRecordType())) {
							if (count > 1 && regDate != null) {
								isRegCol = true;
							}
						}

						if (isRegCol) {
							CollateralAssignment collass = collateralSetupService.getCollDetails(ca.getCollateralRef());
							collateralSetupService.updateCersaiDetails(ca.getCollateralRef(), collass.getSiid(),
									collass.getAssetid());
						}
					}
					auditDetails.addAll(details);
				}

				// Save Collateral setup Details
				List<CollateralSetup> collateralSetupList = fd.getCollaterals();
				if (collateralSetupList != null && !collateralSetupList.isEmpty()) {
					List<AuditDetail> details = collateralSetupService.processCollateralSetupList(aAuditHeader,
							"doApprove");
					auditDetails.addAll(details);
				}

				// Save LegalDetails
				List<LegalDetail> legalDetailsList = fd.getLegalDetailsList();
				if (CollectionUtils.isNotEmpty(legalDetailsList)) {
					List<AuditDetail> details = legalDetailService.processLegalDetails(aAuditHeader, "doApprove");
					auditDetails.addAll(details);
				}

				// FinAssetTypes
				if (fd.getFinAssetTypesList() != null && !fd.getFinAssetTypesList().isEmpty()) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("FinAssetTypes");
					details = processingFinAssetTypesList(details, "", fm);
					auditDetails.addAll(details);
				}

				// Fin Flag Details
				if (CollectionUtils.isNotEmpty(fd.getFinFlagsDetails())) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("FinFlagsHeader");
					details = processingFinFlagHeader(details, "");
					auditDetails.addAll(details);

					details = fd.getAuditDetailMap().get("FinFlagsDetail");
					details = processingFinFlagDetailList(details, "");
					auditDetails.addAll(details);
				}

				// AssetType Extended field Details
				if (fd.getExtendedFieldRenderList() != null && fd.getExtendedFieldRenderList().size() > 0) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldDetails");

					for (int i = 0; i < details.size(); i++) {
						ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) details.get(i).getModelData();
						extendedFieldRender.setReference(finReference);
					}
					details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
							AssetConstants.EXTENDEDFIELDS_MODULE, null, "", serviceUID);
					auditDetails.addAll(details);
				}

				// Extended field Details
				if (fd.getExtendedFieldRender() != null) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("LoanExtendedFieldDetails");
					details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
							ExtendedFieldConstants.MODULE_LOAN, fd.getExtendedFieldHeader().getEvent(), "", serviceUID);
					auditDetails.addAll(details);
				}

			}

			// SubventionDetails
			if (fd.getFinScheduleData().getSubventionDetail() != null && (StringUtils.isBlank(fd.getModuleDefiner())
					|| FinServiceEvent.ORG.equals(fd.getModuleDefiner()))) {
				if (subventionService != null) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("SubventionDetails");
					details = subventionService.processSubventionDetails(details, TableType.MAIN_TAB, fd);
					auditDetails.addAll(details);
				}
			}

			// Finance Fee Details
			if (ProductUtil.isNotOverDraft(fm) && !fd.isExtSource()) {
				if (CollectionUtils.isNotEmpty(finFeeDetails)) {
					for (FinFeeDetail fee : finFeeDetails) {
						fee.setInstructionUID(serviceUID);
					}

					finFeeDetailService.doApprove(finFeeDetails, "", tranType, isWIF);
				}
			}

			// Finance Fee Receipt Details
			/*
			 * //TODO:GANESH NEED TO REMOVE if (StringUtils.equals(financeDetail.getModuleDefiner(),
			 * FinServiceEvent.ORG) || FinServiceEvent.ADDDISB.equalsIgnoreCase(financeDetail.getModuleDefiner())) { if
			 * (financeDetail.getFinScheduleData().getFinFeeReceipts() == null ||
			 * financeDetail.getFinScheduleData().getFinFeeReceipts().isEmpty()) {
			 * finFeeDetailService.createExcessAmount(financeMain.getFinReference(), null, financeMain.getCustID()); }
			 * else {
			 * finFeeDetailService.doApproveFinFeeReceipts(financeDetail.getFinScheduleData().getFinFeeReceipts(), "",
			 * tranType,financeMain.getFinReference(), financeMain.getCustID(), financeDetail.getModuleDefiner()); } }
			 */

			// Plan EMI Holiday Details Deletion, if exists on Old image
			// =======================================
			if ((FinServiceEvent.PLANNEDEMI.equals(moduleDefiner) || (FinServiceEvent.CHGFRQ.equals(moduleDefiner)))
					&& fm.isPlanEMIHAlw()) {
				if (StringUtils.equals(planEMIHMethod, FinanceConstants.PLANEMIHMETHOD_FRQ)) {
					finPlanEmiHolidayDAO.deletePlanEMIHMonths(finID, "");
				} else if (StringUtils.equals(planEMIHMethod, FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
					finPlanEmiHolidayDAO.deletePlanEMIHDates(finID, "");
				}
			}
		}

		if (fm.isPlanEMIHAlw()
				&& (FinServiceEvent.ORG.equals(moduleDefiner) || FinServiceEvent.PLANNEDEMI.equals(moduleDefiner)
						|| (FinServiceEvent.CHGFRQ.equals(moduleDefiner)))) {

			List<FinPlanEmiHoliday> holidayList = new ArrayList<>();
			int planEMIHMonth = 0;
			Date planEMIHDate = null;

			for (int i = 0; i < schdData.getPlanEMIHmonths().size(); i++) {
				if (FinanceConstants.PLANEMIHMETHOD_FRQ.equals(planEMIHMethod)) {
					planEMIHMonth = schdData.getPlanEMIHmonths().get(i);
				} else if (FinanceConstants.PLANEMIHMETHOD_ADHOC.equals(planEMIHMethod)) {
					planEMIHDate = schdData.getPlanEMIHDates().get(i);
				}

				FinPlanEmiHoliday emiHoliday = new FinPlanEmiHoliday();
				emiHoliday.setFinID(finID);
				emiHoliday.setFinReference(finReference);
				emiHoliday.setPlanEMIHMonth(planEMIHMonth);
				emiHoliday.setPlanEMIHDate(planEMIHDate);
				holidayList.add(emiHoliday);
			}

			for (int i = 0; i < schdData.getPlanEMIHDates().size(); i++) {
				if (FinanceConstants.PLANEMIHMETHOD_FRQ.equals(planEMIHMethod)) {
					planEMIHMonth = schdData.getPlanEMIHmonths().get(i);
				} else if (FinanceConstants.PLANEMIHMETHOD_ADHOC.equals(planEMIHMethod)) {
					planEMIHDate = schdData.getPlanEMIHDates().get(i);
				}

				FinPlanEmiHoliday emiHoliday = new FinPlanEmiHoliday();
				emiHoliday.setFinID(finID);
				emiHoliday.setFinReference(finReference);
				emiHoliday.setPlanEMIHMonth(planEMIHMonth);
				emiHoliday.setPlanEMIHDate(planEMIHDate);
				holidayList.add(emiHoliday);
			}

			if (!holidayList.isEmpty()) {

				if (FinanceConstants.PLANEMIHMETHOD_FRQ.equals(planEMIHMethod)) {
					finPlanEmiHolidayDAO.savePlanEMIHMonths(holidayList, "");
				} else if (FinanceConstants.PLANEMIHMETHOD_ADHOC.equals(planEMIHMethod)) {
					finPlanEmiHolidayDAO.savePlanEMIHDates(holidayList, "");
				}

			}
		}

		// Save Finance Schedule Snapshot
		// ===============================
		// TODO commented below line which is leading to column miss match
		// exception
		// financeMainDAO.saveFinanceSnapshot(financeMain);

		// Update Task_log and Task_Owners tables
		// =======================================
		fm.setRoleCode(roleCode);
		updateTaskLog(fm, false);

		// Receipt creation through Restructure Process
		if (restructReceiptReq) {
			cashBackProcessService.createRestructReceipt(fd);
		}

		if (!isWIF) {
			finStageAccountingLogDAO.update(finID, moduleDefiner, false);
		}

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());

		if (!fd.isExtSource()) {

			// Save FInance Tax Details
			if (fd.getFinanceTaxDetail() != null) {
				FinanceTaxDetail tempTaxDetail = financeTaxDetailDAO.getFinanceTaxDetail(finID, "_TView");
				if (tempTaxDetail != null) {
					financeTaxDetailDAO.delete(fd.getFinanceTaxDetail(), TableType.TEMP_TAB);
				}
			}

			// ScheduleDetails delete
			// =======================================
			if (!StringUtils.equals(fm.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)
					|| (auditHeader.getApiHeader() == null) || StringUtils.isNotBlank(fm.getServiceName())) {
				listDeletion(schdData, moduleDefiner, "_Temp", isWIF);
			}

			// Plan EMI Holiday Details Deletion, if exists on Old image
			// =======================================
			FinanceMain befFinMain = fm.getBefImage();
			if (befFinMain != null && befFinMain.isPlanEMIHAlw()) {
				if (StringUtils.equals(befFinMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
					finPlanEmiHolidayDAO.deletePlanEMIHMonths(finID, "_Temp");
				} else if (StringUtils.equals(befFinMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
					finPlanEmiHolidayDAO.deletePlanEMIHDates(finID, "_Temp");
				}
			}

			if (!isWIF && !fd.isDirectFinalApprove()
					&& (!StringUtils.equals(fm.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)
							|| (auditHeader.getApiHeader() == null) || StringUtils.isNotBlank(fm.getServiceName()))) {
				// Additional Field Details Deletion in _Temp Table
				// =======================================
				doDeleteAddlFieldDetails(fd, "_Temp");

				auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
				auditDetailList.addAll(checkListDetailService.delete(fd, "_Temp", auditTranType));
				if (payments != null) {
					auditDetailList.addAll(finAdvancePaymentsService.delete(payments, "_Temp", auditTranType));
				}

				List<FinCovenantType> finCovenantTypes = fd.getCovenantTypeList();
				if (CollectionUtils.isNotEmpty(finCovenantTypes)) {
					// auditDetailList.addAll(finCovenantTypeService.delete(finCovenantTypes,
					// "_Temp", auditTranType));
					finCovenantTypeService.delete(finCovenantTypes, "_Temp", auditTranType);
				}

				List<Covenant> covenants = fd.getCovenants();
				if (CollectionUtils.isNotEmpty(covenants)) {
					// auditDetailList.addAll(covenantsService.delete(covenants,
					// TableType.TEMP_TAB, auditTranType));
					covenantsService.delete(covenants, TableType.TEMP_TAB, auditTranType);
				}

				List<DocumentDetails> documents = fd.getDocumentDetailsList();
				if (!fd.isExtSource() && !isWIF) {
					if (CollectionUtils.isNotEmpty(documents)) {
						listDocDeletion(fd, "_Temp");
					}
				}

				if (fd.getPslDetail() != null) {
					auditDetailList.add(pSLDetailService.delete(fd.getPslDetail(), TableType.TEMP_TAB, auditTranType));
				}

				// Collateral assignment Details
				if (fd.getCollateralAssignmentList() != null && !fd.getCollateralAssignmentList().isEmpty()) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("CollateralAssignments");
					auditDetailList.addAll(details);
					collateralAssignmentDAO.deleteByReference(finReference, "_Temp");
				}

				// FinAssetTypes details
				if (fd.getFinAssetTypesList() != null && !fd.getFinAssetTypesList().isEmpty()) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("FinAssetTypes");
					finAssetTypeDAO.deleteByReference(finReference, "_Temp");
					auditDetailList.addAll(details);
				}

				// AssetType Extended field Details
				if (fd.getExtendedFieldRenderList() != null && fd.getExtendedFieldRenderList().size() > 0) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldDetails");
					auditDetailList.addAll(extendedFieldDetailsService.delete(details,
							AssetConstants.EXTENDEDFIELDS_MODULE, finReference, null, "_Temp"));
				}

				// Vas Recording Details details Prasad
				if (schdData.getVasRecordingList() != null && !schdData.getVasRecordingList().isEmpty()) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("VasRecordings");
					vasRecordingDAO.deleteByPrimaryLinkRef(finReference, "_Temp");
					auditDetailList.addAll(details);

					// Vas Recording Extended field Details
					List<AuditDetail> vasExtDetails = fd.getAuditDetailMap().get("VasExtendedDetails");
					if (CollectionUtils.isNotEmpty(vasExtDetails)) {
						for (AuditDetail auditDetail : vasExtDetails) {
							ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) auditDetail.getModelData();
							extendedFieldDetailsService.delete(vasExtDetails, VASConsatnts.MODULE_NAME,
									extendedFieldRender.getReference(), null, "_Temp");
						}
					}
				}

				// Loan Extended field Details
				if (fd.getExtendedFieldRender() != null) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("LoanExtendedFieldDetails");
					auditDetailList
							.addAll(extendedFieldDetailsService.delete(details, ExtendedFieldConstants.MODULE_LOAN,
									finReference, fd.getExtendedFieldHeader().getEvent(), "_Temp"));
				}

				// SubventionDetails
				if (fd.getFinScheduleData().getSubventionDetail() != null && (StringUtils.isBlank(fd.getModuleDefiner())
						|| FinServiceEvent.ORG.equals(fd.getModuleDefiner()))) {
					if (subventionService != null) {
						subventionService.delete(fd.getFinScheduleData().getSubventionDetail(), TableType.TEMP_TAB);
					}
				}

				if (fd.getFinanceCollaterals() != null) {
					auditDetailList
							.addAll(finCollateralService.delete(fd.getFinanceCollaterals(), "_Temp", auditTranType));
				}
				/*
				 * //Deleting FinancailSummary RisksAndMitigants Details List<RisksAndMitigants> risksAndMitigants =
				 * financeDetail.getRisksAndMitigantsList(); if (CollectionUtils.isNotEmpty(risksAndMitigants)) {
				 * auditDetails.addAll(risksAndMitigantsService.delete( risksAndMitigants, TableType.TEMP_TAB,
				 * auditTranType)); } //Deleting FinancailSummary SanctionConditions Details List<SanctionConditions>
				 * sanctionConditions = financeDetail.getSanctionDetailsList(); if
				 * (CollectionUtils.isNotEmpty(sanctionConditions)) {
				 * auditDetails.addAll(sanctionConditionsService.delete( sanctionConditions, TableType.TEMP_TAB,
				 * auditTranType)); } //Deleting FinancailSummary DealRecommendationMerits Details
				 * List<DealRecommendationMerits> dealRecommendationMerits =
				 * financeDetail.getDealRecommendationMeritsDetailsList(); if
				 * (CollectionUtils.isNotEmpty(dealRecommendationMerits)) {
				 * auditDetails.addAll(dealRecommendationMeritsService.delete( dealRecommendationMerits,
				 * TableType.TEMP_TAB, auditTranType)); } //Deleting FinancailSummary DueDiligence Details
				 * List<DueDiligenceDetails> dueDiligenceDetails = financeDetail.getDueDiligenceDetailsList(); if
				 * (CollectionUtils.isNotEmpty(dueDiligenceDetails)) {
				 * auditDetails.addAll(dueDiligenceDetailsService.delete( dueDiligenceDetails, TableType.TEMP_TAB,
				 * auditTranType)); } //Deleting FinancailSummary DealRecommendationMerits Details
				 * 
				 * 
				 * //Deleting FinancailSummary Synopsis Details if (financeDetail.getSynopsisDetails() != null) {
				 * auditDetailList.add(synopsisDetailsService.delete( financeDetail.getSynopsisDetails(),
				 * TableType.TEMP_TAB, auditTranType)); }
				 */

			}

			if (!StringUtils.equals(fm.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)
					|| auditHeader.getApiHeader() == null || StringUtils.isNotBlank(fm.getServiceName())) {
				if (!fd.isDirectFinalApprove()) {
					// Fin Fee Details Deletion
					if (CollectionUtils.isNotEmpty(finFeeDetails)) {
						auditDetailList
								.addAll(finFeeDetailService.delete(finFeeDetails, "_Temp", auditTranType, isWIF));
					}

					// Fin Fee Receipt Details Deletion
					/*
					 * //TODO:GANESH NEED TO REMOVE if (financeDetail.getFinScheduleData().getFinFeeReceipts() != null)
					 * { auditDetailList.addAll(finFeeDetailService. deleteFinFeeReceipts(
					 * financeDetail.getFinScheduleData().getFinFeeReceipts(), "_Temp",
					 * auditHeader.getAuditTranType())); }
					 */

					// Step Details Deletion
					// =======================================
					financeStepDetailDAO.deleteList(finID, isWIF, "_Temp");

					// Finance Flag Details
					if (CollectionUtils.isNotEmpty(fd.getFinFlagsDetails())) {
						List<AuditDetail> details = fd.getAuditDetailMap().get("FinFlagsHeader");
						finFlagsHeaderDAO.delete(((FinanceFlag) details.get(0).getModelData()), "_Temp");
						auditDetailList.addAll(details);

						details = fd.getAuditDetailMap().get("FinFlagsDetail");
						finFlagDetailsDAO.deleteList(finReference, FinanceConstants.MODULE_NAME, "_Temp");
						auditDetailList.addAll(details);
					}

					// Delete Finance IRR Values
					deleteFinIRR(finID, TableType.TEMP_TAB);

					// Finance Main Details
					// =======================================
					financeMainDAO.delete(fm, TableType.TEMP_TAB, isWIF, true);
				}
			}

			// tasks # >>Start Advance EMI and DSF
			String grcAdvType = fm.getGrcAdvType();
			String repayAdvType = fm.getAdvType();

			AdvancePaymentDetail advPay = fd.getAdvancePaymentDetail();
			if ((grcAdvType != null || repayAdvType != null) && advPay != null) {
				advPay.setInstructionUID(serviceUID);

				// If Advance Payment updation Required
				if (AdvancePaymentUtil.advPayUpdateReq(moduleDefiner)) {
					advancePaymentService.processAdvancePayment(advPay, moduleDefiner, fm.getLastMntBy());
				}

				// Saving of Advance Payment Detail
				advancePaymentService.save(fd.getAdvancePaymentDetail());

			}

			if ((grcAdvType != null || repayAdvType != null)) {
				if (FinServiceEvent.ORG.equals(moduleDefiner) || FinServiceEvent.ADDDISB.equals(moduleDefiner)) {
					processAdvancePayment(finFeeDetails, schdData);
				} else if (ImplementationConstants.ALW_ADV_INTEMI_ADVICE_CREATION) {
					if (FinServiceEvent.RATECHG.equals(moduleDefiner) || FinServiceEvent.ADDTERM.equals(moduleDefiner)
							|| FinServiceEvent.RMVTERM.equals(moduleDefiner)
							|| FinServiceEvent.CANCELDISB.equals(moduleDefiner)
							|| FinServiceEvent.CHGPFT.equals(moduleDefiner)
							|| FinServiceEvent.CHGFRQ.equals(moduleDefiner)
							|| FinServiceEvent.CANCELFIN.equals(moduleDefiner)
							|| FinServiceEvent.PLANNEDEMI.equals(moduleDefiner)
							|| FinServiceEvent.UNPLANEMIH.equals(moduleDefiner)
							|| FinServiceEvent.RESCHD.equals(moduleDefiner)
							|| FinServiceEvent.RECALCULATE.equals(moduleDefiner)
							|| FinServiceEvent.CHGRPY.equals(moduleDefiner)) {
						processAdvancePayment(schdData);
					}
				}
			}

			// Mail Alert Notification for Customer/Dealer/Provider...etc
			Notification notification = new Notification();
			notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_AE);
			notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_CN);
			notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_SP);
			notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_DSAN);
			notification.setModule("LOAN_ORG");
			String finEvent = StringUtils.isEmpty(moduleDefiner) ? FinServiceEvent.ORG : moduleDefiner;
			notification.setSubModule(finEvent);
			notification.setKeyReference(finReference);
			notification.setStage(fm.getRoleCode());
			notification.setReceivedBy(fm.getLastMntBy());
			fm.setWorkflowId(tempWorkflowId);

			if (notificationService != null) {
				notificationService.sendNotifications(notification, fd, fm.getFinType(), fd.getDocumentDetailsList());
			}

			if (!recordType.equals(PennantConstants.RECORD_TYPE_DEL)) {
				fm.setWorkflowId(0);
			}

			// Saving the reasons
			saveReasonDetails(fd);
			// Calling External CMS API system.
			processPayments(fd);

			// Auto Payable creation for Cash back process (DBD/MBD)
			if (FinServiceEvent.ORG.equals(moduleDefiner)) {
				cashBackProcessService.createCashBackAdvice(fm, curBDay, fd);
				if (ProductUtil.isOverDraft(fm)) {
					overdrafLoanService.createDisbursment(fm);
				}
			}

			FinanceDetail tempfinanceDetail = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
			FinanceMain tempfinanceMain = tempfinanceDetail.getFinScheduleData().getFinanceMain();
			auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
					tempfinanceMain.getBefImage(), tempfinanceMain));
			auditHeader.setAuditDetails(auditDetailList);

			// Adding audit as deleted from Temp table
			if (!isWIF) {
				auditHeaderDAO.addAudit(auditHeader);
			}
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));
		// Adding audit as Insert/Update/deleted into main table
		if (!isWIF) {
			auditHeaderDAO.addAudit(auditHeader);
		}

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(fd);

		if (FeeExtension.FEE_SERVICEING_STAMPIN_ON_ORG && FinServiceEvent.ORG.equals(fd.getModuleDefiner()) && !isWIF) {
			List<FinFeeConfig> calculateFees = feeCalculator.convertToFinanceFees(fd);
			if (CollectionUtils.isNotEmpty(calculateFees)) {
				finFeeConfigService.saveList(calculateFees, "");
			}
		}

		// Restructure Detail
		if (fd.getFinScheduleData().getRestructureDetail() != null) {
			auditDetails.addAll(restructureService.doApproveRestructureDetail(fd, "", tranType));
			restructureService.computeLPPandUpdateOD(fd);
		}

		this.manualScheduleService.doApprove(fd);

		this.variableOverdraftSchdService.doApprove(fd);

		logger.debug(Literal.LEAVING);

		return auditHeader;
	}

	private void processAdvancePayment(List<FinFeeDetail> finFeeDetails, FinScheduleData finScheduleData) {
		FinanceMain financeMain = finScheduleData.getFinanceMain();

		if (finFeeDetails == null) {
			return;
		}

		AdvanceStage advStage = AdvanceStage.getStage(financeMain.getAdvStage());

		for (FinFeeDetail fee : finFeeDetails) {
			AdvanceRuleCode advRule = AdvanceRuleCode.getRule(fee.getFeeTypeCode());

			if (advRule == null) {
				continue;
			}

			if (advRule == AdvanceRuleCode.ADVEMI && advStage == AdvanceStage.FE) {
				continue;
			}

			BigDecimal excessAmount = fee.getActualAmountOriginal();

			if (excessAmount == null) {
				excessAmount = BigDecimal.ZERO;
			}

			if (excessAmount.compareTo(BigDecimal.ZERO) != 0) {
				AdvancePayment advPayment = new AdvancePayment(financeMain);
				advPayment.setAdvancePaymentType(advRule.name());
				advPayment.setRequestedAmt(excessAmount);
				advancePaymentService.excessAmountMovement(advPayment, null, AccountConstants.TRANTYPE_CREDIT);
			}
		}
	}

	private void processAdvancePayment(FinScheduleData finScheduleData) {
		advancePaymentService.excessAmountMovement(finScheduleData);
	}

	private void frequencyDatesUpdation(FinScheduleData finScheduleData, FinanceMain financeMain,
			List<FinServiceInstruction> serviceInstructions) {
		// GHF 166278 - Frequency and dates update in finance main - START
		List<FinanceScheduleDetail> modfsd = finScheduleData.getFinanceScheduleDetails();
		boolean rpyFound = false;
		boolean grcFound = false;
		for (FinanceScheduleDetail currSchd : modfsd) {
			Date schDate = currSchd.getSchDate();

			if ((DateUtility.compare(schDate, SysParamUtil.getAppDate()) >= 0) && currSchd.getInstNumber() > 0) {
				if ((currSchd.getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_REPAY)
						|| currSchd.getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_MATURITY)) && !rpyFound) {
					if (currSchd.isCpzOnSchDate()) {
						financeMain.setRepayCpzFrq(serviceInstructions.get(0).getRepayFrq());
						financeMain.setNextRepayCpzDate(schDate);
					}

					if (currSchd.isPftOnSchDate() || currSchd.isRepayOnSchDate()
							|| currSchd.getSchDate().compareTo(financeMain.getMaturityDate()) == 0) {
						financeMain.setRepayFrq(serviceInstructions.get(0).getRepayFrq());
						financeMain.setNextRepayDate(schDate);
					}

					if (currSchd.isPftOnSchDate()
							|| currSchd.getSchDate().compareTo(financeMain.getMaturityDate()) == 0) {
						financeMain.setRepayPftFrq(serviceInstructions.get(0).getRepayFrq());
						financeMain.setNextRepayPftDate(schDate);
					}

					if (currSchd.isRvwOnSchDate()) {
						financeMain.setRepayRvwFrq(serviceInstructions.get(0).getRepayFrq());
						financeMain.setNextRepayRvwDate(schDate);
					}
					rpyFound = true;
					grcFound = true;
				}

				if ((currSchd.getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_GRACE)
						|| currSchd.getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_GRACE_END)) && !grcFound) {

					if (currSchd.isCpzOnSchDate()) {
						financeMain.setGrcCpzFrq(serviceInstructions.get(0).getRepayFrq());
						financeMain.setNextGrcCpzDate(schDate);
					}

					if (currSchd.isPftOnSchDate() || currSchd.isRepayOnSchDate()) {
						financeMain.setGrcPftFrq(serviceInstructions.get(0).getRepayFrq());
						financeMain.setNextGrcPftDate(schDate);
					}

					if (currSchd.isRvwOnSchDate()
							|| currSchd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) == 0) {
						financeMain.setGrcPftRvwFrq(serviceInstructions.get(0).getRepayFrq());
						financeMain.setNextGrcPftRvwDate(schDate);
					}
					grcFound = true;
				}

				if (grcFound && rpyFound) {
					break;
				}
			}
		}
		// GHF 166278 - Frequency and dates update in finance main - END
	}

	/**
	 * Processing the online paymnents if cleint extension available
	 * 
	 * @param financeDetail
	 */
	private void processPayments(FinanceDetail financeDetail) {
		logger.debug(Literal.LEAVING);

		FinanceDetail finDetail = new FinanceDetail();
		List<FinAdvancePayments> advancePayments = new ArrayList<>();

		List<FinAdvancePayments> payments = financeDetail.getAdvancePaymentsList();
		if (CollectionUtils.isEmpty(payments)) {
			return;
		}

		for (FinAdvancePayments finAdvancePayments : payments) {
			if (finAdvancePayments.isOnlineProcReq()) {
				advancePayments.add(finAdvancePayments);
			}
		}

		finDetail.setAdvancePaymentsList(advancePayments);
		this.paymentsProcessService.process(finDetail, DisbursementConstants.CHANNEL_DISBURSEMENT);
		logger.debug(Literal.LEAVING);
	}

	public String getServiceTasks(String taskId, FinanceMain financeMain, String finishedTasks,
			WorkflowEngine workflowEngine) {
		logger.debug(Literal.ENTERING);
		// changes regarding parallel work flow
		String nextRoleCode = StringUtils.trimToEmpty(financeMain.getNextRoleCode());
		String nextRoleCodes[] = nextRoleCode.split(",");

		if (nextRoleCodes.length > 1) {
			return "";
		}

		String serviceTasks = workflowEngine.getServiceOperationsAsString(taskId, financeMain);
		// serviceTasks = "doApprove";
		if (StringUtils.isNotBlank(serviceTasks)) {
			serviceTasks += ";";
		}

		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug(Literal.LEAVING);
		return serviceTasks;
	}

	/**
	 * Method for execute workflow service tasks.
	 * 
	 * @throws Exception
	 * 
	 */
	@Override
	public AuditHeader executeWorkflowServiceTasks(AuditHeader auditHeader, String role, String usrAction,
			WorkflowEngine engine) throws Exception {
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = financeDetail.getFinScheduleData().getFinanceMain();

		if (ImplementationConstants.EXTENDEDFIELDS_ORG_WORKFLOW) {
			addExtFieldsToAttributes(afinanceMain);
		}

		String taskId = engine.getUserTaskId(role);

		// Execute service tasks.
		List<ServiceTask> serviceTasks = engine.getServiceTasks(taskId, afinanceMain);
		ServiceTask task;
		List<String> finishedTasks = new ArrayList<>();
		String operation = null;
		String finalOperation = null;
		auditHeader.setProcessCompleted(false);

		while (!serviceTasks.isEmpty()) {
			task = serviceTasks.get(0);
			for (ServiceTask serviceTask : serviceTasks) {
				if (serviceTask.getOperation().equalsIgnoreCase("doReject")
						|| serviceTask.getOperation().equalsIgnoreCase("doApprove")) {
					operation = serviceTask.getOperation();
					break;
				}
			}
			if (ProcessUtil.isPersistentTask(task)) {
				finalOperation = task.getOperation();

				break;
			}

			auditHeader = execute(auditHeader, task, role, engine, operation);

			// Check whether to proceed with next service tasks.
			auditHeader = nextProcess(auditHeader);

			if (!auditHeader.isNextProcess()) {
				break;
			}

			// Get the next service tasks.
			finishedTasks.add(task.getOperation());
			serviceTasks = getRemainingServiceTasks(engine, taskId, afinanceMain, finishedTasks);
		}

		LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
		afinanceMain.setUserDetails(userDetails);

		// Save the data.
		if (!auditHeader.isProcessCompleted()) {
			// Set work-flow details.
			setNextTaskDetails(taskId, afinanceMain, engine, usrAction, role);
			// Extended Field details
			if (financeDetail.getExtendedFieldRender() != null) {
				ExtendedFieldRender details = financeDetail.getExtendedFieldRender();
				details.setReference(afinanceMain.getFinReference());
				details.setLastMntBy(afinanceMain.getUserDetails().getUserId());
				details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				details.setRecordStatus(afinanceMain.getRecordStatus());
				details.setRecordType(afinanceMain.getRecordType());
				details.setVersion(afinanceMain.getVersion());
				details.setWorkflowId(afinanceMain.getWorkflowId());
				details.setTaskId(afinanceMain.getTaskId());
				details.setNextTaskId(afinanceMain.getNextTaskId());
				details.setRoleCode(afinanceMain.getRoleCode());
				details.setNextRoleCode(afinanceMain.getNextRoleCode());
				details.setNewRecord(financeDetail.isNewRecord());
				if (PennantConstants.RECORD_TYPE_DEL.equals(afinanceMain.getRecordType())) {
					if (StringUtils.trimToNull(details.getRecordType()) == null) {
						details.setRecordType(afinanceMain.getRecordType());
						details.setNewRecord(true);
					}
				}
			}

			auditHeader = save(finalOperation, auditHeader, afinanceMain.getRecordType());
		}

		return auditHeader;
	}

	private List<ServiceTask> getRemainingServiceTasks(WorkflowEngine engine, String taskId, FinanceMain afinanceMain,
			List<String> finishedTasks) {
		// changes regarding parallel work flow
		String nextRoleCode = StringUtils.trimToEmpty(afinanceMain.getNextRoleCode());
		String nextRoleCodes[] = nextRoleCode.split(",");
		if (nextRoleCodes.length > 1) {
			return new ArrayList<>();
		}

		List<ServiceTask> newTasks = engine.getServiceTasks(taskId, afinanceMain);
		List<ServiceTask> result = new ArrayList<>();

		for (ServiceTask newTask : newTasks) {
			if (!finishedTasks.contains(newTask.getOperation())) {
				result.add(newTask);
			}
		}

		return result;
	}

	/**
	 * Method for process and execute workflow service tasks
	 * 
	 * @param auditHeader
	 * @param task
	 * @return
	 * @throws Exception
	 */
	private AuditHeader execute(AuditHeader auditHeader, ServiceTask task, String role, WorkflowEngine engine,
			String operation) throws Exception {
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = financeDetail.getFinScheduleData().getFinanceMain();

		switch (task.getOperation()) {
		case PennantConstants.method_execSanctionExpData:
			afinanceMain.setSanctionedDate(SysParamUtil.getAppDate());
			break;
		case PennantConstants.method_doRevSanctionExpData:
			afinanceMain.setSanctionedDate(null);
			break;
		case PennantConstants.WF_CIBIL:
			creditInformation.getCreditEnquiryDetails(auditHeader, false);
			break;

		case PennantConstants.method_doDms:
			if (null != dmsIdentificationService && SysParamUtil.isAllowed(SMTParameterConstants.DMS_REQ)) {
				dmsIdentificationService.identifyExternalDocument(auditHeader);
			}
			break;
		case PennantConstants.method_doPrfHunter:
			if (null != profectusHunterBreService) {
				profectusHunterBreService.getOnlineMatchDetails(auditHeader);
			}
			break;
		case PennantConstants.method_doCheckDeviations:
			// Deviations Available? [doCheckDeviations]
			String[] nextTasks = StringUtils.trimToEmpty(afinanceMain.getNextTaskId()).split(";");

			if (nextTasks.length <= 1) {
				List<FinanceDeviations> list = new ArrayList<>();
				List<FinanceDeviations> autoDeviations = financeDetail.getFinanceDeviations();
				if (autoDeviations != null && !autoDeviations.isEmpty()) {
					list.addAll(autoDeviations);
				}
				List<FinanceDeviations> manualDeviations = financeDetail.getManualDeviations();
				if (manualDeviations != null && !manualDeviations.isEmpty()) {
					list.addAll(manualDeviations);
				}
				if (list != null && !list.isEmpty()) {
					boolean deviationfound = false;
					for (FinanceDeviations financeDeviations : list) {
						if (StringUtils.isBlank(financeDeviations.getApprovalStatus())
								|| PennantConstants.List_Select.equals(financeDeviations.getApprovalStatus())) {
							deviationfound = true;
							break;
						}
					}
					afinanceMain.setDeviationApproval(deviationfound);
				}
			}

			break;
		case PennantConstants.method_doCheckAuthLimit:
			/*
			 * if (!"Save".equals(afinanceMain.getRecordStatus()) && afinanceMain.getNextRoleCode().equals("")) {
			 * authorizationLimitService.validateFinanceAuthorizationLimit( auditHeader); }
			 */
			authorizationLimitService.validateFinanceAuthorizationLimit(auditHeader);

			break;
		// ### 01-05-2018 - Start - story #361(tuleap server) Manual Deviations
		case PennantConstants.METHOD_DO_CHECK_DEVIATION_APPROVAL:
			deviationDetailsService.doCheckDeviationApproval(auditHeader);

			break;
		// ### Query Management Validations
		case PennantConstants.METHOD_DO_VALIDATE_QUERYMGMT_APPROVAL:
			queryDetailService.getQueryMgmtList(auditHeader, task, role);
			break;
		// ### 01-05-2018 - End
		case PennantConstants.METHOD_UPDATE_ATTRIBUTE:
			Map<String, String> result = UpdateAttributeServiceTask.getAttributes(task.getParameters(), financeDetail);
			afinanceMain.addAttributes(result);

			break;
		case PennantConstants.METHOD_REVERT_QUEUE:
			String actor = userActivityLogDAO.getPreviousRole("FINANCE", afinanceMain.getFinReference(), role,
					task.getParameters());

			if (StringUtils.isEmpty(actor)) {
				throw new AppException("The workflow stage to revert not found.");
			}

			String nextTaskId = engine.getUserTaskId(actor);

			if (StringUtils.isNotEmpty(nextTaskId)) {
				nextTaskId += ";";
			}

			Map<String, String> nextRoles = ProcessUtil.getNextRoles(engine, nextTaskId);
			String nextRoleCode = StringUtils.join(nextRoles.keySet(), ",");

			afinanceMain.setTaskId(engine.getUserTaskId(role));
			afinanceMain.setNextTaskId(nextTaskId);
			afinanceMain.setRoleCode(role);
			afinanceMain.setNextRoleCode(nextRoleCode);

			String assignmentMthd = engine.getUserTask(engine.getUserTaskId(role)).getAssignmentLevel();
			afinanceMain.setLovDescAssignMthd(StringUtils.trimToEmpty(assignmentMthd));
			afinanceMain.setLovDescBaseRoleCodeMap(nextRoles);

			if (!nextRoleCode.contains(role)) {
				afinanceMain.setPriority(0);
				if (StringUtils.isBlank(afinanceMain.getLovDescAssignMthd())) {
					afinanceMain.setNextUserId(null);
				}
			}

			auditHeader = save(null, auditHeader, afinanceMain.getRecordType());
			auditHeader.setProcessCompleted(true);

			break;
		case "doHunterService":
			if (null != hunterService) {
				boolean hunterReq = SysParamUtil.isAllowed(SMTParameterConstants.HUNTER_REQ);
				if (hunterReq) {
					hunterService.getHunterStatus(auditHeader);
				}
			}
			break;
		case "executeBRE":
			if (extBreService != null) {
				extBreService.executeBRE(auditHeader);
			}
			break;
		case PennantConstants.METHOD_INTIATEHUNTERSERVICE:
			if (null != initiateHunterService) {
				boolean hunterReq = SysParamUtil.isAllowed(SMTParameterConstants.HUNTER_REQ);
				if (hunterReq) {
					initiateHunterService.getHunterResponse(financeDetail.getCustomerDetails());
				}
			}
			break;
		case PennantConstants.METHOD_DOMAINCHECKSERVICE:
			if (null != domainCheckService) {
				boolean domainCheq = SysParamUtil.isAllowed(SMTParameterConstants.DOMAIN_CHEQ);
				if (domainCheq) {
					domainCheckService.validateDomain(financeDetail);
				}
			}
			break;
		case PennantConstants.METHOD_DOCUMENTVERIFICATION:
			if (null != documentVerificationService) {
				boolean documentCheck = SysParamUtil
						.isAllowed(SMTParameterConstants.EXTERNAL_DOCUMENT_VERIFICATION_REQUIRED);
				if (documentCheck) {
					documentVerificationService.saveOrUpdateDocuments(financeDetail);
				}
			}
			break;
		case PennantConstants.METHOD_LOAN_DATA_SYNC:
			if (loanDataSyncService != null) {
				loanDataSyncService.executeDataSync(auditHeader);
			}
			break;
		// ST#12 - Push loan details to EFS
		case PennantConstants.method_pushToEFS:
			if (externalFinanceSystemService != null) {
				externalFinanceSystemService.createLoan(afinanceMain, operation);
			}
			break;
		default:
			// Execute any other custom service tasks
			if (StringUtils.isNotBlank(task.getOperation())) {
				boolean taskExecuted = customServiceTask.executeExternalServiceTask(auditHeader, task);
				if (taskExecuted) {
					return auditHeader;
				}
			}

			if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
				auditHeader = delete(auditHeader, false);
				auditHeader.setDeleteNotes(true);
			} else {
				auditHeader = saveOrUpdate(auditHeader, false);
				auditHeader.setProcessCompleted(true);
			}
			break;
		}

		return auditHeader;
	}

	/**
	 * Method for check weather the record is going forward or not for Query Management
	 * 
	 * @param financeMain
	 * @return
	 */
	public boolean isForwardCase(FinanceMain financeMain) {
		if (financeMain.getWorkflowId() == 0) {
			return true;
		}
		WorkflowEngine engine = new WorkflowEngine(
				WorkFlowUtil.getWorkflow(financeMain.getWorkflowId()).getWorkFlowXml());

		if (engine.compareTo(financeMain.getTaskId(), financeMain.getNextTaskId().replace(";", "")) == Flow.SUCCESSOR) {
			return true;
		}
		return false;
	}

	private void setNextTaskDetails(String taskId, FinanceMain financeMain, WorkflowEngine engine, String action,
			String role) {
		logger.trace(Literal.ENTERING);

		String nextTaskId = "";
		if (Action.REVERT.getLabel().equals(action)) {
			String actor = userActivityLogDAO.getPreviousRole("FINANCE", financeMain.getFinReference(), role, null);

			if (StringUtils.isEmpty(actor)) {
				throw new AppException("The workflow stage to revert not found.");
			}

			nextTaskId = engine.getUserTaskId(actor);

			if (StringUtils.isNotEmpty(nextTaskId)) {
				nextTaskId += ";";
			}
		} else {
			nextTaskId = ProcessUtil.getNextTask(engine, taskId, action, financeMain.getNextTaskId(), financeMain);
		}

		Map<String, String> nextRoles = ProcessUtil.getNextRoles(engine, nextTaskId);
		String nextRoleCode = StringUtils.join(nextRoles.keySet(), ",");

		// Clear the base roles if the action is "Resubmit".
		if ("Resubmit".equals(action)) {
			for (Entry<String, String> entry : nextRoles.entrySet()) {
				entry.setValue("");
			}
		}

		// Set the work-flow details.
		financeMain.setTaskId(taskId);
		financeMain.setNextTaskId(nextTaskId);
		financeMain.setRoleCode(role);
		financeMain.setNextRoleCode(nextRoleCode);

		String assignmentMthd = engine.getUserTask(taskId).getAssignmentLevel();
		financeMain.setLovDescAssignMthd(StringUtils.trimToEmpty(assignmentMthd));
		financeMain.setLovDescBaseRoleCodeMap(nextRoles);

		if (!nextRoleCode.contains(role)) {
			financeMain.setPriority(0);
			if (StringUtils.isBlank(financeMain.getLovDescAssignMthd())) {
				financeMain.setNextUserId(null);
			}
		}

		logger.trace(Literal.LEAVING);
	}

	private AuditHeader save(String operation, AuditHeader auditHeader, String recordType) {
		logger.trace(Literal.ENTERING);

		switch (Operation.methodOf(operation)) {
		case DEFAULT:
			if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
				auditHeader = delete(auditHeader, false);
				auditHeader.setDeleteNotes(true);
			} else {
				auditHeader = saveOrUpdate(auditHeader, false);
				auditHeader.setProcessCompleted(true);
			}

			break;
		case APPROVE:
			auditHeader = doApprove(auditHeader, false);
			auditHeader.setProcessCompleted(true);
			if (recordType.equals(PennantConstants.RECORD_TYPE_DEL)) {
				auditHeader.setDeleteNotes(true);
			}

			break;
		case PRE_APPROVE:
			auditHeader = doPreApprove(auditHeader, false);
			auditHeader.setProcessCompleted(true);
			if (recordType.equals(PennantConstants.RECORD_TYPE_DEL)) {
				auditHeader.setDeleteNotes(true);
			}

			break;
		case REJECT:
			auditHeader = doReject(auditHeader, false, false);
			auditHeader.setProcessCompleted(true);
			if (recordType.equals(PennantConstants.RECORD_TYPE_NEW)) {
				auditHeader.setDeleteNotes(true);
			}

			break;
		}

		logger.trace(Literal.LEAVING);
		return auditHeader;
	}

	private FinanceDetail createOrUpdateCrmCustomer(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		if (crm == null) {
			return financeDetail;
		}
		CustomerDetails customerDetails = financeDetail.getCustomerDetails();

		String[] errorParm = new String[2];
		errorParm[0] = "Customer";

		try {
			// begin 09-05-18
			if (!"Y".equalsIgnoreCase((String) SysParamUtil.getValue("GCD_FINONE_PROC_REQD"))) {
				customerDetails.setReturnStatus(new WSReturnStatus());
				customerDetails.getReturnStatus().setReturnCode("0000");
				return financeDetail;
			}
			// end
			customerService.prepareGCDCustomerData(customerDetails);
			crm.create(customerDetails);
		} catch (Exception e) {
			logger.debug(e.getMessage());
		}

		logger.debug(Literal.LEAVING);

		return financeDetail;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using financeMainDAO.delete with parameters
	 * financeMain,"" b) NEW Add new record in to main table by using financeMainDAO.save with parameters financeMain,""
	 * c) EDIT Update record in the main table by using financeMainDAO.update with parameters financeMain,"" 3) Delete
	 * the record from the workFlow table by using financeMainDAO.delete with parameters financeMain,"_Temp" 4) Audit
	 * the record in to AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) for Work flow 5)
	 * Audit the record in to AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) based on the
	 * transaction Type.
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 */
	@Override
	public AuditHeader doPreApprove(AuditHeader aAuditHeader, boolean isWIF) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		String roleCode = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();

		FinanceDetail fd = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
		fd.setValidateUpfrontFees(true);
		aAuditHeader = businessValidation(aAuditHeader, "doApprove", isWIF, false);
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		Date appData = SysParamUtil.getAppDate();

		// Execute Accounting Details Process
		// =======================================
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		String moduleDefiner = fd.getModuleDefiner();
		String auditTranType = auditHeader.getAuditTranType();
		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(fd);

		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : serviceInstructions) {
			serviceUID = finServInst.getInstructionUID();
		}

		if (fm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			fm.setFinApprovedDate(appData);

			if (fm.getFinContractDate() == null) {
				fm.setFinContractDate(fm.getFinStartDate());
			}

			if (fm.getFeeChargeAmt() == null) {
				fm.setFeeChargeAmt(BigDecimal.ZERO);
			}
		}

		// Validation Checking for All Finance Detail data
		// =======================================
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		String preApprovalTableType = PennantConstants.PREAPPROVAL_TABLE_TYPE;

		List<FinFeeDetail> finFeeDetailActuals = schdData.getFinFeeDetailList();

		if (fm.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			financeMainDAO.delete(fm, TableType.MAIN_TAB, isWIF, true);
			listDeletion(schdData, moduleDefiner, "", isWIF);

			// Step Details Deletion
			// =======================================
			financeStepDetailDAO.deleteList(finID, isWIF, "_Temp");

			if (!isWIF) {
				// Additional Field Details Deletion
				// =======================================
				doDeleteAddlFieldDetails(fd, "");
				auditDetails.addAll(jointGuarantorDeletion(fd, "", tranType));
				auditDetails.addAll(checkListDetailService.delete(fd, "", auditTranType));
			}

		} else {
			roleCode = fm.getRoleCode();
			fm.setRcdMaintainSts("");
			fm.setRoleCode("");
			fm.setNextRoleCode("");
			fm.setTaskId("");
			fm.setNextTaskId("");
			fm.setNextUserId(null);
			fm.setWorkflowId(0);

			if (fm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {

				tranType = PennantConstants.TRAN_ADD;
				fm.setRecordType("");
				financeMainDAO.save(fm, TableType.PRE_APPR_TAB, isWIF);

				// Schedule Details
				// =======================================
				listSave(schdData, preApprovalTableType, isWIF, 0, serviceUID);

				// Save Finance Step Policy Details
				// =======================================
				if (fm.isStepFinance()) {
					saveStepDetailList(schdData, isWIF, preApprovalTableType);
				}

				// Fee Charge Details
				// =======================================
				saveFeeChargeList(schdData, moduleDefiner, isWIF, preApprovalTableType);
			} else {

				tranType = PennantConstants.TRAN_UPD;
				fm.setRecordType("");
				financeMainDAO.update(fm, TableType.MAIN_TAB, isWIF);

				if (!isWIF) {

					// Fetch Existing data before Modification
					FinScheduleData oldFinSchdData = null;
					if (schdData.getFinanceMain().isScheduleRegenerated()) {
						oldFinSchdData = getFinSchDataByFinRef(finID, "", -1);
						oldFinSchdData.setFinID(schdData.getFinID());
						oldFinSchdData.setFinReference(schdData.getFinReference());
					}

					// Create log entry for Action for Schedule Modification
					// =======================================
					FinLogEntryDetail entryDetail = new FinLogEntryDetail();
					entryDetail.setFinID(schdData.getFinID());
					entryDetail.setFinReference(schdData.getFinReference());
					entryDetail.setEventAction(fd.getAccountingEventCode());
					entryDetail.setSchdlRecal(schdData.getFinanceMain().isScheduleRegenerated());
					entryDetail.setPostDate(appData);
					entryDetail.setReversalCompleted(false);
					long logKey = finLogEntryDetailDAO.save(entryDetail);

					// Save Schedule Details For Future Modifications
					if (schdData.getFinanceMain().isScheduleRegenerated()) {
						listSave(oldFinSchdData, "_Log", isWIF, logKey, serviceUID);
					}
				}

				// ScheduleDetails delete and save
				// =======================================
				listDeletion(schdData, moduleDefiner, "", isWIF);
				listSave(schdData, "", isWIF, 0, serviceUID);
			}

			if (!fd.isExtSource() && !isWIF) {

				// Asset Details Process
				// =======================================

				// Save Document Details
				// =======================================
				if (fd.getDocumentDetailsList() != null && fd.getDocumentDetailsList().size() > 0) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("DocumentDetails");
					details = processingDocumentDetailsList(details, preApprovalTableType, schdData.getFinanceMain(),
							moduleDefiner, serviceUID);
					auditDetails.addAll(details);
				}

				// set Check list details Audit
				// =======================================
				if (fd.getFinanceCheckList() != null && !fd.getFinanceCheckList().isEmpty()) {
					auditDetails.addAll(checkListDetailService.doApprove(fd, preApprovalTableType, serviceUID));
				}

				// set the Audit Details & Save / Update Guarantor Details
				// =======================================
				if (fd.getGurantorsDetailList() != null && !fd.getGurantorsDetailList().isEmpty()) {
					auditDetails.addAll(guarantorDetailService.doApprove(fd.getGurantorsDetailList(),
							preApprovalTableType, tranType, "", auditHeader.getApiHeader(), fm.getServiceName()));
				}

				// set the Audit Details & Save / Update JointAccount Details
				// =======================================
				if (fd.getJointAccountDetailList() != null && !fd.getJointAccountDetailList().isEmpty()) {
					auditDetails.addAll(jointAccountDetailService.doApprove(fd.getJointAccountDetailList(),
							preApprovalTableType, tranType, "", auditHeader.getApiHeader(), fm.getServiceName()));
				}

				// set Finance Collateral Details Audit
				// =======================================
				if (fd.getFinanceCollaterals() != null && !fd.getFinanceCollaterals().isEmpty()) {
					auditDetails.addAll(finCollateralService.doApprove(fd.getFinanceCollaterals(), preApprovalTableType,
							tranType, "", auditHeader.getApiHeader(), fm.getServiceName()));
				}

				// Finance Eligibility Rule Details
				// =======================================
				auditDetails.addAll(eligibilityDetailService.saveOrUpdate(fd));
				// move to pre-approval table
				List<FinanceEligibilityDetail> list = eligibilityDetailService.getFinElgDetailList(finID);
				if (list != null && !list.isEmpty()) {
					eligibilityDetailService.saveList(list, preApprovalTableType);
					eligibilityDetailService.deleteByFinRef(finID);
				}

				// Finance Scoring Module Details List Saving
				// =======================================
				auditDetails.addAll(scoringDetailService.saveOrUpdate(fd));
				// move to pre-approval table
				List<FinanceScoreHeader> hearlist = scoringDetailService.getFinScoreHeaderList(finReference, "");
				if (hearlist != null && !hearlist.isEmpty()) {
					List<Long> headerIds = new ArrayList<Long>();
					for (FinanceScoreHeader header : hearlist) {
						headerIds.add(header.getHeaderId());
						scoringDetailService.saveHeader(header, preApprovalTableType);
					}
					List<FinanceScoreDetail> detailslist = scoringDetailService.getFinScoreDetailList(headerIds, "");
					scoringDetailService.saveDetailList(detailslist, preApprovalTableType);
					// delete from maintable

					scoringDetailService.deleteDetailList(headerIds, "");
					scoringDetailService.deleteHeaderList(finID, "");

				}

				// Dedup Details
				// =======================================
				auditDetails.addAll(saveDedupDetails(fd));
				moveDedupsPreApproval(finReference, preApprovalTableType);

				// Advance Payment Details
				// =======================================
				if (fd.getAdvancePaymentsList() != null) {
					finAdvancePaymentsService.doApprove(fd.getAdvancePaymentsList(), preApprovalTableType, tranType,
							fd.isDisbStp());
				}

				// Covenant Type Details
				// =======================================
				if (fd.getCovenantTypeList() != null) {
					finCovenantTypeService.doApprove(fd.getCovenantTypeList(), preApprovalTableType, tranType);
				}

				List<Covenant> covenants = fd.getCovenants();
				if (ImplementationConstants.COVENANT_MODULE_NEW && CollectionUtils.isNotEmpty(covenants)) {
					int docSize = 0;
					if (CollectionUtils.isNotEmpty(fd.getDocumentDetailsList())) {
						docSize = fd.getDocumentDetailsList().size();
					}
					covenantsService.doApprove(covenants, TableType.PRE_APPR_TAB, tranType, docSize);
				}

				List<FinOption> finOptions = fd.getFinOptions();
				if (CollectionUtils.isNotEmpty(finOptions)) {
					finOptionService.doApprove(finOptions, TableType.PRE_APPR_TAB, tranType);
				}

				// set Finance Collateral Details Audit
				// =======================================
				if (fd.getCollateralAssignmentList() != null && !fd.getCollateralAssignmentList().isEmpty()) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("CollateralAssignments");
					details = processingCollateralAssignmentList(details, preApprovalTableType,
							schdData.getFinanceMain());
					auditDetails.addAll(details);
				}

				if (fd.getFinAssetTypesList() != null && !fd.getFinAssetTypesList().isEmpty()) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("FinAssetTypes");
					details = processingFinAssetTypesList(details, preApprovalTableType, schdData.getFinanceMain());
					auditDetails.addAll(details);
				}
			}
			// Finance Fee Details
			if (!fd.isExtSource()) {
				if (schdData.getFinFeeDetailList() != null) {
					for (FinFeeDetail fee : finFeeDetailActuals) {
						fee.setInstructionUID(serviceUID);
					}

					finFeeDetailService.doApprove(finFeeDetailActuals, preApprovalTableType, tranType, isWIF);
				}
			}
		}

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());

		if (!fd.isExtSource()) {

			// ScheduleDetails delete
			// =======================================
			listDeletion(schdData, moduleDefiner, "_Temp", isWIF);

			if (!isWIF) {
				// Additional Field Details Deletion in _Temp Table
				// =======================================
				doDeleteAddlFieldDetails(fd, "_Temp");

				auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
				auditDetailList.addAll(checkListDetailService.delete(fd, "_Temp", auditTranType));

				if (fd.getAdvancePaymentsList() != null) {
					auditDetails.addAll(
							finAdvancePaymentsService.delete(fd.getAdvancePaymentsList(), "_Temp", auditTranType));
				}

				List<FinCovenantType> finCovenantTypes = fd.getCovenantTypeList();
				if (CollectionUtils.isNotEmpty(finCovenantTypes)) {
					// auditDetails.addAll(finCovenantTypeService.delete(finCovenantTypes,
					// "_Temp", auditTranType));
					finCovenantTypeService.delete(finCovenantTypes, "_Temp", auditTranType);
				}

				List<Covenant> covenants = fd.getCovenants();
				if (ImplementationConstants.COVENANT_MODULE_NEW && CollectionUtils.isNotEmpty(covenants)) {
					// auditDetails.addAll(covenantsService.delete(covenants,
					// TableType.TEMP_TAB, auditTranType));
					covenantsService.delete(covenants, TableType.TEMP_TAB, auditTranType);
				}

				List<DocumentDetails> documents = fd.getDocumentDetailsList();
				if (!fd.isExtSource() && !isWIF) {
					if (CollectionUtils.isNotEmpty(documents)) {
						listDocDeletion(fd, "_Temp");
					}
				}

				List<FinOption> finOptions = fd.getFinOptions();
				if (CollectionUtils.isNotEmpty(finOptions)) {
					auditDetails.addAll(finOptionService.delete(finOptions, TableType.TEMP_TAB, auditTranType));
				}
				/*
				 * //Deleting FinancailSummary RisksAndMitigants Details List<RisksAndMitigants> risksAndMitigants =
				 * financeDetail.getRisksAndMitigantsList(); if (CollectionUtils.isNotEmpty(risksAndMitigants)) {
				 * auditDetails.addAll(risksAndMitigantsService.delete( risksAndMitigants, TableType.TEMP_TAB,
				 * auditTranType)); } //Deleting FinancailSummary SanctionConditions Details List<SanctionConditions>
				 * sanctionConditions = financeDetail.getSanctionDetailsList(); if
				 * (CollectionUtils.isNotEmpty(sanctionConditions)) {
				 * auditDetails.addAll(sanctionConditionsService.delete( sanctionConditions, TableType.TEMP_TAB,
				 * auditTranType)); } //Deleting FinancailSummary DealRecommendationMerits Details
				 * List<DealRecommendationMerits> dealRecommendationMerits =
				 * financeDetail.getDealRecommendationMeritsDetailsList(); if
				 * (CollectionUtils.isNotEmpty(dealRecommendationMerits)) {
				 * auditDetails.addAll(dealRecommendationMeritsService.delete( dealRecommendationMerits,
				 * TableType.TEMP_TAB, auditTranType)); } //Deleting FinancailSummary DueDiligence Details
				 * List<DueDiligenceDetails> dueDiligenceDetails = financeDetail.getDueDiligenceDetailsList(); if
				 * (CollectionUtils.isNotEmpty(dueDiligenceDetails)) {
				 * auditDetails.addAll(dueDiligenceDetailsService.delete( dueDiligenceDetails, TableType.TEMP_TAB,
				 * auditTranType)); } //Deleting FinancailSummary DealRecommendationMerits Details
				 * 
				 * 
				 * //Deleting FinancailSummary Synopsis Details if (financeDetail.getSynopsisDetails() != null) {
				 * auditDetailList.add(synopsisDetailsService.delete( financeDetail.getSynopsisDetails(),
				 * TableType.TEMP_TAB, auditTranType)); }
				 */

				// Collateral assignment Details
				if (fd.getCollateralAssignmentList() != null && !fd.getCollateralAssignmentList().isEmpty()) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("CollateralAssignments");
					auditDetails.addAll(details);
					collateralAssignmentDAO.deleteByReference(finReference, "_Temp");
				}

				// FinAssetTypes details
				if (fd.getFinAssetTypesList() != null && !fd.getFinAssetTypesList().isEmpty()) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("FinAssetTypes");
					finAssetTypeDAO.deleteByReference(finReference, "_Temp");
					auditDetails.addAll(details);
				}

				// AssetType Extended field Details
				if (fd.getExtendedFieldRenderList() != null && fd.getExtendedFieldRenderList().size() > 0) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldDetails");

					List<String> tableNames = new ArrayList<>();
					for (int i = 0; i < details.size(); i++) {
						ExtendedFieldRender asset = (ExtendedFieldRender) details.get(i).getModelData();
						details.get(i).setExtended(true);
						if (tableNames.contains(asset.getTypeCode())) {
							continue;
						}
						tableNames.add(asset.getTypeCode());

						// Table Name identification
						StringBuilder tableName = new StringBuilder();
						tableName.append(AssetConstants.EXTENDEDFIELDS_MODULE);
						tableName.append("_");
						tableName.append(asset.getTypeCode());
						tableName.append("_ED");

						// Records Deletion from Table
						extendedFieldRenderDAO.deleteList(finReference, tableName.toString(), "_Temp");
					}
					auditDetails.addAll(details);
				}

				// Vas Recording Details details
				if (schdData.getVasRecordingList() != null && !schdData.getVasRecordingList().isEmpty()) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("VasRecordings");
					vasRecordingDAO.deleteByPrimaryLinkRef(finReference, "_Temp");
					auditDetails.addAll(details);

					// Vas Recording Extended field Details
					List<AuditDetail> vasExtDetails = fd.getAuditDetailMap().get("VasExtendedDetails");

					List<String> tableNames = new ArrayList<>();
					for (int i = 0; i < vasExtDetails.size(); i++) {
						ExtendedFieldRender vas = (ExtendedFieldRender) vasExtDetails.get(i).getModelData();
						vasExtDetails.get(i).setExtended(true);
						if (tableNames.contains(vas.getTypeCode())) {
							continue;
						}
						tableNames.add(vas.getTypeCode());

						// Table Name identification
						StringBuilder tableName = new StringBuilder();
						tableName.append(VASConsatnts.MODULE_NAME);
						tableName.append("_");
						tableName.append(vas.getTypeCode());
						tableName.append("_ED");

						// Records Deletion from Table
						extendedFieldRenderDAO.deleteList(vas.getReference(), tableName.toString(), "_Temp");
					}
					auditDetails.addAll(vasExtDetails);
				}
			}

			if (schdData.getFinFeeDetailList() != null) {
				auditDetails.addAll(finFeeDetailService.delete(finFeeDetailActuals, "_Temp", auditTranType, isWIF));
			}

			// Step Details Deletion
			// =======================================
			financeStepDetailDAO.deleteList(finID, isWIF, "_Temp");

			// Finance Main Details
			// =======================================
			financeMainDAO.delete(fm, TableType.TEMP_TAB, isWIF, true);

			FinanceDetail tempfinanceDetail = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
			FinanceMain tempfinanceMain = tempfinanceDetail.getFinScheduleData().getFinanceMain();
			auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
					tempfinanceMain.getBefImage(), tempfinanceMain));
			auditHeader.setAuditDetails(auditDetailList);

			// Adding audit as deleted from Temp table
			if (!isWIF) {
				auditHeaderDAO.addAudit(auditHeader);
			}
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(new AuditDetail(auditTranType, 1, fields[0], fields[1], fm.getBefImage(), fm));
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));

		// Adding audit as Insert/Update/deleted into main table
		if (!isWIF) {
			auditHeaderDAO.addAudit(auditHeader);
		}

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(fd);

		// Save Finance Schedule Snapshot
		// ===============================
		// TODO commented below line which is leading to column miss match
		// exception
		// financeMainDAO.saveFinanceSnapshot(financeMain);

		// Update Task_log and Task_Owners tables
		// =======================================
		fm.setRoleCode(roleCode);
		updateTaskLog(fm, false);

		// TODO: confirm limits is required for pre approve
		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	private List<FinServiceInstruction> getServiceInstructions(FinanceDetail fd) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		List<FinServiceInstruction> serviceInstructions = schdData.getFinServiceInstructions();

		if (CollectionUtils.isEmpty(serviceInstructions)) {
			FinServiceInstruction finServInst = new FinServiceInstruction();
			finServInst.setFinID(fm.getFinID());
			finServInst.setFinReference(fm.getFinReference());
			finServInst.setFinEvent(fd.getModuleDefiner());

			schdData.setFinServiceInstruction(finServInst);
		}

		for (FinServiceInstruction serviceInstruction : serviceInstructions) {
			if (serviceInstruction.getInstructionUID() == Long.MIN_VALUE) {
				serviceInstruction.setInstructionUID(Long.valueOf(ReferenceGenerator.generateNewServiceUID()));
			}

			if (StringUtils.isEmpty(fd.getModuleDefiner())
					|| StringUtils.equals(fd.getModuleDefiner(), FinServiceEvent.ORG)) {
				serviceInstruction.setFinEvent(FinServiceEvent.ORG);

				if (!StringUtils.equals(serviceInstruction.getFinEvent(), FinServiceEvent.ORG)
						&& !StringUtils.contains(serviceInstruction.getFinEvent(), "_O")) {
					serviceInstruction.setFinEvent(serviceInstruction.getFinEvent().concat("_O"));
				}
			}
		}

		return schdData.getFinServiceInstructions();
	}

	/**
	 * @param finReference
	 * @param type
	 */
	private void moveDedupsPreApproval(String finReference, String type) {
		logger.debug(" Entering ");
		blacklistCustomerDAO.moveData(finReference, type);
		financeDedupeDAO.moveData(finReference, type);
		customerDedupDAO.moveData(finReference, type);
		logger.debug(" Leaving ");
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using financeMainDAO.delete with parameters financeMain,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 * @throws JaxenException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	@SuppressWarnings("unchecked")
	@Override
	public AuditHeader doReject(AuditHeader auditHeader, boolean isWIF, boolean isAutoReject) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<>();
		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();
		EventProperties eventProperties = fm.getEventProperties();

		fd.setValidateUpfrontFees(false);
		boolean apiCall = false;
		if (PennantConstants.FINSOURCE_ID_API.equals(fm.getFinSourceID())) {
			apiCall = true;
		}

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		if (isAutoReject) {
			fm = financeMainDAO.getFinanceMainById(finID, "_Temp", false);

			// TODO for API Fix me Issue in Reject Loan
			if (apiCall) {
				if (fm == null) {
					fm = fd.getFinScheduleData().getFinanceMain();
				}
				fm.setFinSourceID(PennantConstants.FINSOURCE_ID_API);
			}

			fm.setRecordStatus(PennantConstants.RCD_STATUS_REJECTED);
			fm.setNextTaskId("");
			fm.setNextRoleCode("");
			fm.setEventProperties(eventProperties);

			FinScheduleData schd = new FinScheduleData();
			schd.setFinanceMain(fm);

			schd.setFinReference(finReference);
			schd.setFinID(finID);

			fd.setFinScheduleData(schd);
			fd.setModuleDefiner(FinServiceEvent.ORG);
			fd = getAutoRejDetails(fd, "_Temp", false);
		}
		auditHeader = businessValidation(auditHeader, "doReject", isWIF, isAutoReject);
		if (!isAutoReject) {
			if (!auditHeader.isNextProcess()) {
				logger.debug(Literal.LEAVING);
				return auditHeader;
			}
		}

		// PSD #139669 - Rejection of Loan under loan queue gives 900 error
		fm.setFinIsActive(false);
		FinanceMain financeMainAvl = financeMainDAO.getFinanceMainById(finID, "_Temp", false);
		fm.setLastMntOn(new Timestamp(SysParamUtil.getAppDate().getTime()));
		if (fm.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW) && financeMainAvl != null) {
			financeMainDAO.updateRejectFinanceMain(fm, TableType.TEMP_TAB, isWIF);
		}

		// Cancel All Transactions done by Finance Reference
		// =======================================
		AccountingEngine.cancelStageAccounting(finID, fd.getModuleDefiner());

		String recordStatus = fm.getRecordStatus();
		if (FinServiceEvent.ORG.equals(fd.getModuleDefiner())) {
			postingsPreparationUtil.postReveralsExceptFeePay(finReference);

			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			String[] fieldsArray = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fieldsArray[0],
					fieldsArray[1], fm.getBefImage(), fm));

			// Saving the reasons
			saveReasonDetails(fd);

			financeMainDAO.saveRejectFinanace(fm);

			// Update Task_log and Task_Owners tables
			// =======================================
			updateTaskLog(fm, false);

			if (StringUtils.isEmpty(fm.getRcdMaintainSts())) {
				if (ImplementationConstants.LIMIT_INTERNAL) {
					limitManagement.processLoanLimitOrgination(fd, false, LimitConstants.UNBLOCK, false);
				} else {
					limitCheckDetails.doProcessLimits(fm, FinanceConstants.CANCEL_RESERVE);
				}
			} else if (FinServiceEvent.ADDDISB.equals(fm.getRcdMaintainSts())) {
				if (ImplementationConstants.LIMIT_INTERNAL) {
					limitManagement.processLoanDisbursments(fd, false, LimitConstants.CANCIL, false);
				}
			}

			// Variable OD Details
			this.variableOverdraftSchdService.doReject(fd);

			if ((StringUtils.containsIgnoreCase(recordStatus, "Reject")
					|| StringUtils.containsIgnoreCase(recordStatus, "Cancel")
					|| StringUtils.containsIgnoreCase(recordStatus, "Rejected"))) {
				TaskOwners taskOwner = new TaskOwners();
				taskOwner.setReference(finReference);
				taskOwner.setProcessed(true);
				taskOwner.setRoleCode(fm.getRoleCode());
				taskOwnersDAO.updateTaskOwner(taskOwner, true);
				if (!isWIF) {
					auditHeaderDAO.addAudit(auditHeader);
				}
				rejectChildLoan(finReference);
				return auditHeader;
			}

		}

		// Save Finance Details Data on Reject Tables
		// =======================================
		if (!isWIF && StringUtils.equals(fm.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			financeMainDAO.saveRejectFinanceDetails(fm);
		}

		// OverDraft Schedule Details Deletion
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, fm.getProductCategory())
				&& fd.getFinScheduleData().getOverdraftScheduleDetails().size() > 0) {
			overdraftScheduleDetailDAO.deleteByFinReference(finID, "_Temp", isWIF);
		}

		// Finance Details deletion
		// =======================================
		listDeletion(fd.getFinScheduleData(), fd.getModuleDefiner(), "_Temp", isWIF);
		finServiceInstructionDAO.deleteList(finID, fd.getModuleDefiner(), "_Temp");

		// Document Details
		// =======================================
		if (fd.getDocumentDetailsList() != null && !fd.getDocumentDetailsList().isEmpty()) {
			documentDetailsDAO.deleteList(fd.getDocumentDetailsList(), "_Temp");
		}

		if (!isWIF) {
			// Additional Field Details Deletion
			// =======================================
			doDeleteAddlFieldDetails(fd, "_Temp");
		}

		// Plan EMI Holiday Details Deletion, if exists on Old image
		// =======================================
		FinanceMain befFinMain = fd.getFinScheduleData().getFinanceMain().getBefImage();
		if (befFinMain != null && befFinMain.isPlanEMIHAlw()) {
			if (StringUtils.equals(befFinMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				finPlanEmiHolidayDAO.deletePlanEMIHMonths(finID, "_Temp");
			} else if (StringUtils.equals(befFinMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
				finPlanEmiHolidayDAO.deletePlanEMIHDates(finID, "_Temp");
			}
		}

		// Save Collateral setup Details
		List<CollateralSetup> collateralSetupList = fd.getCollaterals();
		if (collateralSetupList != null && !collateralSetupList.isEmpty()) {
			List<AuditDetail> details = collateralSetupService.processCollateralSetupList(auditHeader, "doReject");
			if (details != null) {
				auditDetails.addAll(details);
			}
		}

		// Fin OCR Details
		// =========================
		if (fm.isFinOcrRequired() && fd.getFinOCRHeader() != null && finOCRHeaderService != null) {
			auditDetails.addAll(finOCRHeaderService.processFinOCRHeader(auditHeader, "doReject"));
		}

		// Legal Details
		List<LegalDetail> legalDetailsList = fd.getLegalDetailsList();
		if (CollectionUtils.isNotEmpty(legalDetailsList)) {
			List<AuditDetail> details = legalDetailService.processLegalDetails(auditHeader, "doReject");
			if (details != null) {
				auditDetails.addAll(details);
			}
		}

		if (fm.isLegalRequired()) {
			legalDetailService.deleteList(finReference, TableType.TEMP_TAB);
		}

		// Cheque Details
		if (fd.getChequeHeader() != null) {
			String[] fields = PennantJavaUtil.getFieldDetails(new ChequeHeader(),
					fd.getChequeHeader().getExcludeFields());
			finChequeHeaderService.doReject(auditHeader);
			auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
					fd.getChequeHeader().getBefImage(), fd.getChequeHeader()));
		}

		// Delete Finance IRR values
		deleteFinIRR(finID, TableType.TEMP_TAB);

		// Delete Tax Details
		if (fd.getFinanceTaxDetail() != null) {
			FinanceTaxDetail tempTaxDetail = financeTaxDetailDAO.getFinanceTaxDetail(finID, "_TView");
			if (tempTaxDetail != null) {
				financeTaxDetailDAO.delete(fd.getFinanceTaxDetail(), TableType.TEMP_TAB);
			}
		}

		// Finance Main Details Deletion
		// =======================================
		FinanceMain dbFinanceMain = getFinanceMain(finID, "_Temp");
		if (null != dbFinanceMain) {
			financeMainDAO.delete(fm, TableType.TEMP_TAB, isWIF, isAutoReject);
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), fm.getExcludeFields());
		auditHeader.setAuditDetail(
				new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1], fm.getBefImage(), fm));

		// Step Details
		// =======================================
		financeStepDetailDAO.deleteList(finID, isWIF, "_Temp");

		if (CollectionUtils.isNotEmpty(fd.getFinScheduleData().getStepPolicyDetails()) && !isWIF) {
			auditDetails.addAll(fd.getAuditDetailMap().get("FinanceStepPolicyDetail"));
		}

		// Asset deletion
		if (!isWIF) {

			// Finance Eligibility Rule Details
			// =======================================
			List<FinanceEligibilityDetail> elgList = eligibilityDetailService.getFinElgDetailList(finID);
			FinanceEligibilityDetail eligibilityDetail = new FinanceEligibilityDetail();
			String[] elgFields = PennantJavaUtil.getFieldDetails(eligibilityDetail,
					eligibilityDetail.getExcludeFields());
			for (int i = 0; i < elgList.size(); i++) {
				elgList.get(i).setLastMntBy(fm.getLastMntBy());
				elgList.get(i).setLastMntOn(fm.getLastMntOn());
				elgList.get(i).setRoleCode(fm.getRoleCode());
				elgList.get(i).setRecordStatus(recordStatus);
				auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), i + 1, elgFields[0], elgFields[1],
						null, elgList.get(i)));
			}
			eligibilityDetailService.deleteByFinRef(finID);

			// Finance Scoring Module Details List Saving
			// =======================================
			List<Object> scoreObjectList = scoringDetailService.getFinScoreDetailList(finReference);

			// Finance Score Headers
			if (scoreObjectList != null) {
				List<FinanceScoreHeader> headerList = (List<FinanceScoreHeader>) scoreObjectList.get(0);
				FinanceScoreHeader tempHeader = new FinanceScoreHeader();
				String[] headerFields = PennantJavaUtil.getFieldDetails(tempHeader, tempHeader.getExcludeFields());
				tempHeader = null;
				List<Long> headerIdList = new ArrayList<Long>();
				for (int i = 0; i < headerList.size(); i++) {
					headerIdList.add(headerList.get(i).getHeaderId());
					headerList.get(i).setLastMntBy(fm.getLastMntBy());
					headerList.get(i).setRoleCode(fm.getRoleCode());
					headerList.get(i).setRecordStatus(recordStatus);
					auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), i + 1, headerFields[0],
							headerFields[1], null, headerList.get(i)));
				}

				Map<Long, List<FinanceScoreDetail>> scoreMap = (Map<Long, List<FinanceScoreDetail>>) scoreObjectList
						.get(1);
				List<FinanceScoreDetail> detailList = new ArrayList<FinanceScoreDetail>();
				for (int i = 0; i < headerList.size(); i++) {
					detailList.addAll((Collection<FinanceScoreDetail>) scoreMap.get(headerIdList.get(i)));
				}
				FinanceScoreDetail tempDetail = new FinanceScoreDetail();
				String[] detailfields = PennantJavaUtil.getFieldDetails(tempDetail, tempDetail.getExcludeFields());
				tempDetail = null;

				for (int i = 0; i < detailList.size(); i++) {
					detailList.get(i).setLastMntBy(fm.getLastMntBy());
					detailList.get(i).setRoleCode(fm.getRoleCode());
					detailList.get(i).setRecordStatus(recordStatus);
					auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), i + 1, detailfields[0],
							detailfields[1], null, detailList.get(i)));
				}

				// Deletion of Scoring Details
				/*
				 * scoringDetailService.deleteHeaderList(finReference, "");
				 * scoringDetailService.deleteDetailList(headerIdList, "");
				 */
			}

			// Delete Black List Customer Data
			// =======================================
			List<FinBlacklistCustomer> blackListData = blacklistCustomerDAO.fetchFinBlackList(finID);
			FinBlacklistCustomer blData = new FinBlacklistCustomer();
			String[] blFields = PennantJavaUtil.getFieldDetails(blData, blData.getExcludeFields());
			for (int i = 0; i < blackListData.size(); i++) {
				blackListData.get(i).setLastMntBy(fm.getLastMntBy());
				blackListData.get(i).setRoleCode(fm.getRoleCode());
				blackListData.get(i).setRecordStatus(recordStatus);
				auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), i + 1, blFields[0], blFields[1], null,
						blackListData.get(i)));
			}
			// blacklistCustomerDAO.deleteList(finReference);

			// Delete Finance DeDup List Data
			// =======================================
			// financeDedupeDAO.deleteList(finReference);

			auditDetails.addAll(jointGuarantorDeletion(fd, "_Temp", auditHeader.getAuditTranType()));
			auditDetails.addAll(checkListDetailService.delete(fd, "_Temp", auditHeader.getAuditTranType()));
			if (fd.getAdvancePaymentsList() != null) {
				auditDetails.addAll(finAdvancePaymentsService.delete(fd.getAdvancePaymentsList(), "_Temp",
						auditHeader.getAuditTranType()));
			}

			String auditTranType = auditHeader.getAuditTranType();

			List<FinCovenantType> finCovenantTyps = fd.getCovenantTypeList();
			if (CollectionUtils.isNotEmpty(finCovenantTyps)) {
				// auditDetails.addAll(finCovenantTypeService.delete(finCovenantTyps,
				// "_Temp", auditTranType));
				finCovenantTypeService.delete(finCovenantTyps, "_Temp", auditTranType);
			}

			List<Covenant> covenants = fd.getCovenants();
			if (CollectionUtils.isNotEmpty(covenants)) {
				// auditDetails.addAll(covenantsService.delete(covenants,
				// TableType.TEMP_TAB, auditTranType));
				covenantsService.delete(covenants, TableType.TEMP_TAB, auditTranType);
			}

			List<DocumentDetails> documents = fd.getDocumentDetailsList();
			if (!fd.isExtSource() && !isWIF) {
				if (CollectionUtils.isNotEmpty(documents)) {
					listDocDeletion(fd, "_Temp");
				}
			}

			// Collateral assignment Details
			if (fd.getCollateralAssignmentList() != null && !fd.getCollateralAssignmentList().isEmpty()) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("CollateralAssignments");
				if (details != null) {
					auditDetails.addAll(details);
				}
				collateralAssignmentDAO.deleteByReference(finReference, "_Temp");
			}

			// FinAssetTypes details
			if (fd.getFinAssetTypesList() != null && !fd.getFinAssetTypesList().isEmpty()) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("FinAssetTypes");
				finAssetTypeDAO.deleteByReference(finReference, "_Temp");
				if (details != null) {
					auditDetails.addAll(details);
				}
			}

			// AssetType Extended field Details
			if (fd.getExtendedFieldRenderList() != null && fd.getExtendedFieldRenderList().size() > 0) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldDetails");
				auditDetails.addAll(extendedFieldDetailsService.delete(details, AssetConstants.EXTENDEDFIELDS_MODULE,
						finReference, null, "_Temp"));
			}

			// Vas Recording Details details Prasad
			if (fd.getFinScheduleData().getVasRecordingList() != null
					&& !fd.getFinScheduleData().getVasRecordingList().isEmpty()) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("VasRecordings");
				vasRecordingDAO.deleteByPrimaryLinkRef(finReference, "_Temp");
				if (details != null) {
					auditDetails.addAll(details);
				}

				// Vas Recording Extended field Details
				List<AuditDetail> vasExtDetails = fd.getAuditDetailMap().get("VasExtendedDetails");
				if (CollectionUtils.isNotEmpty(vasExtDetails)) {
					for (AuditDetail auditDetail : vasExtDetails) {
						ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) auditDetail.getModelData();
						extendedFieldDetailsService.delete(vasExtDetails, VASConsatnts.MODULE_NAME,
								extendedFieldRender.getReference(), null, "_Temp");
					}
				}
			}

			// Loan Extended field Render Details.
			List<AuditDetail> extendedDetails = fd.getAuditDetailMap().get("LoanExtendedFieldDetails");
			if (extendedDetails != null && extendedDetails.size() > 0) {
				auditDetails.addAll(extendedFieldDetailsService.delete(fd.getExtendedFieldHeader(),
						fd.getExtendedFieldRender().getReference(), fd.getExtendedFieldRender().getSeqNo(), "_Temp",
						auditHeader.getAuditTranType(), extendedDetails));
			}

			// SubventionDetails
			if (fd.getFinScheduleData().getSubventionDetail() != null && (StringUtils.isBlank(fd.getModuleDefiner())
					|| FinServiceEvent.ORG.equals(fd.getModuleDefiner()))) {
				if (subventionService != null) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("SubventionDetails");
					subventionService.delete(fd.getFinScheduleData().getSubventionDetail(), TableType.TEMP_TAB);
					auditDetails.addAll(details);
				}
			}

			if (fd.getFinanceCollaterals() != null) {
				auditDetails.addAll(finCollateralService.delete(fd.getFinanceCollaterals(), "_Temp",
						auditHeader.getAuditTranType()));
			}

			// Finance Flag Details
			if (CollectionUtils.isNotEmpty(fd.getFinFlagsDetails())) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("FinFlagsHeader");
				finFlagsHeaderDAO.delete(((FinanceFlag) details.get(0).getModelData()), "_Temp");
				auditDetails.addAll(details);

				details = fd.getAuditDetailMap().get("FinFlagsDetail");
				finFlagDetailsDAO.deleteList(finReference, FinanceConstants.MODULE_NAME, "_Temp");
				if (details != null) {
					auditDetails.addAll(details);
				}
			}

		}

		List<FinFeeDetail> fees = fd.getFinScheduleData().getFinFeeDetailList();
		if (fees != null) {
			auditDetails.addAll(finFeeDetailService.delete(fees, "_Temp", auditHeader.getAuditTranType(), isWIF));
		}

		/*
		 * //TODO:GANESH NEED TO REMOVE if (financeDetail.getFinScheduleData().getFinFeeReceipts() != null) {
		 * auditDetails.addAll(finFeeDetailService.deleteFinFeeReceipts(
		 * financeDetail.getFinScheduleData().getFinFeeReceipts(), "_Temp", auditHeader.getAuditTranType())); }
		 */

		// Restructure Details
		if (fd.getFinScheduleData().getRestructureDetail() != null) {
			auditDetails.add(restructureService.deleteRestructureDetail(fd.getFinScheduleData().getRestructureDetail(),
					TableType.TEMP_TAB.getSuffix(), auditHeader.getAuditTranType()));
		}

		auditHeader.setAuditDetails(auditDetails);

		// Saving the reasons
		saveReasonDetails(fd);

		// LinkedFinances
		List<LinkedFinances> list = fd.getLinkedFinancesList();
		if (CollectionUtils.isNotEmpty(list)) {
			auditDetails.addAll(linkedFinancesService.doRejectLinkedFinanceList(fd));
		}

		// Manual Schedule Details
		this.manualScheduleService.doReject(fd);

		// ISRA Details
		if (fd.getIsraDetail() != null) {
			auditDetails.addAll(israDetailService.doReject(fd, "_Temp", auditHeader.getAuditTranType()));
		}

		if (!isWIF) {
			auditHeaderDAO.addAudit(auditHeader);
		}

		// Reset Finance Detail Object for Service Task Verifications
		// =======================================
		auditHeader.getAuditDetail().setModelData(fd);

		// Update Task_log and Task_Owners tables
		// =======================================
		updateTaskLog(fm, false);

		finMandateService.doRejct(fd, auditHeader);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private void rejectChildLoan(String finReference) {

		List<Long> finIDList = financeMainDAO.getChildFinRefByParentRef(finReference);

		if (CollectionUtils.isEmpty(finIDList)) {
			return;
		}

		List<FinanceMain> list = new ArrayList<>();
		for (Long finID : finIDList) {
			FinanceMain fm = new FinanceMain();
			fm.setFinID(finID);
			fm.setApproved(null);
			fm.setProcessAttributes("");
			fm.setFinIsActive(false);
			fm.setClosingStatus(PennantConstants.RCD_STATUS_REJECTED);
			fm.setNextRoleCode("");
			fm.setNextTaskId("");
			list.add(fm);
		}
		financeMainDAO.updateRejectFinanceMain(list, "_Temp");
	}

	private FinanceDetail getAutoRejDetails(FinanceDetail fd, String type, boolean isWIF) {
		String moduleName = FinanceConstants.MODULE_NAME;
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		String finType = fm.getFinType();
		long custID = fm.getCustID();

		CustomerDetails customerDetails = new CustomerDetails();

		schdData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, type, false));
		fd.setAdvancePaymentsList(finAdvancePaymentsService.getFinAdvancePaymentsById(finID, "_View"));
		schdData.setFinanceType(financeTypeDAO.getFinanceTypeByID(finType, "_AView"));

		customerDetails.setCustomer(customerDAO.getCustomerByID(custID));
		fd.setCustomerDetails(customerDetails);

		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, isWIF));
		schdData.setOverdraftScheduleDetails(
				overdraftScheduleDetailDAO.getOverdraftScheduleDetails(finID, type, isWIF));

		fd.setDocumentDetailsList(documentDetailsDAO.getDocumentDetailsByRef(finReference, moduleName, type));
		fd.setCollaterals(collateralSetupService.getCollateralDetails(finReference, true));
		fd.setCollateralAssignmentList(
				collateralAssignmentDAO.getCollateralAssignmentByFinRef(finReference, moduleName, type));
		fd.setCovenantTypeList(finCovenantTypeService.getFinCovenantDocTypeByFinRef(finReference, type, false));

		if (ImplementationConstants.COVENANT_MODULE_NEW) {
			fd.setCovenants(covenantsService.getCovenants(finReference, "Loan", TableType.TEMP_TAB));
		}

		fd.setFinanceCheckList(checkListDetailService.getCheckListByFinRef(finID, type));
		fd.setFinAssetTypesList(finAssetTypeDAO.getFinAssetTypesByFinRef(finReference, type));
		fd.setExtendedFieldRenderList(getExtendedAssetDetails(finReference, fd.getFinAssetTypesList()));
		fd.setFinanceCollaterals(finCollateralService.getFinCollateralsByRef(finID, type));
		schdData.setFinFeeDetailList(finFeeDetailDAO.getFinFeeDetailByFinRef(finID, false, type));

		List<Long> feeIds = new ArrayList<>();
		for (FinFeeDetail finFeeDetail : schdData.getFinFeeDetailList()) {
			feeIds.add(finFeeDetail.getFeeID());
		}

		if (!feeIds.isEmpty()) {
			schdData.setFinFeeReceipts(finFeeDetailService.getFinFeeReceiptsById(feeIds, type));
		}
		if (fm.getMandateID() != null && fm.getMandateID() != 0) {
			fd.setMandate(finMandateService.getMnadateByID(fm.getMandateID()));
		}

		fd.setFinFlagsDetails(finFlagDetailsDAO.getFinFlagsByFinRef(finReference, moduleName, type));
		fd.setFinanceTaxDetail(financeTaxDetailDAO.getFinanceTaxDetail(finID, type));
		fd.setChequeHeader(finChequeHeaderService.getChequeHeaderByRef(finID));

		logger.debug(Literal.LEAVING);
		return fd;
	}

	/**
	 * Method for Auto Rejection of Loans After X days
	 */
	@Override
	public void executeAutoFinRejectProcess() {
		EventProperties eventProperties = EODUtil.EVENT_PROPS;

		Date appDate = eventProperties.getAppDate();

		List<FinanceMain> finances = financeMainDAO.getUnApprovedFinances();

		StepUtil.AUTO_CANCELLATION.setTotalRecords(finances.size());

		int processedRecords = 0;
		int failureRecords = 0;

		AuditDetail auditDetail = null;
		AuditHeader auditHeader = null;

		for (FinanceMain fm : finances) {
			StepUtil.AUTO_CANCELLATION.setProcessedRecords(processedRecords++);
			int maxDaysForFinRejection = fm.getAutoRejectionDays();

			Date effctiveDate = DateUtil.addDays(appDate, -(maxDaysForFinRejection - 1));
			Date finStartDate = fm.getFinStartDate();
			String finReference = fm.getFinReference();

			if (finStartDate.compareTo(effctiveDate) > 0) {
				continue;
			}

			FinanceDetail financeDetail = new FinanceDetail();
			FinanceMain financeMain = new FinanceMain();

			financeMain.setFinReference(finReference);
			financeMain.setEventProperties(eventProperties);
			financeDetail.getFinScheduleData().setFinanceMain(financeMain);
			auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, financeDetail);
			auditHeader = new AuditHeader(finReference, null, null, null, auditDetail, null, new HashMap<>());
			try {
				doReject(auditHeader, false, true);
			} catch (Exception e) {
				StepUtil.AUTO_CANCELLATION.setProcessedRecords(failureRecords++);
				logger.info("Finance Reference for Rejection Failed : {}", finReference);
			}
		}

	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from financeMainDAO.getErrorDetail with Error ID and language
	 * as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method, boolean isWIF,
			boolean isAutoReject) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		boolean fromApi = false;

		if (auditHeader.getApiHeader() != null) {
			fromApi = true;
		}
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method, isWIF,
				fromApi);
		doPostHookValidation(auditHeader, isWIF);

		String auditTranType = auditHeader.getAuditTranType();
		FinanceDetail fd = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		String usrLanguage = PennantConstants.default_Language;
		if (fm.getUserDetails() == null) {
			fm.setUserDetails(new LoggedInUser());
			usrLanguage = fm.getUserDetails().getLanguage();
		}

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		// Linking De-linking Validations
		String finReference = fm.getFinReference();
		if (fm.isQuickDisb() && method.equals(PennantConstants.method_doReject)) {
			List<LinkedFinances> lnkdFinance = linkedFinancesService.getLinkedFinancesByFinRef(finReference, "_AView");
			for (LinkedFinances LinkedFinance : lnkdFinance) {
				String[] parameters = new String[2];
				parameters[0] = PennantJavaUtil.getLabel("label_LinkedRef") + ": " + finReference;
				parameters[1] = PennantJavaUtil.getLabel("label_LinkedRef") + ": " + LinkedFinance.getFinReference();

				auditDetail.setErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "90500", parameters, null));
				auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

				auditHeader.setAuditDetail(auditDetail);
				auditHeader.setErrorList(auditDetail.getErrorDetails());
				auditHeader = nextProcess(auditHeader);
				return auditHeader;
			}
		}

		// Additional validations for CovanentTypes
		List<ErrorDetail> errorDetails = new ArrayList<>();
		if (!isWIF) {
			auditHeader = getAuditDetails(auditHeader, method);
			if (!isAutoReject && (auditHeader.getApiHeader() == null || !fd.isStp())) {
				errorDetails = covValidations(auditHeader);
			}
		}

		// =======================================
		if (errorDetails != null) {
			errorDetails = ErrorUtil.getErrorDetails(errorDetails, auditHeader.getUsrLanguage());
			auditHeader.setErrorList(errorDetails);
		}

		// Finance vas recording
		List<VASRecording> vasRecordingList = schdData.getVasRecordingList();
		if (vasRecordingList != null && !vasRecordingList.isEmpty()) {
			fd.getAuditDetailMap().put("VasRecordings", setVasAuditData(fd, auditTranType, method));
			auditDetails.addAll(fd.getAuditDetailMap().get("VasRecordings"));

			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < vasRecordingList.size(); i++) {
				VASRecording recording = vasRecordingList.get(i);
				ExtendedFieldRender extendedFieldRender = recording.getExtendedFieldRender();
				extendedFieldRender.setTypeCode(recording.getProductCode());
				extendedFieldRender.setTypeCodeDesc(recording.getProductDesc());
				extendedFieldRender.setReference(recording.getVasReference());
				extendedFieldRender.setWorkflowId(schdData.getFinanceMain().getWorkflowId());
				if (extendedFieldDetailsService.setExtendedFieldAuditData(extendedFieldRender, auditTranType, method,
						i + 1, null) != null) {
					details.add(extendedFieldDetailsService.setExtendedFieldAuditData(extendedFieldRender,
							auditTranType, method, i + 1, null));
				}

			}
			fd.getAuditDetailMap().put("VasExtendedDetails", details);
			auditDetails.addAll(fd.getAuditDetailMap().get("VasExtendedDetails"));

		}

		if (!isWIF && !fd.isExtSource()) {

			String rcdType = fm.getRecordType();
			if (!fd.isLovDescIsQDE() && rcdType.equals(PennantConstants.RECORD_TYPE_NEW)) {

				// Customer Details Validation
				// =======================================
				if (fd.getCustomerDetails() != null && !isAutoReject) {
					fd.getCustomerDetails().setUserDetails(fd.getUserDetails());
					auditDetails.addAll(customerDetailsService.validate(fd.getCustomerDetails(), fm.getWorkflowId(),
							method, usrLanguage));
				}
			}

			// Cheque Header Details (TODO : Temporary Validation addition)
			// =======================================
			if (fd.getChequeHeader() != null) {
				auditDetail = finChequeHeaderService.validation(auditDetail, usrLanguage);
				auditHeader.setAuditDetail(auditDetail);
				auditHeader.setErrorList(auditDetail.getErrorDetails());
			}

			// Finance Check List Details
			// =======================================
			List<FinanceCheckListReference> financeCheckList = fd.getFinanceCheckList();
			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(checkListDetailService.validate(fd.getAuditDetailMap().get("checkListDetails"),
						method, usrLanguage));
			}

			// Guaranteer Details Validation
			// =======================================
			List<GuarantorDetail> gurantorsDetailList = fd.getGurantorsDetailList();
			if (gurantorsDetailList != null && !gurantorsDetailList.isEmpty()) {
				// 10-Jul-2018 BUG FIX related to TktNo:127415
				List<AuditDetail> details = guarantorDetailService.validate(gurantorsDetailList, fm.getWorkflowId(),
						method, auditTranType, usrLanguage);
				fd.getAuditDetailMap().put("Guarantors", details);
				auditDetails.addAll(details);
			}

			// Joint Account Details Validation
			// =======================================
			List<JointAccountDetail> jointAccountDetailList = fd.getJointAccountDetailList();
			if (jointAccountDetailList != null && !jointAccountDetailList.isEmpty()) {
				// 10-Jul-2018 BUG FIX related to TktNo:127415
				List<AuditDetail> details = jointAccountDetailService.validate(jointAccountDetailList,
						fm.getWorkflowId(), method, auditTranType, usrLanguage);
				fd.getAuditDetailMap().put("JointAccountDetails", details);
				auditDetails.addAll(details);
			}

			FinanceTaxDetail taxDetail = fd.getFinanceTaxDetail();
			FinanceType financeType = fd.getFinScheduleData().getFinanceType();

			if (taxDetail != null && !auditDetails.isEmpty()) {
				if (!fd.isActionSave()) {
					long custId = taxDetail.getTaxCustId();
					String taxNumber = taxDetail.getTaxNumber();
					boolean idExist = false;

					if (custId != 0) {
						// GST Number Validation
						if (StringUtils.isNotBlank(taxNumber)) {
							financeTaxDetailService.gstNumbeValidation(auditDetails.get(0), taxDetail);
						}

						if (PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT.equals(taxDetail.getApplicableFor())) {
							for (JointAccountDetail jointAccountDetail : jointAccountDetailList) {
								if (jointAccountDetail.getCustID() == custId && !(StringUtils
										.equals(PennantConstants.RECORD_TYPE_DEL, jointAccountDetail.getRecordType())
										|| StringUtils.equals(PennantConstants.RECORD_TYPE_CAN,
												jointAccountDetail.getRecordType()))) {
									idExist = true;
									break;
								}
							}
							String[] errParm = new String[1];
							String[] valueParm = new String[1];
							valueParm[0] = taxDetail.getCustCIF();
							errParm[0] = valueParm[0];

							if (!idExist) { // if Co-Applicant is not available
								auditDetails.get(0).setErrorDetail(ErrorUtil.getErrorDetail(
										new ErrorDetail(PennantConstants.KEY_FIELD, "65021", errParm, valueParm),
										usrLanguage));
							}
						} else if (PennantConstants.TAXAPPLICABLEFOR_GUARANTOR.equals(taxDetail.getApplicableFor())) {
							for (GuarantorDetail guarantorDetail : gurantorsDetailList) {
								if (guarantorDetail.getCustID() == custId && !(StringUtils
										.equals(PennantConstants.RECORD_TYPE_DEL, guarantorDetail.getRecordType())
										|| StringUtils.equals(PennantConstants.RECORD_TYPE_CAN,
												guarantorDetail.getRecordType()))) {
									idExist = true;
									break;
								}
							}

							String[] errParm = new String[1];
							String[] valueParm = new String[1];
							valueParm[0] = taxDetail.getCustCIF();
							errParm[0] = valueParm[0];

							if (!idExist) { // if Guarantor is not available
								auditDetails.get(0).setErrorDetail(ErrorUtil.getErrorDetail(
										new ErrorDetail(PennantConstants.KEY_FIELD, "65022", errParm, valueParm),
										usrLanguage));
							}
						}
					}
				}

				if (taxDetail.getApplicableFor() == null) {
					if (FinServiceEvent.ORG.equals(fd.getModuleDefiner()) && financeType.isTaxNoMand()) {
						auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "930501", null, null), usrLanguage));
					}
				}

			}

			// set Finance Collateral Details Audit
			// =======================================
			if (fd.getFinanceCollaterals() != null && !fd.getFinanceCollaterals().isEmpty()) {
				auditDetails.addAll(finCollateralService.validate(fd.getFinanceCollaterals(), fm.getWorkflowId(),
						method, auditTranType, usrLanguage));
			}

			// Advance Payment details
			// =======================================
			if (fd.getAdvancePaymentsList() != null) {
				boolean isApi = false;

				if (auditHeader.getApiHeader() != null) {
					isApi = true;
				}

				auditDetails.addAll(finAdvancePaymentsService.validate(fd.getAdvancePaymentsList(), fm.getWorkflowId(),
						method, auditTranType, usrLanguage, fd, isApi));
			}

			// Covenant Type details
			// =======================================
			if (fd.getCovenantTypeList() != null) {
				auditDetails.addAll(finCovenantTypeService.validate(fd.getCovenantTypeList(), fm.getWorkflowId(),
						method, auditTranType, usrLanguage));
				validateDisbursements(fd, auditDetails);

			}

			List<Covenant> covenats = fd.getCovenants();
			if (ImplementationConstants.COVENANT_MODULE_NEW && CollectionUtils.isNotEmpty(covenats)) {
				auditDetails.addAll(
						covenantsService.validate(covenats, fm.getWorkflowId(), method, auditTranType, usrLanguage));
				// commenting the below line since we are calling covenantsService.validateOTC(financeDetail) method for
				// new covenant module
				// validateDisbursements(financeDetail, auditDetails);

			}

			if (StringUtils.equals(fd.getModuleDefiner(), FinServiceEvent.ORG)
					&& !method.equals(PennantConstants.method_doReject)
					&& !fm.getRecordStatus().contains(PennantConstants.RCD_STATUS_SAVED)
					&& !fm.getRecordStatus().equals(PennantConstants.RCD_STATUS_DECLINED)) {

				if (ImplementationConstants.COVENANT_MODULE_NEW) {
					// Adding the audit details to display error's
					auditDetails.addAll(covenantsService.validateOTC(fd));
				} else {
					validateOtcPayment(auditDetails, fd);
				}

			}
			// Collateral Assignments details
			// =======================================
			if (fd.getCollateralAssignmentList() != null && !fd.getCollateralAssignmentList().isEmpty()) {

				// Collateral Assignments Validation
				List<CollateralAssignment> assignments = fd.getCollateralAssignmentList();
				if (assignments != null && !assignments.isEmpty()) {
					List<AuditDetail> details = fd.getAuditDetailMap().get("CollateralAssignments");
					details = getCollateralAssignmentValidation().vaildateDetails(details, method, usrLanguage);
					auditDetails.addAll(details);
				}
			}

			// Collateral Setup details Business validations
			// ========================
			if (CollectionUtils.isNotEmpty(fd.getCollaterals())) {
				auditDetails.addAll(collateralSetupService.validateDetails(fd, auditTranType, method));
			}

			// FinAssetType Detail
			if (fd.getFinAssetTypesList() != null && !fd.getFinAssetTypesList().isEmpty()) {

				// FinAssetType Validation
				List<AuditDetail> details = fd.getAuditDetailMap().get("FinAssetTypes");
				if (details != null) {
					details = getFinAssetTypesValidation().vaildateDetails(details, method, usrLanguage);
					auditDetails.addAll(details);
				}
			}
			// Extended field details Validation
			if (fd.getExtendedFieldRenderList() != null && !fd.getExtendedFieldRenderList().isEmpty()) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("ExtendedFieldDetails");

				if (details != null) {
					for (AuditDetail assetDetail : details) {
						ExtendedFieldRender aExetendedFieldRender = (ExtendedFieldRender) assetDetail.getModelData();
						StringBuilder tableName = new StringBuilder();
						tableName.append(AssetConstants.EXTENDEDFIELDS_MODULE);
						tableName.append("_");
						tableName.append(aExetendedFieldRender.getTypeCode());
						tableName.append("_ED");
						assetDetail = extendedFieldDetailsService.validate(assetDetail, method, usrLanguage,
								tableName.toString());
						aExetendedFieldRender.setTableName(tableName.toString());
						auditDetails.add(assetDetail);
					}
				}
			}

			// PSL details
			// =======================================
			if (fd.getPslDetail() != null) {
				fd.getPslDetail().setWorkflowId(fm.getWorkflowId());
				auditDetails.add(pSLDetailService.validate(fd.getPslDetail(), method, auditTranType, usrLanguage));
			}

			// Vas Recording Details
			if (schdData.getVasRecordingList() != null && !schdData.getVasRecordingList().isEmpty()) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("VasRecordings");
				if (details != null) {
					details = getVasRecordingValidation().vaildateDetails(details, method, usrLanguage);
					auditDetails.addAll(details);
				}

				// Extended field details Validation
				List<AuditDetail> vasExtDetails = fd.getAuditDetailMap().get("VasExtendedDetails");
				if (vasExtDetails != null && !vasExtDetails.isEmpty()) {
					for (AuditDetail assetDetail : vasExtDetails) {
						if (assetDetail.getModelData() != null) {
							ExtendedFieldRender aExetendedFieldRender = (ExtendedFieldRender) assetDetail
									.getModelData();
							StringBuilder tableName = new StringBuilder();
							tableName.append(VASConsatnts.MODULE_NAME);
							tableName.append("_");
							tableName.append(aExetendedFieldRender.getTypeCode());
							tableName.append("_ED");
							assetDetail = extendedFieldDetailsService.validate(assetDetail, method, usrLanguage,
									tableName.toString());
							auditDetails.add(assetDetail);
						}
					}
				}
			}

			// Finance Flag details Validation
			List<FinFlagsDetail> finFlagsDetailList = fd.getFinFlagsDetails();
			if (CollectionUtils.isNotEmpty(finFlagsDetailList)) {
				List<AuditDetail> details = fd.getAuditDetailMap().get("FinFlagsDetail");
				details = getFlagDetailValidation().vaildateDetails(details, method, usrLanguage);
				auditDetails.addAll(details);
			}

			// Legal details from loan business validation
			// ========================
			if (CollectionUtils.isNotEmpty(fd.getLegalDetailsList())) {
				auditDetails.addAll(legalDetailService.validateDetailsFromLoan(fd, auditTranType, method));
			}
		}

		// Restructure Details
		if (schdData.getRestructureDetail() != null) {
			auditDetails.add(restructureService.validationRestructureDetail(fd, method, usrLanguage));
		}

		// Finance Fee details
		if (!fd.isExtSource()) {
			List<FinFeeDetail> finFeeDeatailsList = schdData.getFinFeeDetailList();

			if (finFeeDeatailsList != null) {
				List<AuditDetail> auditDetailsList = finFeeDetailService.validate(schdData.getFinFeeDetailList(),
						fm.getWorkflowId(), method, auditTranType, usrLanguage, isWIF);

				auditDetails.addAll(auditDetailsList);

				if (StringUtils.isNotBlank(fm.getWifReference())
						&& !StringUtils.equals(fm.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)
						&& (StringUtils.isBlank(fd.getModuleDefiner())
								|| FinServiceEvent.ORG.equals(fd.getModuleDefiner()))) {
					List<FinTypeFees> finTypeFeesList = fd.getFinTypeFeesList();
					boolean warningMessage = false;

					if (finTypeFeesList != null) {
						if (finTypeFeesList.isEmpty()) {
							if (!finFeeDeatailsList.isEmpty()) {
								warningMessage = true;
							}
						} else {
							if (finFeeDeatailsList.isEmpty()) {
								warningMessage = true;
							} else {
								Set<Long> feeTypeSet = new HashSet<Long>();

								for (FinTypeFees finTypeFees : finTypeFeesList) {
									feeTypeSet.add(finTypeFees.getFeeTypeID());
								}

								for (FinFeeDetail finFeeDet : finFeeDeatailsList) {
									if (finFeeDet.getFeeTypeID() != 0
											&& !feeTypeSet.contains(finFeeDet.getFeeTypeID())) {
										warningMessage = true;
										break;
									}
								}
							}
						}
					}

					if (warningMessage) {
						AuditDetail auditDet = new AuditDetail();
						String[] errParm = new String[1];
						String[] valueParm = new String[1];
						errParm[0] = "";
						valueParm[0] = "";
						auditDet.setErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "65026", errParm, valueParm));
						auditDet.setErrorDetails(ErrorUtil.getErrorDetails(auditDet.getErrorDetails(), usrLanguage));

						auditDetails.add(auditDet);
					}
				}
			}

		}

		if (StringUtils.equals(FinServiceEvent.ORG, fd.getModuleDefiner())) {
			// Finance Fee Receipts
			if (schdData.getFinFeeReceipts() == null) {
				schdData.setFinFeeReceipts(new ArrayList<FinFeeReceipt>());
			}
			auditDetails.addAll(finFeeDetailService.validateFinFeeReceipts(fd, fm.getWorkflowId(), method,
					auditTranType, usrLanguage, auditDetails));
		}

		// Extended field details Validation
		if (fd.getExtendedFieldRender() != null) {
			List<AuditDetail> details = fd.getAuditDetailMap().get("LoanExtendedFieldDetails");
			ExtendedFieldHeader extendedFieldHeader = fd.getExtendedFieldHeader();
			if (extendedFieldHeader != null) {
				StringBuilder sb = new StringBuilder();
				sb.append(extendedFieldHeader.getModuleName());
				sb.append("_");
				sb.append(extendedFieldHeader.getSubModuleName());
				if (extendedFieldHeader.getEvent() != null) {
					sb.append("_");
					sb.append(PennantStaticListUtil.getFinEventCode(extendedFieldHeader.getEvent()));
				}
				sb.append("_ED");
				details = extendedFieldDetailsService.vaildateDetails(details, method, usrLanguage, sb.toString());
				auditDetails.addAll(details);
			}
		}
		// validate QueryModule
		if (StringUtils.equalsIgnoreCase("Y", SysParamUtil.getValueAsString("QUERY_ASSIGN_TO_LOAN_AND_LEGAL_ROLES"))) {
			if ("saveOrUpdate".equals(method) && isForwardCase(fm)) {
				String currentRole = fm.getRoleCode();
				List<QueryDetail> qrysList = queryDetailService.getUnClosedQurysForGivenRole(finReference, currentRole);
				if (CollectionUtils.isNotEmpty(qrysList)) {
					String[] errParm = new String[1];
					String[] valueParm = new String[1];
					valueParm[0] = finReference;
					errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ": " + valueParm[0];
					List<ErrorDetail> errorDetailsList = new ArrayList<ErrorDetail>(1);
					ErrorDetail errorDetail = ErrorUtil.getErrorDetail(ErrorUtil
							.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "Q003", null, null), "EN"));
					errorDetailsList.add(errorDetail);
					auditHeader.setErrorList(errorDetailsList);
				}
			}
		}

		// OCR Details Validation

		if (fm.isFinOcrRequired()) {
			if (fd.getFinOCRHeader() != null) {
				FinOCRHeader finOCRHeader = fd.getFinOCRHeader();
				AuditDetail finOCRHeaderAudit = new AuditDetail(auditTranType, 1, finOCRHeader.getBefImage(),
						finOCRHeader);
				auditDetails.add(finOCRHeaderService.validate(finOCRHeaderAudit, auditTranType, method));
			}
		}

		List<TanAssignment> tanAssignments = fd.getTanAssignments();
		if (CollectionUtils.isNotEmpty(tanAssignments)) {
			List<AuditDetail> details = tanAssignmentService.validate(fd, fm.getWorkflowId(), method, auditTranType,
					usrLanguage);

			fd.getAuditDetailMap().put("TanAssignments", details);
			auditDetails.addAll(details);
		}

		for (int i = 0; i < auditDetails.size(); i++) {
			auditHeader.setErrorList(auditDetails.get(i).getErrorDetails());
		}

		auditHeader = nextProcess(auditHeader);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private void validateOtcPayment(List<AuditDetail> auditDetails, FinanceDetail financeDetail) {
		// ####_0.2
		// Not allowed to approve loan with Disbursement type if it is
		// not in the configured OTC Types

		String alwrepayMethods = (String) SysParamUtil.getValue("COVENANT_REPAY_OTC_TYPE");
		List<FinAdvancePayments> advancePaymentsList = financeDetail.getAdvancePaymentsList();

		if (alwrepayMethods == null || CollectionUtils.isEmpty(advancePaymentsList)) {
			return;
		}

		String[] valueParm = new String[2];
		boolean isFound = false;
		boolean isOTCPayment = false;
		boolean isDocExist = false;

		String[] repaymethod = alwrepayMethods.split(",");

		for (FinAdvancePayments finAdvancePayments : advancePaymentsList) {
			isFound = false;
			for (String rpymethod : repaymethod) {
				if (StringUtils.equals(finAdvancePayments.getPaymentType(), rpymethod)) {
					isFound = true;
					break;
				}
			}
			if (!isFound) {
				valueParm[0] = finAdvancePayments.getPaymentType();
				isOTCPayment = true;
				break;
			}
		}

		List<FinCovenantType> covenantTypeList = financeDetail.getCovenantTypeList();
		if (!isOTCPayment || CollectionUtils.isEmpty(covenantTypeList)) {
			return;
		}

		AuditDetail detail = new AuditDetail();

		for (FinCovenantType covenantType : covenantTypeList) {
			isDocExist = false;

			if (PennantConstants.RECORD_TYPE_CAN.equals(covenantType.getRecordType())) {
				continue;
			}

			for (DocumentDetails documentDetails : financeDetail.getDocumentDetailsList()) {
				if (documentDetails.getDocCategory().equals(covenantType.getCovenantType())
						&& !documentDetails.getRecordType().equals(PennantConstants.RECORD_TYPE_CAN)) {
					isDocExist = true;
					break;
				}
			}

			if (!isDocExist && covenantType.isAlwOtc()) {
				valueParm[1] = Labels.getLabel("label_FinCovenantTypeDialog_AlwOTC.value");
				detail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("41101", valueParm)));
				auditDetails.add(detail);
				break;

			}
		}
	}

	// ### 22-06-2018 -End

	private void doPostHookValidation(AuditHeader auditHeader, boolean isWIF) {
		if (postValidationHook != null && !isWIF) {
			List<ErrorDetail> errorDetails = postValidationHook.validation(auditHeader);
			// Bugfix: API Validations are not showing
			if (CollectionUtils.isNotEmpty(errorDetails)) {
				errorDetails = ErrorUtil.getErrorDetails(errorDetails, auditHeader.getUsrLanguage());
				if (auditHeader.getAuditDetail() != null) {
					auditHeader.getAuditDetail().addErrorDetails(errorDetails);
				} else {
					auditHeader.setErrorList(errorDetails);
				}
			}
		}
	}

	// ### 22-06-2018 -End

	/*
	 * validates to allow for disbursements in case of Otc or PDD
	 */
	private List<AuditDetail> validateDisbursements(FinanceDetail financeDetail, List<AuditDetail> auditDetails) {
		FinanceMain financemain = financeDetail.getFinScheduleData().getFinanceMain();
		if (FinServiceEvent.ADDDISB.equals(financeDetail.getModuleDefiner())
				&& financemain.getRecordStatus().equals(PennantConstants.RCD_STATUS_SUBMITTED)) {
			validateOtcPayment(auditDetails, financeDetail);

		}
		return auditDetails;
	}

	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method, boolean isWIF,
			boolean fromApi) {
		logger.debug(Literal.ENTERING);

		FinanceDetail fd = (FinanceDetail) auditDetail.getModelData();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		fm.setDmaCodeReference(fm.getDmaCode());
		fm.setAccountsOfficerReference(String.valueOf(fm.getAccountsOfficer()));

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = finReference;
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ": " + valueParm[0];

		if (fm.getBefImage() != null && StringUtils.isEmpty(fm.getBefImage().getNextRoleCode())
				&& !PennantConstants.RCD_STATUS_APPROVED.equals(fm.getRecordStatus())) {
			if (financeMainDAO.isFinReferenceExists(fm.getFinReference(), "_Temp", false)) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParm, valueParm), usrLanguage));
			}
		}

		if (StringUtils.isNotEmpty(fm.getAdvType()) && PennantConstants.FINSOURCE_ID_API.equals(fm.getFinSourceID())) {
			int ccyFormat = CurrencyUtil.getFormat(fm.getFinCcy());
			BigDecimal actualAmbnt = BigDecimal.ZERO;
			BigDecimal netfinamnt = BigDecimal.ZERO;
			BigDecimal disbAmount = BigDecimal.ZERO;

			if (!"#".equals(fm.getAdvType()) || !"#".equals(fm.getGrcAdvType())) {
				for (FinFeeDetail fee : fd.getFinScheduleData().getFinFeeDetailList()) {
					actualAmbnt = actualAmbnt.add(fee.getActualAmount());
					netfinamnt = fm.getFinAmount().subtract(actualAmbnt);
				}

				fm.setDeductFeeDisb(actualAmbnt);

				for (FinAdvancePayments fap : fd.getAdvancePaymentsList()) {
					disbAmount = disbAmount.add(fap.getAmtToBeReleased());
				}

				if (disbAmount.compareTo(netfinamnt) != 0) {
					String[] vprm = new String[2];
					vprm[0] = PennantApplicationUtil.amountFormate(disbAmount, ccyFormat);
					vprm[1] = PennantApplicationUtil.amountFormate(netfinamnt, ccyFormat);
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("60401", vprm)));
				}
			}
		}

		// Checking , if Customer is in EOD process or not. if Yes, not allowed
		// to do an action
		if (!StringUtils.equals(PennantConstants.RECORD_TYPE_NEW, fm.getRecordType())) {
			int eodProgressCount = customerQueuingDAO.getProgressCountByCust(fm.getCustID());

			// If Customer Exists in EOD Processing, Not allowed to Maintenance
			// till completion
			if (eodProgressCount > 0) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "60203", errParm, valueParm), usrLanguage));
			}
		}

		if (StringUtils.equals(PennantConstants.RECORD_TYPE_NEW, fm.getRecordType()) && !isWIF
				&& ((fm.getRecordStatus().contains(PennantConstants.RCD_STATUS_SUBMITTED)
						|| fm.getRecordStatus().contains(PennantConstants.RCD_STATUS_SAVED))
						|| (StringUtils.equals(fm.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)))) {
			String befAppNo = financeMainDAO.getApplicationNoById(finID, "_Temp");
			if (befAppNo == null || !StringUtils.equals(befAppNo, fm.getApplicationNo())) {
				if (StringUtils.isNotBlank(fm.getApplicationNo())) {
					boolean isDuplicate = financeMainDAO.isAppNoExists(fm.getApplicationNo(), TableType.BOTH_TAB);
					if (isDuplicate) {
						String[] errParmFinMain = new String[1];
						String[] valueParmFinMain = new String[1];
						valueParmFinMain[0] = fm.getApplicationNo();
						errParmFinMain[0] = PennantJavaUtil.getLabel("label_FinanceMainDialog_ApplicationNo.value")
								+ ": " + valueParmFinMain[0];
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "41001", errParmFinMain, valueParmFinMain),
								usrLanguage));
					}
				}
			}
		}
		if (auditDetail.getErrorDetails() == null || auditDetail.getErrorDetails().isEmpty()) {
			if (!isWIF && !method.equals(PennantConstants.method_doReject)
					&& !fm.getRecordStatus().contains(PennantConstants.RCD_STATUS_RESUBMITTED)
					&& !fm.getRecordStatus().contains(PennantConstants.RCD_STATUS_SAVED)
					&& !fm.getRecordStatus().equals(PennantConstants.RCD_STATUS_DECLINED)) {

				// Eligibility Details
				// =======================================
				// FIX ME: Eligibility Rules should be executed before coming
				// here
				if (!StringUtils.equals("MoveLoanStage", fm.getServiceName())
						&& !PennantConstants.RCD_STATUS_REJECTED.equals(fm.getRecordStatus())) {
					eligibilityDetailService.validate(fd.getElgRuleList(), auditDetail, errParm, valueParm,
							usrLanguage);
				}

				// Scoring Details
				// =======================================
				scoringDetailService.validate(fd, auditDetail, errParm, valueParm, usrLanguage);

				// Collateral Details
				FinanceType financeType = schdData.getFinanceType();
				if (financeType.isFinCollateralReq() && (!financeType.isPartiallySecured())
						&& ImplementationConstants.COLLATERAL_INTERNAL && fd.getCollateralAssignmentList() != null
						&& !fd.getCollateralAssignmentList().isEmpty()) {
					BigDecimal totAssignAmt = BigDecimal.ZERO;
					for (CollateralAssignment collateralAssignment : fd.getCollateralAssignmentList()) {
						totAssignAmt = totAssignAmt.add(
								collateralAssignment.getCollateralValue().multiply(collateralAssignment.getAssignPerc())
										.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN));
					}
					if (totAssignAmt.compareTo(fm.getFinAmount()) < 0) {
						int foramtter = CurrencyUtil.getFormat(fm.getFinCcy());
						String[] errParmCollateral = new String[2];
						String[] valueParmCollateral = new String[2];
						valueParmCollateral[0] = String
								.valueOf(PennantApplicationUtil.amountFormate(totAssignAmt, foramtter));
						valueParmCollateral[1] = String
								.valueOf(PennantApplicationUtil.amountFormate(fm.getFinAmount(), foramtter));
						errParmCollateral[0] = PennantJavaUtil.getLabel("label_CollateralAmount") + ":"
								+ valueParmCollateral[0];
						errParmCollateral[1] = PennantJavaUtil.getLabel("label_FinanceAmount") + ":"
								+ valueParmCollateral[1];
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
								"65012", errParmCollateral, valueParmCollateral), usrLanguage));
					}
				}
			}
		}

		// SubventionDetails ,if grace tenure less than subvention tenure
		if ((StringUtils.isBlank(fd.getModuleDefiner()) || FinServiceEvent.ORG.equals(fd.getModuleDefiner()))
				&& fm.isAllowSubvention()
				&& fd.getFinScheduleData().getSubventionDetail().getTenure() > fm.getGraceTerms()) {

			auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
					new ErrorDetail(PennantConstants.KEY_FIELD, "60222", errParm, valueParm), usrLanguage));
		}

		finMandateService.validateMandate(auditDetail, fd, fd.getMandate());

		finMandateService.validateMandate(auditDetail, fd, fd.getSecurityMandate());

		if (!StringUtils.equals(fm.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)) {
			// finMandateService.promptMandate(auditDetail,
			// financeDetail);//FIXME: Override issue to be fixed
		}

		// Drawing power validations.
		auditDetail = this.drawingPowerService.validate(auditDetail, fd);

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(method) && !PennantConstants.RECORD_TYPE_NEW.equals(fm.getRecordType())) {
			auditDetail.setBefImage(financeMainDAO.getFinanceMainById(finID, "", isWIF));
		}
		if (fd.isTvApprovalTab() && !StringUtils.contains(fm.getRecordStatus(), (PennantConstants.RCD_STATUS_SAVED))
				&& !StringUtils.contains(fm.getRecordStatus(), (PennantConstants.RCD_STATUS_RESUBMITTED))
				&& !StringUtils.contains(fm.getRecordStatus(), (PennantConstants.RCD_STATUS_REJECTED))
				&& !StringUtils.contains(fm.getRecordStatus(), (PennantConstants.RCD_STATUS_CANCELLED))) {
			/*
			 * AuditDetail aAuditDetail = technicalVerificationService.validateTVCount(financeDetail); if
			 * (CollectionUtils.isNotEmpty(aAuditDetail.getErrorDetails())) {
			 * auditDetail.setErrorDetail(aAuditDetail.getErrorDetails().get(0)) ; }
			 */
		}

		// validation for Loan amount less than sum of capitalized interest & disbursement amount
		String moduleDefiner = fd.getModuleDefiner();
		if ((FinServiceEvent.ORG.equals(moduleDefiner) || FinServiceEvent.ADDDISB.equals(moduleDefiner)
				|| FinServiceEvent.RATECHG.equals(moduleDefiner) || FinServiceEvent.CHGGRCEND.equals(moduleDefiner)
				|| FinServiceEvent.RESCHD.equals(moduleDefiner) || FinServiceEvent.UNPLANEMIH.equals(moduleDefiner)
				|| FinServiceEvent.PLANNEDEMI.equals(moduleDefiner)) && fm.isStepFinance()
				&& PennantConstants.STEPPING_CALC_AMT.equals(fm.getCalcOfSteps())) {
			if (!PennantConstants.FINSOURCE_ID_API.equals(fm.getFinSourceID()) || !fromApi) {
				BigDecimal totalAmt = fm.getTotalCpz().add(fm.getFinCurrAssetValue());
				if ((totalAmt.compareTo(fm.getFinAssetValue()) > 0)) {
					auditDetail.setErrorDetail(ErrorUtil
							.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "STP002", null, null)));
				}
			}

			List<FinanceScheduleDetail> fsdList = schdData.getFinanceScheduleDetails();
			List<FinanceStepPolicyDetail> spdList = schdData.getStepPolicyDetails();
			List<FinanceStepPolicyDetail> rpyList = new ArrayList<>(1);
			for (FinanceStepPolicyDetail financeStepPolicyDetail : spdList) {
				if (PennantConstants.STEP_SPECIFIER_REG_EMI.equals(financeStepPolicyDetail.getStepSpecifier())) {
					rpyList.add(financeStepPolicyDetail);
				}
			}

			int idxStart = 0;
			idxStart = idxStart + fm.getGraceTerms();

			if (CollectionUtils.isEmpty(rpyList)) {
				return auditDetail;
			}

			boolean isValidEMI = true;
			Collections.sort(rpyList, (step1, step2) -> step1.getStepNo() > step2.getStepNo() ? 1
					: step1.getStepNo() < step2.getStepNo() ? -1 : 0);

			FinanceStepPolicyDetail lastStp = rpyList.get(rpyList.size() - 1);

			if (fm.getFinAssetValue().compareTo(fm.getFinCurrAssetValue()) == 0
					&& fm.getMaturityDate().compareTo(lastStp.getStepEnd()) != 0) {
				auditDetail.setErrorDetail(
						ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "STP013", null, null)));
				return auditDetail;
			}

			for (FinanceStepPolicyDetail spd : rpyList) {
				if (fm.getNoOfSteps() != spd.getStepNo()) {
					int instCount = 0;

					for (int iFsd = idxStart; iFsd < fsdList.size(); iFsd++) {
						FinanceScheduleDetail fsd = fsdList.get(iFsd);
						if (fsd.isRepayOnSchDate()
								&& CalculationConstants.SCH_SPECIFIER_REPAY.equals(fsd.getSpecifier())) {
							if (StringUtils.equals(fsd.getSchdMethod(), CalculationConstants.SCHMTHD_EQUAL)) {
								if (spd.getSteppedEMI().compareTo(BigDecimal.ZERO) > 0
										&& fsd.getProfitCalc().compareTo(spd.getSteppedEMI()) > 0) {
									if (FinServiceEvent.ORG.equals(moduleDefiner)) {
										auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
												new ErrorDetail(PennantConstants.KEY_FIELD, "STP003", null, null)));
									} else {
										auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
												new ErrorDetail(PennantConstants.KEY_FIELD, "STP0016", null, null)));
									}
									isValidEMI = false;
									break;
								}
							}
							instCount = instCount + 1;
						}

						if (spd.getInstallments() == instCount) {
							idxStart = iFsd + 1;
							break;
						}
					}

					if (!isValidEMI) {
						break;
					}
				}
			}
		}

		if (ImplementationConstants.VALIDATION_ON_CHECKER_APPROVER_ALLOWED) {
			doCheckerApproverValidation(auditDetail, usrLanguage, fd);
		}

		if (fd.isValidateUpfrontFees()) {
			validateFees(auditDetail, fd);
		}

		logger.debug(Literal.LEAVING);

		return auditDetail;
	}

	private void doCheckerApproverValidation(AuditDetail auditDetail, String usrLanguage, FinanceDetail fd) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		String recordStatus = fm.getRecordStatus();
		if (!(PennantConstants.RCD_STATUS_SAVED.equals(recordStatus)
				|| PennantConstants.RCD_STATUS_SUBMITTED.equals(recordStatus)
				|| PennantConstants.RCD_STATUS_APPROVED.equals(recordStatus))) {
			return;
		}

		FinServiceInstruction fsi = schdData.getFinServiceInstruction();
		String finEvent = fsi.getFinEvent();
		if (!(FinServiceEvent.RATECHG.equals(finEvent) || FinServiceEvent.CHGRPY.equals(finEvent)
				|| FinServiceEvent.CHGFRQ.equals(finEvent) || FinServiceEvent.RESCHD.equals(finEvent))) {

			return;
		}

		Date initiatedDate = fsi.getInitiatedDate();
		if (ObjectUtils.isEmpty(initiatedDate)) {
			return;
		}

		Date appDate = SysParamUtil.getAppDate();
		List<Date> schDates = financeScheduleDetailDAO.getScheduleDates(fm.getFinID(), appDate);

		for (Date schDate : schDates) {

			if (schDate.after(initiatedDate) && schDate.before(appDate)) {

				String[] value = new String[3];
				value[0] = DateUtil.formatToLongDate(schDate);
				value[1] = DateUtil.formatToLongDate(initiatedDate);
				value[2] = DateUtil.formatToLongDate(appDate);

				auditDetail.getErrorDetails()
						.add(ErrorUtil.getErrorDetail(new ErrorDetail("EXT001", value), usrLanguage));
				break;
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Common Method for Retrieving AuditDetails List
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private AuditHeader getAuditDetails(AuditHeader auditHeader, String method) {
		logger.debug("Entering ");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		Map<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (financeMain.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Finance Flag details
		if (CollectionUtils.isNotEmpty(financeDetail.getFinFlagsDetails())) {
			auditDetailMap.put("FinFlagsDetail", setFinFlagAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinFlagsDetail"));

			auditDetailMap.put("FinFlagsHeader", prepareFinFlagHeader(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinFlagsHeader"));
		}

		if (!financeDetail.isExtSource()) {
			// Finance Document Details
			// =======================================
			if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
				auditDetailMap.put("DocumentDetails",
						setDocumentDetailsAuditData(financeDetail, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
			}

			if (CollectionUtils.isNotEmpty(financeDetail.getTanAssignments())) {
				auditDetailMap.put("TanAssignments", tanAssignmentService.setTanAssignmentAuditData(financeDetail,
						auditTranType, method, financeDetail.getFinScheduleData().getFinanceMain().getWorkflowId()));
				auditDetails.addAll(auditDetailMap.get("TanAssignments"));
			}

			// Finance Check List Details
			// =======================================
			List<FinanceCheckListReference> financeCheckList = financeDetail.getFinanceCheckList();

			if (StringUtils.equals(method, "saveOrUpdate")) {
				if (financeCheckList != null && !financeCheckList.isEmpty()) {
					auditDetails.addAll(checkListDetailService.getAuditDetail(auditDetailMap, financeDetail,
							auditTranType, method));
				}
			} else {
				String tableType = "_Temp";
				if (financeDetail.getFinScheduleData().getFinanceMain().getRecordType()
						.equals(PennantConstants.RECORD_TYPE_DEL)) {
					tableType = "";
				}

				String finReference = financeDetail.getFinScheduleData().getFinReference();
				financeCheckList = checkListDetailService.getCheckListByFinRef(finReference, tableType);
				financeDetail.setFinanceCheckList(financeCheckList);

				if (financeCheckList != null && !financeCheckList.isEmpty()) {
					auditDetails.addAll(checkListDetailService.getAuditDetail(auditDetailMap, financeDetail,
							auditTranType, method));
				}
			}
		}

		// Collateral Assignment Details
		// =======================================
		if (financeDetail.getCollateralAssignmentList() != null
				&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
			auditDetailMap.put("CollateralAssignments",
					setCollateralAssignmentAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("CollateralAssignments"));
		}
		// FinAssetTypes Details
		if (financeDetail.getFinAssetTypesList() != null && !financeDetail.getFinAssetTypesList().isEmpty()) {
			auditDetailMap.put("FinAssetTypes", setFinAssetTypesAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinAssetTypes"));
		}

		// finstep details
		// =======================================
		if (CollectionUtils.isNotEmpty(financeDetail.getFinScheduleData().getStepPolicyDetails())) {
			auditDetailMap.put("FinanceStepPolicyDetail",
					setFinStepDetailAuditData(financeDetail.getFinScheduleData(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinanceStepPolicyDetail"));
		}

		// Asset Type Extended Field Details
		List<ExtendedFieldRender> renderList = financeDetail.getExtendedFieldRenderList();
		if (renderList != null && !renderList.isEmpty()) {
			auditDetailMap.put("ExtendedFieldDetails",
					extendedFieldDetailsService.setExtendedFieldsAuditData(renderList, auditTranType, method, null));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		// Loan Field Details
		if (financeDetail.getExtendedFieldRender() != null) {
			ExtendedFieldRender extendedFieldRender = financeDetail.getExtendedFieldRender();
			if (extendedFieldRender.getInstructionUID() == Long.MIN_VALUE
					&& financeMain.getInstructionUID() != Long.MIN_VALUE) {
				extendedFieldRender.setInstructionUID(financeMain.getInstructionUID());
			}
			auditDetailMap.put("LoanExtendedFieldDetails",
					extendedFieldDetailsService.setExtendedFieldsAuditData(financeDetail.getExtendedFieldHeader(),
							extendedFieldRender, auditTranType, method, ExtendedFieldConstants.MODULE_LOAN));
			financeDetail.setAuditDetailMap(auditDetailMap);
			auditDetails.addAll(auditDetailMap.get("LoanExtendedFieldDetails"));
		}

		if (CollectionUtils.isNotEmpty(financeDetail.getFinScheduleData().getLowerTaxDeductionDetails())) {
			auditDetailMap.put("LowerTaxDeductionDetails", setLTDAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("LowerTaxDeductionDetails"));
		}

		// ISRA Details
		if (financeDetail.getIsraDetail() != null) {
			auditDetailMap.put("ISRALiquidDeatils",
					this.israDetailService.getISRALiquidDeatils(financeDetail.getIsraDetail(), auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("ISRALiquidDeatils"));
		}

		// SubventionDetails
		if (financeDetail.getFinScheduleData().getSubventionDetail() != null
				&& (StringUtils.isBlank(financeDetail.getModuleDefiner())
						|| FinServiceEvent.ORG.equals(financeDetail.getModuleDefiner()))) {
			if (financeDetail.getFinScheduleData().getSubventionDetail() != null && subventionService != null) {
				auditDetailMap.put("SubventionDetails",
						subventionService.setSubventionDetailsAuditData(financeDetail, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("SubventionDetails"));
			}
		}

		financeDetail.setAuditDetailMap(auditDetailMap);
		auditHeader.getAuditDetail().setModelData(financeDetail);
		auditHeader.setAuditDetails(auditDetails);
		logger.debug("Leaving ");

		return auditHeader;
	}

	public List<AuditDetail> setLTDAuditData(FinanceDetail detail, String auditTranType, String method) {
		logger.debug("Entering");

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		LowerTaxDeduction lowerTaxDeduction = new LowerTaxDeduction();
		String[] fields = PennantJavaUtil.getFieldDetails(lowerTaxDeduction, lowerTaxDeduction.getExcludeFields());
		FinanceMain financeMain = detail.getFinScheduleData().getFinanceMain();

		for (int i = 0; i < detail.getFinScheduleData().getLowerTaxDeductionDetails().size(); i++) {
			LowerTaxDeduction ltDeductions = detail.getFinScheduleData().getLowerTaxDeductionDetails().get(i);

			if (StringUtils.isEmpty(ltDeductions.getRecordType())) {
				continue;
			}

			ltDeductions.setWorkflowId(financeMain.getWorkflowId());
			boolean isRcdType = false;

			if (ltDeductions.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				ltDeductions.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (ltDeductions.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				ltDeductions.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				isRcdType = true;
			} else if (ltDeductions.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				ltDeductions.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				isRcdType = true;
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				ltDeductions.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (ltDeductions.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (ltDeductions.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| ltDeductions.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			ltDeductions.setRecordStatus(financeMain.getRecordStatus());
			ltDeductions.setUserDetails(financeMain.getUserDetails());
			ltDeductions.setLastMntOn(financeMain.getLastMntOn());

			auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1], ltDeductions.getBefImage(),
					ltDeductions));
		}

		logger.debug("Leaving");
		return auditDetails;
	}

	/**
	 * Methods for Creating List of Audit Details for DocumentDetails
	 * 
	 * @param vasRecording
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	public List<AuditDetail> setVasAuditData(FinanceDetail financeDetail, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		VASRecording vasRecording = new VASRecording();
		String[] fields = PennantJavaUtil.getFieldDetails(vasRecording, vasRecording.getExcludeFields());
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		for (int i = 0; i < financeDetail.getFinScheduleData().getVasRecordingList().size(); i++) {
			VASRecording recording = financeDetail.getFinScheduleData().getVasRecordingList().get(i);

			if (StringUtils.isEmpty(StringUtils.trimToEmpty(recording.getRecordType()))) {
				continue;
			}

			recording.setPrimaryLinkRef(financeMain.getFinReference());
			recording.setWorkflowId(financeMain.getWorkflowId());
			boolean isRcdType = false;

			if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				recording.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				recording.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (financeMain.isWorkflow()) {
					isRcdType = true;
				}
			} else if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				recording.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				recording.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (recording.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| recording.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}

			recording.setRecordStatus(financeMain.getRecordStatus());
			recording.setUserDetails(financeMain.getUserDetails());
			recording.setLastMntOn(financeMain.getLastMntOn());
			auditDetails.add(
					new AuditDetail(auditTranType, i + 1, fields[0], fields[1], recording.getBefImage(), recording));
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * Methods for Creating List Finance Flag of Audit Details with detailed fields
	 * 
	 * @param financeDetail
	 * @param auditTranType
	 * @param method
	 * @return
	 */
	private List<AuditDetail> setFinFlagAuditData(FinanceDetail financeDetail, String auditTranType, String method) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		for (int i = 0; i < financeDetail.getFinFlagsDetails().size(); i++) {

			FinFlagsDetail finFlagsDetail = financeDetail.getFinFlagsDetails().get(i);
			boolean isRcdType = false;

			if (StringUtils.isEmpty(finFlagsDetail.getRecordType())) {
				continue;
			}

			if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				isRcdType = true;
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				if (finFlagsDetail.isWorkflow()) {
					isRcdType = true;
				}
			} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
				finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
			}

			if ("saveOrUpdate".equals(method) && (isRcdType)) {
				finFlagsDetail.setNewRecord(true);
			}

			if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
				if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
					auditTranType = PennantConstants.TRAN_ADD;
				} else if (finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
						|| finFlagsDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
					auditTranType = PennantConstants.TRAN_DEL;
				} else {
					auditTranType = PennantConstants.TRAN_UPD;
				}
			}
			if (StringUtils.isNotEmpty(finFlagsDetail.getRecordType())) {
				String[] fields = PennantJavaUtil.getFieldDetails(new FinFlagsDetail(),
						finFlagsDetail.getExcludeFields());
				auditDetails.add(new AuditDetail(auditTranType, i + 1, fields[0], fields[1],
						finFlagsDetail.getBefImage(), finFlagsDetail));
			}
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;
	}

	/**
	 * Method for Preparing Customer Eligibility Amount Details in Base(BHD) Currency
	 */
	@Override
	public CustomerEligibilityCheck getCustEligibilityDetail(Customer customer, String productCode, String finReference,
			String finCcy, BigDecimal curFinRpyAmount, int months, BigDecimal custDSR,
			List<JointAccountDetail> jointAccountDetails) {
		logger.debug(Literal.ENTERING);

		CustomerEligibilityCheck eligibilityCheck = new CustomerEligibilityCheck();
		if (customer != null) {

			// Eligibility object
			String dftCcy = SysParamUtil.getAppCurrency();
			BeanUtils.copyProperties(customer, eligibilityCheck);

			if (customer.getCustDOB() != null) {
				int dobMonths = DateUtility.getMonthsBetween(customer.getCustDOB(), SysParamUtil.getAppDate());
				BigDecimal age = new BigDecimal((dobMonths / 12) + "." + (dobMonths % 12));
				eligibilityCheck.setCustAge(age);
				// Minor Age Calculation
				int minorAge = SysParamUtil.getValueAsInt("MINOR_AGE");
				if (age.compareTo(BigDecimal.valueOf(minorAge)) < 0) {
					eligibilityCheck.setCustIsMinor(true);
				} else {
					eligibilityCheck.setCustIsMinor(false);
				}

			}

			Currency finCurrency = null;
			// Customer Total Income & Expense Conversion
			if (StringUtils.isNotBlank(customer.getCustBaseCcy()) && !dftCcy.equals(customer.getCustBaseCcy())) {
				finCurrency = CurrencyUtil.getCurrencyObject(customer.getCustBaseCcy());
				eligibilityCheck.setCustTotalIncome(calculateExchangeRate(customer.getCustTotalIncome(), finCurrency));
				eligibilityCheck
						.setCustTotalExpense(calculateExchangeRate(customer.getCustTotalExpense(), finCurrency));
			}

			if (months > 0) {
				eligibilityCheck.setTenure(new BigDecimal((months / 12) + "." + (months % 12)));
			}

			Date curBussDate = SysParamUtil.getAppDate();
			eligibilityCheck
					.setBlackListExpPeriod(DateUtility.getMonthsBetween(curBussDate, customer.getCustBlackListDate()));

			eligibilityCheck.setCustCtgCode(customer.getCustCtgCode());
			eligibilityCheck.setReqProduct(productCode);

			// Currently
			if (curFinRpyAmount != null && curFinRpyAmount.compareTo(BigDecimal.ZERO) > 0) {
				if (!StringUtils.equals(dftCcy, finCcy)) {
					if (finCurrency != null && finCurrency.getCcyCode().equals(finCcy)) {
						eligibilityCheck.setCurFinRepayAmt(calculateExchangeRate(curFinRpyAmount, finCurrency));
					} else {
						finCurrency = CurrencyUtil.getCurrencyObject(finCcy);
						eligibilityCheck.setCurFinRepayAmt(calculateExchangeRate(curFinRpyAmount, finCurrency));
					}
				} else {
					eligibilityCheck.setCurFinRepayAmt(curFinRpyAmount);
				}
			}

			// Finance Amount Calculations
			List<FinanceProfitDetail> financeProfitDetailsList = customerDAO.getCustFinAmtDetails(customer.getCustID(),
					eligibilityCheck);

			BigDecimal custFinAmount = BigDecimal.ZERO;
			BigDecimal custODAmount = BigDecimal.ZERO;

			for (FinanceProfitDetail financeProfitDetail : financeProfitDetailsList) {
				custFinAmount = custFinAmount.add(CalculationUtil.getConvertedAmount(financeProfitDetail.getFinCcy(),
						finCcy, financeProfitDetail.getTotalPriBal()));
				custODAmount = custODAmount.add(CalculationUtil.getConvertedAmount(financeProfitDetail.getFinCcy(),
						finCcy, financeProfitDetail.getODPrincipal()));
			}

			eligibilityCheck.setCustLiveFinAmount(custFinAmount);
			eligibilityCheck.setCustPastDueAmt(custODAmount);

			// set Customer Designation if customer status is Employed
			eligibilityCheck.setCustEmpSts(customer.getCustEmpSts());

			// Get Customer Repay Totals On Bank
			eligibilityCheck.setCustRepayBank(customerDAO.getCustRepayBankTotal(customer.getCustID()));

			// Customer Current Processing Finance Amounts
			if (StringUtils.isNotEmpty(finReference)) {
				eligibilityCheck
						.setCustProcRepayBank(customerDAO.getCustRepayProcBank(customer.getCustID(), finReference));
			}

			// Get Co-Applicants Repay Totals On Bank
			BigDecimal totalCoAppRepayBank = BigDecimal.ZERO;
			BigDecimal totalCoAppIncome = BigDecimal.ZERO;
			BigDecimal totalCoAppExpense = BigDecimal.ZERO;
			BigDecimal totalCoAppCurFinEMI = BigDecimal.ZERO;

			int ccyFormat = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);

			if (CollectionUtils.isNotEmpty(jointAccountDetails)) {
				for (JointAccountDetail accountDetail : jointAccountDetails) {
					FinanceExposure exposure = customerDAO.getCoAppRepayBankTotal(accountDetail.getCustCIF());
					if (exposure == null) {
						continue;
					}

					BigDecimal currentExpoSure = getFinanceExposure(exposure.getCustID());
					exposure.setCurrentExpoSure(currentExpoSure);
					exposure.setCurrentExpoSureinBaseCCY(currentExpoSure);

					totalCoAppRepayBank = totalCoAppRepayBank
							.add(PennantApplicationUtil.unFormateAmount(exposure.getCurrentExpoSure(), ccyFormat));

					totalCoAppIncome = totalCoAppIncome.add(
							CalculationUtil.getConvertedAmount(exposure.getFinCCY(), finCcy, exposure.getFinanceAmt()));
					totalCoAppExpense = totalCoAppExpense.add(
							CalculationUtil.getConvertedAmount(exposure.getFinCCY(), finCcy, exposure.getOverdueAmt()));
					BigDecimal curFinEMI = CalculationUtil.getConvertedAmount(exposure.getFinCCY(), finCcy,
							exposure.getCurrentExpoSureinBaseCCY());
					totalCoAppCurFinEMI = totalCoAppCurFinEMI
							.add(PennantApplicationUtil.unFormateAmount(curFinEMI, ccyFormat));
				}
			}
			eligibilityCheck.setCoAppRepayBank(totalCoAppRepayBank);
			eligibilityCheck.setCoAppIncome(totalCoAppIncome);
			eligibilityCheck.setCoAppExpense(totalCoAppExpense);
			eligibilityCheck.setCoAppCurFinEMI(totalCoAppCurFinEMI);

			// Get Customer Repay Totals by Other Commitments
			eligibilityCheck.setCustRepayOther(BigDecimal.ZERO);// customerDAO.getCustRepayOtherTotal(customer.getCustID())

			// Get Customer Worst Status From Finances
			eligibilityCheck.setCustWorstSts(customerDAO.getCustWorstSts(customer.getCustID()));

			// FIXME to be Removed
			// DSR Calculation
			/*
			 * Rule rule = getRuleDAO().getRuleByID(RuleConstants.ELGRULE_DSRCAL, RuleConstants.MODULE_ELGRULE,
			 * RuleConstants.EVENT_ELGRULE, ""); if (rule != null) { Object dscr =
			 * getRuleExecutionUtil().executeRule(rule.getSQLRule(), eligibilityCheck.getDeclaredFieldValues(), finCcy,
			 * RuleReturnType.DECIMAL); eligibilityCheck.setDSCR(PennantApplicationUtil.getDSR(dscr)); }
			 */
		}

		/*
		 * if(){ eligibilityCheck.setFinIsActive(finIsActive); }
		 */
		logger.debug(Literal.LEAVING);
		return eligibilityCheck;
	}

	@Override
	public CustomerEligibilityCheck getODLoanCustElgDetail(FinanceDetail detail) {
		logger.debug(Literal.ENTERING);
		// FIXME:in single query we want get the details
		if (detail.getCustomerEligibilityCheck() != null) {
			String finType = detail.getFinScheduleData().getFinanceType().getFinType();
			long custID = detail.getCustomerDetails().getCustomer().getCustID();
			detail.getCustomerEligibilityCheck()
					.setActiveLoansOnFinType(financeMainDAO.getActiveCount(finType, custID));
			detail.getCustomerEligibilityCheck().setTotalLoansOnFinType(financeMainDAO.getODLoanCount(finType, custID));
		}
		logger.debug(Literal.LEAVING);
		return detail.getCustomerEligibilityCheck();
	}

	/**
	 * Method for Preparing Customer Eligibility Amount Details in Base(BHD) Currency
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	@Override
	public CustomerEligibilityCheck getWIFCustEligibilityDetail(WIFCustomer customer, String finCcy)
			throws IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		CustomerEligibilityCheck eligibilityCheck = new CustomerEligibilityCheck();
		if (customer != null) {

			eligibilityCheck.setCustCtgCode(customer.getCustCtgCode());

			// Eligibility object
			org.apache.commons.beanutils.BeanUtils.copyProperties(eligibilityCheck, customer);
			int dobMonths = DateUtility.getMonthsBetween(customer.getCustDOB(), SysParamUtil.getAppDate());
			BigDecimal age = new BigDecimal((dobMonths / 12) + "." + (dobMonths % 12));
			eligibilityCheck.setCustAge(age);

			// Minor Age Calculation

			int minorAge = SysParamUtil.getValueAsInt("MINOR_AGE");
			if (age.compareTo(BigDecimal.valueOf(minorAge)) < 0) {

				eligibilityCheck.setCustIsMinor(true);
			} else {
				eligibilityCheck.setCustIsMinor(false);
			}

			Date curBussDate = SysParamUtil.getAppDate();
			eligibilityCheck
					.setBlackListExpPeriod(DateUtility.getMonthsBetween(curBussDate, customer.getCustBlackListDate()));

			if (customer.getExistCustID() != 0) {

				// Finance Amount Calculations
				List<FinanceProfitDetail> financeProfitDetailsList = customerDAO
						.getCustFinAmtDetails(customer.getExistCustID(), eligibilityCheck);

				BigDecimal custFinAmount = BigDecimal.ZERO;
				BigDecimal custODAmount = BigDecimal.ZERO;

				for (FinanceProfitDetail financeProfitDetail : financeProfitDetailsList) {
					custFinAmount.add(CalculationUtil.getConvertedAmount(financeProfitDetail.getFinCcy(), finCcy,
							financeProfitDetail.getTotalPriBal().add(financeProfitDetail.getTotalPftBal())));
					custODAmount.add(CalculationUtil.getConvertedAmount(financeProfitDetail.getFinCcy(), finCcy,
							financeProfitDetail.getODPrincipal().add(financeProfitDetail.getODProfit())));
				}

				eligibilityCheck.setCustLiveFinAmount(custFinAmount);
				eligibilityCheck.setCustPastDueAmt(custODAmount);

				// get Customer Designation if customer status is Employed
				eligibilityCheck.setCustEmpDesg(customerDAO.getCustEmpDesg(customer.getExistCustID()));
				eligibilityCheck.setCustEmpSts(customer.getCustEmpSts());

				// Get Customer Worst Status From Finances
				eligibilityCheck.setCustWorstSts(customerDAO.getCustWorstSts(customer.getExistCustID()));
				eligibilityCheck.setCustTotalIncome(customer.getTotalIncome());
				eligibilityCheck.setCustTotalExpense(customer.getTotalExpense());
				// Get Customer Repay Totals On Bank
				eligibilityCheck.setCustRepayBank(customerDAO.getCustRepayBankTotal(customer.getCustID()));

			} else {

				// Get Customer Worst Status From Finances
				eligibilityCheck.setCustWorstSts(getCustStatusByMinDueDays());
			}

			// DSR Calculation
			Rule rule = getRuleDAO().getRuleByID(RuleConstants.ELGRULE_DSRCAL, RuleConstants.MODULE_ELGRULE,
					RuleConstants.EVENT_ELGRULE, "");
			if (rule != null) {
				Object dscr = RuleExecutionUtil.executeRule(rule.getSQLRule(),
						eligibilityCheck.getDeclaredFieldValues(), finCcy, RuleReturnType.DECIMAL);
				eligibilityCheck.setDSCR(PennantApplicationUtil.getDSR(dscr));
			}
		}
		logger.debug(Literal.LEAVING);
		return eligibilityCheck;
	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference (String)
	 * @param isWIF        (boolean)
	 **/
	public FinScheduleData getFinSchDataByFinRef(long finID, String type, long logKey) {
		logger.debug(Literal.ENTERING);

		FinScheduleData finSchData = new FinScheduleData();

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, type, false);
		List<FinanceScheduleDetail> schedules = financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false);

		finSchData.setFinanceMain(fm);
		finSchData.setFinanceScheduleDetails(schedules);

		// Fee Details
		finSchData.setFinFeeDetailList(finFeeDetailDAO.getFinFeeDetailByFinRef(finID, false, "_View"));

		// Finance Fee Schedule Details
		if (finSchData.getFinFeeDetailList() != null && !finSchData.getFinFeeDetailList().isEmpty()) {

			List<Long> feeIDList = new ArrayList<>();
			for (int i = 0; i < finSchData.getFinFeeDetailList().size(); i++) {
				FinFeeDetail feeDetail = finSchData.getFinFeeDetailList().get(i);

				if (StringUtils.equals(feeDetail.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)
						|| StringUtils.equals(feeDetail.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)
						|| StringUtils.equals(feeDetail.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)) {
					feeIDList.add(feeDetail.getFeeID());
				}
			}

			List<FinFeeScheduleDetail> scheduleFees = null;
			if (!feeIDList.isEmpty()) {
				scheduleFees = finFeeScheduleDetailDAO.getFeeScheduleByFinID(feeIDList, false, "");

				if (CollectionUtils.isNotEmpty(scheduleFees)) {
					Map<Long, List<FinFeeScheduleDetail>> schFeeMap = new HashMap<>();
					for (FinFeeScheduleDetail scheduleFee : scheduleFees) {
						List<FinFeeScheduleDetail> schList = new ArrayList<>();
						if (schFeeMap.containsKey(scheduleFee.getFeeID())) {
							schList = schFeeMap.get(scheduleFee.getFeeID());
							schFeeMap.remove(scheduleFee.getFeeID());
						}
						schList.add(scheduleFee);
						schFeeMap.put(scheduleFee.getFeeID(), schList);

					}

					for (int i = 0; i < finSchData.getFinFeeDetailList().size(); i++) {
						FinFeeDetail feeDetail = finSchData.getFinFeeDetailList().get(i);
						if (schFeeMap.containsKey(feeDetail.getFeeID())) {
							feeDetail.setFinFeeScheduleDetailList(schFeeMap.get(feeDetail.getFeeID()));
						}
					}
				}
			}
		}

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, finSchData.getFinanceMain().getProductCategory())) {
			String odType = "_Temp";
			if (StringUtils.isBlank(finSchData.getFinanceMain().getRecordType())) {
				odType = "";
			}
			finSchData.setOverdraftScheduleDetails(
					overdraftScheduleDetailDAO.getOverdraftScheduleDetails(finID, odType, false));
		}
		finSchData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, type, false));
		if (subventionService != null) {
			subventionService.setSubventionScheduleDetails(finSchData, type);
		}
		finSchData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, type, false));

		if (logKey == 0) {
			finSchData.setFinanceType(financeTypeDAO.getFinanceTypeByID(fm.getFinType(), "_AView"));
			finSchData = getFinMaintainenceDetails(finSchData);
			finSchData.setAccrueValue(getAccrueAmount(finID));
		}
		logger.debug(Literal.LEAVING);
		return finSchData;
	}

	@Override
	public FinScheduleData getFinMaintainenceDetails(FinScheduleData schdData) {
		logger.debug(Literal.ENTERING);
		FinanceMain fm = schdData.getFinanceMain();
		long finID = fm.getFinID();
		schdData.setRepayDetails(getFinRepayList(finID));
		schdData.setPenaltyDetails(getFinancePenaltysByFinRef(finID));
		logger.debug(Literal.LEAVING);
		return schdData;
	}

	/**
	 * Method for Get the Accrue Details
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public BigDecimal getAccrueAmount(long finID) {
		return profitDetailsDAO.getAccrueAmount(finID);
	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference (String)
	 * @param isWIF        (boolean)
	 **/
	public FinScheduleData getFinSchDataById(long finID, String type, boolean summaryRequired) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = new FinScheduleData();
		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, type, false);

		FinanceHoldDetail financeHoldDetails = holdRefundUploadDAO.getFinanceHoldDetails(finID, type, false);
		if (financeHoldDetails != null) {
			fm.setHoldStatus(financeHoldDetails.getHoldStatus());
			fm.setReason(financeHoldDetails.getReason());
		}

		String productCategory = fm.getProductCategory();
		String finReference = fm.getFinReference();

		setDasAndDmaData(fm);

		schdData.setFinID(fm.getFinID());
		schdData.setFinReference(finReference);

		schdData.setFinanceMain(fm);

		// Overdraft Schedule Details
		if (FinanceConstants.PRODUCT_ODFACILITY.equals(productCategory)) {
			schdData.setOverdraftScheduleDetails(
					overdraftScheduleDetailDAO.getOverdraftScheduleDetails(finID, "_Temp", false));
		}

		// Schedule details
		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false));

		// Disbursement Details
		schdData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, type, false));

		// Repay instructions
		schdData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, type, false));

		// od penality details
		schdData.setFinODPenaltyRate(finODPenaltyRateDAO.getEffectivePenaltyRate(finID, type));

		if (summaryRequired) {
			FinanceType financeType = financeTypeDAO.getFinanceTypeByFinType(fm.getFinType());
			if (StringUtils.isNotBlank(fm.getPromotionCode())) {
				// Fetching Promotion Details
				Promotion promotion = this.promotionDAO.getPromotionByReferenceId(fm.getPromotionSeqId(), "_AView");
				financeType.setFInTypeFromPromotiion(promotion);
			}
			schdData.setFinanceType(financeType);

			// Suspense
			schdData.setFinPftSuspended(false);
			FinanceSuspHead financeSuspHead = financeSuspHeadDAO.getFinanceSuspHeadById(finID, "");
			if (financeSuspHead != null && financeSuspHead.isFinIsInSusp()) {
				schdData.setFinPftSuspended(true);
				schdData.setFinSuspDate(financeSuspHead.getFinSuspDate());
			}

			// Finance Summary Details Preparation
			final Date curBussDate = SysParamUtil.getAppDate();
			FinanceSummary summary = new FinanceSummary();
			summary.setFinID(fm.getFinID());
			summary.setFinReference(fm.getFinReference());
			summary.setSchDate(curBussDate);

			if (fm.isAllowGrcPeriod() && curBussDate.compareTo(fm.getNextGrcPftDate()) <= 0) {
				summary.setNextSchDate(fm.getNextGrcPftDate());
			} else if (fm.getNextRepayDate().compareTo(fm.getNextRepayPftDate()) < 0) {
				summary.setNextSchDate(fm.getNextRepayDate());
			} else {
				summary.setNextSchDate(fm.getNextRepayPftDate());
			}

			summary.setFinCurODDays(profitDetailsDAO.getCurOddays(finID));
			schdData.setFinanceSummary(summary);

			FinODDetails finODDetails = finODDetailsDAO.getFinODSummary(finID);
			if (finODDetails != null) {
				summary.setFinODTotPenaltyAmt(finODDetails.getTotPenaltyAmt());
				summary.setFinODTotWaived(finODDetails.getTotWaived());
				summary.setFinODTotPenaltyPaid(finODDetails.getTotPenaltyPaid());
				summary.setFinODTotPenaltyBal(finODDetails.getTotPenaltyBal());
			}
		}

		logger.debug(Literal.LEAVING);
		return schdData;
	}

	/**
	 * Method for Fetching Profit Details for Particular Finance Reference
	 * 
	 * @param finReference
	 * @return
	 */
	public FinanceProfitDetail getFinProfitDetailsById(long finID) {
		return profitDetailsDAO.getFinProfitDetailsById(finID);
	}

	/**
	 * Method for Fetching Profit Details
	 */
	public FinanceSummary getFinanceProfitDetails(long finID) {
		return financeMainDAO.getFinanceProfitDetails(finID);
	}

	@Override
	public List<ReturnDataSet> getPostingsByFinRefAndEvent(String finReference, String finEvent, boolean showZeroBal,
			String postingGroupBy, String type) {
		List<ReturnDataSet> postings = postingsDAO.getPostingsByFinRefAndEvent(finReference, finEvent, showZeroBal,
				postingGroupBy, type);
		List<VASRecording> vasReferences = vasRecordingDAO.getVASRecordingsByLinkRef(finReference, "");
		if (CollectionUtils.isNotEmpty(vasReferences)) {
			for (VASRecording vas : vasReferences) {
				if (postings != null) {
					postings.addAll(postingsDAO.getPostingsByFinRefAndEvent(vas.getVasReference(), "'INSPAY'",
							showZeroBal, postingGroupBy, ""));
				}
			}
		}
		return postings;
	}

	/**
	 * Method for fetching list of entries executed based on Linked Transaction ID
	 */
	@Override
	public List<ReturnDataSet> getPostingsByLinkTransId(long linkedTranid) {
		return postingsDAO.getPostingsByLinkTransId(linkedTranid);
	}

	/**
	 * Method to CheckLimits
	 * 
	 * @param AuditHeader
	 * 
	 *                    1. Check limit category exists or not for the account type, if not exists set limitValid =
	 *                    true other wise goto next step. 2. Fetch customer limits from core banking. 3. If the limits
	 *                    not available set the ErrMessage. 4. If available limit is less than finance amount, set
	 *                    warning message if the user have the permission 'override Limits' otherwise set Error message.
	 * 
	 */
	public AuditHeader doCheckLimits(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);
		FinanceDetail finDetails = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = finDetails.getFinScheduleData().getFinanceMain();

		financeMain.setLimitValid(true);
		auditHeader.getAuditDetail().setModelData(finDetails);
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Method for Checking Black List Abuser data Against Customer Included in Finance
	 * 
	 * @param financeDetail
	 * @return
	 */
	@Override
	public boolean checkExistCustIsBlackListed(long custId) {
		logger.debug(Literal.ENTERING);

		String custCRCPR = customerDAO.getCustCRCPRById(custId, "");
		if (StringUtils.isNotBlank(custCRCPR)) {
			Date blackListDate = customerDAO.getCustBlackListedDate(custCRCPR, "");
			if (blackListDate != null) {
				logger.debug(Literal.LEAVING);
				return true;
			}
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	/**
	 * Method for Checking exception List based upon Requirements
	 */
	public AuditHeader doCheckExceptions(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		FinanceDetail aFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		// Check for Exception
		aFinanceMain.setException(false);

		// *** Case 1 : Amount Case Check Exception for 100K BHD ***
		String dftCcy = SysParamUtil.getAppCurrency();
		final BigDecimal finAmount = PennantApplicationUtil.formateAmount(aFinanceMain.getFinAmount(),
				CurrencyUtil.getFormat(aFinanceMain.getFinCcy()));
		if (dftCcy.equals(aFinanceMain.getFinCcy())) {
			aFinanceMain.setAmount(finAmount);
		} else {
			// Covert Amount into BHD Format
			Currency fCurrency = CurrencyUtil.getCurrencyObject(aFinanceMain.getFinCcy());
			aFinanceMain.setAmount(finAmount.multiply(fCurrency.getCcySpotRate()));
		}

		if (aFinanceMain.getAmount().compareTo(BigDecimal.valueOf(100000.000)) > 0) {
			aFinanceMain.setException(true);
		}
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
		auditHeader.getAuditDetail().setModelData(aFinanceDetail);

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * Method for Calculating Exchange Rate for Fiannce Schedule Calculation
	 * 
	 * @param amount
	 * @param aCurrency
	 * @return
	 */
	private BigDecimal calculateExchangeRate(BigDecimal amount, Currency aCurrency) {
		String dftCcy = SysParamUtil.getAppCurrency();

		if (StringUtils.equals(dftCcy, aCurrency.getCcyCode())) {
			return amount;
		} else {
			if (amount == null) {
				amount = BigDecimal.ZERO;
			}

			amount = amount.multiply(aCurrency.getCcySpotRate());
		}
		return amount;
	}

	@Override
	public void updateCustCIF(long custID, long finID) {
		financeMainDAO.updateCustCIF(custID, finID);

	}

	// Document Details List Maintainance
	public void listDocDeletion(FinanceDetail financeDetail, String tableType) {
		documentDetailsDAO.deleteList(new ArrayList<DocumentDetails>(financeDetail.getDocumentDetailsList()),
				tableType);
	}

	@Override
	public List<DocumentDetails> getFinDocByFinRef(String finReference, String finEvent, String type) {
		return documentDetailsDAO.getDocumentDetailsByRef(finReference, FinanceConstants.MODULE_NAME, finEvent, type);
	}

	@Override
	public DocumentDetails getFinDocDetailByDocId(long docId) {
		return documentDetailsDAO.getDocumentDetailsById(docId, "");
	}

	// ******************************************************//
	// ***************** Additional Details *****************//
	// ******************************************************//

	/**
	 * Method for Save/ Update Additional Field Details
	 */
	@SuppressWarnings("deprecation")
	public void doSaveAddlFieldDetails(FinanceDetail financeDetail, String tableType) {
		logger.debug(Literal.ENTERING);

		if (financeDetail.getLovDescExtendedFieldValues() != null
				&& financeDetail.getLovDescExtendedFieldValues().size() > 0) {

			String tableName = "";
			if (PennantStaticListUtil.getModuleName().containsKey("Finance")) {
				tableName = PennantStaticListUtil.getModuleName().get("Finance")
						.get(financeDetail.getFinScheduleData().getFinanceMain().getFinPurpose());
			}

			if (!extendedFieldDetailDAO.isExist(tableName, financeDetail.getFinScheduleData().getFinReference(),
					tableType)) {
				extendedFieldDetailDAO.saveAdditional(financeDetail.getFinScheduleData().getFinReference(),
						financeDetail.getLovDescExtendedFieldValues(), tableType, tableName);
			} else {
				extendedFieldDetailDAO.updateAdditional(financeDetail.getLovDescExtendedFieldValues(),
						financeDetail.getFinScheduleData().getFinReference(), tableType, tableName);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Delete Additional Field Details
	 */
	@SuppressWarnings("deprecation")
	private void doDeleteAddlFieldDetails(FinanceDetail financeDetail, String tableType) {
		logger.debug(Literal.ENTERING);

		if (financeDetail.getLovDescExtendedFieldValues() != null
				&& financeDetail.getLovDescExtendedFieldValues().size() > 0) {

			String tableName = "";
			if (PennantStaticListUtil.getModuleName().containsKey("Finance")) {
				tableName = PennantStaticListUtil.getModuleName().get("Finance")
						.get(financeDetail.getFinScheduleData().getFinanceMain().getFinPurpose());
			}

			extendedFieldDetailDAO.deleteAdditional(financeDetail.getFinScheduleData().getFinReference(), tableName,
					tableType);
		}
		logger.debug(Literal.LEAVING);
	}

	// ******************************************************//
	// ************* Cust Related Finance Details ***********//
	// ******************************************************//

	@Override
	public FinanceDetail fetchFinCustDetails(FinanceDetail fd, String ctgType, String finType, String userRole,
			String procEdtEvent) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		// Finance Commitment Accounting Posting Details
		if (PennantConstants.RECORD_TYPE_NEW.equals(fm.getRecordType())) {
			if (schdData.getFinanceType().isFinCommitmentReq() && StringUtils.isNotBlank(fm.getFinCommitmentRef())) {

				long accountingSetId = accountingSetDAO.getAccountingSetId(AccountingEvent.CMTDISB,
						AccountingEvent.CMTDISB);
				if (accountingSetId != 0) {
					fd.setCmtFinanceEntries(
							transactionEntryDAO.getListTransactionEntryById(accountingSetId, "_AEView", true));
				}
			}
		}

		// Finance Stage Accounting Posting Details
		fd.setStageTransactionEntries(transactionEntryDAO.getListTransactionEntryByRefType(finType,
				StringUtils.isEmpty(procEdtEvent) ? FinServiceEvent.ORG : procEdtEvent,
				FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

		// Set Eligibility Details to finaceDetail
		long finID = fm.getFinID();
		fd.setElgRuleList(eligibilityDetailService.setFinanceEligibilityDetails(finID, fm.getFinCcy(),
				fm.getFinAmount(), fm.isNewRecord(), finType, userRole, procEdtEvent));

		// Set Scoring Details to finaceDetail
		scoringDetailService.setFinanceScoringDetails(fd, finType, userRole, ctgType, procEdtEvent);

		// Reset Finance Document Details
		String preAppref = StringUtils.trimToEmpty(fm.getFinPreApprovedRef());
		if (fm.isNewRecord() && ("".equals(preAppref) || preAppref.equals(FinServiceEvent.PREAPPROVAL))) {
			fd.setDocumentDetailsList(new ArrayList<DocumentDetails>(1));
		}

		// Set Check List Details to finaceDetail
		checkListDetailService.setFinanceCheckListDetails(fd, finType,
				StringUtils.isEmpty(procEdtEvent) ? FinServiceEvent.ORG : procEdtEvent, userRole);

		logger.debug(Literal.LEAVING);
		return fd;
	}

	public FinanceMain getFinanceMainParms(long finID) {
		return financeMainDAO.getFinanceMainParms(finID);
	}

	@Override
	public String getCustStatusByMinDueDays() {
		CustomerStatusCode customerStatusCode = customerStatusCodeDAO.getCustStatusByMinDueDays("");
		if (customerStatusCode != null) {
			return customerStatusCode.getCustStsCode();
		}
		return "";
	}

	@Override
	public List<CustomerIncome> prepareIncomeDetails() {
		logger.debug(Literal.ENTERING);

		List<IncomeType> incomeTypeList = getIncomeTypeDAO().getIncomeTypeList();
		List<CustomerIncome> customerIncomes = new ArrayList<CustomerIncome>();
		for (IncomeType incomeType : incomeTypeList) {
			CustomerIncome income = new CustomerIncome();
			income.setIncomeExpense(incomeType.getIncomeExpense().trim());
			income.setIncomeType(incomeType.getIncomeTypeCode().trim());
			income.setJointCust(false);
			income.setMargin(BigDecimal.ZERO);
			income.setCategory(incomeType.getCategory().trim());
			income.setIncome(BigDecimal.ZERO);
			income.setVersion(1);
			income.setRecordType(PennantConstants.RCD_ADD);
			income.setWorkflowId(0);
			income.setCategoryDesc(incomeType.getLovDescCategoryName().trim());
			income.setIncomeTypeDesc(incomeType.getIncomeTypeDesc().trim());

			customerIncomes.add(income);
		}
		logger.debug(Literal.LEAVING);
		return customerIncomes;
	}

	/**
	 * Method to update Task_log table
	 * 
	 * @param fm
	 */
	private void updateTaskLog(FinanceMain fm, boolean isSaveorUpdate) {
		logger.debug(Literal.ENTERING);

		if (StringUtils.equalsIgnoreCase("Y", SysParamUtil.getValueAsString("ALLOW_LOAN_APP_LOCK"))) {
			if (!PennantConstants.RCD_STATUS_SAVED.equals(fm.getRecordStatus())) {
				Map<String, String> roleUsers = new HashMap<>();
				roleUsers.put(fm.getRoleCode(), String.valueOf(fm.getLastMntBy()));

				String[] nextRoles = StringUtils.split(fm.getNextRoleCode(), ",");

				for (String nextRole : nextRoles) {
					if (!fm.getNextRoleCode().contains(fm.getRoleCode())) {
						roleUsers.remove(fm.getRoleCode());
						roleUsers.put(nextRole, String.valueOf("0"));
					}
				}

				fm.setLovDescNextUsersRolesMap(roleUsers);

				if (fm.getLovDescNextUsersRolesMap() != null) {
					saveUserActivityDetails(fm);
				}

				userActivityLogDAO.updateFinStatus(fm.getFinReference(), PennantConstants.WORFLOW_MODULE_FINANCE);

				fm.setNextUserId(null);
			}

			return;
		}

		List<QueueAssignment> queueAssignList = new ArrayList<QueueAssignment>();
		List<TaskOwners> taskOwnerList = new ArrayList<TaskOwners>();
		TaskOwners taskOwner = null;
		boolean addRecord = false;
		String roleCode = null;
		String userId = "";

		if (PennantConstants.RCD_STATUS_RESUBMITTED.equals(fm.getRecordStatus()) && !fm.isNewRecord()) {
			TaskOwners owner = fetchTaskOwner(fm.getFinReference(), fm.getRoleCode());
			if (owner.getCurrentOwner() == 0) {
				owner.setCurrentOwner(fm.getLastMntBy());
				owner.setActualOwner(fm.getLastMntBy());
			}
			owner.setNewRecord(false);
			owner.setProcessed(true);
			taskOwnerList.add(owner);

			// Update resubmitting task owner in queue assignment
			queueAssignList = getResubmitQueueDetails(fm, owner.isProcessed());

			roleCode = fm.getNextRoleCode();
		} else {
			roleCode = fm.getRoleCode();
			userId = String.valueOf(fm.getLastMntBy());
		}

		List<TaskOwners> existingTaskOwners = getTaskOwnersDAO().getTaskOwnerList(fm.getFinReference(), roleCode);
		if (existingTaskOwners.size() == 0) {
			taskOwner = new TaskOwners();
			taskOwner.setReference(fm.getFinReference());
			taskOwner.setRoleCode(fm.getRoleCode());
			taskOwner.setActualOwner(fm.getLastMntBy());
			taskOwner.setCurrentOwner(fm.getLastMntBy());
			if (!PennantConstants.RCD_STATUS_SAVED.equals(fm.getRecordStatus())) {
				taskOwner.setProcessed(true);
			}
			taskOwner.setNewRecord(true);
			existingTaskOwners.add(taskOwner);
			addRecord = true;
		} else {
			for (int i = 0; i < existingTaskOwners.size(); i++) {
				taskOwner = existingTaskOwners.get(i);
				taskOwner.setNewRecord(false);

				if (PennantConstants.RCD_STATUS_SAVED.equals(fm.getRecordStatus()) || (taskOwner.getCurrentOwner() != 0
						&& PennantConstants.RCD_STATUS_RESUBMITTED.equals(fm.getRecordStatus()))) {
					taskOwner.setProcessed(false);
				} else {
					taskOwner.setProcessed(true);
				}

				if (taskOwner.getCurrentOwner() == 0 && taskOwner.getRoleCode().equals(fm.getRoleCode())) {
					taskOwner.setActualOwner(fm.getLastMntBy());
					taskOwner.setCurrentOwner(fm.getLastMntBy());
					addRecord = true;
				} else {
					if (StringUtils.isEmpty(userId)) {
						userId = userId.concat(taskOwner.getCurrentOwner() + ",");
						if (i == existingTaskOwners.size() - 1) {
							userId = userId.substring(0, userId.length() - 1);
						}
					}
				}
			}
		}

		if (fm.isNewRecord() || !PennantConstants.RCD_STATUS_SAVED.equals(fm.getRecordStatus()) || addRecord) {
			queueAssignList.addAll(
					addQueueAssignmentDetails(existingTaskOwners, fm, userId, roleCode, taskOwner.isProcessed()));
			taskOwnerList.addAll(existingTaskOwners);
		}

		String nextUsers = null;
		if (isSaveorUpdate && StringUtils.isNotEmpty(fm.getNextTaskId())) {
			addTaskQueueDetails(fm, queueAssignList, taskOwnerList);
			nextUsers = fm.getNextUserId();
		}

		if (fm.getLovDescNextUsersRolesMap() != null) {
			saveUserActivityDetails(fm);
			getTaskOwnersDAO().saveOrUpdateList(taskOwnerList);
			if (queueAssignList.size() > 0 && StringUtils.isNotBlank(fm.getLovDescAssignMthd())
					&& (!StringUtils.equals(fm.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)
							|| (fm.isQuickDisb()))) {
				getQueueAssignmentDAO().saveOrUpdate(queueAssignList);
			}
		}

		if (isSaveorUpdate) {
			List<Long> refList = new ArrayList<>();
			refList.add(fm.getFinID());
			financeMainDAO.updateNextUserId(refList, "", nextUsers, true); // Update
																			// nextuserid
																			// value
																			// in
																			// finance
																			// table
		} else {
			getUserActivityLogDAO().updateFinStatus(fm.getFinReference(), PennantConstants.WORFLOW_MODULE_FINANCE);
		}
		logger.debug(Literal.LEAVING);
	}

	private List<QueueAssignment> getResubmitQueueDetails(FinanceMain financeMain, boolean isProcessed) {
		// Update resubmitting task owner in queue assignment
		List<QueueAssignment> queueList = getQueueAssignmentDAO().getQueueAssignmentList(
				String.valueOf(financeMain.getLastMntBy()), PennantConstants.WORFLOW_MODULE_FINANCE,
				financeMain.getRoleCode());
		QueueAssignment queueAssignment = null;
		List<QueueAssignment> queueAssignmentList = new ArrayList<QueueAssignment>();
		if (queueList.isEmpty()) {
			queueAssignment = getNewQueueAssignment(financeMain.getRoleCode(), financeMain.getLastMntBy(), true);
			queueAssignment.setLastAssignedOn(financeMain.getLastMntOn());
			queueAssignment.setLovDescUserAction(financeMain.getRecordStatus());
			queueAssignment.setLovDescQAUserId(0);
			queueAssignment.setAssignedCount(isProcessed ? 0 : 1);
			queueAssignment.setProcessedCount(isProcessed ? 1 : 0);
			queueAssignmentList.add(queueAssignment);
			queueAssignment = null;
		} else {
			for (int i = 0; i < queueList.size(); i++) {
				queueAssignment = queueList.get(i);
				queueAssignment.setNewRecord(false);
				queueAssignment.setRecordProcessed(isProcessed);
				queueAssignment.setAssignedCount(1);
				queueAssignment.setProcessedCount(isProcessed ? 1 : 0);
				queueAssignmentList.add(queueAssignment);
				queueAssignment = null;
			}
		}
		return queueAssignmentList;
	}

	private List<QueueAssignment> addQueueAssignmentDetails(List<TaskOwners> dbTaskOwnersList, FinanceMain financeMain,
			String userId, String roleCode, boolean isProcessed) {
		List<QueueAssignment> queueAssignmentList = new ArrayList<QueueAssignment>(dbTaskOwnersList.size());
		QueueAssignment queueAssignment = null;
		List<QueueAssignment> queueList = null;
		if (StringUtils.isNotEmpty(userId)) {
			queueList = getQueueAssignmentDAO().getQueueAssignmentList(userId, PennantConstants.WORFLOW_MODULE_FINANCE,
					roleCode);
		}
		if (queueList == null || queueList.size() == 0) {
			queueAssignment = getNewQueueAssignment(financeMain.getRoleCode(), financeMain.getLastMntBy(), true);
			queueAssignment.setLastAssignedOn(financeMain.getLastMntOn());
			queueAssignment.setLovDescUserAction(financeMain.getRecordStatus());
			queueAssignment.setLovDescQAUserId(0);
			queueAssignment.setAssignedCount(isProcessed ? 0 : 1);
			queueAssignment.setProcessedCount(isProcessed ? 1 : 0);
			queueAssignmentList.add(queueAssignment);
		} else {
			for (TaskOwners owner : dbTaskOwnersList) {
				if (owner.getCurrentOwner() != 0) {
					for (int i = 0; i < queueList.size(); i++) {
						queueAssignment = queueList.get(i);
						queueAssignment.setNewRecord(false);
						queueAssignment.setRecordProcessed(isProcessed);
						if (PennantConstants.RCD_STATUS_RESUBMITTED.equals(financeMain.getRecordStatus())) {
							if (owner.getCurrentOwner() == queueAssignment.getUserId()
									&& owner.getRoleCode().equals(queueAssignment.getUserRoleCode())) {
								queueAssignment.setProcessedCount(-1);
								queueAssignment.setAssignedCount(1);
								queueAssignmentList.add(queueAssignment);
								break;
							}
						} else if (!PennantConstants.RCD_STATUS_SAVED.equals(financeMain.getRecordStatus())) {
							queueAssignment.setProcessedCount(1);
							queueAssignment.setAssignedCount(1);
							queueAssignmentList.add(queueAssignment);
							break;
						} else if (financeMain.isNewRecord()
								&& PennantConstants.RCD_STATUS_SAVED.equals(financeMain.getRecordStatus())) {
							queueAssignment.setAssignedCount(1);
							queueAssignment.setProcessedCount(0);
							queueAssignmentList.add(queueAssignment);
							break;
						}
					}
				}
			}
		}
		return queueAssignmentList;
	}

	private void addTaskQueueDetails(FinanceMain financeMain, List<QueueAssignment> queueAssignList,
			List<TaskOwners> taskOwnerList) {
		logger.debug(Literal.ENTERING);
		String nextUsers = null;
		Map<String, String> nextUsersRolesMap = new HashMap<String, String>();
		nextUsersRolesMap.put(financeMain.getRoleCode(), String.valueOf(financeMain.getLastMntBy()));

		if (!PennantConstants.RCD_STATUS_SAVED.equals(financeMain.getRecordStatus())
				&& !PennantConstants.RCD_STATUS_RESUBMITTED.equals(financeMain.getRecordStatus())) {
			String nextRoleCodes[] = null;
			if (financeMain.getNextRoleCode().contains(",")) {
				nextRoleCodes = financeMain.getNextRoleCode().split(",");
			} else {
				nextRoleCodes = new String[] { financeMain.getNextRoleCode() };
			}
			String baseRole = "";
			TaskOwners taskOwner = null;
			QueueAssignment queueAssignment = null;
			String excludeUsers = String.valueOf(financeMain.getLastMntBy());
			for (int i = 0; i < nextRoleCodes.length; i++) {
				long nextUserId = 0;

				// Check if any base role available for the role code
				if (financeMain.getLovDescBaseRoleCodeMap() != null) {
					baseRole = financeMain.getLovDescBaseRoleCodeMap().get(nextRoleCodes[i]);
				}

				// If base role available, check for the BASE ROLE user in Task
				// Owners table else for the ROLE user and
				// assign
				if (StringUtils.isNotEmpty(baseRole)) {
					taskOwner = fetchTaskOwner(financeMain.getFinReference(), baseRole);
					nextUserId = taskOwner.getCurrentOwner();
					TaskOwners temp = fetchTaskOwner(financeMain.getFinReference(), nextRoleCodes[i]);
					if (null != temp && (temp.getCurrentOwner() == nextUserId || temp.getCurrentOwner() == 0)) {
						taskOwner.setNewRecord(false);
					} else {
						taskOwner.setNewRecord(true);
					}
					taskOwner.setProcessed(false);
					taskOwner.setRoleCode(nextRoleCodes[i]);
					taskOwnerList.add(taskOwner);

					if (taskOwner.getCurrentOwner() != 0) {
						queueAssignment = getNewQueueAssignment(taskOwner.getRoleCode(), taskOwner.getCurrentOwner(),
								false);
						queueAssignment.setRecordProcessed(false);
						queueAssignment.setAssignedCount(1);
						queueAssignment.setProcessedCount(0);
						queueAssignList.add(queueAssignment);
					}

				} else {
					taskOwner = fetchTaskOwner(financeMain.getFinReference(), nextRoleCodes[i]);
					if (null == taskOwner) {
						if (StringUtils.trimToEmpty(financeMain.getLovDescAssignMthd())
								.equalsIgnoreCase(PennantConstants.AUTO_ASSIGNMENT)) {

							queueAssignment = getQueueAssignmentDAO().getNewUserId(
									PennantConstants.WORFLOW_MODULE_FINANCE, nextRoleCodes[i], excludeUsers);
							queueAssignment.setModule(PennantConstants.WORFLOW_MODULE_FINANCE);
							queueAssignment.setUserRoleCode(nextRoleCodes[i]);
							queueAssignment.setNewRecord(false);
							queueAssignment.setRecordProcessed(false);
							queueAssignment.setAssignedCount(1);
							queueAssignment.setProcessedCount(0);
							queueAssignList.add(queueAssignment);

							nextUserId = queueAssignment.getUserId();
						}
						taskOwner = new TaskOwners();
						taskOwner.setReference(financeMain.getFinReference());
						taskOwner.setRoleCode(nextRoleCodes[i]);
						taskOwner.setActualOwner(nextUserId);
						taskOwner.setCurrentOwner(nextUserId);
						taskOwner.setProcessed(false);
						taskOwner.setNewRecord(true);
						taskOwnerList.add(taskOwner);
					} else {
						taskOwner.setProcessed(false);
						taskOwner.setNewRecord(false);
						taskOwnerList.add(taskOwner);

						if (taskOwner.getCurrentOwner() != 0 && !StringUtils
								.trimToEmpty(financeMain.getBefImage().getNextRoleCode()).contains(nextRoleCodes[i])) {
							queueAssignment = getNewQueueAssignment(taskOwner.getRoleCode(),
									taskOwner.getCurrentOwner(), false);
							queueAssignment.setRecordProcessed(false);
							queueAssignment.setAssignedCount(1);
							queueAssignment.setProcessedCount(0);
							queueAssignList.add(queueAssignment);
						}
						nextUserId = taskOwner.getCurrentOwner();
					}
				}

				excludeUsers = excludeUsers.concat("," + String.valueOf(nextUserId));

				if (!financeMain.getNextRoleCode().contains(financeMain.getRoleCode())) {
					nextUsersRolesMap.remove(financeMain.getRoleCode());
					nextUsersRolesMap.put(nextRoleCodes[i], String.valueOf(nextUserId));
				} else {
					nextUsers = String.valueOf(financeMain.getLastMntBy());
				}

				if (nextUserId != 0 && (!financeMain.getNextRoleCode().contains(financeMain.getRoleCode()))) {
					if (StringUtils.isBlank(nextUsers)) {
						nextUsers = String.valueOf(nextUserId);
					} else {
						nextUsers = nextUsers.concat("," + String.valueOf(nextUserId));
					}
				}
			}
		} else {
			if (PennantConstants.RCD_STATUS_RESUBMITTED.equals(financeMain.getRecordStatus())) {
				TaskOwners taskOwnerTemp = fetchTaskOwner(financeMain.getFinReference(), financeMain.getNextRoleCode());
				if (taskOwnerTemp != null) {
					financeMain.setNextUserId(String.valueOf(taskOwnerTemp.getCurrentOwner()));
				}
			}
			nextUsers = financeMain.getNextUserId() == null ? String.valueOf(financeMain.getLastMntBy())
					: financeMain.getNextUserId();
		}

		financeMain.setLovDescNextUsersRolesMap(nextUsersRolesMap);
		financeMain.setNextUserId(nextUsers);
		logger.debug(Literal.LEAVING);
	}

	private void saveUserActivityDetails(FinanceMain financeMain) {
		List<UserActivityLog> logList = new ArrayList<UserActivityLog>();
		for (Map.Entry<String, String> entry : financeMain.getLovDescNextUsersRolesMap().entrySet()) {
			UserActivityLog userActivityLog = new UserActivityLog();
			userActivityLog.setModule(PennantConstants.WORFLOW_MODULE_FINANCE);
			userActivityLog.setReference(financeMain.getFinReference());
			userActivityLog.setFromUser(financeMain.getLastMntBy());
			userActivityLog.setActivity(financeMain.getRecordStatus());
			userActivityLog.setRoleCode(financeMain.getRoleCode());
			if (Long.parseLong(entry.getValue()) != 0) {
				userActivityLog.setToUser(Long.parseLong(entry.getValue()));
			}
			userActivityLog.setNextRoleCode(entry.getKey());
			userActivityLog.setLogTime(financeMain.getLastMntOn());
			userActivityLog.setProcessed(false);
			if (StringUtils.isEmpty(financeMain.getNextTaskId())) {
				userActivityLog.setRoleCode(entry.getKey());
				userActivityLog.setToUser(Long.valueOf(0));
			}
			logList.add(userActivityLog);
		}
		getUserActivityLogDAO().saveList(logList); // Always Save / Insert
	}

	private QueueAssignment getNewQueueAssignment(String usrRoleCode, long usrID, boolean isNewRcd) {
		QueueAssignment queueAssignment = new QueueAssignment();
		queueAssignment.setModule(PennantConstants.WORFLOW_MODULE_FINANCE);
		queueAssignment.setUserRoleCode(usrRoleCode);
		queueAssignment.setUserId(usrID);
		queueAssignment.setNewRecord(isNewRcd);
		return queueAssignment;
	}

	private TaskOwners fetchTaskOwner(String finRef, String roleCode) {
		return taskOwnersDAO.getTaskOwner(finRef, roleCode);
	}

	@Override
	public BigDecimal getCustRepayBankTotal(long custId) {
		return customerDAO.getCustRepayBankTotal(custId);
	}

	@Override
	public String getApprovedRepayMethod(long finID, String type) {
		return financeMainDAO.getApprovedRepayMethod(finID, type);
	}

	@Override
	public FeeRule getFeeChargesByFinRefAndFeeCode(long finID, String feeCode, String tableType) {
		return finFeeChargesDAO.getFeeChargesByFinRefAndFee(finID, feeCode, tableType);
	}

	@Override
	public boolean updateFeeChargesByFinRefAndFeeCode(FeeRule feeRule, String tableType) {
		return finFeeChargesDAO.updateFeeChargesByFinRefAndFee(feeRule, tableType);
	}

	@Override
	public List<FinanceSummary> getFinExposureByCustId(long custId) {
		return financeMainDAO.getFinExposureByCustId(custId);
	}

	@Override
	public BigDecimal getTotalRepayAmount(long finID) {
		return financeScheduleDetailDAO.getTotalRepayAmount(finID);
	}

	// ******************************************************//
	// *************Queue Assignment Details*****************//
	// ******************************************************//

	@Override
	public String getUserRoleCodeByRefernce(long userId, String reference, List<String> userRoles) {
		logger.debug(Literal.ENTERING);
		String usrRoleCode = getTaskOwnersDAO().getUserRoleCodeByRefernce(userId, reference, userRoles);
		logger.debug(Literal.LEAVING);
		return usrRoleCode;
	}

	/**
	 * @param finReference
	 * @return
	 */
	@Override
	public FinanceDetail getPreApprovalFinanceDetailsById(long finID) {
		logger.debug(" Entering ");
		String type = PennantConstants.PREAPPROVAL_TABLE_TYPE;

		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, type, false);

		String finReference = fm.getFinReference();

		schdData.setFinID(finID);
		schdData.setFinReference(finReference);

		schdData.setFinanceMain(fm);

		// Step Policy Details List
		if (fm.isStepFinance()) {
			schdData.setStepPolicyDetails(financeStepDetailDAO.getFinStepDetailListByFinRef(finID, type, false));
		}

		// Finance Schedule Details
		schdData.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, type, false));

		// Finance Disbursement Details
		schdData.setDisbursementDetails(financeDisbursementDAO.getFinanceDisbursementDetails(finID, type, false));

		// Finance Repayments Instruction Details
		schdData.setRepayInstructions(repayInstructionDAO.getRepayInstructions(finID, type, false));

		// Finance Overdue Penalty Rate Details
		schdData.setFinODPenaltyRate(finODPenaltyRateDAO.getEffectivePenaltyRate(finID, type));

		// Fetch Finance Premium Details
		// financeDetail.setPremiumDetail(getFinancePremiumDetailDAO().getFinPremiumDetailsById(finReference,
		// type));

		// scheduleData.setFeeRules(finFeeChargesDAO.getFeeChargesByFinRef(finReference,FinServiceEvent.PREAPPROVAL,
		// false, type));

		// Finance Fee Schedule Details
		schdData.setFinFeeDetailList(finFeeDetailService.getFinFeeDetailById(finID, false, type));

		// Finance Guaranteer Details
		fd.setGurantorsDetailList(guarantorDetailService.getGuarantorDetail(finID, type));

		// Finance Joint Account Details
		fd.setJointAccountDetailList(jointAccountDetailService.getJoinAccountDetail(finID, type));

		// Asset Type Details
		fd.setFinAssetTypesList(finAssetTypeDAO.getFinAssetTypesByFinRef(finReference, "_Temp"));

		// Extended Field Details for Assets
		fd.setExtendedFieldRenderList(getExtendedAssetDetails(finReference, fd.getFinAssetTypesList()));

		// Collateral Details
		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			fd.setCollateralAssignmentList(collateralAssignmentDAO.getCollateralAssignmentByFinRef(finReference,
					FinanceConstants.MODULE_NAME, "_TView"));
		} else {
			fd.setFinanceCollaterals(finCollateralService.getFinCollateralsByRef(finID, type));
		}

		// document details
		fd.setDocumentDetailsList(documentDetailsDAO.getDocumentDetailsByRef(finReference, FinanceConstants.MODULE_NAME,
				FinServiceEvent.PREAPPROVAL, type));

		logger.debug(" Leaving ");
		return fd;
	}

	private AuditHeader processLimitSaveOrUpdate(AuditHeader aAuditHeader, boolean validateOnly) {

		if (ImplementationConstants.LIMIT_INTERNAL) {

			FinanceDetail financeDetail = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
			FinanceMain finmain = financeDetail.getFinScheduleData().getFinanceMain();
			FinanceType finType = financeDetail.getFinScheduleData().getFinanceType();
			String nextrole = finmain.getNextRoleCode();
			String role = finmain.getRoleCode();
			String moduleType = StringUtils.trimToEmpty(finmain.getRcdMaintainSts());
			String prodCategory = StringUtils.trimToEmpty(finType.getProductCategory());

			// process block
			if (!financeDetail.isActionSave() && !StringUtils.equals(nextrole, role)) {
				// Checking for Limit check Authority i.e Is current Role
				// contains limit check authority (or) Not
				List<FinanceReferenceDetail> limitCheckList = getLimitCheckDetails().doLimitChek(role,
						finmain.getFinType());
				if (limitCheckList == null || limitCheckList.isEmpty()) {
					return aAuditHeader;
				}

				boolean validateReserve = false;
				for (FinanceReferenceDetail finRefDetail : limitCheckList) {
					if (StringUtils.equals(finRefDetail.getLovDescNamelov(), FinanceConstants.PRECHECK)) {
						validateReserve = true;
						break;
					}
				}
				if (validateReserve) {

					if ("".equals(moduleType) || FinServiceEvent.ORG.equals(moduleType)) {
						List<ErrorDetail> errorDetails = getLimitManagement().processLoanLimitOrgination(financeDetail,
								aAuditHeader.isOveride(), LimitConstants.BLOCK, validateOnly);
						if (!errorDetails.isEmpty()) {
							aAuditHeader.setErrorList(errorDetails);
						}
					} else if (moduleType.equals(FinServiceEvent.ADDDISB)
							&& !prodCategory.equals(FinanceConstants.PRODUCT_ODFACILITY)) {
						if (finmain.getFinAssetValue().compareTo(finmain.getFinCurrAssetValue()) == 0) {
							List<ErrorDetail> errorDetails = getLimitManagement().processLoanDisbursments(financeDetail,
									aAuditHeader.isOveride(), LimitConstants.BLOCK, validateOnly);
							if (!errorDetails.isEmpty()) {
								aAuditHeader.setErrorList(errorDetails);
							}
						}

					}
				}
			}
		}
		aAuditHeader = nextProcess(aAuditHeader);
		return aAuditHeader;
	}

	private AuditHeader processLimitApprove(AuditHeader aAuditHeader, boolean validateOnly) {

		if (ImplementationConstants.LIMIT_INTERNAL) {

			FinanceDetail financeDetail = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
			FinanceMain finmain = financeDetail.getFinScheduleData().getFinanceMain();
			FinanceType finType = financeDetail.getFinScheduleData().getFinanceType();
			String moduleType = StringUtils.trimToEmpty(finmain.getRcdMaintainSts());
			String prodCategory = StringUtils.trimToEmpty(finType.getProductCategory());
			// Origination
			if ("".equals(moduleType) || FinServiceEvent.ORG.equals(moduleType)) {

				String transType = "";
				if (prodCategory.equals(FinanceConstants.PRODUCT_ODFACILITY)) {
					transType = LimitConstants.BLOCK;
				} else {
					transType = LimitConstants.APPROVE;
				}
				List<ErrorDetail> errorDetails = getLimitManagement().processLoanLimitOrgination(financeDetail,
						aAuditHeader.isOveride(), transType, validateOnly);
				if (!errorDetails.isEmpty()) {
					aAuditHeader.setErrorList(errorDetails);
				}
			} else {

				if (moduleType.equals(FinServiceEvent.OVERDRAFTSCHD)) {
					List<ErrorDetail> errorDetails = getLimitManagement().processLimitIncrease(financeDetail,
							aAuditHeader.isOveride(), validateOnly);
					if (!errorDetails.isEmpty()) {
						aAuditHeader.setErrorList(errorDetails);
					}
				} else {

					String tranType = "";

					if (moduleType.equals(FinServiceEvent.ADDDISB)) {
						tranType = LimitConstants.APPROVE;
					} else if (moduleType.equals(FinServiceEvent.CANCELDISB)) {
						tranType = LimitConstants.UNBLOCK;
					}

					if (!StringUtils.isBlank(tranType)) {
						List<ErrorDetail> errorDetails = getLimitManagement().processLoanDisbursments(financeDetail,
								aAuditHeader.isOveride(), tranType, validateOnly);
						if (!errorDetails.isEmpty()) {
							aAuditHeader.setErrorList(errorDetails);
						}
					}
				}

			}
		}
		aAuditHeader = nextProcess(aAuditHeader);
		return aAuditHeader;
	}

	/**
	 * Method for Preparing Data for Finance Repay Details Object
	 * 
	 * @param detail
	 * @param main
	 * @param valueDate
	 * @param repayAmtBal
	 * @return
	 */
	private FinanceRepayments prepareBpiRepayData(FinanceMain finMain, Date bpiDate, long linkedTranId,
			BigDecimal bpiAmount, BigDecimal tdsAmount) {
		logger.debug(Literal.ENTERING);

		FinanceRepayments repayment = new FinanceRepayments();
		Date curAppDate = SysParamUtil.getAppDate();

		repayment.setFinID(finMain.getFinID());
		repayment.setFinReference(finMain.getFinReference());
		repayment.setFinSchdDate(bpiDate);
		repayment.setFinRpyFor(FinanceConstants.SCH_TYPE_SCHEDULE);
		repayment.setLinkedTranId(linkedTranId);

		repayment.setFinRpyAmount(bpiAmount);
		repayment.setFinPostDate(curAppDate);
		repayment.setFinValueDate(finMain.getFinStartDate());
		repayment.setFinBranch(finMain.getFinBranch());
		repayment.setFinType(finMain.getFinType());
		repayment.setFinCustID(finMain.getCustID());
		repayment.setFinSchdPftPaid(bpiAmount);
		repayment.setFinSchdTdsPaid(tdsAmount);
		repayment.setFinSchdPriPaid(BigDecimal.ZERO);
		repayment.setFinTotSchdPaid(bpiAmount);
		repayment.setFinFee(BigDecimal.ZERO);
		repayment.setFinWaiver(BigDecimal.ZERO);
		repayment.setFinRefund(BigDecimal.ZERO);

		// Fee Details
		repayment.setSchdFeePaid(BigDecimal.ZERO);

		logger.debug(Literal.LEAVING);
		return repayment;
	}

	@Override
	public String getFinanceMainByRcdMaintenance(long finID) {
		return financeMainDAO.getFinanceMainByRcdMaintenance(finID);
	}

	@Override
	public FinanceMain getRcdMaintenanceByRef(long finID, String type) {
		return financeMainDAO.getRcdMaintenanceByRef(finID, type);
	}

	@Override
	public List<FinanceDisbursement> getFinanceDisbursements(long finID, String type, boolean isWIF) {
		List<FinanceDisbursement> fdd = financeDisbursementDAO.getFinanceDisbursementDetails(finID, type, isWIF);
		// SubventionDetails
		if (subventionService != null) {
			subventionService.setSubventionScheduleDetails(fdd, type);
		}
		return fdd;
	}

	@Override
	public Date getFinStartDate(long finID) {
		return financeMainDAO.getFinStartDate(finID);
	}

	public TATDetail getTATDetail(String reference, String rolecode) {
		return getTatDetailDAO().getTATDetail(reference, rolecode);
	}

	public void saveTATDetail(TATDetail tatDetail) {
		getTatDetailDAO().save(tatDetail);
	}

	public void updateTATDetail(TATDetail tatDetail) {
		getTatDetailDAO().update(tatDetail);
	}

	// ******************************************************//
	// ************ LPO Status Updation Details *************//
	// ******************************************************//

	@Override
	public String getNextRoleCodeByRef(long finID) {
		return financeMainDAO.getNextRoleCodeByRef(finID);

	}

	@Override
	public DocumentDetails getFinDocDetailByDocId(long docId, String type, boolean readAttachment) {
		return documentDetailsDAO.getDocumentDetailsById(docId, type, readAttachment);
	}

	@Override
	public DocumentDetails getDocumentDetails(long docId, String type) {
		return documentDetailsDAO.getDocumentDetails(docId, type);
	}

	@Override
	public List<DocumentDetails> getDocumentDetails(String finReference, String finProcEvent) {
		return documentDetailsDAO.getDocumentDetailsByRef(finReference, FinanceConstants.MODULE_NAME, finProcEvent,
				"_View");
	}

	@Override
	public List<String> getUsersLoginList(List<String> nextRoleCodes) {
		return financeMainDAO.getUsersLoginList(nextRoleCodes);
	}

	@Override
	public FinanceMain getFinanceMainForBatch(long finID) {
		return financeMainDAO.getFinanceMainForBatch(finID);
	}

	@Override
	public FinanceScheduleDetail getFinSchduleDetails(long finID, Date schdDate) {
		return financeScheduleDetailDAO.getFinanceScheduleDetailById(finID, schdDate, "", false);
	}

	// ******************************************************//
	// *************** EOD PROCESS Details ******************//
	// ******************************************************//

	@Override
	public int getProgressCountByCust(long custID) {
		return customerQueuingDAO.getProgressCountByCust(custID);
	}

	@Override
	public FinanceDetail getFinanceDetailForCovenants(FinanceMain fm) {
		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		long finID = fm.getFinID();
		long custID = fm.getCustID();
		String finReference = fm.getFinReference();

		schdData.setFinID(finID);
		schdData.setFinReference(fm.getFinReference());

		schdData.setFinanceMain(fm);

		if (ImplementationConstants.COVENANT_MODULE_NEW) {
			fd.setCovenants(covenantsService.getCovenants(finReference, "Loan", TableType.VIEW));
		} else {
			fd.setCovenantTypeList(finCovenantTypeService.getFinCovenantTypeById(finReference, "_View", false));
		}

		fd.setCustomerDetails(customerDetailsService.getCustomerAndCustomerDocsById(custID, ""));

		fd.setDocumentDetailsList(
				documentDetailsDAO.getDocumentDetailsByRef(finReference, FinanceConstants.MODULE_NAME, "", ""));

		return fd;
	}

	@Override
	public FinanceDetail getFinanceDetailForCollateral(FinanceMain fm) {
		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schData = fd.getFinScheduleData();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();

		schData.setFinID(finID);
		schData.setFinReference(finReference);
		schData.setFinanceMain(fm);

		fd.setCollateralAssignmentList(collateralAssignmentDAO.getCollateralAssignmentByFinRef(finReference,
				FinanceConstants.MODULE_NAME, "_View"));
		fd.setCustomerDetails(customerDetailsService.getCustomerAndCustomerDocsById(fm.getCustID(), ""));

		return fd;
	}

	@Override
	public FinanceDetail getFinanceDetailForFinOptions(FinanceMain fm) {
		FinanceDetail fd = new FinanceDetail();

		FinScheduleData schdData = fd.getFinScheduleData();

		long finID = fm.getFinID();
		String finReference = fm.getFinReference();
		String module = FinanceConstants.MODULE_NAME;

		fm = financeMainDAO.getFinanceMainById(finID, "_View", false);

		schdData.setFinID(finID);
		schdData.setFinReference(finReference);
		schdData.setFinanceMain(fm);

		fd.setFinScheduleData(schdData);
		fd.setFinOptions(finOptionService.getFinOptions(finID, TableType.VIEW));
		fd.setCustomerDetails(customerDetailsService.getCustomerAndCustomerDocsById(fm.getCustID(), ""));
		fd.setDocumentDetailsList(documentDetailsDAO.getDocumentDetailsByRef(finReference, module, "", ""));

		return fd;
	}

	/**
	 * Method for Add loan type Expense to the loan
	 */
	private List<FinTypeExpense> saveFinExpenseDetails(FinanceMain fm) {
		String finType = fm.getFinType();

		List<FinTypeExpense> finTypeExpenseList = finTypeExpenseDAO.getLoanQueueExpenseListByFinType(finType, "");

		for (FinTypeExpense finTypeExpense : finTypeExpenseList) {
			// Expense Amount calculation
			BigDecimal txnAmount = getFinExpenseAmount(finTypeExpense, fm);

			FinExpenseDetails fid = new FinExpenseDetails();

			fid.setFinID(fm.getFinID());
			fid.setFinReference(fm.getFinReference());
			fid.setExpenseTypeId(finTypeExpense.getExpenseTypeID());
			fid.setLastMntOn(fm.getLastMntOn());
			fid.setLastMntBy(fm.getLastMntBy());
			fid.setAmount(txnAmount);

			finExpenseDetailsDAO.saveFinExpenseDetails(fid);
		}
		return finTypeExpenseList;
	}

	/**
	 * Method for Return the amount,calculate the amount for expenses also.
	 */
	private BigDecimal getFinExpenseAmount(FinTypeExpense finTypeExpense, FinanceMain financeMain) {
		BigDecimal txnAmount = BigDecimal.ZERO;

		if (PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT.equals(finTypeExpense.getCalculationType())) {
			txnAmount = finTypeExpense.getAmount();
		} else if (PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE.equals(finTypeExpense.getCalculationType())) {

			BigDecimal percentage = finTypeExpense.getPercentage();
			int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

			if (percentage != null && percentage.compareTo(BigDecimal.ZERO) != 0) {

				BigDecimal assetValue = BigDecimal.ZERO;

				if (PennantConstants.EXPENSE_CALCULATEDON_ODLIMIT.equals(finTypeExpense.getCalculateOn())) {
					if (financeMain.getFinAssetValue() != null
							&& financeMain.getFinAssetValue().compareTo(BigDecimal.ZERO) != 0) {
						assetValue = PennantApplicationUtil.formateAmount(financeMain.getFinAssetValue(), formatter);
					}
				} else if (PennantConstants.EXPENSE_UPLOAD_LOAN.equals(finTypeExpense.getCalculateOn())) {
					if (financeMain.getFinCurrAssetValue() != null
							&& financeMain.getFinCurrAssetValue().compareTo(BigDecimal.ZERO) != 0) {
						assetValue = PennantApplicationUtil.formateAmount(financeMain.getFinCurrAssetValue(),
								formatter);
					}
				}

				txnAmount = (percentage.multiply(assetValue)).divide(new BigDecimal(100));

				txnAmount = PennantApplicationUtil.unFormateAmount(txnAmount, formatter);
			}
		}
		return txnAmount;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public CustomerIncomeDAO getCustomerIncomeDAO() {
		return customerIncomeDAO;
	}

	public void setCustomerIncomeDAO(CustomerIncomeDAO customerIncomeDAO) {
		this.customerIncomeDAO = customerIncomeDAO;
	}

	public RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	public CustomerLimitIntefaceService getCustLimitIntefaceService() {
		return custLimitIntefaceService;
	}

	public void setCustLimitIntefaceService(CustomerLimitIntefaceService custLimitIntefaceService) {
		this.custLimitIntefaceService = custLimitIntefaceService;
	}

	public FinanceWriteoffDAO getFinanceWriteoffDAO() {
		return financeWriteoffDAO;
	}

	public void setFinanceWriteoffDAO(FinanceWriteoffDAO financeWriteoffDAO) {
		this.financeWriteoffDAO = financeWriteoffDAO;
	}

	public AccountTypeDAO getAccountTypeDAO() {
		return accountTypeDAO;
	}

	public void setAccountTypeDAO(AccountTypeDAO accountTypeDAO) {
		this.accountTypeDAO = accountTypeDAO;
	}

	public void setIncomeTypeDAO(IncomeTypeDAO incomeTypeDAO) {
		this.incomeTypeDAO = incomeTypeDAO;
	}

	public IncomeTypeDAO getIncomeTypeDAO() {
		return incomeTypeDAO;
	}

	public NotesDAO getNotesDAO() {
		return notesDAO;
	}

	public void setNotesDAO(NotesDAO notesDAO) {
		this.notesDAO = notesDAO;
	}

	public QueueAssignmentDAO getQueueAssignmentDAO() {
		return queueAssignmentDAO;
	}

	public void setQueueAssignmentDAO(QueueAssignmentDAO queueAssignmentDAO) {
		this.queueAssignmentDAO = queueAssignmentDAO;
	}

	public UserActivityLogDAO getUserActivityLogDAO() {
		return userActivityLogDAO;
	}

	public void setUserActivityLogDAO(UserActivityLogDAO userActivityLogDAO) {
		this.userActivityLogDAO = userActivityLogDAO;
	}

	public TaskOwnersDAO getTaskOwnersDAO() {
		return taskOwnersDAO;
	}

	public void setTaskOwnersDAO(TaskOwnersDAO taskOwnersDAO) {
		this.taskOwnersDAO = taskOwnersDAO;
	}

	public LimitInterfaceDAO getLimitInterfaceDAO() {
		return limitInterfaceDAO;
	}

	public void setLimitInterfaceDAO(LimitInterfaceDAO limitInterfaceDAO) {
		this.limitInterfaceDAO = limitInterfaceDAO;
	}

	public DedupParmService getDedupParmService() {
		return dedupParmService;
	}

	public void setDedupParmService(DedupParmService dedupParmService) {
		this.dedupParmService = dedupParmService;
	}

	public PayOrderIssueHeaderDAO getPayOrderIssueHeaderDAO() {
		return payOrderIssueHeaderDAO;
	}

	public void setPayOrderIssueHeaderDAO(PayOrderIssueHeaderDAO payOrderIssueHeaderDAO) {
		this.payOrderIssueHeaderDAO = payOrderIssueHeaderDAO;
	}

	public TATDetailDAO getTatDetailDAO() {
		return tatDetailDAO;
	}

	public void setTatDetailDAO(TATDetailDAO tatDetailDAO) {
		this.tatDetailDAO = tatDetailDAO;
	}

	public LimitManagement getLimitManagement() {
		return limitManagement;
	}

	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	public LimitCheckDetails getLimitCheckDetails() {
		return limitCheckDetails;
	}

	public void setLimitCheckDetails(LimitCheckDetails limitCheckDetails) {
		this.limitCheckDetails = limitCheckDetails;
	}

	/*
	 * Method to get the schedule change module list from the ScheduleEffectModule table
	 */
	public List<String> getScheduleEffectModuleList(boolean schdChangeReq) {
		return financeMainDAO.getScheduleEffectModuleList(schdChangeReq);
	}

	public List<FinTypeFees> getFinTypeFees(String finType, String eventCode, boolean origination, int moduleId) {
		return finTypeFeesDAO.getFinTypeFeesList(finType, eventCode, "_AView", origination, moduleId);
	}

	public List<FinTypeFees> getSchemeFeesList(long referenceId, String finEvent, String type, boolean origination,
			int moduleId) {
		return getFinTypeFeesDAO().getSchemeFeesList(referenceId, finEvent, type, origination, moduleId);
	}

	@Override
	public List<FinTypeFees> getSchemeFeesList(long referenceId, String eventCode, boolean origination, int moduleId) {
		return getFinTypeFeesDAO().getSchemeFeesList(referenceId, eventCode, "_AView", origination, moduleId);
	}

	@Override
	public List<FinanceStepPolicyDetail> getFinStepPolicyDetails(long finID, String type, boolean isWIF) {
		return financeStepDetailDAO.getFinStepDetailListByFinRef(finID, type, isWIF);
	}

	@Override
	public BigDecimal getOutStandingBalFromFees(long finID) {
		return financeScheduleDetailDAO.getOutStandingBalFromFees(finID);
	}

	public FinTypeFeesDAO getFinTypeFeesDAO() {
		return finTypeFeesDAO;
	}

	public void setFinTypeFeesDAO(FinTypeFeesDAO finTypeFeesDAO) {
		this.finTypeFeesDAO = finTypeFeesDAO;
	}

	public OverdraftScheduleDetailDAO getOverdraftScheduleDetailDAO() {
		return overdraftScheduleDetailDAO;
	}

	public void setOverdraftScheduleDetailDAO(OverdraftScheduleDetailDAO overdraftScheduleDetailDAO) {
		this.overdraftScheduleDetailDAO = overdraftScheduleDetailDAO;
	}

	public FlagDetailValidation getFlagDetailValidation() {
		if (flagDetailValidation == null) {
			this.flagDetailValidation = new FlagDetailValidation(finFlagDetailsDAO);
		}
		return this.flagDetailValidation;
	}

	public void setVasRecordingDAO(VASRecordingDAO vasRecordingDAO) {
		this.vasRecordingDAO = vasRecordingDAO;
	}

	public VasRecordingValidation getVasRecordingValidation() {
		if (vasRecordingValidation == null) {
			this.vasRecordingValidation = new VasRecordingValidation(vasRecordingDAO);
		}
		return this.vasRecordingValidation;
	}

	@Override
	public BigDecimal getFinAssetValue(long finID) {
		return financeMainDAO.getFinAssetValue(finID);
	}

	public void setPromotionDAO(PromotionDAO promotionDAO) {
		this.promotionDAO = promotionDAO;
	}

	public void setFinanceTaxDetailService(FinanceTaxDetailService financeTaxDetailService) {
		this.financeTaxDetailService = financeTaxDetailService;
	}

	public void setExtendedFieldDetailsService(ExtendedFieldDetailsService extendedFieldDetailsService) {
		this.extendedFieldDetailsService = extendedFieldDetailsService;
	}

	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	public void setExtendedFieldDetailDAO(ExtendedFieldDetailDAO extendedFieldDetailDAO) {
		this.extendedFieldDetailDAO = extendedFieldDetailDAO;
	}

	public void setCustomServiceTask(CustomServiceTask customServiceTask) {
		this.customServiceTask = customServiceTask;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	@Override
	public FinanceMain setDefaultFinanceMain(FinanceMain financeMain, FinanceType financeType) {
		if (financeMain == null) {
			WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("FinanceMain");
			financeMain = new FinanceMain();
			if (workFlowDetails != null) {
				financeMain.setWorkflowId(workFlowDetails.getWorkFlowId());
			}
		}

		// Basic Details
		financeMain.setFinType(financeType.getFinType());
		financeMain.setLovDescFinTypeName(financeType.getFinTypeDesc());
		financeMain.setPromotionCode(financeType.getPromotionCode());
		financeMain.setFinCcy(financeType.getFinCcy());
		financeMain.setProfitDaysBasis(financeType.getFinDaysCalType());
		financeMain.setScheduleMethod(financeType.getFinSchdMthd());
		financeMain.setFinStartDate(SysParamUtil.getAppDate());
		financeMain.setTDSApplicable(financeType.isTdsApplicable());
		// Setting Default TDS Type
		if (!PennantConstants.TDS_USER_SELECTION.equals(financeType.getTdsType())) {
			financeMain.setTdsType(financeType.getTdsType());
		}
		financeMain.setProductCategory(financeType.getProductCategory());
		financeMain.setFinCategory(financeType.getFinCategory());
		// Step Policy Details
		if (financeType.isSteppingMandatory()) {
			financeMain.setStepFinance(financeType.isStepFinance());
			financeMain.setStepsAppliedFor(financeType.getStepsAppliedFor());
			financeMain.setCalcOfSteps(financeType.getCalcOfSteps());
			financeMain.setAlwManualSteps(financeType.isAlwManualSteps());
			financeMain.setStepPolicy(
					StringUtils.trimToEmpty(financeType.getDftStepPolicy()).equals(PennantConstants.List_Select) ? ""
							: financeType.getDftStepPolicy());
			financeMain.setLovDescStepPolicyName(StringUtils.trimToEmpty(financeType.getLovDescDftStepPolicyName()));
			financeMain.setStepType(financeType.getDftStepPolicyType());
		} else {
			financeMain.setStepFinance(false);
		}
		financeMain.setManualSchedule(financeType.isManualSchedule());
		// Grace period details

		financeMain.setAllowGrcPeriod(false);

		if (financeMain.isAllowGrcPeriod()) {

			financeMain.setGraceBaseRate(financeType.getFinGrcBaseRate());
			financeMain.setGraceSpecialRate(financeType.getFinGrcSplRate());
			financeMain.setGrcPftRate(financeType.getFinGrcIntRate());
			financeMain.setGrcPftFrq(financeType.getFinGrcDftIntFrq());
			financeMain.setGrcProfitDaysBasis(financeType.getFinDaysCalType());
			if (StringUtils.isNotEmpty(financeType.getFinGrcDftIntFrq())
					&& FrequencyUtil.validateFrequency(financeType.getFinGrcDftIntFrq()) == null) {
				financeMain.setNextGrcPftDate(
						FrequencyUtil.getNextDate(financeType.getFinGrcDftIntFrq(), 1, financeMain.getFinStartDate(),
								"A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
			}
			financeMain.setAllowGrcPftRvw(financeType.isFinGrcIsRvwAlw());
			financeMain.setGrcPftRvwFrq(financeType.getFinGrcRvwFrq());
			if (StringUtils.isNotEmpty(financeType.getFinGrcRvwFrq())
					&& FrequencyUtil.validateFrequency(financeType.getFinGrcRvwFrq()) == null) {
				if (ImplementationConstants.ALLOW_FDD_ON_RVW_DATE) {
					financeMain.setNextGrcPftRvwDate(
							FrequencyUtil.getNextDate(financeType.getFinGrcRvwFrq(), 1, financeMain.getFinStartDate(),
									"A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
				} else {
					financeMain.setNextGrcPftRvwDate(FrequencyUtil
							.getNextDate(financeType.getFinGrcRvwFrq(), 1, financeMain.getFinStartDate(), "A", false, 0)
							.getNextFrequencyDate());
				}
			}
			financeMain.setAllowGrcCpz(financeType.isFinGrcIsIntCpz());
			financeMain.setGrcCpzFrq(financeType.getFinGrcCpzFrq());
			if (StringUtils.isNotEmpty(financeType.getFinGrcCpzFrq())
					&& FrequencyUtil.validateFrequency(financeType.getFinGrcCpzFrq()) == null) {
				financeMain.setNextGrcCpzDate(
						FrequencyUtil.getNextDate(financeType.getFinGrcCpzFrq(), 1, financeMain.getFinStartDate(), "A",
								false, financeType.getFddLockPeriod()).getNextFrequencyDate());
			}
			financeMain.setCpzAtGraceEnd(financeType.isFinIsIntCpzAtGrcEnd());
			financeMain.setGrcRateBasis(financeType.getFinGrcRateType().substring(0, 1));
			financeMain.setAllowGrcRepay(financeType.isFinIsAlwGrcRepay());
			financeMain.setGrcSchdMthd(financeType.getFinGrcSchdMthd());
			financeMain.setGrcMargin(financeType.getFinGrcMargin());

			// Setting the GrcRepayRvwFrq from Base rate master if exists.
			// Otherwise will take it from loan type.
			String finGrcRvwFrq = null;
			if (CalculationConstants.RATE_BASIS_R.equals(financeType.getFinGrcRateType())) {
				BaseRateCode baseRateCode = baseRateCodeDAO.getBaseRateCodeById(financeType.getFinGrcBaseRate(), "");
				if (baseRateCode != null && StringUtils.trimToNull(baseRateCode.getbRRepayRvwFrq()) != null) {
					finGrcRvwFrq = baseRateCode.getbRRepayRvwFrq();
					financeMain.setGrcPftRvwFrq(finGrcRvwFrq);
					financeMain.setGrcFrqEditable(true);
				} else {
					finGrcRvwFrq = financeType.getFinGrcRvwFrq();
					financeMain.setGrcPftRvwFrq(finGrcRvwFrq);
				}
			} else {
				finGrcRvwFrq = financeType.getFinGrcRvwFrq();
				financeMain.setGrcPftRvwFrq(finGrcRvwFrq);
			}

			if (StringUtils.isNotEmpty(finGrcRvwFrq) && FrequencyUtil.validateFrequency(finGrcRvwFrq) == null) {
				if (ImplementationConstants.ALLOW_FDD_ON_RVW_DATE) {
					financeMain.setNextGrcPftRvwDate(
							FrequencyUtil.getNextDate(finGrcRvwFrq, 1, financeMain.getFinStartDate(), "A", false,
									financeType.getFddLockPeriod()).getNextFrequencyDate());
				} else {
					financeMain.setNextGrcPftRvwDate(
							FrequencyUtil.getNextDate(finGrcRvwFrq, 1, financeMain.getFinStartDate(), "A", false, 0)
									.getNextFrequencyDate());
				}
			}

			financeMain.setAllowGrcCpz(financeType.isFinGrcIsIntCpz());
			financeMain.setGrcCpzFrq(financeType.getFinGrcCpzFrq());
			if (StringUtils.isNotEmpty(financeType.getFinGrcCpzFrq())
					&& FrequencyUtil.validateFrequency(financeType.getFinGrcCpzFrq()) == null) {
				financeMain.setNextGrcCpzDate(
						FrequencyUtil.getNextDate(financeType.getFinGrcCpzFrq(), 1, financeMain.getFinStartDate(), "A",
								false, financeType.getFddLockPeriod()).getNextFrequencyDate());
			}
			financeMain.setCpzAtGraceEnd(financeType.isFinIsIntCpzAtGrcEnd());
			financeMain.setGrcRateBasis(financeType.getFinGrcRateType().substring(0, 1));
			financeMain.setAllowGrcRepay(financeType.isFinIsAlwGrcRepay());
			financeMain.setGrcSchdMthd(financeType.getFinGrcSchdMthd());
			financeMain.setGrcMargin(financeType.getFinGrcMargin());
		}
		// Setting the Gestation Period default values to financemain
		financeMain.setAlwGrcAdj(financeType.isGrcAdjReq());
		financeMain.setEndGrcPeriodAftrFullDisb(financeType.isGrcPeriodAftrFullDisb());
		financeMain.setAutoIncGrcEndDate(financeType.isAutoIncrGrcEndDate());
		// RepaymentDetails
		financeMain.setNumberOfTerms(financeType.getFinDftTerms());
		financeMain.setRepayBaseRate(financeType.getFinBaseRate());
		financeMain.setRepaySpecialRate(financeType.getFinSplRate());
		financeMain.setRepayMargin(financeType.getFinMargin());
		financeMain.setRepayProfitRate(financeType.getFinIntRate());
		financeMain.setRepayFrq(financeType.getFinRpyFrq());
		if (StringUtils.isNotEmpty(financeType.getFinRpyFrq())
				&& FrequencyUtil.validateFrequency(financeType.getFinRpyFrq()) == null) {
			financeMain.setNextRepayDate(FrequencyUtil.getNextDate(financeType.getFinRpyFrq(), 1,
					financeMain.getFinStartDate(), "A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
		}
		financeMain.setRepayPftFrq(financeType.getFinDftIntFrq());

		int fddLockPeriod = financeType.getFddLockPeriod();
		fddLockPeriod = fddLogic(financeMain, financeType, fddLockPeriod);

		if (StringUtils.isNotEmpty(financeType.getFinDftIntFrq())
				&& FrequencyUtil.validateFrequency(financeType.getFinDftIntFrq()) == null) {
			financeMain.setNextRepayPftDate(FrequencyUtil.getNextDate(financeType.getFinDftIntFrq(), 1,
					financeMain.getFinStartDate(), "A", false, fddLockPeriod).getNextFrequencyDate());
		}
		financeMain.setAllowRepayRvw(financeType.isFinIsRvwAlw());

		// Setting the RepayRvwFrq from Base rate master if exists. Otherwise
		// will take it from loan type.
		String finRvwFrq = null;
		if (CalculationConstants.RATE_BASIS_R.equals(financeType.getFinRateType())) {
			BaseRateCode baseRateCode = baseRateCodeDAO.getBaseRateCodeById(financeMain.getRepayBaseRate(), "");
			if (baseRateCode != null && StringUtils.trimToNull(baseRateCode.getbRRepayRvwFrq()) != null) {
				finRvwFrq = baseRateCode.getbRRepayRvwFrq();
				financeMain.setFrqEditable(true);
				financeMain.setRepayRvwFrq(finRvwFrq);
			} else {
				finRvwFrq = financeType.getFinRvwFrq();
				financeMain.setRepayRvwFrq(finRvwFrq);
			}
		} else {
			finRvwFrq = financeType.getFinRvwFrq();
			financeMain.setRepayRvwFrq(finRvwFrq);
		}

		financeMain.setSchCalOnRvw(financeType.getFinSchCalCodeOnRvw());
		financeMain.setPastduePftCalMthd(financeType.getPastduePftCalMthd());
		financeMain.setPastduePftMargin(financeType.getPastduePftMargin());
		financeMain.setDroppingMethod(financeType.getDroppingMethod());
		financeMain.setRateChgAnyDay(financeType.isRateChgAnyDay());

		if (StringUtils.isNotEmpty(finRvwFrq) && FrequencyUtil.validateFrequency(finRvwFrq) == null) {
			if (ImplementationConstants.ALLOW_FDD_ON_RVW_DATE) {
				financeMain.setNextRepayRvwDate(FrequencyUtil.getNextDate(finRvwFrq, 1, financeMain.getFinStartDate(),
						"A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
			} else {
				financeMain.setNextRepayRvwDate(
						FrequencyUtil.getNextDate(finRvwFrq, 1, financeMain.getFinStartDate(), "A", false, 0)
								.getNextFrequencyDate());
			}
		}

		financeMain.setAllowRepayCpz(financeType.isFinIsIntCpz());
		financeMain.setRepayCpzFrq(financeType.getFinCpzFrq());
		if (StringUtils.isNotEmpty(financeType.getFinCpzFrq())
				&& FrequencyUtil.validateFrequency(financeType.getFinCpzFrq()) == null) {
			financeMain.setNextRepayCpzDate(FrequencyUtil.getNextDate(financeType.getFinCpzFrq(), 1,
					financeMain.getFinStartDate(), "A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
		}
		financeMain.setRepayRateBasis(financeType.getFinRateType().substring(0, 1));
		financeMain.setEqualRepay(financeType.isEqualRepayment());
		financeMain.setNewRecord(true);
		financeMain.setRecordType("");

		financeMain.setLovDescIsSchdGenerated(false);
		financeMain.setDefferments(financeType.getFinMaxDifferment());
		financeMain.setPlanDeferCount(financeType.getPlanDeferCount());
		financeMain.setRvwRateApplFor(financeType.getFinRvwRateApplFor());
		financeMain.setFinRepayMethod(financeType.getFinRepayMethod());
		financeMain.setAlwBPI(financeType.isAlwBPI());
		financeMain.setBpiTreatment(financeType.getBpiTreatment());
		financeMain.setBpiPftDaysBasis(financeType.getBpiPftDaysBasis());
		financeMain.setPlanEMIHAlw(financeType.isPlanEMIHAlw());
		financeMain.setPlanEMIHMethod(financeType.getPlanEMIHMethod());
		financeMain.setPlanEMIHLockPeriod(financeType.getPlanEMIHLockPeriod());
		financeMain.setPlanEMIHMaxPerYear(financeType.getPlanEMIHMaxPerYear());
		financeMain.setPlanEMIHMax(financeType.getPlanEMIHMax());
		financeMain.setPlanEMICpz(financeType.isPlanEMICpz());
		financeMain.setCalRoundingMode(financeType.getRoundingMode());
		financeMain.setRoundingTarget(financeType.getRoundingTarget());
		financeMain.setAlwMultiDisb(financeType.isFinIsAlwMD());
		financeMain.setUnPlanEMIHLockPeriod(financeType.getUnPlanEMIHLockPeriod());
		financeMain.setMaxUnplannedEmi(financeType.getMaxUnplannedEmi());
		financeMain.setMaxReAgeHolidays(financeType.getMaxReAgeHolidays());
		financeMain.setUnPlanEMICpz(financeType.isUnPlanEMICpz());
		financeMain.setReAgeCpz(financeType.isReAgeCpz());

		financeMain.setFixedRateTenor(financeType.getFixedRateTenor());

		// tasks # >>Start Advance EMI and DSF
		if (financeType.isGrcAdvIntersetReq()) {
			financeMain.setGrcAdvType(financeType.getGrcAdvType());
			financeMain.setGrcAdvTerms(financeType.getGrcAdvDefaultTerms());
		} else {
			financeMain.setGrcAdvType(PennantConstants.List_Select);
			financeMain.setGrcAdvTerms(0);
		}

		if (financeType.isAdvIntersetReq()) {
			financeMain.setAdvType(financeType.getAdvType());
			financeMain.setAdvTerms(financeType.getAdvDefaultTerms());
			financeMain.setAdvStage(financeType.getAdvStage());
		} else {
			financeMain.setAdvType(PennantConstants.List_Select);
			financeMain.setAdvTerms(0);
			financeMain.setAdvStage(PennantConstants.List_Select);
		}
		// tasks # >>End Advance EMI and DSF

		financeMain.setEntityCode(financeType.getLovDescEntityCode());

		return financeMain;
	}

	private int fddLogic(FinanceMain financeMain, FinanceType financeType, int fddLockPeriod) {
		logger.debug(Literal.ENTERING);
		if (SysParamUtil.isAllowed(SMTParameterConstants.BPI_MONTHWISE_REQ)) {
			if (StringUtils.equals("BL", financeMain.getFinType())) {
				String dueDate = financeType.getFinDftIntFrq().length() > 2
						? financeType.getFinDftIntFrq().substring(financeType.getFinDftIntFrq().length() - 2)
						: financeType.getFinDftIntFrq();

				int month = DateUtility.getMonth(financeMain.getFinStartDate());
				int year = DateUtility.getYear(financeMain.getFinStartDate());

				YearMonth yearMonthObject = YearMonth.of(year, month);
				int daysInMonth = yearMonthObject.lengthOfMonth();

				if (dueDate.equals("07") && daysInMonth == 31) {
					fddLockPeriod = 17;
				}
				if (dueDate.equals("07") && daysInMonth == 30) {
					fddLockPeriod = 16;
				}
				if (dueDate.equals("07") && daysInMonth == 29) {
					fddLockPeriod = 15;
				}
				if (dueDate.equals("07") && daysInMonth == 28) {
					fddLockPeriod = 14;
				}

				if (dueDate.equals("02") && daysInMonth == 31) {
					fddLockPeriod = 12;
				}
				if (dueDate.equals("02") && daysInMonth == 30) {
					fddLockPeriod = 11;
				}
				if (dueDate.equals("02") && daysInMonth == 29) {
					fddLockPeriod = 10;
				}
				if (dueDate.equals("02") && daysInMonth == 28) {
					fddLockPeriod = 9;
				}
			}
		}
		logger.debug(Literal.ENTERING);
		return fddLockPeriod;
	}

	@Override
	public FinODPenaltyRate setDefaultODPenalty(FinODPenaltyRate finODPenaltyRate, FinanceType financeType) {
		// overdue Penalty Details
		if (finODPenaltyRate == null) {
			finODPenaltyRate = new FinODPenaltyRate();
		}
		finODPenaltyRate.setApplyODPenalty(financeType.isApplyODPenalty());
		finODPenaltyRate.setODIncGrcDays(financeType.isODIncGrcDays());
		finODPenaltyRate.setODChargeCalOn(financeType.getODChargeCalOn());
		finODPenaltyRate.setODGraceDays(financeType.getODGraceDays());
		finODPenaltyRate.setODChargeType(financeType.getODChargeType());
		finODPenaltyRate.setODChargeAmtOrPerc(financeType.getODChargeAmtOrPerc());
		finODPenaltyRate.setODAllowWaiver(financeType.isODAllowWaiver());
		finODPenaltyRate.setODMaxWaiverPerc(financeType.getODMaxWaiverPerc());
		finODPenaltyRate.setODRuleCode(financeType.getODRuleCode());

		return finODPenaltyRate;
	}

	// Saving the reason details
	private void saveReasonDetails(FinanceDetail financeDetail) {
		ReasonHeader reasonHeader = financeDetail.getReasonHeader();
		if (reasonHeader != null) {
			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
			reasonHeader.setModule(PennantConstants.WORFLOW_MODULE_FINANCE);
			reasonHeader.setReference(financeMain.getFinReference());
			reasonHeader.setRoleCode(financeMain.getRoleCode());
			reasonHeader.setActivity(financeMain.getRecordStatus());
			reasonHeader.setToUser(financeMain.getLastMntBy());
			reasonHeader.setLogTime(financeMain.getLastMntOn());
			this.reasonDetailDAO.save(reasonHeader);
		}
	}

	private List<ErrorDetail> covValidations(AuditHeader auditHeader) {
		List<ErrorDetail> errorDetails = new ArrayList<>();

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		WorkflowEngine workflowEngine = null;
		if (financeDetail.getCovenantTypeList() != null && financeDetail.getCovenantTypeList().size() > 0) {

			// Get the open covenant roles.
			List<String> covenantRoles = new ArrayList<>();
			String workflowType = PennantApplicationUtil.getWorkFlowType(financeMain.getWorkflowId());

			WorkFlowDetails workflow = WorkFlowUtil.getDetailsByType(workflowType);
			workflowEngine = new WorkflowEngine(workflow.getWorkFlowXml());

			List<DocumentDetails> documentList = financeDetail.getDocumentDetailsList();

			for (FinCovenantType finCovenantType : financeDetail.getCovenantTypeList()) {
				// Check whether the covenant received or not.
				if (CollectionUtils.isNotEmpty(documentList)) {
					if (!isCovenantReceived(documentList, finCovenantType)) {
						covenantRoles.add(finCovenantType.getMandRole());
					}
				}
			}

			// Check whether any covenant role is prior to next role code.
			for (String role : covenantRoles) {
				if (workflowEngine.compareRoles(role, financeMain.getNextRoleCode()) == Flow.SUCCESSOR) {
					errorDetails.add(new ErrorDetail("CV001"));
					break;
				}
			}
		}

		return errorDetails;
	}

	private boolean isCovenantReceived(List<DocumentDetails> documents, FinCovenantType covenant) {
		for (DocumentDetails document : documents) {
			if (StringUtils.equals(covenant.getCovenantType(), document.getDocCategory())
					&& !StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, document.getRecordType())) {
				return true;
			}
		}

		return false;
	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleList(long finID) {
		return financeScheduleDetailDAO.getFinSchdDetailsForBatch(finID);
	}

	@Override
	public boolean isholdDisbursementProcess(long finID) {
		return holdDisbursementDAO.isholdDisbursementProcess(finID, "_View");
	}

	@Override
	public void updateNextUserId(long finID, String nextUserId) {
		if (nextUserId != null) {
			String currUserId = financeMainDAO.getNextUserId(finID);

			if (StringUtils.isNotEmpty(currUserId)) {
				throw new AppException("The record was locked by another user.");
			}
		}

		financeMainDAO.updateNextUserId(finID, nextUserId);
	}

	@Override
	public String getNextUserId(long finID) {
		return financeMainDAO.getNextUserId(finID);
	}

	// FinAsset details
	@Override
	public List<FinAssetTypes> getFinAssetTypesByFinRef(String reference, String type) {
		return finAssetTypeDAO.getFinAssetTypesByFinRef(reference, type);
	}

	@Override
	public List<Integer> getFinanceDisbSeqs(long finID, boolean isWIF) {
		return financeDisbursementDAO.getFinanceDisbSeqs(finID, "", isWIF);
	}

	@Override
	public List<FinanceProfitDetail> getFinProfitListByFinRefList(List<Long> finIDList) {
		return profitDetailsDAO.getFinProfitListByFinRefList(finIDList);
	}

	@Override
	public List<FinanceMain> getFinanceMainForLinkedLoans(String finReference) {
		return financeMainDAO.getFinanceMainForLinkedLoans(finReference);
	}

	@Override
	public List<FinanceMain> getFinanceMainForLinkedLoans(long custId) {
		return financeMainDAO.getFinanceMainForLinkedLoans(custId);
	}

	/**
	 * Generating all fin reference which are having dues against the customer.
	 */
	@Override
	public String getCustomerDueFinReferces(long custId) {
		List<FinODDetails> odDetailsList = finODDetailsDAO.getCustomerDues(custId);

		if (CollectionUtils.isEmpty(odDetailsList)) {
			return "";
		}

		StringBuilder finReferences = new StringBuilder();

		for (FinODDetails finODDetails : odDetailsList) {
			if (finODDetails.getTotPenaltyBal().compareTo(BigDecimal.ZERO) > 0) {
				finReferences.append("\n").append(finODDetails.getFinReference());
			}
		}
		return finReferences.toString();
	}

	/**
	 * Saving the LMS service log
	 * 
	 * @param finDetail
	 * @param tableType
	 */
	public void saveLMSServiceLogs(FinScheduleData finDetail, String tableType) {
		logger.debug(Literal.ENTERING);

		List<FinServiceInstruction> finServiceInstructions = finDetail.getFinServiceInstructions();

		if (CollectionUtils.isEmpty(finServiceInstructions)) {
			return;
		}

		if (StringUtils.isNotEmpty(tableType)) {
			return;
		}

		String lmsServiceLogReq = SysParamUtil.getValueAsString(SMTParameterConstants.LMS_SERVICE_LOG_REQ);
		if (!StringUtils.equals(lmsServiceLogReq, PennantConstants.YES)) {
			return;
		}

		List<LMSServiceLog> lmsServiceLogs = new ArrayList<>();

		for (FinServiceInstruction fsi : finServiceInstructions) {
			if (FinServiceEvent.RATECHG.equals(fsi.getFinEvent()) && fsi.getFromDate() != null) {

				BigDecimal oldRate = finServiceInstructionDAO.getOldRate(fsi.getFinID(), fsi.getFromDate());
				BigDecimal newRate = finServiceInstructionDAO.getNewRate(fsi.getFinID(), fsi.getFromDate());

				LMSServiceLog lmsServiceLog = new LMSServiceLog();
				lmsServiceLog.setOldRate(oldRate);
				lmsServiceLog.setNewRate(newRate);
				lmsServiceLog.setEvent(fsi.getFinEvent());
				lmsServiceLog.setFinReference(fsi.getFinReference());
				lmsServiceLog.setNotificationFlag(PennantConstants.NO);
				lmsServiceLog.setEffectiveDate(fsi.getFromDate());
				lmsServiceLogs.add(lmsServiceLog);
			}
		}

		if (CollectionUtils.isNotEmpty(lmsServiceLogs)) {
			finServiceInstructionDAO.saveLMSServiceLOGList(lmsServiceLogs);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Getting Fin schedule details for rate report
	 */
	@Override
	public List<FinanceScheduleDetail> getFinSchdDetailsForRateReport(long finID) {
		return financeScheduleDetailDAO.getFinSchdDetailsForRateReport(finID);
	}

	/*
	 * Getting the verification initiation details
	 */
	@Override
	public FinanceDetail getVerificationInitiationDetails(long finID, VerificationType verificationType,
			String tableType) {

		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, tableType, false);

		String finReference = fm.getFinReference();
		long custID = fm.getCustID();
		String finType = fm.getFinType();

		schdData.setFinID(finID);
		schdData.setFinReference(finReference);

		schdData.setFinanceMain(fm);

		// Finance Type Details

		FinanceType financeType = financeTypeDAO.getOrgFinanceTypeByID(finType, "_ORGView");
		schdData.setFinanceType(financeType);

		// Customer details

		if (custID != 0 && custID != Long.MIN_VALUE) {
			fd.setCustomerDetails(customerDetailsService.getCustomerDetailsById(custID, true, "_AView"));
		}
		fd.setJointAccountDetailList(jointAccountDetailService.getJoinAccountDetail(finID, "_View"));

		// Collateral Details
		List<CollateralAssignment> assignmentListMain = null;
		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			assignmentListMain = collateralAssignmentDAO.getCollateralAssignmentByFinRef(finReference,
					FinanceConstants.MODULE_NAME, "_TView");
		} else {
			fd.setFinanceCollaterals(finCollateralService.getFinCollateralsByRef(finID, "_TView"));
		}

		// Collateral setup details and assignment details
		List<CollateralSetup> collateralSetupList = collateralSetupService.getCollateralDetails(finReference);
		List<CollateralAssignment> assignmentListTemp = null;
		if (CollectionUtils.isNotEmpty(collateralSetupList)) {
			if (ImplementationConstants.COLLATERAL_INTERNAL) {
				assignmentListTemp = collateralAssignmentDAO.getCollateralAssignmentByFinRef(finReference,
						FinanceConstants.MODULE_NAME, "_CTView");
			}
		}
		fd.setCollaterals(collateralSetupList);
		fd = setCollateralAssignments(fd, assignmentListMain, assignmentListTemp);

		// Document Details
		List<DocumentDetails> documentList = documentDetailsDAO.getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, FinServiceEvent.ORG, "_TView");
		if (fd.getDocumentDetailsList() != null && !fd.getDocumentDetailsList().isEmpty()) {
			fd.getDocumentDetailsList().addAll(documentList);
		} else {
			fd.setDocumentDetailsList(documentList);
		}

		logger.debug("Leaving");
		return fd;
	}

	/**
	 * Next Review dates resetting for back dated loans
	 * 
	 * @param financeDetail
	 */
	private void resetNextFrqDates(FinanceDetail financeDetail, Date appDate) {
		logger.debug(Literal.ENTERING);

		FinanceMain finMain = financeDetail.getFinScheduleData().getFinanceMain();
		if (!StringUtils.equals(finMain.getProductCategory(), FinanceConstants.PRODUCT_CONVENTIONAL)) {
			return;
		}

		if (finMain.getFinStartDate().compareTo(appDate) >= 0 && !finMain.isManualSchedule()) {
			return;
		}

		List<FinanceScheduleDetail> scheduleDetails = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
		if (CollectionUtils.isEmpty(scheduleDetails)) {
			return;
		}

		boolean nxtGrcCpzDateSet = false;
		boolean nxtGrcPftDateSet = false;
		boolean nxtGrcRvwDateSet = false;

		boolean nxtRpyCpzDateSet = false;
		boolean nxtRpyRvwDateSet = false;
		boolean nxtRpyPftDateSet = false;
		boolean nxtRpyDateSet = false;

		if (!finMain.isAllowRepayRvw()) {
			nxtRpyRvwDateSet = true;
		}
		if (!finMain.isAllowRepayCpz()) {
			nxtRpyCpzDateSet = true;
		}

		Date grcEndDate = finMain.getGrcPeriodEndDate();
		Date maturityDate = finMain.getMaturityDate();
		for (FinanceScheduleDetail curSchd : scheduleDetails) {

			Date schdDate = curSchd.getSchDate();
			if (schdDate.compareTo(appDate) <= 0) {
				continue;
			}

			// Grace Period Details
			if (schdDate.compareTo(grcEndDate) <= 0) {

				if (finMain.isManualSchedule()) {
					if (!nxtGrcCpzDateSet) {
						finMain.setNextGrcCpzDate(grcEndDate);
						nxtGrcCpzDateSet = true;
					}

					if (!nxtGrcRvwDateSet) {
						finMain.setNextGrcPftRvwDate(grcEndDate);
						nxtGrcRvwDateSet = true;
					}

					if (!nxtGrcPftDateSet) {
						finMain.setNextGrcPftDate(grcEndDate);
						nxtGrcPftDateSet = true;
					}
				}

				// Set Next Grace Capitalization Date
				if (curSchd.isCpzOnSchDate() && !nxtGrcCpzDateSet) {
					if (finMain.getNextGrcCpzDate().compareTo(grcEndDate) < 0) {
						finMain.setNextGrcCpzDate(schdDate);
					} else {
						finMain.setNextGrcCpzDate(grcEndDate);
					}
					nxtGrcCpzDateSet = true;
				}

				// Set Next Grace Review Date
				if (curSchd.isRvwOnSchDate() && !nxtGrcRvwDateSet) {
					if (finMain.getNextGrcPftRvwDate().compareTo(grcEndDate) < 0) {
						finMain.setNextGrcPftRvwDate(schdDate);
					} else {
						finMain.setNextGrcPftRvwDate(grcEndDate);
					}
					nxtGrcRvwDateSet = true;
				}

				// Set Next Grace Profit Date
				if ((curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()) && !nxtGrcPftDateSet) {
					if (finMain.getNextGrcPftDate().compareTo(grcEndDate) < 0) {
						finMain.setNextGrcPftDate(schdDate);
					} else {
						finMain.setNextGrcPftDate(grcEndDate);
					}
					nxtGrcPftDateSet = true;
				}
			} else {

				if (!nxtGrcCpzDateSet) {
					finMain.setNextGrcCpzDate(grcEndDate);
					nxtGrcCpzDateSet = true;
				}

				if (!nxtGrcRvwDateSet) {
					finMain.setNextGrcPftRvwDate(grcEndDate);
					nxtGrcRvwDateSet = true;
				}

				if (!nxtGrcPftDateSet) {
					finMain.setNextGrcPftDate(grcEndDate);
					nxtGrcPftDateSet = true;
				}

				// Set Next Repay Capitalization Date
				if (curSchd.isCpzOnSchDate() && !nxtRpyCpzDateSet) {
					if (finMain.getNextRepayCpzDate().compareTo(maturityDate) < 0) {
						finMain.setNextRepayCpzDate(schdDate);
					} else {
						finMain.setNextRepayCpzDate(maturityDate);
					}
					nxtRpyCpzDateSet = true;
				}

				// Set Next Repay Review Date
				if (curSchd.isRvwOnSchDate() && !nxtRpyRvwDateSet) {
					if (finMain.getNextRepayRvwDate().compareTo(maturityDate) < 0) {
						finMain.setNextRepayRvwDate(schdDate);
					} else {
						finMain.setNextRepayRvwDate(maturityDate);
					}
					nxtRpyRvwDateSet = true;
				}

				// Set Next Repay Profit Date
				if (curSchd.isPftOnSchDate() && !nxtRpyPftDateSet) {
					if (finMain.getNextRepayPftDate().compareTo(maturityDate) < 0) {
						finMain.setNextRepayPftDate(schdDate);
					} else {
						finMain.setNextRepayPftDate(maturityDate);
					}
					nxtRpyPftDateSet = true;
				}

				// Set Next Repay Date
				if (curSchd.isRepayOnSchDate() && !nxtRpyDateSet) {
					if (finMain.getNextRepayDate().compareTo(maturityDate) < 0) {
						finMain.setNextRepayDate(schdDate);
					} else {
						finMain.setNextRepayDate(maturityDate);
					}
					nxtRpyDateSet = true;
				}

				if (nxtRpyRvwDateSet && nxtRpyCpzDateSet && nxtRpyPftDateSet && nxtRpyDateSet) {
					break;
				}
			}
		}

		logger.debug(Literal.ENTERING);
	}

	@Override
	public void addExtFieldsToAttributes(FinanceMain fm) {
		StringBuilder tableName = new StringBuilder(ExtendedFieldConstants.MODULE_LOAN);

		if (StringUtils.isNotEmpty(fm.getFinCategory())) {
			tableName.append("_" + fm.getFinCategory() + "_");
		} else {
			tableName.append("_" + fm.getLovDescProductCodeName() + "_");
		}

		tableName.append(ExtendedFieldConstants.MODULE_ORGANIZATION + "_ED_TEMP");

		Map<String, Object> extfields = financeMainDAO.getExtendedFields(fm.getFinReference(), tableName.toString());

		if (MapUtils.isNotEmpty(extfields)) {
			Map<String, String> extflds = extfields.entrySet().stream()
					.collect(Collectors.toMap(Map.Entry::getKey, v -> String.valueOf(v.getValue())));

			fm.addAttributes(extflds);
		}

	}

	@Override
	public void processRestructureAccounting(AEEvent aeEvent, FinanceDetail financeDetail) {
		restructureService.processRestructureAccounting(aeEvent, financeDetail);
	}

	@Override
	public List<ReturnDataSet> prepareSubVenAccounting(AEEvent aeEvent, FinanceDetail financeDetail) {
		return procesSubVenAccounting(aeEvent, financeDetail, false);
	}

	private BigDecimal getFinanceExposure(long custID) {
		BigDecimal totRepayAmt = BigDecimal.ZERO;

		List<FinanceMain> list = financeMainDAO.getForFinanceExposer(custID);

		for (FinanceMain fm : list) {
			BigDecimal totalRepayAmt = fm.getTotalRepayAmt();
			Date maturityDate = fm.getMaturityDate();
			Date finStartDate = fm.getFinStartDate();

			int yearsDiff = DateUtil.getYear(maturityDate) - DateUtil.getYear(finStartDate);
			int montsDiff = DateUtil.getMonth(maturityDate) - DateUtil.getMonth(finStartDate);

			int i = (yearsDiff * 12) + montsDiff;
			if (i == 0) {
				totalRepayAmt = totalRepayAmt.divide(new BigDecimal("1"));
			} else {
				totalRepayAmt = totalRepayAmt.divide(new BigDecimal(i), RoundingMode.UP);
			}

			totRepayAmt = totRepayAmt.add(totalRepayAmt);
		}

		return PennantApplicationUtil.formateAmount(totRepayAmt, 2);
	}

	private void validateFees(AuditDetail auditDetail, FinanceDetail fd) {
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();
		List<FinFeeDetail> fees = schdData.getFinFeeDetailList();

		if (fm.isWifLoan() || CollectionUtils.isEmpty(fees)) {
			return;
		}

		List<FinanceReferenceDetail> details = financeReferenceDetailDAO
				.getLMTFinRefDetails(FinanceConstants.FEE_UPFRONT_REQ, fm.getFinType());

		List<FinanceReferenceDetail> frdList = new ArrayList<>();
		for (FinanceReferenceDetail frd : details) {
			if (FinServiceEvent.ORG.equals(frd.getFinEvent())) {
				frdList.add(frd);
			}
		}

		int format = CurrencyUtil.getFormat(fm.getFinCcy());

		for (FinFeeDetail fee : fees) {
			BigDecimal amount = fee.getRemainingFee();
			String feeMthd = fee.getFeeScheduleMethod();

			if (!CalculationConstants.REMFEE_PAID_BY_CUSTOMER.equals(feeMthd)
					|| amount.compareTo(BigDecimal.ZERO) == 0) {
				continue;
			}

			String[] errParm = new String[2];
			errParm[0] = fee.getFeeTypeCode();
			errParm[1] = PennantApplicationUtil.amountFormate(amount, format);

			if (StringUtils.isEmpty(fm.getNextRoleCode())) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("UPFEE_001", errParm)));
				return;
			}

			if (CollectionUtils.isEmpty(frdList) && !StringUtils.equals(fm.getRoleCode(), fm.getNextRoleCode())) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("UPFEE_001", errParm)));
				return;
			}

			for (FinanceReferenceDetail frd : frdList) {
				String mandInputInStage = frd.getMandInputInStage();

				if (mandInputInStage.contains(fm.getNextRoleCode())) {
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail("UPFEE_001", errParm)));
					return;
				}
			}
		}
	}

	/**
	 * Getting Fin details for rate report
	 */
	@Override
	public FinanceMain getFinanceMainForRateReport(long finID, String type) {
		return financeScheduleDetailDAO.getFinanceMainForRateReport(finID, type);
	}

	@Autowired
	public void setAdvancePaymentService(AdvancePaymentService advancePaymentService) {
		this.advancePaymentService = advancePaymentService;
	}

	@Override
	public Map<String, Object> getUpLevelUsers(long usrId, String branch) {
		Map<String, Object> upLvlUsers = new HashMap<String, Object>();

		List<SecurityUser> upLevelUsers = financeReferenceDetailDAO.getUpLevelUsers(usrId, branch);
		if (!CollectionUtils.isEmpty(upLevelUsers)) {
			for (SecurityUser securityUser : upLevelUsers) {
				switch (StringUtils.trimToEmpty(securityUser.getUsrDesg())) {

				case "ASM":
					upLvlUsers.put("ASM", securityUser);
					break;

				}
			}
		}

		return upLvlUsers;

	}

	@Override
	public FinanceDetail getFinanceDetailsForPmay(long finID) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = financeMainDAO.getFinanceMainById(finID, "_View", false);
		FinanceDetail fd = new FinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();

		schdData.setFinID(fm.getFinID());
		schdData.setFinReference(fm.getFinReference());

		schdData.setFinanceMain(fm);

		fd.setCustomerDetails(customerDetailsService.getCustomerDetailsById(fm.getCustID(), true, "_AView"));
		fd.setJointAccountDetailList(jointAccountDetailService.getJoinAccountDetail(finID, "_AView"));
		fd.setPmay(pmayService.getPMAY(finID, "_AView"));

		logger.debug(Literal.LEAVING);

		return fd;
	}

	@Override
	public FinCustomerDetails getDetailsByOfferID(String offerID) {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);
		return financeMainDAO.getDetailsByOfferID(offerID);
	}

	@Override
	public void saveDisbDetails(List<FinanceDisbursement> disbursementDetails, long finID) {
		financeDisbursementDAO.deleteByFinReference(finID, "", false, 0);
		financeDisbursementDAO.saveList(disbursementDetails, "", false);
	}

	@Override
	public void saveFinSchdDetail(List<FinanceScheduleDetail> financeScheduleDetails, long finID) {
		financeScheduleDetailDAO.deleteByFinReference(finID, "", false, 0);
		financeScheduleDetailDAO.saveList(financeScheduleDetails, "", false);

	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(long finID, String string, boolean b) {
		return financeScheduleDetailDAO.getFinScheduleDetails(finID, string, b);
	}

	@Override
	public List<FinanceStepPolicyDetail> getFinStepDetailListByFinRef(long finID, String type, boolean isWIF) {
		return financeStepDetailDAO.getFinStepDetailListByFinRef(finID, type, isWIF);
	}

	@Override
	public List<RepayInstruction> getRepayInstructions(long finID, String type, boolean isWIF) {
		return repayInstructionDAO.getRepayInstructions(finID, type, isWIF);
	}

	private void processPricingLoans(FinanceDetail financeDetail, String tableType, String auditTranType) {
		PricingDetail pricingDetail = financeDetail.getPricingDetail();
		FinanceMain main = financeDetail.getFinScheduleData().getFinanceMain();

		if (pricingDetail == null) {
			return;
		}

		List<FinanceMain> financeMains = pricingDetail.getFinanceMains();

		if (CollectionUtils.isEmpty(financeMains)) {
			return;
		}

		BigDecimal totalLoanAmt = main.getFinAssetValue();
		if (CollectionUtils.isNotEmpty(financeMains)) {
			for (FinanceMain financeMain : financeMains) {
				totalLoanAmt = totalLoanAmt.add(financeMain.getFinAssetValue());
			}
		}

		CollateralAssignment collateralAssignment = null;
		if (CollectionUtils.isNotEmpty(financeMains) && pricingDetail.isSplit()) {
			for (FinanceMain fm : financeMains) {
				if (CollectionUtils.isNotEmpty(financeDetail.getCollateralAssignmentList())) {
					for (CollateralAssignment parentColAssignment : financeDetail.getCollateralAssignmentList()) {
						collateralAssignment = new CollateralAssignment();
						collateralAssignment.setReference(fm.getFinReference());
						collateralAssignment.setCollateralRef(parentColAssignment.getCollateralRef());
						BigDecimal colPerForChild = setCollateralAssignmenForChildLoans(fm, totalLoanAmt,
								parentColAssignment.getAssignPerc());

						BigDecimal remPer = collateralAssignment.getAssignPerc()
								.subtract(parentColAssignment.getAssignPercent());
						if (colPerForChild.compareTo(remPer) > 1) {
							colPerForChild = remPer;
						}

						collateralAssignment.setAssignPerc(colPerForChild);
						collateralAssignment.setModule(FinanceConstants.MODULE_NAME);
						collateralAssignment.setWorkflowId(0);
						collateralAssignment.setVersion(main.getVersion());
						collateralAssignment.setLastMntBy(main.getLastMntBy());
						collateralAssignment.setLastMntOn(main.getLastMntOn());
						collateralAssignment.setRecordStatus(main.getRecordStatus());
						collateralAssignment.setRecordType(main.getRecordType());
						collateralAssignmentDAO.save(collateralAssignment, "_Temp");
						parentColAssignment.setAssignPercent(
								collateralAssignment.getAssignPerc().add(parentColAssignment.getAssignPercent()));
					}
				}

				List<JointAccountDetail> jointAccountDetailList = financeDetail.getJointAccountDetailList();
				if (CollectionUtils.isNotEmpty(jointAccountDetailList)) {
					for (JointAccountDetail details : jointAccountDetailList) {
						Cloner cloner = new Cloner();
						JointAccountDetail jointAccountDetail = cloner.deepClone(details);
						jointAccountDetail.setId(Long.MIN_VALUE);
						jointAccountDetail.setFinID(fm.getFinID());
						jointAccountDetail.setFinReference(fm.getFinReference());
						jointAccountDetail.setNewRecord(true);
						if (!PennantConstants.RECORD_TYPE_CAN.equals(details.getRecordType())) {
							try {
								jointAccountDetailDAO.save(jointAccountDetail, "_Temp");
							} catch (Exception e) {
								logger.error(Literal.EXCEPTION, e);
							}
						}
					}
				}

				List<GuarantorDetail> gurantorsDetailList = financeDetail.getGurantorsDetailList();
				if (CollectionUtils.isNotEmpty(gurantorsDetailList)) {
					for (GuarantorDetail details : gurantorsDetailList) {
						Cloner cloner = new Cloner();
						GuarantorDetail gurantorDtls = cloner.deepClone(details);
						gurantorDtls.setId(Long.MIN_VALUE);
						gurantorDtls.setFinID(fm.getFinID());
						gurantorDtls.setFinReference(fm.getFinReference());
						gurantorDtls.setNewRecord(true);

						if (!PennantConstants.RECORD_TYPE_CAN.equals(details.getRecordType())) {
							try {
								guarantorDetailDAO.save(gurantorDtls, "_Temp");
							} catch (Exception e) {
								logger.error(Literal.EXCEPTION, e);
							}
						}
					}
				}
			}

			for (CollateralAssignment parentColAssignment : financeDetail.getCollateralAssignmentList()) {
				parentColAssignment.setAssignPerc(
						parentColAssignment.getAssignPerc().subtract(parentColAssignment.getAssignPercent()));
				collateralAssignmentDAO.update(parentColAssignment, "_Temp");
			}

		}

		for (FinanceMain fm : financeMains) {
			fm.setFinStartDate(main.getFinStartDate());

			String finType = fm.getFinType();
			FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(finType,
					FinServiceEvent.ORG, PennantConstants.WORFLOW_MODULE_FINANCE);
			if (financeWorkFlow != null) {
				WorkFlowDetails workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
				if (workFlowDetails != null) {
					WorkflowEngine workflow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
					String taskid = workflow.getUserTaskId(workflow.firstTaskOwner());
					fm.setWorkflowId(workFlowDetails.getWorkFlowId());
					fm.setRoleCode(workflow.firstTaskOwner());
					fm.setNextRoleCode(workflow.firstTaskOwner());
					fm.setTaskId(taskid);
					fm.setNextTaskId(taskid + ";");
				}
			} else {
				fm.setRoleCode(main.getRoleCode());
				fm.setNextRoleCode(main.getNextRoleCode());
				fm.setTaskId(main.getTaskId());
				fm.setNextTaskId(main.getNextTaskId());
			}
			fm.setLastMntBy(main.getLastMntBy());
			fm.setLastMntOn(main.getLastMntOn());
			fm.setRecordStatus(main.getRecordStatus());
			fm.setRecordType(PennantConstants.RECORD_TYPE_NEW);

			if (StringUtils.isNotBlank(fm.getParentRef())) {
				fm.setInvestmentRef("");
			}

			fm.setCustID(main.getCustID());

			if (fm.isNewRecord()) {
				financeMainDAO.save(fm, TableType.TEMP_TAB, false);
			} else {
				if (StringUtils.equals(fm.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
					// financeMainDAO.delete(financeMain,
					// TableType.TEMP_TAB, false, true);
				} else {
					String[] roles = StringUtils.trimToEmpty(SysParamUtil.getValueAsString("BRANCH_OPS_ROLE"))
							.split(",");
					boolean update = false;
					update = true;
					for (String role : roles) {
						if (StringUtils.equals(financeDetail.getFinScheduleData().getFinanceMain().getRoleCode(),
								role)) {
							update = true;
							break;
						}
					}

					if (fm.getParentRef() == null || update) {
						financeMainDAO.update(fm, TableType.TEMP_TAB, false);
					}
				}
			}
		}

		for (FinFeeDetail finFeeDetail : pricingDetail.getTopUpFinFeeDetails()) {
			if (finFeeDetail.getTaxHeaderId() == null) {
				finFeeDetail.setTaxHeaderId(0L);
			}
			TaxHeader taxHeader = finFeeDetail.getTaxHeader();
			if (taxHeader != null && (finFeeDetail.isNewRecord() || (!finFeeDetail.isNewRecord()
					&& (finFeeDetail.getTaxHeaderId() != null && finFeeDetail.getTaxHeaderId() > 0)))) {
				taxHeader.setRecordType(finFeeDetail.getRecordType());
				taxHeader.setNewRecord(finFeeDetail.isNewRecord());
				taxHeader.setLastMntBy(finFeeDetail.getLastMntBy());
				taxHeader.setLastMntOn(finFeeDetail.getLastMntOn());
				taxHeader.setRecordStatus(finFeeDetail.getRecordStatus());

				Long taxHeaderId = finFeeDetail.getTaxHeaderId();
				if (taxHeaderId != null && taxHeaderId > 0) {
					taxHeader.setHeaderId(taxHeaderId);
				}
				if (finFeeDetail.isTaxApplicable()) {
					TaxHeader txHeader = taxHeaderDetailsService.saveOrUpdate(taxHeader, tableType, auditTranType);
					finFeeDetail.setTaxHeaderId(txHeader.getHeaderId());
				}
			}
		}

		for (FinFeeDetail feeDetail : pricingDetail.getTopUpFinFeeDetails()) {
			feeDetail.setRecordType(main.getRecordType());
			feeDetail.setRecordStatus(main.getRecordStatus());
			feeDetail.setLastMntOn(main.getLastMntOn());
			feeDetail.setLastMntBy(main.getLastMntBy());
			String finType = financeMainDAO.getFinanceType(feeDetail.getFinID(), TableType.TEMP_TAB);
			FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(finType,
					FinServiceEvent.ORG, PennantConstants.WORFLOW_MODULE_FINANCE);
			if (financeWorkFlow != null) {
				WorkFlowDetails workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
				if (workFlowDetails != null) {
					WorkflowEngine workflow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
					String taskid = workflow.getUserTaskId(workflow.firstTaskOwner());
					feeDetail.setWorkflowId(workFlowDetails.getWorkFlowId());
					feeDetail.setRoleCode(workflow.firstTaskOwner());
					feeDetail.setNextRoleCode(workflow.firstTaskOwner());
					feeDetail.setTaskId(taskid);
					feeDetail.setNextTaskId(taskid + ";");
					feeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				}
			} else {
				feeDetail.setWorkflowId(main.getWorkflowId());
				feeDetail.setRoleCode(main.getRoleCode());
				feeDetail.setNextRoleCode(main.getNextRoleCode());
				feeDetail.setTaskId(main.getTaskId());
				feeDetail.setNextTaskId(main.getNextTaskId());
				feeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			}
		}

		for (VASRecording vasRecording : pricingDetail.getTopUpVasDetails()) {
			vasRecording.setRecordType(main.getRecordType());
			vasRecording.setRecordStatus(main.getRecordStatus());
			vasRecording.setLastMntOn(main.getLastMntOn());
			vasRecording.setLastMntBy(main.getLastMntBy());

			String finType = financeMainDAO.getFinanceType(vasRecording.getPrimaryLinkRef(), TableType.TEMP_TAB);
			FinanceWorkFlow financeWorkFlow = financeWorkFlowService.getApprovedFinanceWorkFlowById(finType,
					FinServiceEvent.ORG, PennantConstants.WORFLOW_MODULE_FINANCE);
			if (financeWorkFlow != null) {
				WorkFlowDetails workFlowDetails = WorkFlowUtil.getDetailsByType(financeWorkFlow.getWorkFlowType());
				if (workFlowDetails != null) {
					WorkflowEngine workflow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
					String taskid = workflow.getUserTaskId(workflow.firstTaskOwner());
					vasRecording.setWorkflowId(workFlowDetails.getWorkFlowId());
					vasRecording.setRoleCode(workflow.firstTaskOwner());
					vasRecording.setNextRoleCode(workflow.firstTaskOwner());
					vasRecording.setTaskId(taskid);
					vasRecording.setNextTaskId(taskid + ";");
				}
			} else {
				vasRecording.setRoleCode(main.getRoleCode());
				vasRecording.setNextRoleCode(main.getNextRoleCode());
				vasRecording.setTaskId(main.getTaskId());
				vasRecording.setNextTaskId(main.getNextTaskId());
				vasRecording.setWorkflowId(main.getWorkflowId());
			}

			if (vasRecording.isNewRecord()) {
				vasRecordingDAO.save(vasRecording, "_Temp");
			} else {
				if (StringUtils.equals(vasRecording.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
					vasRecordingDAO.delete(vasRecording, "_Temp");
				} else {
					vasRecordingDAO.update(vasRecording, "_Temp");
				}
			}
		}

		// Delete parent data
		for (FinanceMain financeMain : financeMains) {
			if (!financeMain.isNewRecord()
					&& StringUtils.equals(financeMain.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
				financeMainDAO.delete(financeMain, TableType.TEMP_TAB, false, true);
			}
		}

		if (CollectionUtils.isNotEmpty(financeMains)) {
			for (FinanceMain financeMain : financeMains) {
				financeMain.setRecordType(main.getRecordType());
				financeMain.setRecordStatus(main.getRecordStatus());
				financeMain.setVersion(main.getVersion());
				financeMain.setLastMntOn(main.getLastMntOn());
				financeMain.setLastMntBy(main.getLastMntBy());
				financeMain.setRoleCode(main.getRoleCode());
				financeMain.setNextRoleCode(main.getNextRoleCode());
				financeMain.setTaskId(main.getTaskId());
				financeMain.setNextTaskId(main.getNextTaskId());
			}
		}

		logger.debug("Leaving");
	}

	private BigDecimal setCollateralAssignmenForChildLoans(FinanceMain financeMain, BigDecimal totalLoanAmt,
			BigDecimal parentAssignementPerc) {
		BigDecimal childLoanAmt = financeMain.getFinAssetValue();
		BigDecimal collateralAssignmentPerc = parentAssignementPerc
				.multiply(childLoanAmt.divide(totalLoanAmt, MathContext.DECIMAL64));
		return collateralAssignmentPerc;

	}

	private List<AuditDetail> prepareFinFlagHeader(FinanceDetail fd, String auditTranType, String method) {
		boolean isRcdType = false;

		FinanceFlag flag = new FinanceFlag();

		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		flag.setFinID(fm.getFinID());
		flag.setFinReference(fm.getFinReference());
		flag.setFinFlagDetailList(fd.getFinFlagsDetails());
		flag.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		flag.setRecordStatus(fm.getRecordStatus());
		flag.setWorkflowId(fm.getWorkflowId());
		flag.setRecordType(fm.getRecordType());
		flag.setNewRecord(fm.isNewRecord());
		flag.setTaskId(fm.getTaskId());
		flag.setNextTaskId(fm.getNextTaskId());
		flag.setRoleCode(fm.getRoleCode());
		flag.setNextRoleCode(fm.getNextRoleCode());
		flag.setVersion(1);

		List<AuditDetail> auditDetails = new ArrayList<>();

		if (fm.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
			flag.setRecordType(PennantConstants.RECORD_TYPE_NEW);
			isRcdType = true;
		} else if (fm.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
			flag.setRecordType(PennantConstants.RECORD_TYPE_UPD);
			if (fm.isWorkflow()) {
				isRcdType = true;
			}
		} else if (fm.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
			flag.setRecordType(PennantConstants.RECORD_TYPE_DEL);
		}

		if ("saveOrUpdate".equals(method) && (isRcdType)) {
			flag.setNewRecord(true);
		}

		if (!auditTranType.equals(PennantConstants.TRAN_WF)) {
			if (fm.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				auditTranType = PennantConstants.TRAN_ADD;
			} else if (fm.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)
					|| fm.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				auditTranType = PennantConstants.TRAN_DEL;
			} else {
				auditTranType = PennantConstants.TRAN_UPD;
			}
		}
		if (StringUtils.isNotEmpty(fm.getRecordType())) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceFlag(), flag.getExcludeFields());
			auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], flag.getBefImage(), flag));
		}

		return auditDetails;
	}

	private List<AuditDetail> processingFinFlagHeader(List<AuditDetail> auditDetails, String type) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {
			FinanceFlag flag = (FinanceFlag) auditDetails.get(i).getModelData();

			if (StringUtils.isEmpty(flag.getRecordType())) {
				continue;
			}

			saveRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";
			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				flag.setRoleCode("");
				flag.setNextRoleCode("");
				flag.setTaskId("");
				flag.setNextTaskId("");
				flag.setWorkflowId(0);
			}

			if (flag.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (flag.isNewRecord()) {
				saveRecord = true;
				if (flag.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					flag.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (flag.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					flag.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (flag.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					flag.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (flag.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				}
			} else if (flag.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (flag.isNewRecord()) {
					saveRecord = true;
				}
			}
			if (approveRec) {
				rcdType = flag.getRecordType();
				recordStatus = flag.getRecordStatus();
				flag.setRecordType("");
				flag.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				finFlagsHeaderDAO.save(flag, type);
			}

			if (deleteRecord) {
				finFlagsHeaderDAO.delete(flag, type);
			}

			if (approveRec) {
				flag.setRecordType(rcdType);
				flag.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(flag);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;

	}

	@Override
	public String getFinCategory(String finReference) {
		return financeMainDAO.getFinCategory(finReference);
	}

	public String getOrgFinCategory(String finReference) {
		return financeMainDAO.getOrgFinCategory(finReference);
	}

	public void setReasonDetailDAO(ReasonDetailDAO reasonDetailDAO) {
		this.reasonDetailDAO = reasonDetailDAO;
	}

	public void setFinChequeHeaderService(FinChequeHeaderService finChequeHeaderService) {
		this.finChequeHeaderService = finChequeHeaderService;
	}

	public void setFinTypeExpenseDAO(FinTypeExpenseDAO finTypeExpenseDAO) {
		this.finTypeExpenseDAO = finTypeExpenseDAO;
	}

	public void setFinExpenseDetailsDAO(FinExpenseDetailsDAO finExpenseDetailsDAO) {
		this.finExpenseDetailsDAO = finExpenseDetailsDAO;
	}

	public void setCreditInformation(CreditInformation creditInformation) {
		this.creditInformation = creditInformation;
	}

	public void setFinIRRDetailsDAO(FinIRRDetailsDAO finIRRDetailsDAO) {
		this.finIRRDetailsDAO = finIRRDetailsDAO;
	}

	public void setVehicleDealerService(VehicleDealerService vehicleDealerService) {
		this.vehicleDealerService = vehicleDealerService;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	public void setpSLDetailService(PSLDetailService pSLDetailService) {
		this.pSLDetailService = pSLDetailService;
	}

	public void setLegalDetailService(LegalDetailService legalDetailService) {
		this.legalDetailService = legalDetailService;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public void setHoldDisbursementDAO(HoldDisbursementDAO holdDisbursementDAO) {
		this.holdDisbursementDAO = holdDisbursementDAO;
	}

	public void setLowerTaxDeductionDAO(LowerTaxDeductionDAO lowerTaxDeductionDAO) {
		this.lowerTaxDeductionDAO = lowerTaxDeductionDAO;
	}

	public void setPaymentsProcessService(PaymentsProcessService paymentsProcessService) {
		this.paymentsProcessService = paymentsProcessService;
	}

	public void setBaseRateCodeDAO(BaseRateCodeDAO baseRateCodeDAO) {
		this.baseRateCodeDAO = baseRateCodeDAO;
	}

	public void setCreditReviewDetailDAO(CreditReviewDetailDAO creditReviewDetailDAO) {
		this.creditReviewDetailDAO = creditReviewDetailDAO;
	}

	public void setIrrScheduleDetailDAO(IRRScheduleDetailDAO irrScheduleDetailDAO) {
		this.irrScheduleDetailDAO = irrScheduleDetailDAO;
	}

	public void setRisksAndMitigantsDAO(RisksAndMitigantsDAO risksAndMitigantsDAO) {
		this.risksAndMitigantsDAO = risksAndMitigantsDAO;
	}

	public void setSanctionConditionsDAO(SanctionConditionsDAO sanctionConditionsDAO) {
		this.sanctionConditionsDAO = sanctionConditionsDAO;
	}

	public void setDealRecommendationMeritsDAO(DealRecommendationMeritsDAO dealRecommendationMeritsDAO) {
		this.dealRecommendationMeritsDAO = dealRecommendationMeritsDAO;
	}

	public void setDueDiligenceDetailsDAO(DueDiligenceDetailsDAO dueDiligenceDetailsDAO) {
		this.dueDiligenceDetailsDAO = dueDiligenceDetailsDAO;
	}

	public void setRecommendationNotesDetailsDAO(RecommendationNotesDetailsDAO recommendationNotesDetailsDAO) {
		this.recommendationNotesDetailsDAO = recommendationNotesDetailsDAO;
	}

	public void setSynopsisDetailsService(SynopsisDetailsService synopsisDetailsService) {
		this.synopsisDetailsService = synopsisDetailsService;
	}

	public void setDrawingPowerService(DrawingPowerService drawingPowerService) {
		this.drawingPowerService = drawingPowerService;
	}

	@Autowired
	public void setCashBackProcessService(@Lazy CashBackProcessService cashBackProcessService) {
		this.cashBackProcessService = cashBackProcessService;
	}

	public void setFinFeeConfigService(FinFeeConfigService finFeeConfigService) {
		this.finFeeConfigService = finFeeConfigService;
	}

	public void setFeeCalculator(FeeCalculator feeCalculator) {
		this.feeCalculator = feeCalculator;
	}

	public void setPmayService(PMAYService pMAYService) {
		pmayService = pMAYService;
	}

	public void setJointAccountDetailDAO(JointAccountDetailDAO jointAccountDetailDAO) {
		this.jointAccountDetailDAO = jointAccountDetailDAO;
	}

	public void setGuarantorDetailDAO(GuarantorDetailDAO guarantorDetailDAO) {
		this.guarantorDetailDAO = guarantorDetailDAO;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public void setPostExteranalServiceHook(PostExteranalServiceHook postExteranalServiceHook) {
		this.postExteranalServiceHook = postExteranalServiceHook;
	}

	public void setTaxHeaderDetailsService(TaxHeaderDetailsService taxHeaderDetailsService) {
		this.taxHeaderDetailsService = taxHeaderDetailsService;
	}

	public void setLinkedFinancesService(LinkedFinancesService linkedFinancesService) {
		this.linkedFinancesService = linkedFinancesService;
	}

	@Override
	public FinanceMain getFinanceMain(long finID, TableType tableType) {
		return financeMainDAO.getFinanceMain(finID, tableType);
	}

	@Override
	public FinanceMain getFinanceMain(String finReference, TableType tableType) {
		return financeMainDAO.getFinanceMain(finReference, tableType);
	}

	@Override
	public Long getFinID(String finReference) {
		return financeMainDAO.getFinID(finReference);
	}

	@Override
	public Long getFinID(String finReference, TableType tableType) {
		return financeMainDAO.getFinID(finReference, tableType);
	}

	public void setExtendedFieldMaintenanceDAO(ExtendedFieldMaintenanceDAO extendedFieldMaintenanceDAO) {
		this.extendedFieldMaintenanceDAO = extendedFieldMaintenanceDAO;
	}

	public void setManualScheduleService(ManualScheduleService manualScheduleService) {
		this.manualScheduleService = manualScheduleService;
	}

	public void setIsraDetailService(ISRADetailService israDetailService) {
		this.israDetailService = israDetailService;
	}

	public void setVariableOverdraftSchdService(VariableOverdraftSchdService variableOverdraftSchdService) {
		this.variableOverdraftSchdService = variableOverdraftSchdService;
	}

	public void setOverdrafLoanService(OverdrafLoanService overdrafLoanService) {
		this.overdrafLoanService = overdrafLoanService;
	}

	@Autowired(required = false)
	public void setExternalFinanceSystemService(ExternalFinanceSystemService externalFinanceSystemService) {
		this.externalFinanceSystemService = externalFinanceSystemService;
	}

	public void setHoldRefundUploadDAO(HoldRefundUploadDAO holdRefundUploadDAO) {
		this.holdRefundUploadDAO = holdRefundUploadDAO;
	}

	@Autowired
	public void setFinFlagsHeaderDAO(FinFlagsHeaderDAO finFlagsHeaderDAO) {
		this.finFlagsHeaderDAO = finFlagsHeaderDAO;
	}

	@Override
	public ReceiptDTO prepareReceiptDTO(FinanceDetail fd) {
		Date appDate = SysParamUtil.getAppDate();

		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceType financeType = schdData.getFinanceType();
		FinanceMain fm = schdData.getFinanceMain();

		ReceiptDTO receiptDTO = new ReceiptDTO();

		FinReceiptData rd = prepareReceiptData(fd);
		feeCalculator.calculateFees(rd);

		receiptDTO.setFinanceMain(fm);
		receiptDTO.setSchedules(schdData.getFinanceScheduleDetails());
		receiptDTO.setOdDetails(schdData.getFinODDetails());
		receiptDTO.setManualAdvises(manualAdviseDAO.getReceivableAdvises(fm.getFinID(), appDate, "_AView"));
		receiptDTO.setFees(rd.getFinanceDetail().getFinScheduleData().getFinFeeDetailList());

		receiptDTO.setRoundAdjMth(SysParamUtil.getValueAsString(SMTParameterConstants.ROUND_ADJ_METHOD));
		receiptDTO.setLppFeeType(feeTypeDAO.getTaxDetailByCode(Allocation.ODC));
		receiptDTO.setFinType(financeType);
		receiptDTO.setValuedate(appDate);

		return receiptDTO;
	}

	private FinReceiptData prepareReceiptData(FinanceDetail fd) {
		FinReceiptData frd = new FinReceiptData();
		FinReceiptHeader frh = new FinReceiptHeader();
		List<CustomerAddres> ca = null;

		ca = fd.getCustomerDetails().getAddressList();
		if (ca == null) {
			ca = new ArrayList<>();
			fd.getCustomerDetails().setAddressList(ca);
		}

		FinScheduleData fsd = fd.getFinScheduleData();

		String finType = fsd.getFinanceType().getFinType();

		fsd.setFeeEvent(AccountingEvent.EARLYSTL);

		fd.setFinTypeFeesList(finTypeFeesDAO.getFinTypeFeesForLMSEvent(finType, AccountingEvent.EARLYSTL));

		frd.setFinanceDetail(fd);
		frd.setTdPriBal(fsd.getFinPftDeatil().getTdSchdPriBal());
		frd.setReceiptHeader(frh);

		return frd;
	}
}
