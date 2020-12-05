/********************************************************************************************************************
 * Copyright 2011 - Pennant Technologies																			*
 * 																													*
 * This file is part of Pennant Java Application Framework and related Products. All								*			
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant	*
 * Technologies.																									*
 * 																													*
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the	*
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a	*
 * violation of copyright law.																						*
 ******************************************************************************************************************** 
 * FILE HEADER 																										*	
 ******************************************************************************************************************** 
 * * FileName : FinanceDetailServiceImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011     *
 * Modified Date : 15-11-2011 * * Description : * *																	*	
 ******************************************************************************************************************** 
 * Date Author Version Comments 																					*
 ********************************************************************************************************************
 * 15-11-2011 Pennant 0.1 																							*
 * 08-05-2018 Vinay   0.2  As per mail from Raju ,subject : Daily status call : 19 April							*			
 * 						   added validations in Disbursement and Covenant types										*
 ********************************************************************************************************************
 *16-05-2018 Madhu Babu 0.3******************																		*
 *As per mail from Raju added the validations to proceed with loan approval and disbursement basd on PDD/OTC 		*
 *																													*
 * 13-06-2018       Siva					 0.4        Stage Accounting Modifications      						* 
 * 																													*
 * 22-06-2018       Srinivas Varma		 	0.5        Post Hook Validation Implementation							*
 *																													*
 *																													* 
 *																													*
 *																													*
 *																													*
 *																													*
 *																													*
 *																													*
 *																													*
 *																													*
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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.security.auth.login.AccountNotFoundException;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.zkoss.util.resource.Labels;

import com.pennant.Interface.service.CustomerLimitIntefaceService;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
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
import com.pennant.app.util.ScheduleCalculator;
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
import com.pennant.backend.dao.finance.FinContributorDetailDAO;
import com.pennant.backend.dao.finance.FinContributorHeaderDAO;
import com.pennant.backend.dao.finance.FinExpenseDetailsDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinFlagDetailsDAO;
import com.pennant.backend.dao.finance.FinTypeVASProductsDAO;
import com.pennant.backend.dao.finance.FinanceTaxDetailDAO;
import com.pennant.backend.dao.finance.FinanceWriteoffDAO;
import com.pennant.backend.dao.finance.HoldDisbursementDAO;
import com.pennant.backend.dao.finance.IRRScheduleDetailDAO;
import com.pennant.backend.dao.finance.IndicativeTermDetailDAO;
import com.pennant.backend.dao.finance.JountAccountDetailDAO;
import com.pennant.backend.dao.finance.LowerTaxDeductionDAO;
import com.pennant.backend.dao.finance.OverdraftScheduleDetailDAO;
import com.pennant.backend.dao.finance.RolledoverFinanceDAO;
import com.pennant.backend.dao.finance.SubventionDetailDAO;
import com.pennant.backend.dao.finance.financialSummary.DealRecommendationMeritsDAO;
import com.pennant.backend.dao.finance.financialSummary.DueDiligenceDetailsDAO;
import com.pennant.backend.dao.finance.financialSummary.RecommendationNotesDetailsDAO;
import com.pennant.backend.dao.finance.financialSummary.RisksAndMitigantsDAO;
import com.pennant.backend.dao.finance.financialSummary.SanctionConditionsDAO;
import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.dao.limits.LimitInterfaceDAO;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.dao.payorderissue.PayOrderIssueHeaderDAO;
import com.pennant.backend.dao.psl.PSLDetailDAO;
import com.pennant.backend.dao.reason.deatil.ReasonDetailDAO;
import com.pennant.backend.dao.receipts.FinExcessAmountDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.dao.rmtmasters.AccountTypeDAO;
import com.pennant.backend.dao.rmtmasters.AccountingSetDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeExpenseDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.dao.rmtmasters.FinTypeInsuranceDAO;
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
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.expenses.FinExpenseDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.AdvancePaymentDetail;
import com.pennant.backend.model.finance.BulkDefermentChange;
import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.model.finance.BundledProductsDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinCustomerDetails;
import com.pennant.backend.model.finance.FinFeeConfig;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinLogEntryDetail;
import com.pennant.backend.model.finance.FinOCRHeader;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinPlanEmiHoliday;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
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
import com.pennant.backend.model.finance.IndicativeTermDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.LMSServiceLog;
import com.pennant.backend.model.finance.LowerTaxDeduction;
import com.pennant.backend.model.finance.OverdraftMovements;
import com.pennant.backend.model.finance.PricingDetail;
import com.pennant.backend.model.finance.ProspectCustomer;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RolledoverFinanceDetail;
import com.pennant.backend.model.finance.RolledoverFinanceHeader;
import com.pennant.backend.model.finance.SubventionScheduleDetail;
import com.pennant.backend.model.finance.TATDetail;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.finance.financialsummary.DealRecommendationMerits;
import com.pennant.backend.model.finance.financialsummary.DueDiligenceDetails;
import com.pennant.backend.model.finance.financialsummary.RecommendationNotes;
import com.pennant.backend.model.finance.financialsummary.RisksAndMitigants;
import com.pennant.backend.model.finance.financialsummary.SanctionConditions;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.model.policecase.PoliceCase;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennant.backend.model.reports.AvailFinance;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rmtmasters.FinTypeExpense;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.service.UpdateAttributeServiceTask;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.backend.service.authorization.AuthorizationLimitService;
import com.pennant.backend.service.collateral.CollateralMarkProcess;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.collateral.impl.FlagDetailValidation;
import com.pennant.backend.service.configuration.impl.VasRecordingValidation;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.dda.DDAControllerService;
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
import com.pennant.backend.service.finance.PSLDetailService;
import com.pennant.backend.service.finance.financialsummary.DealRecommendationMeritsService;
import com.pennant.backend.service.finance.financialsummary.DueDiligenceDetailsService;
import com.pennant.backend.service.finance.financialsummary.RecommendationNotesDetailsService;
import com.pennant.backend.service.finance.financialsummary.RisksAndMitigantsService;
import com.pennant.backend.service.finance.financialsummary.SanctionConditionsService;
import com.pennant.backend.service.finance.financialsummary.SynopsisDetailsService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditFinancialService;
import com.pennant.backend.service.handlinstruction.HandlingInstructionService;
import com.pennant.backend.service.insurance.InsuranceDetailService;
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
import com.pennant.constants.InterfaceConstants;
import com.pennant.coreinterface.model.CustomerLimit;
import com.pennant.coreinterface.model.handlinginstructions.HandlingInstruction;
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
import com.pennanttech.pennapps.notification.Notification;
import com.pennanttech.pennapps.pff.finsampling.service.FinSamplingService;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pennapps.pff.service.hook.PostExteranalServiceHook;
import com.pennanttech.pennapps.pff.service.hook.PostValidationHook;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.TechnicalVerificationService;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceRuleCode;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.model.AdvancePayment;
import com.pennanttech.pff.advancepayment.service.AdvancePaymentService;
import com.pennanttech.pff.core.TableType;
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
import com.pennanttech.pff.notifications.service.NotificationService;
import com.pennanttech.pff.service.sampling.SamplingService;
import com.rits.cloning.Cloner;

/**
 * Service implementation for methods that depends on <b>FinanceMain</b>.<br>
 * 
 */
public class FinanceDetailServiceImpl extends GenericFinanceDetailService implements FinanceDetailService {
	private static final Logger logger = Logger.getLogger(FinanceDetailServiceImpl.class);

	private CustomerIncomeDAO customerIncomeDAO;
	private IncomeTypeDAO incomeTypeDAO;
	private FinContributorHeaderDAO finContributorHeaderDAO;
	private FinContributorDetailDAO finContributorDetailDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	private RuleDAO ruleDAO;
	private AccountingSetDAO accountingSetDAO;
	private RuleExecutionUtil ruleExecutionUtil;
	private AccountTypeDAO accountTypeDAO;
	private CustomerLimitIntefaceService custLimitIntefaceService;
	private FinanceWriteoffDAO financeWriteoffDAO;
	private IndicativeTermDetailDAO indicativeTermDetailDAO;
	private NotesDAO notesDAO;
	private QueueAssignmentDAO queueAssignmentDAO;
	private UserActivityLogDAO userActivityLogDAO;
	private TaskOwnersDAO taskOwnersDAO;
	private LimitInterfaceDAO limitInterfaceDAO;
	private DedupParmService dedupParmService;
	private DDAControllerService ddaControllerService;
	protected RolledoverFinanceDAO rolledoverFinanceDAO;
	private PayOrderIssueHeaderDAO payOrderIssueHeaderDAO;
	private TATDetailDAO tatDetailDAO;
	private CollateralMarkProcess collateralMarkProcess;
	private HandlingInstructionService handlingInstructionService;
	private IRRScheduleDetailDAO irrScheduleDetailDAO;

	private LimitManagement limitManagement;
	private LimitCheckDetails limitCheckDetails;

	private OverdraftScheduleDetailDAO overdraftScheduleDetailDAO;
	private FlagDetailValidation flagDetailValidation;
	private FinFlagDetailsDAO finFlagDetailsDAO;
	private FinChequeHeaderService finChequeHeaderService;
	private FinTypeInsuranceDAO finTypeInsuranceDAO;
	private VASRecordingDAO vasRecordingDAO;
	private FinTypeFeesDAO finTypeFeesDAO;
	private VasRecordingValidation vasRecordingValidation;
	private FinTypeVASProductsDAO finTypeVASProductsDAO;
	private PromotionDAO promotionDAO;
	private FinFeeDetailDAO finFeeDetailDAO;
	private FinanceTaxDetailDAO financeTaxDetailDAO;
	private PSLDetailDAO pSLDetailDAO;
	private FinanceTaxDetailService financeTaxDetailService;

	private ExtendedFieldDetailsService extendedFieldDetailsService;
	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	private ExtendedFieldDetailDAO extendedFieldDetailDAO;
	private FinReceiptHeaderDAO finReceiptHeaderDAO;

	private CustomServiceTask customServiceTask;
	private CustomerService customerService;
	private TechnicalVerificationService technicalVerificationService;

	@Autowired(required = false)
	private CreditInformation creditInformation;
	private ReasonDetailDAO reasonDetailDAO;

	private FinTypeExpenseDAO finTypeExpenseDAO;
	private FinExpenseDetailsDAO finExpenseDetailsDAO;
	private FinIRRDetailsDAO finIRRDetailsDAO;
	private PSLDetailService pSLDetailService;
	private CollateralSetupService collateralSetupService;
	private HoldDisbursementDAO holdDisbursementDAO;
	private PaymentsProcessService paymentsProcessService;
	private ProvisionDAO provisionDAO;

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
	private FinExcessAmountDAO finExcessAmountDAO;
	private SubventionDetailDAO subventionDetailDAO;
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
	private InsuranceDetailService insuranceDetailService;
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

	private long tempWorkflowId;

	@Autowired(required = false)
	private FinOCRHeaderService finOCRHeaderService;
	private PMAYService pmayService;
	private JountAccountDetailDAO jountAccountDetailDAO;
	private FinFeeConfigService finFeeConfigService;
	private FeeCalculator feeCalculator;
	@Autowired(required = false)
	@Qualifier("verificationPostExteranalServiceHook")
	private PostExteranalServiceHook postExteranalServiceHook;

	public FinanceDetailServiceImpl() {
		super();
	}

	@Override
	public FinanceDetail getFinanceDetail(boolean isWIF) {
		logger.debug(Literal.ENTERING);
		FinanceDetail financeDetail = new FinanceDetail();
		financeDetail.getFinScheduleData().setFinanceMain(getFinanceMainDAO().getFinanceMain(isWIF));
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

		List<String> listFirsttaskOwners = getFinanceMainDAO().getFinanceWorlflowFirstTaskOwners(event, moduleName);

		if (listFirsttaskOwners != null && !listFirsttaskOwners.isEmpty()) {

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
		}

		return false;
	}

	/**
	 * getFinanceDetailById fetch the details by using FinanceMainDAO's getFinanceDetailsOrg method.
	 * 
	 * @param finReference
	 *            (String)
	 * @param procEdtEvent
	 *            (String)
	 * @param userrole
	 *            (String)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getOriginationFinance(String finReference, String nextRoleCode, String procEdtEvent,
			String userrole) {
		logger.debug(Literal.ENTERING);

		// Finance Details
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMain(finReference, nextRoleCode, "_TView");
		if (financeMain == null) {
			return null;
		}

		setDasAndDmaData(financeMain);

		FinanceDetail financeDetail = getFinanceOrgDetails(financeMain, "_TView");
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();

		// IRR codes tab
		scheduleData.setiRRDetails(finIRRDetailsDAO.getFinIRRList(financeMain.getFinReference(), "_View"));

		if (scheduleData.getFinanceMain().istDSApplicable() && SysParamUtil.isAllowed("ALLOW_LOWER_TAX_DED_REQ")) {
			scheduleData.setLowerTaxDeductionDetails(getLowerTaxDeductionDAO()
					.getLowerTaxDeductionDetails(scheduleData.getFinanceMain().getFinReference(), "_Temp"));
		}

		financeMain = scheduleData.getFinanceMain();

		// Customer Details
		if (financeMain.getCustID() != 0 && financeMain.getCustID() != Long.MIN_VALUE) {
			financeDetail.setCustomerDetails(
					getCustomerDetailsService().getCustomerDetailsById(financeMain.getCustID(), true, "_AView"));
		}

		// Guaranteer Details
		financeDetail.setGurantorsDetailList(getGuarantorDetailService().getGuarantorDetail(finReference, "_TView"));

		// Joint Account Details
		financeDetail
				.setJountAccountDetailList(getJointAccountDetailService().getJoinAccountDetail(finReference, "_TView"));

		// Finance Fee Details
		financeDetail.getFinScheduleData()
				.setFinFeeDetailList(getFinFeeDetailService().getFinFeeDetailById(finReference, false, "_TView"));

		// Finance Receipt Details
		scheduleData.setFinReceiptDetails(
				getFinFeeDetailService().getFinReceiptDetais(finReference, financeMain.getCustID()));
		//to load Upfront Fee Details by LeadId 
		if (StringUtils.isNotEmpty(financeMain.getOfferId())) {
			// Finance Fee Details
			List<FinReceiptDetail> finReceiptDetails = getFinFeeDetailService()
					.getFinReceiptDetais(financeMain.getOfferId(), financeMain.getCustID());
			if (scheduleData.getFinReceiptDetails() != null) {
				scheduleData.getFinReceiptDetails().addAll(finReceiptDetails);
			} else {
				scheduleData.setFinReceiptDetails(finReceiptDetails);
			}
		}
		List<Long> feeIds = new ArrayList<Long>();
		for (FinFeeDetail finFeeDetail : scheduleData.getFinFeeDetailList()) {
			feeIds.add(finFeeDetail.getFeeID());
		}

		if (!feeIds.isEmpty()) {
			scheduleData.setFinFeeReceipts(getFinFeeDetailService().getFinFeeReceiptsById(feeIds, "_View"));
		}

		// insurance Details
		if (ImplementationConstants.ALLOW_INSURANCE) {
			scheduleData
					.setFinInsuranceList(getFinInsurancesDAO().getFinInsuranceListByRef(finReference, "_TView", false));

			// FinSchFrqInsurance Details
			if (scheduleData.getFinInsuranceList() != null && !scheduleData.getFinInsuranceList().isEmpty()) {

				List<FinSchFrqInsurance> finSchFrqInsurances = getFinInsurancesDAO()
						.getFinSchFrqInsuranceFinRef(finReference, false, "_TView");

				if (finSchFrqInsurances != null && !finSchFrqInsurances.isEmpty()) {

					HashMap<Long, List<FinSchFrqInsurance>> schInsMap = new HashMap<>();
					for (int i = 0; i < finSchFrqInsurances.size(); i++) {
						FinSchFrqInsurance finSchFrqInsurance = finSchFrqInsurances.get(i);

						List<FinSchFrqInsurance> schList = new ArrayList<>();
						if (schInsMap.containsKey(finSchFrqInsurance.getInsId())) {
							schList = schInsMap.get(finSchFrqInsurance.getInsId());
							schInsMap.remove(finSchFrqInsurance.getInsId());
						}
						schList.add(finSchFrqInsurance);
						schInsMap.put(finSchFrqInsurance.getInsId(), schList);

					}

					for (int i = 0; i < scheduleData.getFinInsuranceList().size(); i++) {
						FinInsurances finInsurance = scheduleData.getFinInsuranceList().get(i);
						if (schInsMap.containsKey(finInsurance.getInsId())) {
							finInsurance.setFinSchFrqInsurances(schInsMap.get(finInsurance.getInsId()));
						}
					}
				}
			}
		}

		// Process Editor Details List
		financeDetail = getFinanceReferenceDetails(financeDetail, userrole, "DDE", "", procEdtEvent, true);

		// Document Details
		List<DocumentDetails> documentList = getDocumentDetailsDAO().getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, procEdtEvent, "_TView");
		if (financeDetail.getDocumentDetailsList() != null && !financeDetail.getDocumentDetailsList().isEmpty()) {
			financeDetail.getDocumentDetailsList().addAll(documentList);
		} else {
			financeDetail.setDocumentDetailsList(documentList);
		}

		// Deviations
		if (ImplementationConstants.ALLOW_DEVIATIONS) {
			List<FinanceDeviations> finDeviations = getDeviationDetailsService().getFinanceDeviations(finReference);
			List<FinanceDeviations> apprFinDeviations = getDeviationDetailsService()
					.getApprovedFinanceDeviations(finReference);
			deviationHelper.setDeviationDetails(financeDetail, finDeviations, apprFinDeviations);
		}

		// Mandate
		if (financeMain.getMandateID() > 0) {
			financeDetail.setMandate(getFinMandateService().getMnadateByID(financeMain.getMandateID()));
		}

		// Finance Tax Detail
		financeDetail.setFinanceTaxDetail(getFinanceTaxDetailDAO().getFinanceTaxDetail(finReference, "_TView"));

		// Contributor Details
		if (scheduleData.getFinanceType().isAllowRIAInvestment()) {
			financeDetail.setFinContributorHeader(
					getFinContributorHeaderDAO().getFinContributorHeaderById(finReference, "_TView"));
			if (financeDetail.getFinContributorHeader() != null) {
				financeDetail.getFinContributorHeader().setContributorDetailList(
						getFinContributorDetailDAO().getFinContributorDetailByFinRef(finReference, "_TView"));
			}
		}

		// Contract Asset Details
		if (StringUtils.equals(FinanceConstants.PRODUCT_ISTISNA,
				financeDetail.getFinScheduleData().getFinanceType().getProductCategory())) {
			getContractorAssetDetailService().setContractorAssetDetails(financeDetail, "_TView");
		}

		// Multiple Party Disbursement Details
		financeDetail.setAdvancePaymentsList(
				getFinAdvancePaymentsService().getFinAdvancePaymentsById(finReference, "_View"));

		// Etihad Credit Bureau Details
		if (ImplementationConstants.ALLOW_CREDITBUREAU) {
			financeDetail.setEtihadCreditBureauDetail(
					getEtihadCreditBureauDetailService().getEtihadCreditBureauDetailById(finReference, "_TView"));
		}

		// Bundled Products Details
		if (ImplementationConstants.ALLOW_BUNDLEDPRODUCT) {
			financeDetail.setBundledProductsDetail(
					getBundledProductsDetailService().getBundledProductsDetailById(finReference, "_TView"));
		}

		// Ijarah Related Changes
		if (StringUtils.equals(scheduleData.getFinanceType().getFinDivision(), FinanceConstants.FIN_DIVISION_RETAIL)
				&& (StringUtils.equals(scheduleData.getFinanceType().getFinCategory(), FinanceConstants.PRODUCT_IJARAH)
						|| StringUtils.equals(scheduleData.getFinanceType().getFinCategory(),
								FinanceConstants.PRODUCT_FWIJARAH))) {

			// Agreement Field Details
			financeDetail.setAgreementFieldDetails(
					getAgreementFieldsDetailService().getAgreementFieldDetailsById(finReference, "_TView"));

			// Asset Evaluation Details
			financeDetail.setFinAssetEvaluation(
					getFinAssetEvaluationService().getFinAssetEvaluationById(finReference, "_TView"));
		}

		// Covenant Type Details
		if (ImplementationConstants.ALLOW_COVENANT_TYPES) {
			financeDetail.setCovenantTypeList(
					getFinCovenantTypeService().getFinCovenantTypeById(finReference, "_View", false));
			if (SysParamUtil.isAllowed(SMTParameterConstants.NEW_COVENANT_MODULE)) {
				financeDetail.setCovenants(covenantsService.getCovenants(finReference, "Loan", TableType.VIEW));
			}
		}

		// Put-call

		financeDetail.setFinOptions(finOptionService.getFinOptions(finReference, TableType.VIEW));

		// Asset Type Details
		financeDetail.setFinAssetTypesList(getFinAssetTypeDAO().getFinAssetTypesByFinRef(finReference, "_TView"));

		// Extended Field Details for Assets
		financeDetail.setExtendedFieldRenderList(
				getExtendedAssetDetails(finReference, financeDetail.getFinAssetTypesList()));

		// VAS Recording Details
		if (ImplementationConstants.ALLOW_VAS) {
			financeDetail.getFinScheduleData().setVasRecordingList(getVasRecordings(finReference, "_TView"));
		}

		// Cheque Header and Cheque Details getting
		financeDetail.setChequeHeader(finChequeHeaderService.getChequeHeaderByRef(finReference));

		// Collateral Details
		List<CollateralAssignment> assignmentListMain = null;
		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			assignmentListMain = getCollateralAssignmentDAO().getCollateralAssignmentByFinRef(finReference,
					FinanceConstants.MODULE_NAME, "_TView");
		} else {
			financeDetail
					.setFinanceCollaterals(getFinCollateralService().getFinCollateralsByRef(finReference, "_TView"));
		}

		// Collateral setup details and assignment details
		List<CollateralSetup> collateralSetupList = null;
		if (StringUtils.isNotBlank(financeMain.getParentRef())) {
			collateralSetupList = getCollateralSetupService().getCollateralDetails(financeMain.getParentRef(), false);
		} else {
			collateralSetupList = getCollateralSetupService().getCollateralDetails(finReference, false);
		}

		List<CollateralAssignment> assignmentListTemp = null;
		if (CollectionUtils.isNotEmpty(collateralSetupList)) {
			if (ImplementationConstants.COLLATERAL_INTERNAL) {
				assignmentListTemp = getCollateralAssignmentDAO().getCollateralAssignmentByFinRef(finReference,
						FinanceConstants.MODULE_NAME, "_CTView");
			}
		}
		financeDetail.setCollaterals(collateralSetupList);
		financeDetail = setCollateralAssignments(financeDetail, assignmentListMain, assignmentListTemp);

		financeDetail.setSampling(
				samplingService.getSampling(financeDetail.getFinScheduleData().getFinReference(), "_aview"));

		if (financeDetail.isSamplingApprover()) {
			financeDetail.setSampling(finSamplingService
					.getSamplingDetails(financeDetail.getFinScheduleData().getFinReference(), "_aview"));
		}

		// PSL details
		financeDetail.setPslDetail(pSLDetailService.getPSLDetail((finReference)));

		// Legal details
		if (financeMain.isLegalRequired()) {
			List<LegalDetail> ligelDetailsList = legalDetailService.getLegalDetailByFinreference(finReference);
			if (CollectionUtils.isNotEmpty(ligelDetailsList)) {
				financeDetail.setLegalDetailsList(ligelDetailsList);
			}
		}

		// Financial Summary RisksAndMitigants Details
		List<RisksAndMitigants> risksAndMitigantsList = getRisksAndMitigantsDAO().getRisksAndMitigants(finReference);
		if (financeDetail.getRisksAndMitigantsList() != null && !financeDetail.getRisksAndMitigantsList().isEmpty()) {
			financeDetail.getRisksAndMitigantsList().addAll(risksAndMitigantsList);
		} else {
			financeDetail.setRisksAndMitigantsList(risksAndMitigantsList);
		}

		// Financial Summary SanctionConditions Details
		List<SanctionConditions> sanctionConditionsList = getSanctionConditionsDAO()
				.getSanctionConditions(finReference);
		if (financeDetail.getSanctionDetailsList() != null && !financeDetail.getSanctionDetailsList().isEmpty()) {
			financeDetail.getSanctionDetailsList().addAll(sanctionConditionsList);
		} else {
			financeDetail.setSanctionDetailsList(sanctionConditionsList);
		}
		// Financial Summary DealRecommendationMerits Details
		List<DealRecommendationMerits> dealRecommendationMeritsList = getDealRecommendationMeritsDAO()
				.getDealRecommendationMerits(finReference);
		if (financeDetail.getDealRecommendationMeritsDetailsList() != null
				&& !financeDetail.getDealRecommendationMeritsDetailsList().isEmpty()) {
			financeDetail.getDealRecommendationMeritsDetailsList().addAll(dealRecommendationMeritsList);
		} else {
			financeDetail.setDealRecommendationMeritsDetailsList(dealRecommendationMeritsList);
		}
		// Financial Summary DueDiligences Details
		List<DueDiligenceDetails> dueDiligenceDetailsList = getDueDiligenceDetailsDAO()
				.getDueDiligenceDetails(finReference);
		if (financeDetail.getDueDiligenceDetailsList() != null
				&& !financeDetail.getDueDiligenceDetailsList().isEmpty()) {
			financeDetail.getDueDiligenceDetailsList().addAll(dueDiligenceDetailsList);
		} else {
			financeDetail.setDueDiligenceDetailsList(dueDiligenceDetailsList);
		}
		// Financial Summary DueDiligences Details
		List<RecommendationNotes> recommendationNotesDetailsList = getRecommendationNotesDetailsDAO()
				.getRecommendationNotesDetails(finReference);
		if (financeDetail.getRecommendationNoteList() != null && !financeDetail.getRecommendationNoteList().isEmpty()) {
			financeDetail.getRecommendationNoteList().addAll(recommendationNotesDetailsList);
		} else {
			financeDetail.setRecommendationNoteList(recommendationNotesDetailsList);
		}

		// SynopsisDetails details
		financeDetail.setSynopsisDetails(synopsisDetailsService.getSynopsisDetails(finReference));
		// Finance OCR Details
		financeDetail.setFinOCRHeader(finOCRHeaderService.getFinOCRHeaderByRef(finReference, "_View"));
		// PMAY
		financeDetail.setPmay(pmayService.getPMAY(finReference, "_View"));

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * Removing the collateral assignment if the assignment is available in both main and temp.
	 * 
	 * @param financeDetail
	 * @param assignmentListMain
	 * @param assignmentListTemp
	 * @return
	 */
	private FinanceDetail setCollateralAssignments(FinanceDetail financeDetail,
			List<CollateralAssignment> assignmentListMain, List<CollateralAssignment> assignmentListTemp) {
		if (CollectionUtils.isEmpty(assignmentListMain) && CollectionUtils.isEmpty(assignmentListTemp)) {
			return financeDetail;
		}

		if (CollectionUtils.isEmpty(assignmentListMain)) {
			financeDetail.getCollateralAssignmentList().addAll(assignmentListTemp);
			return financeDetail;
		}

		if (CollectionUtils.isEmpty(assignmentListTemp)) {
			financeDetail.getCollateralAssignmentList().addAll(assignmentListMain);
			return financeDetail;
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
		financeDetail.getCollateralAssignmentList().addAll(resultantList);
		return financeDetail;
	}

	private void setDasAndDmaData(FinanceMain financeMain) {
		logger.debug(Literal.ENTERING);
		List<Long> dealerIds = new ArrayList<Long>();
		long dsaCode = 0;
		long dmaCode = 0;
		long connectorCode = 0;
		if (StringUtils.isNotBlank(financeMain.getDsaCode()) && StringUtils.isNumeric(financeMain.getDsaCode())) {
			dsaCode = Long.parseLong(financeMain.getDsaCode());
			dealerIds.add(dsaCode);
		}
		if (StringUtils.isNotBlank(financeMain.getDmaCode()) && StringUtils.isNumeric(financeMain.getDmaCode())) {
			dmaCode = Long.parseLong(financeMain.getDmaCode());
			dealerIds.add(dmaCode);
		}
		if (financeMain.getConnector() > 0) {
			connectorCode = financeMain.getConnector();
			dealerIds.add(financeMain.getConnector());
		}
		if (dealerIds.size() > 0) {
			List<VehicleDealer> vehicleDealerList = vehicleDealerService.getVehicleDealerById(dealerIds);
			if (vehicleDealerList != null && !vehicleDealerList.isEmpty()) {
				for (VehicleDealer dealer : vehicleDealerList) {
					if (dealer.getDealerId() == dmaCode) {
						financeMain.setDmaName(dealer.getDealerName());
						financeMain.setDmaCodeDesc(dealer.getCode());
					} else if (dealer.getDealerId() == dsaCode) {
						financeMain.setDsaName(dealer.getDealerName());
						financeMain.setDsaCodeDesc(dealer.getCode());
					} else if (dealer.getDealerId() == connectorCode) {
						financeMain.setConnectorCode(dealer.getDealerName());
						financeMain.setConnectorDesc(dealer.getCode());
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Fetching Extended Field Details for the Asset Types
	 * 
	 * @param finReference
	 * @param assetTypes
	 * @return
	 */
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
				aExetendedFieldRender
						.setRecordStatus(StringUtils.equals(String.valueOf(extFieldMap.get("RecordStatus")), "null")
								? "" : String.valueOf(extFieldMap.get("RecordStatus")));
				extFieldMap.remove("RecordStatus");
				aExetendedFieldRender
						.setRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("RoleCode")), "null") ? ""
								: String.valueOf(extFieldMap.get("RoleCode")));
				extFieldMap.remove("RoleCode");
				aExetendedFieldRender
						.setNextRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("NextRoleCode")), "null")
								? "" : String.valueOf(extFieldMap.get("NextRoleCode")));
				extFieldMap.remove("NextRoleCode");
				aExetendedFieldRender.setTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("TaskId")), "null")
						? "" : String.valueOf(extFieldMap.get("TaskId")));
				extFieldMap.remove("TaskId");
				aExetendedFieldRender
						.setNextTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("NextTaskId")), "null") ? ""
								: String.valueOf(extFieldMap.get("NextTaskId")));
				extFieldMap.remove("NextTaskId");
				aExetendedFieldRender
						.setRecordType(StringUtils.equals(String.valueOf(extFieldMap.get("RecordType")), "null") ? ""
								: String.valueOf(extFieldMap.get("RecordType")));
				extFieldMap.remove("RecordType");
				aExetendedFieldRender.setWorkflowId(Long.valueOf(extFieldMap.get("WorkflowId").toString()));
				extFieldMap.remove("WorkflowId");

				aExetendedFieldRender.setMapValues(extFieldMap);
				extendedFieldRenderList.add(aExetendedFieldRender);
			}
		}
		return extendedFieldRenderList;
	}

	/**
	 * Method for Fetching Extended Field Details for the Asset Types
	 * 
	 * @param finReference
	 * @param assetTypes
	 * @return
	 */
	private List<VASRecording> getVasRecordings(String finReference, String tableType) {

		List<VASRecording> vasRecordingsList = new ArrayList<VASRecording>();
		vasRecordingsList = getVasRecordingDAO().getVASRecordingsByLinkRef(finReference, tableType);
		if (vasRecordingsList == null || vasRecordingsList.isEmpty()) {
			return vasRecordingsList;
		}

		List<String> renderedVasProducts = new ArrayList<>();

		// Extended FieldDetails
		for (VASRecording recording : vasRecordingsList) {

			// If Duplicate asset types with multiple record , to avoid multiple
			// DB callings
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
			aExetendedFieldRender
					.setRecordStatus(StringUtils.equals(String.valueOf(extFieldMap.get("RecordStatus")), "null") ? ""
							: String.valueOf(extFieldMap.get("RecordStatus")));
			extFieldMap.remove("RecordStatus");
			aExetendedFieldRender.setRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("RoleCode")), "null")
					? "" : String.valueOf(extFieldMap.get("RoleCode")));
			extFieldMap.remove("RoleCode");
			aExetendedFieldRender
					.setNextRoleCode(StringUtils.equals(String.valueOf(extFieldMap.get("NextRoleCode")), "null") ? ""
							: String.valueOf(extFieldMap.get("NextRoleCode")));
			extFieldMap.remove("NextRoleCode");
			aExetendedFieldRender.setTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("TaskId")), "null") ? ""
					: String.valueOf(extFieldMap.get("TaskId")));
			extFieldMap.remove("TaskId");
			aExetendedFieldRender
					.setNextTaskId(StringUtils.equals(String.valueOf(extFieldMap.get("NextTaskId")), "null") ? ""
							: String.valueOf(extFieldMap.get("NextTaskId")));
			extFieldMap.remove("NextTaskId");
			aExetendedFieldRender
					.setRecordType(StringUtils.equals(String.valueOf(extFieldMap.get("RecordType")), "null") ? ""
							: String.valueOf(extFieldMap.get("RecordType")));
			extFieldMap.remove("RecordType");
			aExetendedFieldRender.setWorkflowId(Long.valueOf(extFieldMap.get("WorkflowId").toString()));
			extFieldMap.remove("WorkflowId");

			aExetendedFieldRender.setMapValues(extFieldMap);
			recording.setExtendedFieldRender(aExetendedFieldRender);
		}
		return vasRecordingsList;
	}

	@Override
	public FinanceDetail getServicingFinanceForQDP(String financeReference, String eventCode, String procEdtEvent,
			String userrole) {
		logger.debug("Entering");

		FinanceDetail financeDetail = getServicingFinance(financeReference, eventCode, procEdtEvent, userrole);
		financeDetail.getFinScheduleData().setFinanceScheduleDetails(
				getFinanceScheduleDetailDAO().getFinScheduleDetails(financeReference, "", false));

		logger.debug("Leaving");

		return financeDetail;
	}

	/**
	 * getFinanceDetailById fetch the details by using FinanceMainDAO's getFinanceDetailsServicing method.
	 * 
	 * @param finReference
	 *            (String)
	 * @param eventCodeRef
	 *            (String)
	 * @param procEdtEvent
	 *            (String)
	 * @param userrole
	 *            (String)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getServicingFinance(String finReference, String eventCodeRef, String procEdtEvent,
			String userrole) {
		logger.debug(Literal.ENTERING);

		// Finance Details
		FinanceDetail financeDetail = getFinSchdDetailById(finReference, "_View", false);
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();

		List<FinServiceInstruction> serviceInstructions = getFinServiceInstructionDAO()
				.getFinServiceInstructions(finReference, "_Temp", procEdtEvent);

		scheduleData.setFinServiceInstructions(serviceInstructions);

		BigDecimal pftChg = BigDecimal.ZERO;
		for (FinServiceInstruction finServiceInstruction : serviceInstructions) {
			pftChg = pftChg.add(finServiceInstruction.getPftChg());
		}

		scheduleData.setPftChg(pftChg);

		// Rolledover Finance Details
		if (StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_ROLLOVER)) {
			RolledoverFinanceHeader header = getRolledoverFinanceDAO().getRolledoverFinanceHeader(finReference,
					"_Temp");
			if (header != null) {
				header.setRolledoverFinanceDetails(
						getRolledoverFinanceDAO().getRolledoverDetailList(finReference, "_View"));
			}
			financeDetail.setRolledoverFinanceHeader(header);
		}

		String tableType = "";
		if (StringUtils.isNotBlank(financeMain.getRecordType())) {
			tableType = "_Temp";
		}
		// Plan EMI Holiday Details
		if (financeMain.isPlanEMIHAlw()) {
			if (StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				scheduleData
						.setPlanEMIHmonths(getFinPlanEmiHolidayDAO().getPlanEMIHMonthsByRef(finReference, tableType));
			} else if (StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
				scheduleData.setPlanEMIHDates(getFinPlanEmiHolidayDAO().getPlanEMIHDatesByRef(finReference, ""));
			}
		}

		// Finance Customer Details
		if (financeMain.getCustID() != 0 && financeMain.getCustID() != Long.MIN_VALUE) {
			financeDetail.setCustomerDetails(
					getCustomerDetailsService().getCustomerDetailsById(financeMain.getCustID(), true, "_View"));
		}

		// Finance Reference Details List
		financeDetail = getFinanceReferenceDetails(financeDetail, userrole, "DDE", eventCodeRef, procEdtEvent, true);

		// Finance Document Details
		List<DocumentDetails> documentList = getDocumentDetailsDAO().getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, procEdtEvent, "_View");
		if (financeDetail.getDocumentDetailsList() != null && !financeDetail.getDocumentDetailsList().isEmpty()) {
			financeDetail.getDocumentDetailsList().addAll(documentList);
		} else {
			financeDetail.setDocumentDetailsList(documentList);
		}

		if (StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_BASICMAINTAIN)
				|| StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_ADDDISB)) {
			// Finance Guaranteer Details
			financeDetail.setGurantorsDetailList(getGuarantorDetailService().getGuarantorDetail(finReference, "_View"));
			// Finance Joint Account Details
			financeDetail.setJountAccountDetailList(
					getJointAccountDetailService().getJoinAccountDetail(finReference, "_View"));
		}

		if (StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_ADDDISB)
				|| StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_CANCELDISB)) {
			// Advance Payment Details
			financeDetail.setAdvancePaymentsList(
					getFinAdvancePaymentsService().getFinAdvancePaymentsById(finReference, "_View"));
		}

		if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
			financeDetail.setFinTypeFeesList(getFinTypeFeesDAO().getFinTypeFeesList(financeMain.getPromotionCode(),
					eventCodeRef, "_AView", false, FinanceConstants.MODULEID_PROMOTION));
		} else {
			financeDetail.setFinTypeFeesList(getFinTypeFeesDAO().getFinTypeFeesList(financeMain.getFinType(),
					eventCodeRef, "_AView", false, FinanceConstants.MODULEID_FINTYPE));
		}

		// Finance Fee Details
		scheduleData.getFinFeeDetailList()
				.addAll((getFinFeeDetailService().getFinFeeDetailById(finReference, false, "_TView", eventCodeRef)));

		List<FinanceDisbursement> deductDisbFeeList = getFinanceDisbursementDAO().getDeductDisbFeeDetails(finReference);

		if (CollectionUtils.isNotEmpty(deductDisbFeeList)) {
			for (FinanceDisbursement disbursement : deductDisbFeeList) {
				for (FinanceDisbursement finDisb : scheduleData.getDisbursementDetails()) {
					if (finDisb.getDisbSeq() == disbursement.getDisbSeq()) {
						finDisb.setDeductFeeDisb(disbursement.getDeductFeeDisb());
						break;
					}
				}
			}
		}

		/*
		 * // Finance Receipt Details scheduleData.setFinReceiptDetails(getFinFeeDetailService().
		 * getFinReceiptDetais(finReference)); List<Long> feeIds = new ArrayList<Long>(); for (FinFeeDetail finFeeDetail
		 * : scheduleData.getFinFeeDetailList()) { feeIds.add(finFeeDetail.getFeeID()); }
		 * 
		 * if (!feeIds.isEmpty()) { scheduleData.setFinFeeReceipts(getFinFeeDetailService().
		 * getFinFeeReceiptsById(feeIds, "_TView")); }
		 */

		// Collateral Details
		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			financeDetail.setCollateralAssignmentList(getCollateralAssignmentDAO()
					.getCollateralAssignmentByFinRef(finReference, FinanceConstants.MODULE_NAME, "_View"));
			if (CollectionUtils.isEmpty(financeDetail.getCollateralAssignmentList())) {
				financeDetail.setCollateralAssignmentList(collateralSetupService.getCollateralAssignmentByFinRef(
						financeMain.getFinReference(), FinanceConstants.MODULE_NAME, "_CTView"));
			}
		} else {
			financeDetail
					.setFinanceCollaterals(getFinCollateralService().getFinCollateralsByRef(finReference, "_TView"));
		}

		financeDetail.setCovenantTypeList(
				getFinCovenantTypeService().getFinCovenantTypeById(financeMain.getFinReference(), "_View", false));

		if (SysParamUtil.isAllowed(SMTParameterConstants.NEW_COVENANT_MODULE)) {
			financeDetail.setCovenants(covenantsService.getCovenants(finReference, "Loan", TableType.VIEW));
		}
		// Finance OCR Details
		if (financeMain.isFinOcrRequired()) {
			financeDetail.setFinOCRHeader(finOCRHeaderService.getApprovedFinOCRHeaderByRef(finReference, "_View"));
		}
		logger.debug(Literal.LEAVING);

		return financeDetail;
	}

	/**
	 * getFinanceDetailById fetch the details by using FinanceMainDAO's getFinanceDetailsWIF method.
	 * 
	 * @param finReference
	 *            (String)
	 * @param reqCustDetail
	 *            (String)
	 * @param procEdtEvent
	 *            (boolean)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getWIFFinance(String finReference, boolean reqCustDetail, String procEdtEvent) {
		logger.debug(Literal.ENTERING);

		// Finance Details
		FinanceDetail financeDetail = getFinSchdDetailById(finReference, "_View", true);
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();

		// Finance Fee Details
		scheduleData.setFinFeeDetailList(getFinFeeDetailService().getFinFeeDetailById(finReference, true, "_View"));

		if (scheduleData.getFinanceMain() != null) {
			long custId = scheduleData.getFinanceMain().getCustID();
			if (custId != 0) {
				financeDetail.setCustomer(getCustomerDAO().getWIFCustomerByID(custId, null, "_AView"));

				if (financeDetail.getCustomer() == null) {
					Customer customer = getCustomerDAO().getCustomerByID(custId, "");
					WIFCustomer wifcustomer = new WIFCustomer();
					BeanUtils.copyProperties(customer, wifcustomer);
					wifcustomer.setExistCustID(wifcustomer.getCustID());
					wifcustomer.setCustID(0);
					wifcustomer.setNewRecord(true);
					financeDetail.setCustomer(wifcustomer);
				} else {
					financeDetail.getCustomer().setNewRecord(false);
				}
			}
		}

		if (reqCustDetail && scheduleData.getFinanceMain() != null) {

			if (StringUtils.equals(scheduleData.getFinanceType().getFinDivision(),
					FinanceConstants.FIN_DIVISION_RETAIL)) {

				long custId = scheduleData.getFinanceMain().getCustID();
				if (custId != 0) {
					financeDetail.setCustomer(getCustomerDAO().getWIFCustomerByID(custId, null, "_AView"));

					if (financeDetail.getCustomer() == null) {
						Customer customer = getCustomerDAO().getCustomerByID(custId, "");
						WIFCustomer wifcustomer = new WIFCustomer();
						BeanUtils.copyProperties(customer, wifcustomer);
						wifcustomer.setExistCustID(wifcustomer.getCustID());
						wifcustomer.setCustID(0);
						wifcustomer.setNewRecord(true);
						financeDetail.setCustomer(wifcustomer);
					}
				} else {
					WIFCustomer wifcustomer = new WIFCustomer();
					wifcustomer.setNewRecord(true);
					PFSParameter parameter = SysParamUtil.getSystemParameterObject(PennantConstants.LOCAL_CCY);
					wifcustomer.setCustBaseCcy(parameter.getSysParmValue().trim());

					Country defaultCountry = PennantApplicationUtil.getDefaultCounty();

					wifcustomer.setCustNationality(defaultCountry.getCountryCode());
					wifcustomer.setLovDescCustNationalityName(defaultCountry.getCountryDesc());
					wifcustomer.setCustTypeCode("EA");
					wifcustomer.setLovDescCustTypeCodeName("Individual");
					wifcustomer.setCustCtgCode("INDV");
					wifcustomer.setLovDescCustCtgCodeName("Individual");
					financeDetail.setCustomer(wifcustomer);
				}

				financeDetail.getCustomer().setCustomerIncomeList(prepareIncomeDetails());
				String finType = scheduleData.getFinanceType().getFinType();

				financeDetail.setElgRuleList(getEligibilityDetailService().setFinanceEligibilityDetails(finReference,
						financeMain.getFinCcy(), financeMain.getFinAmount(), financeMain.isNewRecord(), finType, null,
						procEdtEvent));

				financeDetail = getScoringDetailService().setFinanceScoringDetails(financeDetail, finType, null,
						PennantConstants.PFF_CUSTCTG_INDIV, procEdtEvent);
			} else {
				IndicativeTermDetail termDetail = getIndicativeTermDetailDAO().getIndicateTermByRef(finReference,
						"_View", true);
				if (termDetail == null) {
					termDetail = new IndicativeTermDetail();
					termDetail.setCustId(financeMain.getCustID());
					termDetail.setLovDescCustShrtName(financeMain.getLovDescCustShrtName());
					termDetail.setNewRecord(true);
					termDetail.setWorkflowId(0);
				}
				financeDetail.setIndicativeTermDetail(termDetail);
			}

		} else if (StringUtils.equals(scheduleData.getFinanceType().getFinDivision(),
				FinanceConstants.FIN_DIVISION_RETAIL)) {

			ProspectCustomer propCustomer = getCustomerDAO().getProspectCustomer(finReference, "_View");
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

	/**
	 * getFinanceDetailById fetch the details by using FinanceMainDAO's getFinanceDetailById method.
	 * 
	 * @param finReference
	 *            (String)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getFinanceDetailById(String finReference, boolean isWIF, String eventCodeRef,
			boolean reqCustDetail, String procEdtEvent, String userRole) {
		logger.debug(Literal.ENTERING);

		// Finance Details
		FinanceDetail financeDetail = getFinSchdDetailById(finReference, "_View", isWIF);
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();

		// Finance Service Instructions
		financeDetail.getFinScheduleData().setFinServiceInstructions(
				getFinServiceInstructionDAO().getFinServiceInstructions(finReference, "_Temp", procEdtEvent));

		// Rolledover Finance Details
		if (StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_ROLLOVER)) {
			RolledoverFinanceHeader header = getRolledoverFinanceDAO().getRolledoverFinanceHeader(finReference,
					"_Temp");

			if (header != null) {
				header.setRolledoverFinanceDetails(
						getRolledoverFinanceDAO().getRolledoverDetailList(finReference, "_View"));
			}
			financeDetail.setRolledoverFinanceHeader(header);
		}

		if (isWIF && reqCustDetail && scheduleData.getFinanceMain() != null) {

			if (StringUtils.equals(scheduleData.getFinanceType().getFinDivision(),
					FinanceConstants.FIN_DIVISION_RETAIL)) {

				long custId = scheduleData.getFinanceMain().getCustID();
				if (custId != 0) {
					financeDetail.setCustomer(getCustomerDAO().getWIFCustomerByID(custId, null, "_AView"));
					if (financeDetail.getCustomer() == null) {
						Customer customer = getCustomerDAO().getCustomerByID(custId, "");
						WIFCustomer wifcustomer = new WIFCustomer();
						BeanUtils.copyProperties(customer, wifcustomer);
						wifcustomer.setExistCustID(wifcustomer.getCustID());
						wifcustomer.setCustID(0);
						wifcustomer.setNewRecord(true);
						financeDetail.setCustomer(wifcustomer);
					}
				} else {
					WIFCustomer wifcustomer = new WIFCustomer();
					wifcustomer.setNewRecord(true);
					PFSParameter parameter = SysParamUtil.getSystemParameterObject(PennantConstants.LOCAL_CCY);
					wifcustomer.setCustBaseCcy(parameter.getSysParmValue().trim());

					Country defaultCountry = PennantApplicationUtil.getDefaultCounty();
					wifcustomer.setCustNationality(defaultCountry.getCountryCode());
					wifcustomer.setLovDescCustNationalityName(defaultCountry.getCountryDesc());

					wifcustomer.setCustTypeCode("EA");
					wifcustomer.setLovDescCustTypeCodeName("Individual");
					wifcustomer.setCustCtgCode("INDV");
					wifcustomer.setLovDescCustCtgCodeName("Individual");
					financeDetail.setCustomer(wifcustomer);
				}
				financeDetail.getCustomer().setCustomerIncomeList(prepareIncomeDetails());
				String finType = scheduleData.getFinanceType().getFinType();

				financeDetail.setElgRuleList(getEligibilityDetailService().setFinanceEligibilityDetails(finReference,
						financeMain.getFinCcy(), financeMain.getFinAmount(), financeMain.isNewRecord(), finType, null,
						procEdtEvent));

				financeDetail = getScoringDetailService().setFinanceScoringDetails(financeDetail, finType, null,
						PennantConstants.PFF_CUSTCTG_INDIV, procEdtEvent);
			} else {
				IndicativeTermDetail termDetail = getIndicativeTermDetailDAO().getIndicateTermByRef(finReference,
						"_View", true);
				if (termDetail == null) {
					termDetail = new IndicativeTermDetail();
					termDetail.setCustId(financeMain.getCustID());
					termDetail.setLovDescCustShrtName(financeMain.getLovDescCustShrtName());
					termDetail.setNewRecord(true);
					termDetail.setWorkflowId(0);
				}
				financeDetail.setIndicativeTermDetail(termDetail);
			}

		} else if (isWIF && StringUtils.equals(scheduleData.getFinanceType().getFinDivision(),
				FinanceConstants.FIN_DIVISION_RETAIL)) {
			ProspectCustomer propCustomer = getCustomerDAO().getProspectCustomer(finReference, "_View");
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

		if (!isWIF) {

			// Finance Customer Details
			if (financeMain.getCustID() != 0 && financeMain.getCustID() != Long.MIN_VALUE) {
				financeDetail.setCustomerDetails(
						getCustomerDetailsService().getCustomerDetailsById(financeMain.getCustID(), true, "_View"));
			}

			// Deviation Details
			if (ImplementationConstants.ALLOW_DEVIATIONS) {
				if (StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_ORG)) {
					List<FinanceDeviations> finDeviations = getDeviationDetailsService()
							.getFinanceDeviations(finReference);
					List<FinanceDeviations> apprFinDeviations = getDeviationDetailsService()
							.getApprovedFinanceDeviations(finReference);
					deviationHelper.setDeviationDetails(financeDetail, finDeviations, apprFinDeviations);
				}
			}

			// Mandate Details
			financeDetail.setMandate(getFinMandateService().getMnadateByID(financeMain.getMandateID()));
		}

		if (!isWIF && financeMain != null) {

			// Finance Reference Details List
			financeDetail = getFinanceReferenceDetails(financeDetail, userRole, "DDE", eventCodeRef, procEdtEvent,
					true);

			// Finance Document Details
			List<DocumentDetails> documentList = getDocumentDetailsDAO().getDocumentDetailsByRef(finReference,
					FinanceConstants.MODULE_NAME, procEdtEvent, "_View");
			if (financeDetail.getDocumentDetailsList() != null && !financeDetail.getDocumentDetailsList().isEmpty()) {
				financeDetail.getDocumentDetailsList().addAll(documentList);
			} else {
				financeDetail.setDocumentDetailsList(documentList);
			}

			if (StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_ORG)
					|| StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_BASICMAINTAIN)) {
				// Finance Guaranteer Details
				financeDetail
						.setGurantorsDetailList(getGuarantorDetailService().getGuarantorDetail(finReference, "_View"));

				// Finance Joint Account Details
				financeDetail.setJountAccountDetailList(
						getJointAccountDetailService().getJoinAccountDetail(finReference, "_View"));
			}

			if (StringUtils.equals(procEdtEvent, FinanceConstants.FINSER_EVENT_ORG)) {
				// Finance Contributor Details
				if (scheduleData.getFinanceType().isAllowRIAInvestment()) {
					financeDetail.setFinContributorHeader(
							getFinContributorHeaderDAO().getFinContributorHeaderById(finReference, "_View"));
					if (financeDetail.getFinContributorHeader() != null) {
						financeDetail.getFinContributorHeader().setContributorDetailList(
								getFinContributorDetailDAO().getFinContributorDetailByFinRef(finReference, "_View"));
					}
				}
				// finance Contract Asset Details
				if (FinanceConstants.PRODUCT_ISTISNA
						.equals(financeDetail.getFinScheduleData().getFinanceType().getFinCategory())) {
					getContractorAssetDetailService().setContractorAssetDetails(financeDetail, "_View");
				} else {
					financeDetail.setContractorAssetDetails(null);
				}

				// Etihad Credit Bureau Details
				if (ImplementationConstants.ALLOW_CREDITBUREAU) {
					financeDetail.setEtihadCreditBureauDetail(getEtihadCreditBureauDetailService()
							.getEtihadCreditBureauDetailById(finReference, "_View"));
				}

				// Bundled Products Details
				if (ImplementationConstants.ALLOW_BUNDLEDPRODUCT) {
					if (scheduleData.getFinanceType() != null && StringUtils.equals(
							scheduleData.getFinanceType().getFinDivision(), FinanceConstants.FIN_DIVISION_RETAIL)) {
						financeDetail.setBundledProductsDetail(
								getBundledProductsDetailService().getBundledProductsDetailById(finReference, "_View"));
					}
				}

				// Agreement Field Details
				if (scheduleData.getFinanceType() != null
						&& StringUtils.equals(scheduleData.getFinanceType().getFinDivision(),
								FinanceConstants.FIN_DIVISION_RETAIL)
						&& (StringUtils.equals(scheduleData.getFinanceType().getFinCategory(),
								FinanceConstants.PRODUCT_IJARAH)
								|| StringUtils.equals(scheduleData.getFinanceType().getFinCategory(),
										FinanceConstants.PRODUCT_FWIJARAH))) {
					financeDetail.setAgreementFieldDetails(
							getAgreementFieldsDetailService().getAgreementFieldDetailsById(finReference, "_View"));
				}

				// Advance Payment Details
				financeDetail.setAdvancePaymentsList(
						getFinAdvancePaymentsService().getFinAdvancePaymentsById(finReference, "_View"));

				// Covenant Type Details
				if (ImplementationConstants.ALLOW_COVENANT_TYPES) {
					financeDetail.setCovenantTypeList(
							getFinCovenantTypeService().getFinCovenantTypeById(finReference, "_View", false));
				}

				if (SysParamUtil.isAllowed(SMTParameterConstants.NEW_COVENANT_MODULE)) {
					financeDetail.setCovenants(covenantsService.getCovenants(finReference, "Loan", TableType.VIEW));
				}

				financeDetail.setFinOptions(finOptionService.getFinOptions(finReference, TableType.VIEW));

				// Asset Evaluation Details
				financeDetail.setFinAssetEvaluation(
						getFinAssetEvaluationService().getFinAssetEvaluationById(finReference, "_View"));

				// FinAssetType
				financeDetail
						.setFinAssetTypesList(getFinAssetTypeDAO().getFinAssetTypesByFinRef(finReference, "_Temp"));

				// Extended Field Details for Assets
				financeDetail.setExtendedFieldRenderList(
						getExtendedAssetDetails(finReference, financeDetail.getFinAssetTypesList()));

				// Collateral Details
				if (ImplementationConstants.COLLATERAL_INTERNAL) {
					financeDetail.setCollateralAssignmentList(getCollateralAssignmentDAO()
							.getCollateralAssignmentByFinRef(finReference, FinanceConstants.MODULE_NAME, "_View"));
				} else {
					financeDetail.setFinanceCollaterals(
							getFinCollateralService().getFinCollateralsByRef(finReference, "_View"));
				}
			}
		}

		// Finance Fee Details
		financeDetail.getFinScheduleData()
				.setFinFeeDetailList(getFinFeeDetailService().getFinFeeDetailById(finReference, isWIF, "_View"));

		logger.debug(Literal.LEAVING);

		return financeDetail;
	}

	/**
	 * getFinanceDetailById fetch the details by using FinanceMainDAO's getFinanceDetailById method.
	 * 
	 * @param finReference
	 *            (String)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getWIFFinanceDetailById(String finReference, String procEdtEvent) {
		logger.debug(Literal.ENTERING);

		// Finance Details
		FinanceDetail financeDetail = getFinSchdDetailById(finReference, "_View", true);
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();

		// Finance Service Instructions
		financeDetail.getFinScheduleData().setFinServiceInstructions(
				getFinServiceInstructionDAO().getFinServiceInstructions(finReference, "_Temp", procEdtEvent));

		// Finance Fee Details
		financeDetail.getFinScheduleData()
				.setFinFeeDetailList(getFinFeeDetailService().getFinFeeDetailById(finReference, true, "_View"));

		if (StringUtils.equals(scheduleData.getFinanceType().getFinDivision(), FinanceConstants.FIN_DIVISION_RETAIL)) {
			ProspectCustomer propCustomer = getCustomerDAO().getProspectCustomer(finReference, "_View");
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
	public FinanceMain getFinanceMain(String finReference, String type) {
		return getFinanceMainDAO().getFinanceMainById(finReference, type, false);
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
		Long accSetId = getFinTypeAccountingDAO().getAccountSetID(finType.getFinType(), eventCode,
				FinanceConstants.MODULEID_FINTYPE);

		// Fetch Stage Accounting AccountingSetId List
		List<Long> accSetIdList = new ArrayList<Long>();
		accSetIdList.addAll(getFinanceReferenceDetailDAO().getRefIdListByFinType(finType.getFinType(),
				FinanceConstants.FINSER_EVENT_ORG, null, "_ACView"));
		if (accSetId != Long.MIN_VALUE) {
			accSetIdList.add(Long.valueOf(accSetId));
		}

		// Finance Fee Charge Details
		List<Rule> feeChargeList = new ArrayList<Rule>();
		if (!accSetIdList.isEmpty()) {
			feeChargeList = getTransactionEntryDAO().getListFeeChargeRules(accSetIdList,
					eventCode.startsWith(AccountEventConstants.ACCEVENT_ADDDBS) ? AccountEventConstants.ACCEVENT_ADDDBS
							: eventCode,
					"", 0);
		}

		logger.debug(Literal.LEAVING);
		return feeChargeList;
	}

	/**
	 * Method for Fetch Ins insurance Details Object for Early Settlement Refund
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public FeeRule getInsFee(String finReference) {
		return getFinFeeChargesDAO().getInsFee(finReference, "");
	}

	/**
	 * Method for Fetching List of Fee Rules From Approved Finance
	 */
	@Override
	public List<FeeRule> getApprovedFeeRules(String finReference, String finEvent, boolean isWIF) {
		logger.debug(Literal.ENTERING);
		List<FeeRule> feeRuleList = getFinFeeChargesDAO().getFeeChargesByFinRef(finReference, finEvent, isWIF, "");
		logger.debug(Literal.LEAVING);
		return feeRuleList;
	}

	@Override
	public List<ContractorAssetDetail> getContractorAssetDetailList(String finReference) {
		logger.debug(Literal.ENTERING);
		List<ContractorAssetDetail> assetDetails = getContractorAssetDetailService()
				.getContractorAssetDetailList(finReference, "_AView");
		logger.debug(Literal.LEAVING);
		return assetDetails;
	}

	/**
	 * getApprovedFinanceDetailById fetch the details by using FinanceMainDAO's getFinanceDetailById method . with
	 * parameter id and type as blank. it fetches the approved records from the FinanceMain.
	 * 
	 * @param finReference
	 *            (String)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getApprovedFinanceDetailById(String finReference, boolean isWIF) {
		return getFinSchdDetailById(finReference, "_AView", isWIF);

	}

	/**
	 * Method to fetch finance details by id from given table type
	 * 
	 * @param finReference
	 *            (String)
	 * @param type
	 *            (String)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getFinanceOrgDetails(FinanceMain financeMain, String type) {
		logger.debug(Literal.ENTERING);

		// Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(financeMain.getFinReference());
		scheduleData.setFinanceMain(financeMain);
		String finReference = scheduleData.getFinanceMain().getFinReference();

		// Finance Type Details
		FinanceType financeType = getFinanceTypeDAO().getOrgFinanceTypeByID(scheduleData.getFinanceMain().getFinType(),
				"_ORGView");
		if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
			// Fetching Promotion Details
			Promotion promotion = this.promotionDAO.getPromotionByReferenceId(financeMain.getPromotionSeqId(),
					"_AView");
			financeType.setFInTypeFromPromotiion(promotion);
		}
		scheduleData.setFinanceType(financeType);

		// Step Policy Details List
		if (scheduleData.getFinanceMain().isStepFinance()) {
			scheduleData.setStepPolicyDetails(
					getFinanceStepDetailDAO().getFinStepDetailListByFinRef(finReference, type, false));
		}

		// Overdraft Details
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			scheduleData.setOverdraftScheduleDetails(
					getOverdraftScheduleDetailDAO().getOverdraftScheduleDetails(finReference, "_Temp", false));
		}

		// Finance Flag Details
		financeDetail.setFinFlagsDetails(
				getFinFlagDetailsDAO().getFinFlagsByFinRef(finReference, FinanceConstants.MODULE_NAME, "_Temp"));

		// Finance Schedule Details
		scheduleData.setFinanceScheduleDetails(
				getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, false));

		// Finance Disbursement Details
		scheduleData.setDisbursementDetails(
				getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type, false));

		// Finance Repayments Instruction Details
		scheduleData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));

		String tableType = "";
		if (StringUtils.isNotBlank(scheduleData.getFinanceMain().getRecordType())) {
			tableType = "_Temp";
		}

		scheduleData.setFinServiceInstructions(
				finServiceInstructionDAO.getOrgFinServiceInstructions(finReference, tableType));

		// Finance Overdue Penalty Rate Details
		scheduleData.setFinODPenaltyRate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference, type));

		// Fetch Secondary account Details
		if (ImplementationConstants.ACCOUNTS_APPLICABLE) {
			scheduleData.getFinanceMain().setSecondaryAccount(getSecondaryAccountDAO()
					.getSecondaryAccountsByFinRef(scheduleData.getFinanceMain().getFinReference(), type));
		}

		// Plan EMI Holiday Details
		int sdSize = scheduleData.getFinanceScheduleDetails().size();
		if (financeMain.isPlanEMIHAlw() && sdSize > 0) {
			if (StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				scheduleData
						.setPlanEMIHmonths(getFinPlanEmiHolidayDAO().getPlanEMIHMonthsByRef(finReference, tableType));
			} else if (StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
				scheduleData.setPlanEMIHDates(getFinPlanEmiHolidayDAO().getPlanEMIHDatesByRef(finReference, tableType));
			}
		}

		financeDetail.setFinScheduleData(scheduleData);
		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * Method to fetch finance details by id from given table type
	 * 
	 * @param finReference
	 *            (String)
	 * @param type
	 *            (String)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getFinSchdDetailById(String finReference, String type, boolean isWIF) {
		logger.debug(Literal.ENTERING);

		// Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		scheduleData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, isWIF));

		if (scheduleData.getFinanceMain() != null) {

			setDasAndDmaData(scheduleData.getFinanceMain());

			// Finance Type Details
			FinanceType financeType = getFinanceTypeDAO()
					.getOrgFinanceTypeByID(scheduleData.getFinanceMain().getFinType(), "_ORGView");
			if (StringUtils.isNotBlank(scheduleData.getFinanceMain().getPromotionCode())) {
				// Fetching Promotion Details
				Promotion promotion = this.promotionDAO
						.getPromotionByReferenceId(scheduleData.getFinanceMain().getPromotionSeqId(), "_AView");
				financeType.setFInTypeFromPromotiion(promotion);
			}
			scheduleData.setFinanceType(financeType);

			// Step Policy Details List
			if (scheduleData.getFinanceMain().isStepFinance()) {
				scheduleData.setStepPolicyDetails(getFinanceStepDetailDAO().getFinStepDetailListByFinRef(finReference,
						isWIF ? "_View" : "_TView", isWIF));
			}

			// Finance Schedule Details
			scheduleData.setFinanceScheduleDetails(
					getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, isWIF));

			// Finance Disbursement Details
			scheduleData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference,
					isWIF ? "_View" : type, isWIF));

			// Finance Repayments Instruction Details
			scheduleData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, isWIF));

			// Fee Details
			List<FinFeeDetail> finOriginationFeeList = getFinFeeDetailDAO().getFinScheduleFees(finReference, false,
					"_View");
			scheduleData.setFinFeeDetailList(finOriginationFeeList);

			// Finance Fee Schedule Details
			if (finOriginationFeeList != null && !finOriginationFeeList.isEmpty()) {
				List<Long> feeIDList = new ArrayList<>();
				for (FinFeeDetail feeDetail : finOriginationFeeList) {
					feeIDList.add(feeDetail.getFeeID());
					feeDetail.setRcdVisible(false);
				}

				if (!feeIDList.isEmpty()) {
					List<FinFeeScheduleDetail> feeScheduleList = getFinFeeScheduleDetailDAO()
							.getFeeScheduleByFinID(feeIDList, false, "");

					if (feeScheduleList != null && !feeScheduleList.isEmpty()) {
						HashMap<Long, List<FinFeeScheduleDetail>> schFeeMap = new HashMap<>();
						for (int i = 0; i < feeScheduleList.size(); i++) {
							FinFeeScheduleDetail schdFee = feeScheduleList.get(i);

							List<FinFeeScheduleDetail> schList = new ArrayList<>();
							if (schFeeMap.containsKey(schdFee.getFeeID())) {
								schList = schFeeMap.get(schdFee.getFeeID());
								schFeeMap.remove(schdFee.getFeeID());
							}
							schList.add(schdFee);
							schFeeMap.put(schdFee.getFeeID(), schList);

						}

						for (int i = 0; i < finOriginationFeeList.size(); i++) {
							FinFeeDetail feeDetail = finOriginationFeeList.get(i);
							if (schFeeMap.containsKey(feeDetail.getFeeID())) {
								feeDetail.setFinFeeScheduleDetailList(schFeeMap.get(feeDetail.getFeeID()));
							}
						}
					}
				}
			}

			// Finance Overdue Penalty Rate Details
			if (!isWIF) {

				// Finance Flag Details
				financeDetail.setFinFlagsDetails(
						getFinFlagDetailsDAO().getFinFlagsByFinRef(finReference, FinanceConstants.MODULE_NAME, type));

			}

			// insurance Details
			scheduleData.setFinInsuranceList(getFinInsurancesDAO().getFinInsuranceListByRef(finReference, type, isWIF));

			if (scheduleData.getFinanceMain().istDSApplicable()) {
				scheduleData.setLowerTaxDeductionDetails(getLowerTaxDeductionDAO()
						.getLowerTaxDeductionDetails(scheduleData.getFinanceMain().getFinReference(), ""));
			}

			// FinSchFrqInsurance Details
			if (scheduleData.getFinInsuranceList() != null && !scheduleData.getFinInsuranceList().isEmpty()) {
				List<FinSchFrqInsurance> finSchFrqInsurances = getFinInsurancesDAO()
						.getFinSchFrqInsuranceFinRef(finReference, isWIF, "_View");
				HashMap<Long, List<FinSchFrqInsurance>> schInsMap = new HashMap<>();

				for (int i = 0; i < finSchFrqInsurances.size(); i++) {
					FinSchFrqInsurance finSchFrqInsurance = finSchFrqInsurances.get(i);

					List<FinSchFrqInsurance> schList = new ArrayList<>();
					if (schInsMap.containsKey(finSchFrqInsurance.getInsId())) {
						schList = schInsMap.get(finSchFrqInsurance.getInsId());
						schInsMap.remove(finSchFrqInsurance.getInsId());
					}
					schList.add(finSchFrqInsurance);
					schInsMap.put(finSchFrqInsurance.getInsId(), schList);

				}

				for (int i = 0; i < scheduleData.getFinInsuranceList().size(); i++) {
					FinInsurances finInsurance = scheduleData.getFinInsuranceList().get(i);
					if (StringUtils.equals(finInsurance.getPaymentMethod(), InsuranceConstants.PAYTYPE_SCH_FRQ)
							&& schInsMap.containsKey(finInsurance.getInsId())) {
						finInsurance.setFinSchFrqInsurances(schInsMap.get(finInsurance.getInsId()));
					}
				}
				schInsMap = null;
			}

			if (!isWIF) {

				String tableType = "";
				if (StringUtils.isNotBlank(scheduleData.getFinanceMain().getRecordType())) {
					tableType = "_Temp";
				}

				// Overdue Penalty Rates
				scheduleData.setFinODPenaltyRate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference,
						StringUtils.equals(tableType, "") ? type : tableType));

				// Overdraft Schedule Detail
				if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
						scheduleData.getFinanceMain().getProductCategory())) {
					scheduleData.setOverdraftScheduleDetails(getOverdraftScheduleDetailDAO()
							.getOverdraftScheduleDetails(finReference, tableType, isWIF));
				}

				// Fetch Secondary account Details
				if (ImplementationConstants.ACCOUNTS_APPLICABLE) {
					scheduleData.getFinanceMain().setSecondaryAccount(getSecondaryAccountDAO()
							.getSecondaryAccountsByFinRef(scheduleData.getFinanceMain().getFinReference(), type));
				}

				// Get Collateral Details if any
				List<CollateralAssignment> assignmentListMain = null;
				if (ImplementationConstants.COLLATERAL_INTERNAL) {
					assignmentListMain = getCollateralAssignmentDAO().getCollateralAssignmentByFinRef(finReference,
							FinanceConstants.MODULE_NAME, "_TView");
				} else {
					financeDetail.setFinanceCollaterals(
							getFinCollateralService().getFinCollateralsByRef(finReference, "_TView"));
				}

				// Collateral setup details and assignment details
				List<CollateralSetup> collateralSetupList = getCollateralSetupService()
						.getCollateralDetails(finReference);
				List<CollateralAssignment> assignmentListTemp = null;
				if (CollectionUtils.isNotEmpty(collateralSetupList)) {
					if (ImplementationConstants.COLLATERAL_INTERNAL) {
						assignmentListTemp = getCollateralAssignmentDAO().getCollateralAssignmentByFinRef(finReference,
								FinanceConstants.MODULE_NAME, "_CTView");
					}
				}
				financeDetail.setCollaterals(collateralSetupList);
				financeDetail = setCollateralAssignments(financeDetail, assignmentListMain, assignmentListTemp);
			}

		}
		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * Method for Fetching Finance Reference Details List by using FinReference
	 */
	@Override
	public FinanceDetail getFinanceReferenceDetails(FinanceDetail financeDetail, String nextRoleCode, String screenCode,
			String eventCode, String procEdtEvent, boolean extFieldsReq) {
		logger.debug(Literal.ENTERING);

		List<Long> accSetIdList = new ArrayList<Long>();
		boolean isCustExist = true;
		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String ctgType = "";
		if (financeMain.getCustID() <= 0) {
			isCustExist = false;
		}
		if (financeDetail.getCustomerDetails() != null && financeDetail.getCustomerDetails().getCustomer() != null) {
			ctgType = financeDetail.getCustomerDetails().getCustomer().getCustCtgCode();
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
			event = FinanceConstants.FINSER_EVENT_ORG;
		}

		finRefDetails = financeReferenceDetailDAO.getFinanceProcessEditorDetails(finType, event, "_FINVIEW");
		if (CollectionUtils.isNotEmpty(finRefDetails)) {
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
						setMiscellaneousTabs(financeDetail, reference);
					}
					break;
				case FinanceConstants.PROCEDT_FINANCETABS:
					showTabMap.put(StringUtils.leftPad(String.valueOf(finrefDetail.getFinRefId()), 3, "0"),
							mandInputInStage);
					break;
				default:
					break;
				}
			}
		}

		// Finance Agreement Details
		financeDetail.setAggrementList(aggrementList);
		financeDetail.setShowTabDetailMap(showTabMap);

		if (isCustExist) {
			if (financeMain.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())) {

				// Eligibility Details
				financeDetail.setElgRuleList(
						getEligibilityDetailService().fetchEligibilityDetails(financeMain, eligibilityList));

				// Scoring Details
				if (PennantConstants.PFF_CUSTCTG_INDIV.equals(ctgType)) {
					getScoringDetailService().fetchFinScoringDetails(financeDetail, retScoringGroupList, ctgType);
				} else {
					getScoringDetailService().fetchFinScoringDetails(financeDetail, corpScoringGroupList, ctgType);
				}
			}

			// Checklist Details
			checkListDetailService.fetchFinCheckListDetails(financeDetail, checkListdetails);

			// Finance Stage Accounting Posting Details
			// =======================================
			List<TransactionEntry> stageEntries = new ArrayList<>();
			for (int i = 0; i < accSetIdList.size(); i++) {
				stageEntries.addAll(AccountingConfigCache.getTransactionEntry(accSetIdList.get(i)));
			}
			financeDetail.setStageTransactionEntries(stageEntries);
		}

		// Accounting Set Details
		if (StringUtils.isBlank(eventCode)) {

			if (StringUtils.equalsIgnoreCase(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
				eventCode = AccountEventConstants.ACCEVENT_CMTDISB;
			} else {
				eventCode = PennantApplicationUtil.getEventCode(financeMain.getFinStartDate());
			}
		}

		// Finance Commitment Accounting Posting Details
		// =======================================
		if (PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())) {
			if (financeType.isFinCommitmentReq() && StringUtils.isNotBlank(financeMain.getFinCommitmentRef())) {

				long accountingSetId = getAccountingSetDAO().getAccountingSetId(AccountEventConstants.ACCEVENT_CMTDISB,
						AccountEventConstants.ACCEVENT_CMTDISB);// TODO :

				if (accountingSetId != 0) {
					financeDetail.setCmtFinanceEntries(AccountingConfigCache.getTransactionEntry(accountingSetId));
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return financeDetail;
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
	public boolean isFinReferenceExits(String financeReference, String tableType, boolean isWIF) {
		logger.debug(Literal.ENTERING);
		if (isWIF) {
			tableType = "";
		}
		logger.debug(Literal.LEAVING);
		return getFinanceMainDAO().isFinReferenceExists(financeReference, tableType, isWIF);
	}

	/**
	 * saveOrUpdate method method do the following steps. 1) Do the Business validation by using
	 * businessValidation(auditHeader) method if there is any error or warning message then return the auditHeader. 2)
	 * Do Add or Update the Record a) Add new Record for the new record in the DB table FinanceMain/FinanceMain_Temp by
	 * using FinanceMainDAO's save method b) Update the Record in the table. based on the module workFlow Configuration.
	 * by using FinanceMainDAO's update method 3) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader)
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws DatatypeConfigurationException
	 * @throws AccountNotFoundException
	 */
	@Override
	public AuditHeader saveOrUpdate(AuditHeader aAuditHeader, boolean isWIF) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		FinanceDetail financeDetail = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = scheduleData.getFinanceMain();

		String finReference = financeMain.getFinReference();
		Date curBDay = SysParamUtil.getAppDate();

		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(financeDetail);
		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : serviceInstructions) {
			serviceUID = finServInst.getInstructionUID();
		}

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
		if (financeMain.isWorkflow()) {
			tableType = TableType.TEMP_TAB;
		}

		// Accounting (Stage/Posting) Execution Process
		// =======================================
		if (!isWIF) {

			// Finance Stage Accounting Process
			// =======================================
			auditHeader = executeStageAccounting(auditHeader);
			if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
				return auditHeader;
			}

			// Accounting Execution Process on Maintenance
			// =======================================
			if (tableType == TableType.MAIN_TAB
					&& financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {

				auditHeader = executeAccountingProcess(auditHeader, curBDay);
				if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
					return auditHeader;
				}
			}
		}

		if (isWIF) {

			// Customer Basic Details Maintenance
			// =======================================
			WIFCustomer customer = financeDetail.getCustomer();
			if (customer != null) {
				long custId = customer.getCustID();
				if (customer.isNewRecord()) {
					custId = getCustomerDAO().saveWIFCustomer(customer);
				} else {
					getCustomerDAO().updateWIFCustomer(customer);
					getCustomerIncomeDAO().deleteByCustomer(customer.getCustID(), "", true);
				}

				if (customer.getCustomerIncomeList() != null && !customer.getCustomerIncomeList().isEmpty()) {
					for (CustomerIncome income : customer.getCustomerIncomeList()) {
						income.setCustId(custId);
					}
					getCustomerIncomeDAO().saveBatch(customer.getCustomerIncomeList(), "", true);
				}
				financeMain.setCustID(custId);
			}

			// Indicative Term Sheet Details Maintenance
			// =======================================
			IndicativeTermDetail termDetail = financeDetail.getIndicativeTermDetail();
			if (termDetail != null) {
				termDetail.setFinReference(finReference);
				if (termDetail.isNewRecord()) {
					getIndicativeTermDetailDAO().save(termDetail, tableType.getSuffix(), true);
				} else {
					getIndicativeTermDetailDAO().update(termDetail, tableType.getSuffix(), true);
				}
			}
		} else {
			// set Customer Details Audit
			if (!StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)
					|| auditHeader.getApiHeader() == null) {
				if (financeDetail.getCustomerDetails() != null
						&& StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ORG)) {
					auditDetails.addAll(getCustomerDetailsService().saveOrUpdate(financeDetail, ""));
				}
			}
		}

		// Finance Main Details Save And Update
		// =======================================
		// Update Task_log and Task_Owners tables
		if (!isWIF) {
			updateTaskLog(financeMain, true);
		}

		// Mandate Should be processed first for changes since the mandate id
		// will be updated in the finance main.
		getFinMandateService().saveOrUpdate(financeDetail, auditHeader, tableType.getSuffix());

		if (financeMain.isNew()) {
			// Lock Functionality not required while Creating loan From API
			if (StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)
					&& auditHeader.getApiHeader() != null) {
				financeMain.setNextUserId(null);
			}
			getFinanceMainDAO().save(financeMain, tableType, isWIF);

			// Overdraft Details
			if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
					&& !scheduleData.getOverdraftScheduleDetails().isEmpty()) {

				for (int i = 0; i < scheduleData.getOverdraftScheduleDetails().size(); i++) {
					scheduleData.getOverdraftScheduleDetails().get(i).setFinReference(finReference);
				}
				getOverdraftScheduleDetailDAO().saveList(scheduleData.getOverdraftScheduleDetails(),
						tableType.getSuffix());
			}

			// Save Rolledover Finance Details
			if (financeDetail.getRolledoverFinanceHeader() != null) {
				getRolledoverFinanceDAO().saveHeader(financeDetail.getRolledoverFinanceHeader(), tableType.getSuffix());

				// Rolledover Details
				getRolledoverFinanceDAO().saveDetailList(
						financeDetail.getRolledoverFinanceHeader().getRolledoverFinanceDetails(),
						tableType.getSuffix());
			}

		} else {
			getFinanceMainDAO().update(financeMain, tableType, isWIF);

			// Overdraft Details
			if (financeMain.isLovDescIsSchdGenerated()
					&& StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
					&& !scheduleData.getOverdraftScheduleDetails().isEmpty()) {

				// Existing Data deletion
				getOverdraftScheduleDetailDAO().deleteByFinReference(financeMain.getFinReference(), "_Temp", isWIF);

				// Save New list of records
				for (int i = 0; i < scheduleData.getOverdraftScheduleDetails().size(); i++) {
					scheduleData.getOverdraftScheduleDetails().get(i).setFinReference(finReference);
				}
				getOverdraftScheduleDetailDAO().saveList(scheduleData.getOverdraftScheduleDetails(),
						tableType.getSuffix());
			}

			// Update Rolled over Finance Details
			if (financeDetail.getRolledoverFinanceHeader() != null) {
				getRolledoverFinanceDAO().updateHeader(financeDetail.getRolledoverFinanceHeader(),
						tableType.getSuffix());

				// Rolledover Details
				getRolledoverFinanceDAO().deleteListByRef(financeMain.getFinReference(), tableType.getSuffix());
				getRolledoverFinanceDAO().saveDetailList(
						financeDetail.getRolledoverFinanceHeader().getRolledoverFinanceDetails(),
						tableType.getSuffix());
			}

		}

		// Save or Update FInance Tax Details
		FinanceTaxDetail taxDetail = financeDetail.getFinanceTaxDetail();
		if (taxDetail != null) {
			FinanceTaxDetail tempTaxDetail = getFinanceTaxDetailDAO().getFinanceTaxDetail(taxDetail.getFinReference(),
					"_View");
			if (tempTaxDetail != null) {
				getFinanceTaxDetailDAO().delete(taxDetail, tableType);
			}
			if (PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER.equals(taxDetail.getApplicableFor())
					|| PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT.equals(taxDetail.getApplicableFor())
					|| PennantConstants.TAXAPPLICABLEFOR_GUARANTOR.equals(taxDetail.getApplicableFor())) {

				// if we take new customer case
				if (taxDetail.getTaxCustId() <= 0
						&& PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER.equals(taxDetail.getApplicableFor())) {
					taxDetail.setTaxCustId(financeMain.getCustID());
				}

				taxDetail.setFinReference(finReference);
				taxDetail.setTaskId(financeMain.getTaskId());
				taxDetail.setNextTaskId(financeMain.getNextTaskId());
				taxDetail.setRoleCode(financeMain.getRoleCode());
				taxDetail.setNextRoleCode(financeMain.getNextRoleCode());
				taxDetail.setRecordStatus(financeMain.getRecordStatus());
				taxDetail.setWorkflowId(financeMain.getWorkflowId());
				getFinanceTaxDetailDAO().save(taxDetail, tableType);
			}
		}

		// Save Contributor Header Details
		// =======================================
		String auditTranType = auditHeader.getAuditTranType();
		if (financeDetail.getFinContributorHeader() != null) {

			FinContributorHeader contributorHeader = financeDetail.getFinContributorHeader();
			contributorHeader.setWorkflowId(0);
			String[] fields = PennantJavaUtil.getFieldDetails(new FinContributorHeader());
			if (contributorHeader.isNewRecord()) {
				getFinContributorHeaderDAO().save(contributorHeader, tableType.getSuffix());
			} else {
				getFinContributorHeaderDAO().update(contributorHeader, tableType.getSuffix());
			}

			auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1],
					financeDetail.getFinContributorHeader().getBefImage(), financeDetail.getFinContributorHeader()));

			if (contributorHeader.getContributorDetailList() != null
					&& contributorHeader.getContributorDetailList().size() > 0) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("Contributor");
				details = processingContributorList(details, tableType.getSuffix(),
						contributorHeader.getFinReference());
				auditDetails.addAll(details);
			}
		}

		// Save Cheque Header Details
		// =======================================
		if (financeDetail.getChequeHeader() != null) {
			String[] fields = PennantJavaUtil.getFieldDetails(new ChequeHeader());
			finChequeHeaderService.saveOrUpdate(auditHeader, tableType);
			auditDetails.addAll(auditHeader.getAuditDetails());
			auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1],
					financeDetail.getChequeHeader().getBefImage(), financeDetail.getChequeHeader()));
		}

		// Save schedule details
		// =======================================
		if (!financeDetail.isNewRecord()) {

			if (!isWIF && tableType == TableType.MAIN_TAB
					&& financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_UPD)) {
				// Fetch Existing data before Modification

				FinScheduleData oldFinSchdData = null;
				if (financeMain.isScheduleRegenerated()) {
					oldFinSchdData = getFinSchDataByFinRef(scheduleData.getFinReference(), "", -1);
					oldFinSchdData.setFinReference(scheduleData.getFinReference());
				}

				// Create log entry for Action for Schedule Modification
				FinLogEntryDetail entryDetail = new FinLogEntryDetail();
				entryDetail.setFinReference(scheduleData.getFinReference());
				entryDetail.setEventAction(financeDetail.getAccountingEventCode());
				entryDetail.setSchdlRecal(financeMain.isScheduleRegenerated());
				entryDetail.setPostDate(curBDay);
				entryDetail.setReversalCompleted(false);
				long logKey = getFinLogEntryDetailDAO().save(entryDetail);

				// Save Schedule Details For Future Modifications
				if (financeMain.isScheduleRegenerated()) {
					listSave(oldFinSchdData, "_Log", false, logKey, serviceUID);
				}
			}

			listDeletion(scheduleData, financeDetail.getModuleDefiner(), tableType.getSuffix(), isWIF);
			getFinServiceInstructionDAO().deleteList(scheduleData.getFinReference(), financeDetail.getModuleDefiner(),
					"_Temp");
			listSave(scheduleData, tableType.getSuffix(), isWIF, 0, serviceUID);
			saveFeeChargeList(scheduleData, financeDetail.getModuleDefiner(), isWIF, tableType.getSuffix());
			// Secondary account details
			saveSecondaryAccountList(scheduleData, financeDetail.getModuleDefiner(), isWIF, tableType.getSuffix());

			// Finance IRR Values
			// =======================================
			deleteFinIRR(finReference, tableType);
			saveFinIRR(scheduleData.getiRRDetails(), finReference, tableType);

			// Plan EMI Holiday Details Deletion, if exists on Old image
			// =======================================
			FinanceMain befFinMain = financeMain.getBefImage();
			if (befFinMain != null && befFinMain.isPlanEMIHAlw()) {
				if (StringUtils.equals(befFinMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
					getFinPlanEmiHolidayDAO().deletePlanEMIHMonths(finReference, tableType.getSuffix());
				} else if (StringUtils.equals(befFinMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
					getFinPlanEmiHolidayDAO().deletePlanEMIHDates(finReference, tableType.getSuffix());
				}
			}

		} else {
			listSave(scheduleData, tableType.getSuffix(), isWIF, 0, serviceUID);
			saveFeeChargeList(scheduleData, financeDetail.getModuleDefiner(), isWIF, tableType.getSuffix());

			// Finance IRR Values
			// =======================================
			saveFinIRR(scheduleData.getiRRDetails(), finReference, tableType);

			// Secondary account details
			saveSecondaryAccountList(scheduleData, financeDetail.getModuleDefiner(), isWIF, tableType.getSuffix());

		}

		// Plan EMI Holiday Details
		// =======================================
		if (financeMain.isPlanEMIHAlw()) {
			if (StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				if (scheduleData.getPlanEMIHmonths() != null && !scheduleData.getPlanEMIHmonths().isEmpty()) {

					List<FinPlanEmiHoliday> holidayList = new ArrayList<>();
					for (int i = 0; i < scheduleData.getPlanEMIHmonths().size(); i++) {
						int planEMIHMonth = scheduleData.getPlanEMIHmonths().get(i);
						FinPlanEmiHoliday emiHoliday = new FinPlanEmiHoliday();
						emiHoliday.setFinReference(finReference);
						emiHoliday.setPlanEMIHMonth(planEMIHMonth);
						holidayList.add(emiHoliday);
					}

					getFinPlanEmiHolidayDAO().savePlanEMIHMonths(holidayList, tableType.getSuffix());
				}
			} else if (StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
				if (scheduleData.getPlanEMIHDates() != null && !scheduleData.getPlanEMIHDates().isEmpty()) {

					List<FinPlanEmiHoliday> holidayList = new ArrayList<>();
					for (int i = 0; i < scheduleData.getPlanEMIHDates().size(); i++) {
						Date planEMIHDate = scheduleData.getPlanEMIHDates().get(i);
						FinPlanEmiHoliday emiHoliday = new FinPlanEmiHoliday();
						emiHoliday.setFinReference(finReference);
						emiHoliday.setPlanEMIHDate(planEMIHDate);
						holidayList.add(emiHoliday);
					}

					getFinPlanEmiHolidayDAO().savePlanEMIHDates(holidayList, tableType.getSuffix());
				}
			}
		}

		// Save Finance Step Policy Details
		// =======================================
		if (isWIF || financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			getFinanceStepDetailDAO().deleteList(finReference, isWIF, tableType.getSuffix());
			saveStepDetailList(scheduleData, isWIF, tableType.getSuffix());
		}

		if (!isWIF) {

			// Finance Eligibility Rule Details
			// =======================================
			auditDetails.addAll(getEligibilityDetailService().saveOrUpdate(financeDetail));

			// Finance Scoring Module Details List Saving
			// =======================================
			auditDetails.addAll(getScoringDetailService().saveOrUpdate(financeDetail));
		}

		// Save asset details
		// =======================================
		if (!isWIF) {
			// =======================================
			List<DocumentDetails> documentDetails = financeDetail.getDocumentDetailsList();
			if (CollectionUtils.isNotEmpty(documentDetails)) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
				details = processingDocumentDetailsList(details, tableType.getSuffix(), financeMain,
						financeDetail.getModuleDefiner(), serviceUID);
				auditDetails.addAll(details);
			}

			// set Finance Check List audit details to auditDetails
			// =======================================
			if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {
				auditDetails
						.addAll(checkListDetailService.saveOrUpdate(financeDetail, tableType.getSuffix(), serviceUID));
			}

			// set contract Details Audit
			// =======================================
			if (financeDetail.getContractorAssetDetails() != null
					&& !financeDetail.getContractorAssetDetails().isEmpty()) {
				auditDetails.addAll(getContractorAssetDetailService().saveOrUpdate(finReference,
						financeDetail.getContractorAssetDetails(), tableType.getSuffix(), auditTranType));
			}

			// set Guaranteer Details Audit
			// =======================================
			if (financeDetail.getGurantorsDetailList() != null && !financeDetail.getGurantorsDetailList().isEmpty()) {
				// 10-Jul-2018 BUG FIX related to TktNo:127415
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("Guarantors");
				auditDetails
						.addAll(getGuarantorDetailService().processingGuarantorsList(details, tableType.getSuffix()));
			}

			// set JountAccount Details Audit
			// =======================================
			if (financeDetail.getJountAccountDetailList() != null
					&& !financeDetail.getJountAccountDetailList().isEmpty()) {
				// 10-Jul-2018 BUG FIX related to TktNo:127415
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("JountAccountDetails");
				auditDetails.addAll(getJointAccountDetailService().processingJointAccountDetail(details,
						tableType.getSuffix(), auditTranType));
			}

			// set Finance Collateral Details Audit
			// =======================================
			if (financeDetail.getFinanceCollaterals() != null && !financeDetail.getFinanceCollaterals().isEmpty()) {
				auditDetails.addAll(getFinCollateralService().saveOrUpdate(financeDetail.getFinanceCollaterals(),
						tableType.getSuffix(), auditTranType));
			}

			// Deviation details
			List<FinanceDeviations> deviations = new ArrayList<FinanceDeviations>();
			if (financeDetail.getFinanceDeviations() != null) {
				deviations.addAll(financeDetail.getFinanceDeviations());
			}

			if (financeDetail.getManualDeviations() != null) {
				deviations.addAll(financeDetail.getManualDeviations());
			}

			if (ImplementationConstants.ALLOW_DEVIATIONS) {
				getDeviationDetailsService().processDevaitions(finReference, deviations, auditHeader);
				getDeviationDetailsService().processApprovedDevaitions(finReference,
						financeDetail.getApprovedFinanceDeviations(), auditHeader);
			}

			// Dedup Details
			// =======================================
			auditDetails.addAll(saveDedupDetails(financeDetail));

			// Additional Field Details Save / Update
			// =======================================
			doSaveAddlFieldDetails(financeDetail, tableType.getSuffix());

			// Etihad Credit Bureau Details
			// =======================================
			if (financeDetail.getEtihadCreditBureauDetail() != null) {
				auditDetails.add(getEtihadCreditBureauDetailService().saveOrUpdate(
						financeDetail.getEtihadCreditBureauDetail(), tableType.getSuffix(), auditTranType));
			}

			// Bundled Products Details
			// =======================================
			if (financeDetail.getBundledProductsDetail() != null) {
				auditDetails.add(getBundledProductsDetailService()
						.saveOrUpdate(financeDetail.getBundledProductsDetail(), tableType.getSuffix(), auditTranType));
			}
			// Agreement Field Details
			// =======================================
			if (financeDetail.getAgreementFieldDetails() != null) {
				auditDetails.add(getAgreementFieldsDetailService()
						.saveOrUpdate(financeDetail.getAgreementFieldDetails(), tableType.getSuffix(), auditTranType));
			}

			// Advance Payment Details
			// =======================================
			// Payment Order Issue Details
			// =======================================
			// Quick disbursement
			if (financeMain.isQuickDisb()) {
				if (!StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)
						|| auditHeader.getApiHeader() == null) {
					auditDetails.addAll(getFinAdvancePaymentsService().processQuickDisbursment(financeDetail,
							tableType.getSuffix(), auditTranType));
				} else {
					auditDetails.addAll(getFinAdvancePaymentsService().processAPIQuickDisbursment(financeDetail,
							tableType.getSuffix(), auditTranType));
				}

			} else {
				if (financeDetail.getAdvancePaymentsList() != null
						&& !financeDetail.getAdvancePaymentsList().isEmpty()) {
					auditDetails
							.addAll(getFinAdvancePaymentsService().saveOrUpdate(financeDetail.getAdvancePaymentsList(),
									tableType.getSuffix(), auditTranType, financeDetail.isDisbStp()));
				}
			}

			// Covenant Type Details
			// =======================================
			if (financeDetail.getCovenantTypeList() != null && !financeDetail.getCovenantTypeList().isEmpty()) {
				for (FinCovenantType finCovenantType : financeDetail.getCovenantTypeList()) {
					finCovenantType.setFinReference(finReference);
					finCovenantType.setTaskId(financeMain.getTaskId());
					finCovenantType.setNextTaskId(financeMain.getNextTaskId());
					finCovenantType.setRoleCode(financeMain.getRoleCode());
					finCovenantType.setNextRoleCode(financeMain.getNextRoleCode());
					finCovenantType.setRecordStatus(financeMain.getRecordStatus());
					finCovenantType.setWorkflowId(financeMain.getWorkflowId());
					finCovenantType.setLastMntOn(financeMain.getLastMntOn());
				}

				List<AuditDetail> covenantAuditDetails = null;
				covenantAuditDetails = finCovenantTypeService.saveOrUpdate(financeDetail.getCovenantTypeList(),
						tableType.getSuffix(), auditTranType);

				auditDetails.addAll(covenantAuditDetails);
			}

			List<Covenant> covenants = financeDetail.getCovenants();
			if (CollectionUtils.isNotEmpty(covenants)) {
				int docSize = 0;
				for (Covenant covenant : financeDetail.getCovenants()) {
					covenant.setKeyReference(finReference);
					covenant.setTaskId(financeMain.getTaskId());
					covenant.setNextTaskId(financeMain.getNextTaskId());
					covenant.setRoleCode(financeMain.getRoleCode());
					covenant.setNextRoleCode(financeMain.getNextRoleCode());
					covenant.setRecordStatus(financeMain.getRecordStatus());
					covenant.setWorkflowId(financeMain.getWorkflowId());
					covenant.setLastMntOn(financeMain.getLastMntOn());
				}
				if (CollectionUtils.isNotEmpty(financeDetail.getDocumentDetailsList())) {
					docSize = financeDetail.getDocumentDetailsList().size();
				}
				auditDetails.addAll(covenantsService.doProcess(covenants, tableType, auditTranType, false, docSize));
			}

			// set Finance Collateral Details Audit
			// =======================================
			if (financeDetail.getCollateralAssignmentList() != null
					&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
				details = processingCollateralAssignmentList(details, tableType.getSuffix(), financeMain);
				auditDetails.addAll(details);
			}
			// Save Collateral setup Details
			List<CollateralSetup> collateralSetupList = financeDetail.getCollaterals();
			if (collateralSetupList != null && !collateralSetupList.isEmpty()) {
				List<AuditDetail> details = getCollateralSetupService().processCollateralSetupList(aAuditHeader,
						"saveOrUpdate");
				auditDetails.addAll(details);
			}

			// Save Legal details
			List<LegalDetail> legalDetails = financeDetail.getLegalDetailsList();
			if (CollectionUtils.isNotEmpty(legalDetails)) {
				List<AuditDetail> details = getLegalDetailService().processLegalDetails(aAuditHeader, "saveOrUpdate");
				auditDetails.addAll(details);
			}

			// FinAssetTypes Audit
			if (financeDetail.getFinAssetTypesList() != null && !financeDetail.getFinAssetTypesList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinAssetTypes");
				details = processingFinAssetTypesList(details, tableType.getSuffix(), financeMain);
				auditDetails.addAll(details);
			}

			// Vas Recording Details
			if (scheduleData.getVasRecordingList() != null && !scheduleData.getVasRecordingList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("VasRecordings");
				details = processingVasRecordngList(financeDetail, details, tableType.getSuffix());
				auditDetails.addAll(details);
			}

			// Vas Recording Extended Field Details
			if (scheduleData.getVasRecordingList() != null && !scheduleData.getVasRecordingList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("VasExtendedDetails");

				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
						VASConsatnts.MODULE_NAME, null, tableType.getSuffix(), serviceUID);
				auditDetails.addAll(details);
			}

			// LowerTax Deductions
			// ===============
			if (CollectionUtils.isNotEmpty(financeDetail.getFinScheduleData().getLowerTaxDeductionDetails())
					&& FinanceConstants.FINSER_EVENT_ORG.equals(financeDetail.getModuleDefiner())) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("LowerTaxDeductionDetails");
				details = processLowerTaxDeductionDetails(details, tableType, financeDetail);
				auditDetails.addAll(details);
			}
			// AssetType Extended field Details
			if (financeDetail.getExtendedFieldRenderList() != null
					&& financeDetail.getExtendedFieldRenderList().size() > 0) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");

				for (int i = 0; i < details.size(); i++) {
					ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) details.get(i).getModelData();
					extendedFieldRender.setReference(finReference);
				}
				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
						AssetConstants.EXTENDEDFIELDS_MODULE, null, tableType.getSuffix(), serviceUID);
				auditDetails.addAll(details);
			}

			// Extended field Details
			String event = null;
			if (financeDetail.getExtendedFieldHeader() != null) {
				event = financeDetail.getExtendedFieldHeader().getEvent();
			}
			if (financeDetail.getExtendedFieldRender() != null) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("LoanExtendedFieldDetails");
				details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
						ExtendedFieldConstants.MODULE_LOAN, event, tableType.getSuffix(), serviceUID);
				if (details != null) {
					auditDetails.addAll(details);
				}
			}

			// Flag Details
			// =======================================
			if (financeDetail.getFinFlagsDetails() != null && financeDetail.getFinFlagsDetails().size() > 0) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinFlagsDetail");
				details = processingFinFlagDetailList(details, tableType.getSuffix());
				auditDetails.addAll(details);
			}

			// Asset Evaluation Details
			// =======================================
			if (financeDetail.getFinAssetEvaluation() != null) {
				auditDetails.add(getFinAssetEvaluationService().saveOrUpdate(financeDetail.getFinAssetEvaluation(),
						tableType.getSuffix(), auditTranType));
			}

			// psl Details
			// =======================================
			if (financeDetail.getPslDetail() != null) {
				financeDetail.getPslDetail().setRecordStatus(financeMain.getRecordStatus());
				financeDetail.getPslDetail().setRoleCode(financeMain.getRoleCode());
				financeDetail.getPslDetail().setNextRoleCode(financeMain.getNextRoleCode());
				financeDetail.getPslDetail().setTaskId((financeMain.getTaskId()));
				financeDetail.getPslDetail().setNextTaskId((financeMain.getNextTaskId()));
				financeDetail.getPslDetail().setWorkflowId((financeMain.getWorkflowId()));
				auditDetails.add(
						getpSLDetailService().saveOrUpdate(financeDetail.getPslDetail(), tableType, auditTranType));
			}

			// Verifications
			saveOrUpdateVerifications(auditDetails, financeDetail, financeMain, auditTranType);

			//calling post hoot
			if (postExteranalServiceHook != null) {
				postExteranalServiceHook.doProcess(aAuditHeader, "saveOrUpdate");
			}

			/**
			 * save sampling details
			 */

			if (financeMain.isSamplingRequired() && !financeDetail.isActionSave()
					&& !samplingService.isExist(scheduleData.getFinReference(), "_view")) {
				Sampling sampling = new Sampling();
				sampling.setKeyReference(financeMain.getFinReference());
				sampling.setLastMntBy(financeMain.getLastMntBy());
				sampling.setCreatedBy(financeMain.getLastMntBy());
				samplingService.save(sampling);
			}

			/**
			 * save Legal details
			 */
			if (financeMain.isLegalRequired() && (!financeDetail.isActionSave() || auditHeader.getApiHeader() != null)
					&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
				legalDetailService.saveLegalDetails(financeDetail, auditHeader.getApiHeader());
			}

		}

		// Finance Fee Details
		// =======================================
		List<FinFeeDetail> finFeeDetails = scheduleData.getFinFeeDetailList();
		if (CollectionUtils.isNotEmpty(finFeeDetails)) {
			for (FinFeeDetail fee : finFeeDetails) {
				fee.setInstructionUID(serviceUID);
			}
			auditDetails.addAll(
					finFeeDetailService.saveOrUpdate(finFeeDetails, tableType.getSuffix(), auditTranType, isWIF));
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
		 * auditDetails.addAll(getFinFeeDetailService().saveOrUpdateFinFeeReceipts(
		 * financeDetail.getFinScheduleData().getFinFeeReceipts(), tableType.getSuffix(),
		 * auditHeader.getAuditTranType()));
		 * 
		 * }
		 */

		// set Finance Insurance Details Audit
		// =======================================
		if (scheduleData.getFinInsuranceList() != null && !scheduleData.getFinInsuranceList().isEmpty()) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinInsuranceDetails");
			details = processFinInsuranceDetails(details, tableType.getSuffix(), financeDetail, isWIF);
			auditDetails.addAll(details);
		}

		// Put-Call
		List<FinOption> finOptions = financeDetail.getFinOptions();
		if (CollectionUtils.isNotEmpty(finOptions)) {
			for (FinOption finOption : financeDetail.getFinOptions()) {
				finOption.setFinReference(finReference);
				finOption.setTaskId(financeMain.getTaskId());
				finOption.setNextTaskId(financeMain.getNextTaskId());
				finOption.setRoleCode(financeMain.getRoleCode());
				finOption.setNextRoleCode(financeMain.getNextRoleCode());
				finOption.setRecordStatus(financeMain.getRecordStatus());
				finOption.setWorkflowId(financeMain.getWorkflowId());
				finOption.setLastMntOn(financeMain.getLastMntOn());
			}
			auditDetails.addAll(finOptionService.doProcess(finOptions, tableType, auditTranType, false));
		}

		if (!isWIF) {
			processLimitSaveOrUpdate(aAuditHeader, false);
		}

		// E-mail notification for Create Loan API.
		try {
			if (ImplementationConstants.SEND_NOTIFICATION_ON_CREATE_LOAN_API
					&& PennantConstants.FINSOURCE_ID_API.equalsIgnoreCase(financeMain.getFinSourceID())
					&& financeMain.isNewRecord()) {
				sendMailNotification(financeDetail);
			}
		} catch (Exception e) {
			logger.error(e);
			e.printStackTrace();
		}

		// Saving the reasons
		saveReasonDetails(financeDetail);

		if (financeDetail.getCreditReviewData() != null && creditFinancialService != null) {
			creditFinancialService.saveOrUpdate(financeDetail, auditHeader, tableType.getSuffix());
		}

		if (!isWIF) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
			auditHeader.setAuditDetail(
					new AuditDetail(auditTranType, 1, fields[0], fields[1], financeMain.getBefImage(), financeMain));

			auditHeader.setAuditDetails(auditDetails);
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		// Reset Finance Detail Object for Service Task Verifications
		// =======================================
		auditHeader.getAuditDetail().setModelData(financeDetail);

		// Push Notification API
		if (pushNotificationsService != null && SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_PUSH_NOTIFICATION)) {
			pushNotificationsService.sendPushNotification(auditHeader);
		}

		// FinancialSummary Risks and Mitigants Details
		// =======================================
		if (financeDetail.getRisksAndMitigantsList() != null && risksAndMitigantsService != null) {
			for (RisksAndMitigants risksAndMitigants : financeDetail.getRisksAndMitigantsList()) {
				risksAndMitigants.setFinReference(finReference);
				risksAndMitigants.setTaskId(financeMain.getTaskId());
				risksAndMitigants.setNextTaskId(financeMain.getNextTaskId());
				risksAndMitigants.setRoleCode(financeMain.getRoleCode());
				risksAndMitigants.setNextRoleCode(financeMain.getNextRoleCode());
				risksAndMitigants.setRecordStatus(financeMain.getRecordStatus());
				risksAndMitigants.setWorkflowId(financeMain.getWorkflowId());
				risksAndMitigants.setLastMntOn(financeMain.getLastMntOn());
			}
			auditDetails.addAll(risksAndMitigantsService.doProcess(financeDetail.getRisksAndMitigantsList(), tableType,
					auditTranType, false));
		}
		// FinancialSummary Sanction Condition Details
		// =======================================
		if (financeDetail.getSanctionDetailsList() != null && sanctionConditionsService != null) {
			for (SanctionConditions sanctionConditions : financeDetail.getSanctionDetailsList()) {
				sanctionConditions.setFinReference(finReference);
				sanctionConditions.setTaskId(financeMain.getTaskId());
				sanctionConditions.setNextTaskId(financeMain.getNextTaskId());
				sanctionConditions.setRoleCode(financeMain.getRoleCode());
				sanctionConditions.setNextRoleCode(financeMain.getNextRoleCode());
				sanctionConditions.setRecordStatus(financeMain.getRecordStatus());
				sanctionConditions.setWorkflowId(financeMain.getWorkflowId());
				sanctionConditions.setLastMntOn(financeMain.getLastMntOn());
			}
			auditDetails.addAll(sanctionConditionsService.doProcess(financeDetail.getSanctionDetailsList(), tableType,
					auditTranType, false));
		}
		// FinancialSummary Deal Recommendation Merits Details
		// =======================================
		if (financeDetail.getDealRecommendationMeritsDetailsList() != null && dealRecommendationMeritsService != null) {
			for (DealRecommendationMerits dealRecommendationMerits : financeDetail
					.getDealRecommendationMeritsDetailsList()) {
				dealRecommendationMerits.setFinReference(finReference);
				dealRecommendationMerits.setTaskId(financeMain.getTaskId());
				dealRecommendationMerits.setNextTaskId(financeMain.getNextTaskId());
				dealRecommendationMerits.setRoleCode(financeMain.getRoleCode());
				dealRecommendationMerits.setNextRoleCode(financeMain.getNextRoleCode());
				dealRecommendationMerits.setRecordStatus(financeMain.getRecordStatus());
				dealRecommendationMerits.setWorkflowId(financeMain.getWorkflowId());
				dealRecommendationMerits.setLastMntOn(financeMain.getLastMntOn());
			}
			auditDetails.addAll(dealRecommendationMeritsService.doProcess(
					financeDetail.getDealRecommendationMeritsDetailsList(), tableType, auditTranType, false));
		}
		// FinancialSummary DueDiligence Details
		// =======================================
		if (financeDetail.getDueDiligenceDetailsList() != null && dueDiligenceDetailsService != null) {
			for (DueDiligenceDetails dueDiligenceDetails : financeDetail.getDueDiligenceDetailsList()) {
				dueDiligenceDetails.setFinReference(finReference);
				dueDiligenceDetails.setTaskId(financeMain.getTaskId());
				dueDiligenceDetails.setNextTaskId(financeMain.getNextTaskId());
				dueDiligenceDetails.setRoleCode(financeMain.getRoleCode());
				dueDiligenceDetails.setNextRoleCode(financeMain.getNextRoleCode());
				dueDiligenceDetails.setRecordStatus(financeMain.getRecordStatus());
				dueDiligenceDetails.setWorkflowId(financeMain.getWorkflowId());
				dueDiligenceDetails.setLastMntOn(financeMain.getLastMntOn());
			}
			auditDetails.addAll(dueDiligenceDetailsService.doProcess(financeDetail.getDueDiligenceDetailsList(),
					tableType, auditTranType, false));
		}
		// FinancialSummary DueDiligence Details
		// =======================================
		if (financeDetail.getRecommendationNoteList() != null && recommendationNotesDetailsService != null) {
			for (RecommendationNotes recommendationNotesDetails : financeDetail.getRecommendationNoteList()) {
				recommendationNotesDetails.setFinReference(finReference);
				recommendationNotesDetails.setTaskId(financeMain.getTaskId());
				recommendationNotesDetails.setNextTaskId(financeMain.getNextTaskId());
				recommendationNotesDetails.setRoleCode(financeMain.getRoleCode());
				recommendationNotesDetails.setNextRoleCode(financeMain.getNextRoleCode());
				recommendationNotesDetails.setRecordStatus(financeMain.getRecordStatus());
				recommendationNotesDetails.setWorkflowId(financeMain.getWorkflowId());
				recommendationNotesDetails.setLastMntOn(financeMain.getLastMntOn());
			}
			auditDetails.addAll(recommendationNotesDetailsService.doProcess(financeDetail.getRecommendationNoteList(),
					tableType, auditTranType, false));
		}

		// Synoposis Details
		// =======================================
		if (financeDetail.getSynopsisDetails() != null && synopsisDetailsService != null) {
			financeDetail.getSynopsisDetails().setRecordStatus(financeMain.getRecordStatus());
			financeDetail.getSynopsisDetails().setRoleCode(financeMain.getRoleCode());
			financeDetail.getSynopsisDetails().setNextRoleCode(financeMain.getNextRoleCode());
			financeDetail.getSynopsisDetails().setTaskId((financeMain.getTaskId()));
			financeDetail.getSynopsisDetails().setNextTaskId((financeMain.getNextTaskId()));
			financeDetail.getSynopsisDetails().setWorkflowId((financeMain.getWorkflowId()));
			financeDetail.getSynopsisDetails().setFinReference((financeMain.getFinReference()));
			auditDetails.add(getSynopsisDetailsService().saveOrUpdate(financeDetail.getSynopsisDetails(), tableType,
					auditTranType));
		}
		//Fin OCR Details
		//=========================
		if (financeDetail.getFinOCRHeader() != null && finOCRHeaderService != null) {
			auditDetails.addAll(finOCRHeaderService.processFinOCRHeader(aAuditHeader, "saveOrUpdate"));
		}
		//PMAY
		//=========================
		if (financeDetail.getPmay() != null && pmayService != null) {
			auditDetails.add(pmayService.saveOrUpdate(financeDetail.getPmay(), tableType, auditTranType));
		}
		auditHeader.setAuditDetails(auditDetails);

		List<String> splittedLoans = getFinanceMainDAO().getParentRefifAny(financeMain.getFinReference(), "_view",
				false);
		if (CollectionUtils.isEmpty(splittedLoans)) {
			processPricingLoans(financeDetail);
		}
		logger.debug(Literal.LEAVING);
		return auditHeader;

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
				} else if (lowerTaxDeduction.isNew()) {
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

		try {
			Notification notification = new Notification();
			notification.setKeyReference(financeMain.getFinReference());
			notification.setModule("LOAN");
			notification.setSubModule(FinanceConstants.FINSER_EVENT_ORG);
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
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void saveOrUpdateVerifications(List<AuditDetail> auditDetails, FinanceDetail financeDetail,
			FinanceMain financeMain, String auditTranType) {
		// FI Verification details
		if ((!SysParamUtil.isAllowed(SMTParameterConstants.VERIFICATION_INTIATION_FROM_OUTSIDE)
				&& financeDetail.isFiInitTab()) || financeDetail.isFiApprovalTab()) {
			setVerificationWorkflowDetails(financeDetail.getFiVerification(), financeMain);
		}

		// Technical Verification details
		if ((!SysParamUtil.isAllowed(SMTParameterConstants.VERIFICATION_INTIATION_FROM_OUTSIDE)
				&& financeDetail.isTvInitTab()) || financeDetail.isTvApprovalTab()) {
			setVerificationWorkflowDetails(financeDetail.getTvVerification(), financeMain);
		}

		// Legal Verification details
		if ((!SysParamUtil.isAllowed(SMTParameterConstants.VERIFICATION_INTIATION_FROM_OUTSIDE)
				&& financeDetail.isLvInitTab()) || financeDetail.isLvApprovalTab()) {
			setVerificationWorkflowDetails(financeDetail.getLvVerification(), financeMain);
		}

		// Legal Vetting details
		if ((!SysParamUtil.isAllowed(SMTParameterConstants.VERIFICATION_INTIATION_FROM_OUTSIDE)
				&& financeDetail.isVettingInitTab()) || financeDetail.isVettingApprovalTab()) {
			setVerificationWorkflowDetails(financeDetail.getLegalVetting(), financeMain);
		}

		// RCU Verification details
		if ((!SysParamUtil.isAllowed(SMTParameterConstants.VERIFICATION_INTIATION_FROM_OUTSIDE)
				&& financeDetail.isRcuInitTab()) || financeDetail.isRcuApprovalTab()) {
			setVerificationWorkflowDetails(financeDetail.getRcuVerification(), financeMain);
		}
		// PD Verification details
		if (financeDetail.isPdInitTab() || financeDetail.isPdApprovalTab()) {
			setVerificationWorkflowDetails(financeDetail.getPdVerification(), financeMain);
		}

		List<AuditDetail> adtVerifications = new ArrayList<>();

		// save FI Initiation details
		// =======================================
		if (!SysParamUtil.isAllowed(SMTParameterConstants.VERIFICATION_INTIATION_FROM_OUTSIDE)) {
			if (financeDetail.isFiInitTab() && financeDetail.getFiVerification() != null) {
				adtVerifications.addAll(
						verificationService.saveOrUpdate(financeDetail, VerificationType.FI, auditTranType, true));
			}
		}

		// save FI Approval details
		// =======================================
		if (financeDetail.isFiApprovalTab() && financeDetail.getFiVerification() != null) {
			adtVerifications
					.addAll(verificationService.saveOrUpdate(financeDetail, VerificationType.FI, auditTranType, false));
		}

		// save TV Initiation details
		// TO-DO
		// FIXME - To be uncommented while merging
		// =======================================
		if (!SysParamUtil.isAllowed(SMTParameterConstants.VERIFICATION_INTIATION_FROM_OUTSIDE)) {
			if (financeDetail.isTvInitTab() && financeDetail.getTvVerification() != null) {
				adtVerifications.addAll(
						verificationService.saveOrUpdate(financeDetail, VerificationType.TV, auditTranType, true));
			}
		}

		// save TV Approval details
		// =======================================
		if (financeDetail.isTvApprovalTab() && financeDetail.getTvVerification() != null) {
			adtVerifications
					.addAll(verificationService.saveOrUpdate(financeDetail, VerificationType.TV, auditTranType, false));
		}

		// save LV Initiation details
		// =======================================
		if (!SysParamUtil.isAllowed(SMTParameterConstants.VERIFICATION_INTIATION_FROM_OUTSIDE)) {
			if (financeDetail.isLvInitTab() && financeDetail.getLvVerification() != null) {
				Verification verification = financeDetail.getLvVerification();
				verification.setVerificationType(VerificationType.LV.getKey());
				adtVerifications.addAll(
						verificationService.saveOrUpdate(financeDetail, VerificationType.LV, auditTranType, true));
			}
		}

		// save LV Approval details
		// =======================================
		if (financeDetail.isLvApprovalTab() && financeDetail.getLvVerification() != null) {
			Verification verification = financeDetail.getLvVerification();
			verification.setVerificationType(VerificationType.LV.getKey());
			adtVerifications
					.addAll(verificationService.saveOrUpdate(financeDetail, VerificationType.LV, auditTranType, false));
		}

		// save Legal Vetting Initiation details
		// =======================================
		if (!SysParamUtil.isAllowed(SMTParameterConstants.VERIFICATION_INTIATION_FROM_OUTSIDE)) {
			if (financeDetail.isVettingInitTab() && financeDetail.getLegalVetting() != null) {
				Verification verification = financeDetail.getLegalVetting();
				verification.setVerificationType(VerificationType.VETTING.getKey());
				adtVerifications.addAll(
						verificationService.saveOrUpdate(financeDetail, VerificationType.VETTING, auditTranType, true));
			}
		}

		// save Legal Vetting Approval details
		// =======================================
		if (financeDetail.isVettingApprovalTab() && financeDetail.getLegalVetting() != null) {
			Verification verification = financeDetail.getLegalVetting();
			verification.setVerificationType(VerificationType.VETTING.getKey());
			adtVerifications.addAll(
					verificationService.saveOrUpdate(financeDetail, VerificationType.VETTING, auditTranType, false));
		}

		// save RCU Initiation details
		// =======================================
		if (!SysParamUtil.isAllowed(SMTParameterConstants.VERIFICATION_INTIATION_FROM_OUTSIDE)) {
			if (financeDetail.isRcuInitTab() && financeDetail.getRcuVerification() != null) {
				adtVerifications.addAll(
						verificationService.saveOrUpdate(financeDetail, VerificationType.RCU, auditTranType, true));
			}
		}

		// save RCU Approval details
		// =======================================
		if (financeDetail.isRcuApprovalTab() && financeDetail.getRcuVerification() != null) {
			adtVerifications.addAll(
					verificationService.saveOrUpdate(financeDetail, VerificationType.RCU, auditTranType, false));
		}

		// Update Sampling details
		// =======================================
		if (financeDetail.isSamplingApprover() && financeDetail.getSampling() != null) {
			adtVerifications.add(finSamplingService.saveOrUpdate(financeDetail, auditTranType));
		}

		// save PD Initiation details
		// =======================================
		if (financeDetail.isPdInitTab() && financeDetail.getPdVerification() != null) {
			adtVerifications
					.addAll(verificationService.saveOrUpdate(financeDetail, VerificationType.PD, auditTranType, true));
		}
		// save pd Approval details
		// =======================================
		if (financeDetail.isPdApprovalTab() && financeDetail.getPdVerification() != null) {
			adtVerifications
					.addAll(verificationService.saveOrUpdate(financeDetail, VerificationType.PD, auditTranType, false));
		}

		// preparing audit seqno for same table(adtverifications)
		int i = 0;
		for (AuditDetail auditDetail : adtVerifications) {
			auditDetail.setAuditSeq(++i);
		}

		// Update verification stage tables document id's after saving to
		// database and reference of RCU documntes
		verificationService.updateReferenceIds(financeDetail);

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

	/**
	 * Method For Preparing List of AuditDetails for Check List for Fin Flag Details
	 * 
	 * @param auditDetails
	 * @param financeDetail
	 * @param type
	 * @return
	 */
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
				} else if (finFlagsDetail.isNew()) {
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
				getFinFlagDetailsDAO().save(finFlagsDetail, type);
			}

			if (updateRecord) {
				getFinFlagDetailsDAO().update(finFlagsDetail, type);
			}

			if (deleteRecord) {
				getFinFlagDetailsDAO().delete(finFlagsDetail.getReference(), finFlagsDetail.getFlagCode(),
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
				} else if (recording.isNew()) {
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
						|| FinanceConstants.FINSER_EVENT_ORG.equals(financeDetail.getModuleDefiner())) {
					recording.setStatus(InsuranceConstants.PENDING);
				}
			}
			if (saveRecord) {
				getVasRecordingDAO().save(recording, type);
			}

			if (updateRecord) {
				getVasRecordingDAO().update(recording, type);
			}

			if (deleteRecord) {
				getVasRecordingDAO().delete(recording, type);
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
				getBlacklistCustomerDAO().saveList(insertList, "");
			}
			if (!updateList.isEmpty()) {
				getBlacklistCustomerDAO().updateList(updateList);
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
				getFinanceDedupeDAO().saveList(insertList, "");
			}
			if (!updateList.isEmpty()) {
				getFinanceDedupeDAO().updateList(updateList);
			}

			insertList = null;
			updateList = null;
			dedupFields = null;
			dedup = null;
		}

		// Save Dedup PoliceCase Data
		// =======================================
		if (financeDetail.getDedupPoliceCaseDetails() != null && !financeDetail.getDedupPoliceCaseDetails().isEmpty()) {
			List<PoliceCase> insertList = new ArrayList<PoliceCase>();
			List<PoliceCase> updateList = new ArrayList<PoliceCase>();

			PoliceCase policeCase = new PoliceCase();
			String[] pcFields = PennantJavaUtil.getFieldDetails(policeCase, policeCase.getExcludeFields());
			policeCase = null;

			for (int i = 0; i < financeDetail.getDedupPoliceCaseDetails().size(); i++) {

				policeCase = financeDetail.getDedupPoliceCaseDetails().get(i);
				policeCase.setLastMntBy(lastmntby);
				policeCase.setRoleCode(roleCode);
				policeCase.setRecordStatus(recordSts);

				if (!policeCase.isNewPolicecaseRecord()) {
					updateList.add(policeCase);
				} else {
					insertList.add(policeCase);
				}

				auditDetails.add(
						new AuditDetail(PennantConstants.TRAN_WF, i + 1, pcFields[0], pcFields[1], null, policeCase));
			}

			if (!insertList.isEmpty()) {
				getPoliceCaseDAO().saveList(insertList, "");
			}
			if (!updateList.isEmpty()) {
				getPoliceCaseDAO().updatePoliceCaseList(updateList);
			}

			pcFields = null;
			policeCase = null;
			insertList = null;
			updateList = null;
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
				getCustomerDedupDAO().saveList(insertList, "");
			}
			if (!updateList.isEmpty()) {
				getCustomerDedupDAO().updateList(updateList);
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
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	@Override
	public AuditHeader delete(AuditHeader auditHeader, boolean isWIF) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		auditHeader = businessValidation(auditHeader, "delete", isWIF, false);
		if (!auditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String productCode = StringUtils
				.trimToEmpty(financeDetail.getFinScheduleData().getFinanceType().getFinCategory());

		// Finance SubChild List And Reference Details List Deletion
		listDeletion(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), "", isWIF);
		getFinServiceInstructionDAO().deleteList(financeDetail.getFinScheduleData().getFinReference(),
				financeDetail.getModuleDefiner(), "");

		if (!isWIF) {
			// Additional Field Details Deletion
			doDeleteAddlFieldDetails(financeDetail, "");
		}

		// Indicative Term Sheet Details deletion on WIF FinanceMain Deletion
		if (isWIF && financeDetail.getIndicativeTermDetail() != null) {
			getIndicativeTermDetailDAO().delete(financeDetail.getIndicativeTermDetail(), "", true);
		}

		if (!isWIF) {
			auditDetails.addAll(jointGuarantorDeletion(financeDetail, "", auditHeader.getAuditTranType()));
			auditDetails.addAll(checkListDetailService.delete(financeDetail, "", auditHeader.getAuditTranType()));
			if (financeDetail.getEtihadCreditBureauDetail() != null) {
				auditDetails.add(getEtihadCreditBureauDetailService()
						.delete(financeDetail.getEtihadCreditBureauDetail(), "", auditHeader.getAuditTranType()));
			}
			if (financeDetail.getBundledProductsDetail() != null) {
				auditDetails.add(getBundledProductsDetailService().delete(financeDetail.getBundledProductsDetail(), "",
						auditHeader.getAuditTranType()));
			}
			if (financeDetail.getAgreementFieldDetails() != null) {
				auditDetails.add(getAgreementFieldsDetailService().delete(financeDetail.getAgreementFieldDetails(), "",
						auditHeader.getAuditTranType()));
			}
			if (financeDetail.getAdvancePaymentsList() != null) {
				auditDetails.addAll(getFinAdvancePaymentsService().delete(financeDetail.getAdvancePaymentsList(), "",
						auditHeader.getAuditTranType()));
			}

			String auditTranType = auditHeader.getAuditTranType();
			List<FinCovenantType> finCovenantTypes = financeDetail.getCovenantTypeList();
			if (CollectionUtils.isNotEmpty(finCovenantTypes)) {
				auditDetails.addAll(finCovenantTypeService.delete(finCovenantTypes, "", auditTranType));
			}

			List<Covenant> covenants = financeDetail.getCovenants();
			if (CollectionUtils.isNotEmpty(covenants)) {
				auditDetails.addAll(covenantsService.delete(covenants, TableType.MAIN_TAB, auditTranType));
			}

			// Collateral assignment Details
			if (financeDetail.getCollateralAssignmentList() != null
					&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
				auditDetails.addAll(details);
				getCollateralAssignmentDAO().deleteByReference(financeMain.getFinReference(), "");
			}

			// Save Collateral setup Details
			List<CollateralSetup> collateralSetupList = financeDetail.getCollaterals();
			if (collateralSetupList != null && !collateralSetupList.isEmpty()) {
				List<AuditDetail> details = getCollateralSetupService().processCollateralSetupList(auditHeader,
						"delete");
				auditDetails.addAll(details);
			}
			//Fin OCR Details
			//=========================
			if (financeDetail.getFinOCRHeader() != null && finOCRHeaderService != null) {
				auditDetails.addAll(finOCRHeaderService.processFinOCRHeader(auditHeader, "delete"));
			}

			// Legal details
			List<LegalDetail> legalDetails = financeDetail.getLegalDetailsList();
			if (CollectionUtils.isNotEmpty(legalDetails)) {
				List<AuditDetail> details = getLegalDetailService().processLegalDetails(auditHeader, "delete");
				auditDetails.addAll(details);
			}

			// FinAssetTypes details
			if (financeDetail.getFinAssetTypesList() != null && !financeDetail.getFinAssetTypesList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinAssetTypes");
				getFinAssetTypeDAO().deleteByReference(financeMain.getFinReference(), "");
				auditDetails.addAll(details);
			}

			getLegalDetailService().deleteList(financeMain.getFinReference(), TableType.TEMP_TAB);

			// Cheque details
			if (financeDetail.getChequeHeader() != null) {
				String[] fields = PennantJavaUtil.getFieldDetails(new ChequeHeader());
				finChequeHeaderService.delete(auditHeader);
				auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
						financeDetail.getChequeHeader().getBefImage(), financeDetail.getChequeHeader()));
			}

			// AssetType Extended field Details
			if (financeDetail.getExtendedFieldRenderList() != null
					&& financeDetail.getExtendedFieldRenderList().size() > 0) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");
				auditDetails.addAll(extendedFieldDetailsService.delete(details, AssetConstants.EXTENDEDFIELDS_MODULE,
						financeMain.getFinReference(), null, "_Temp"));
			}

			// Vas Recording Details details Prasad
			if (financeDetail.getFinScheduleData().getVasRecordingList() != null
					&& !financeDetail.getFinScheduleData().getVasRecordingList().isEmpty()) {

				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("VasRecordings");
				getVasRecordingDAO().deleteByPrimaryLinkRef(financeMain.getFinReference(), "_Temp");
				auditDetails.addAll(details);

				// Vas Recording Extended field Details
				List<AuditDetail> vasExtDetails = financeDetail.getAuditDetailMap().get("VasExtendedDetails");
				if (CollectionUtils.isNotEmpty(vasExtDetails)) {
					for (AuditDetail auditDetail : vasExtDetails) {
						ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) auditDetail.getModelData();
						extendedFieldDetailsService.delete(vasExtDetails, VASConsatnts.MODULE_NAME,
								extendedFieldRender.getReference(), null, "_Temp");
					}
				}
			}

			// Loan Extended field Details
			if (financeDetail.getExtendedFieldRender() != null) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("LoanExtendedFieldDetails");
				auditDetails.addAll(extendedFieldDetailsService.delete(details, ExtendedFieldConstants.MODULE_LOAN,
						financeMain.getFinReference(), financeDetail.getExtendedFieldHeader().getEvent(), "_Temp"));
			}

			// Deleting Finance Insurance Details
			if (financeDetail.getFinScheduleData().getFinInsuranceList() != null
					&& !financeDetail.getFinScheduleData().getFinInsuranceList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinInsuranceDetails");
				getFinInsurancesDAO().deleteFinInsurancesList(financeMain.getFinReference(), isWIF, "");
				// delete from finschedule temp
				for (int i = 0; i < financeDetail.getFinScheduleData().getFinInsuranceList().size(); i++) {
					FinInsurances insurance = financeDetail.getFinScheduleData().getFinInsuranceList().get(i);
					getFinInsurancesDAO().deleteFreqBatch(insurance.getInsId(), isWIF, "");
				}
				auditDetails.addAll(details);
			}

			if (financeDetail.getFinAssetEvaluation() != null) {
				auditDetails.add(getFinAssetEvaluationService().delete(financeDetail.getFinAssetEvaluation(), "",
						auditHeader.getAuditTranType()));
			}
		}

		List<FinFeeDetail> fees = financeDetail.getFinScheduleData().getFinFeeDetailList();
		if (fees != null) {
			auditDetails.addAll(getFinFeeDetailService().delete(fees, "", auditHeader.getAuditTranType(), isWIF));
		}

		// Delete Finance Premium Details
		if (productCode.equals(FinanceConstants.PRODUCT_SUKUK)) {
			// FIXME : DataSet Removal to be worked on if it requires in future
		}

		creditReviewDetailDAO.delete(financeMain.getFinReference(), TableType.MAIN_TAB);

		// Finance Deletion
		getFinanceMainDAO().delete(financeMain, TableType.MAIN_TAB, isWIF, true);

		// Step Details Deletion
		getFinanceStepDetailDAO().deleteList(financeMain.getFinReference(), isWIF, "");

		// Saving the reasons
		saveReasonDetails(financeDetail);

		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				financeMain.getBefImage(), financeMain));
		auditHeader.setAuditDetails(auditDetails);
		if (!isWIF) {
			getAuditHeaderDAO().addAudit(auditHeader);
		}
		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"" b) NEW Add new record in to main table by using getFinanceMainDAO().save with
	 * parameters financeMain,"" c) EDIT Update record in the main table by using getFinanceMainDAO().update with
	 * parameters financeMain,"" 3) Delete the record from the workFlow table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"_Temp" 4) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtFinanceMain by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws JaxenException
	 * @throws DatatypeConfigurationException
	 * @throws AccountNotFoundException
	 */
	@Override
	public AuditHeader doApprove(AuditHeader aAuditHeader, boolean isWIF) throws InterfaceException, JaxenException {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		String roleCode = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove", isWIF, false);

		if (!isWIF) {
			aAuditHeader = processLimitApprove(aAuditHeader, true);
		}
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

		// process to send FIN-one request and create or update the cust data.
		String moduleDefiner = financeDetail.getModuleDefiner();
		if (!isWIF && StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_ORG)) {
			createOrUpdateCrmCustomer(financeDetail);
		}

		if (!aAuditHeader.isNextProcess()) {
			logger.debug(Literal.LEAVING);
			return aAuditHeader;
		}

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();

		Date curBDay = SysParamUtil.getAppDate();
		// Review dates reset for backdated loans.
		if (!isWIF && StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_ORG)) {
			resetNextFrqDates(financeDetail, curBDay);
		}

		// gCDCustomerService.processGcdCustomer(financeDetail, "insert"); //
		// inserting gcdcustomer.
		// Execute Accounting Details Process
		// =======================================
		String productCode = finScheduleData.getFinanceType().getFinCategory();
		boolean isSanctionBasedSchd = finScheduleData.getFinanceMain().isSanBsdSchdle();

		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(financeDetail);
		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : serviceInstructions) {
			serviceUID = finServInst.getInstructionUID();
		}

		if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			financeMain.setFinApprovedDate(curBDay);

			if (financeMain.getFinContractDate() == null) {
				financeMain.setFinContractDate(financeMain.getFinStartDate());
			}

			if (financeMain.getFeeChargeAmt() == null) {
				financeMain.setFeeChargeAmt(BigDecimal.ZERO);
			}

			if (financeMain.getInsuranceAmt() == null) {
				financeMain.setInsuranceAmt(BigDecimal.ZERO);
			}
		}
		// To maintain record Maintained status for furthur process
		String recordMainStatus = StringUtils.trimToEmpty(financeMain.getRcdMaintainSts());

		if (!isWIF) {
			String recordType = financeMain.getRecordType();
			if (CollectionUtils.isNotEmpty(financeDetail.getAdvancePaymentsList())
					&& !StringUtils.trimToEmpty(recordMainStatus).equals(FinanceConstants.FINSER_EVENT_CANCELDISB)
					&& !PennantConstants.RECORD_TYPE_DEL.equals(recordType) && !financeDetail.isExtSource()
					&& ((FinanceConstants.FINSER_EVENT_ORG.equals(financeDetail.getModuleDefiner())
							&& PennantConstants.RECORD_TYPE_NEW.equals(recordType))
							|| (FinanceConstants.FINSER_EVENT_ADDDISB.equals(financeDetail.getModuleDefiner())
									&& PennantConstants.RECORD_TYPE_UPD.equals(recordType)))) {
				financeDetail.setAdvancePaymentsList(
						getFinAdvancePaymentsService().splitRequest(financeDetail.getAdvancePaymentsList()));
				auditHeader.getAuditDetail().setModelData(financeDetail);
			}

			auditHeader = executeAccountingProcess(auditHeader, curBDay);
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
				if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW) || StringUtils
						.trimToEmpty(financeMain.getRcdMaintainSts()).equals(FinanceConstants.FINSER_EVENT_ROLLOVER)) {
					getLimitCheckDetails().doProcessLimits(financeMain, FinanceConstants.CONFIRM);
				}

			}
		}

		// Rolled Over Details Accounting Process
		// =======================================
		if (financeDetail.getRolledoverFinanceHeader() != null) {

			// Actual Customer payment Amounts Process
			List<RolledoverFinanceDetail> rolledoverFinanceDetails = financeDetail.getRolledoverFinanceHeader()
					.getRolledoverFinanceDetails();
			for (RolledoverFinanceDetail detail : rolledoverFinanceDetails) {

				if (detail.getCustPayment().compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}
				try {
					doRolledOverFinancePostings(detail.getFinReference(), detail.getCustPayment(),
							financeDetail.getRolledoverFinanceHeader().getPaymentAccount());
				} catch (IllegalAccessException | InvocationTargetException e) {
					logger.debug("Exception: ", e);
					ArrayList<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
					errorDetails.add(new ErrorDetail("RolledOver", PennantConstants.ERR_UNDEF, "E", e.getMessage(),
							new String[] {}, new String[] {}));
					auditHeader.setErrorList(errorDetails);
				} catch (InterfaceException e) {
					logger.debug("Exception: ", e);
					ArrayList<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
					errorDetails.add(new ErrorDetail(e.getErrorCode(), PennantConstants.ERR_UNDEF, "E",
							e.getErrorMessage(), new String[] {}, new String[] {}));
					auditHeader.setErrorList(errorDetails);
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
		if (StringUtils.trimToEmpty(financeMain.getRcdMaintainSts()).equals(FinanceConstants.FINSER_EVENT_CHGFRQ)) {

			List<FinanceScheduleDetail> schdList = finScheduleData.getFinanceScheduleDetails();
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
				finRepayQueue.setFinReference(financeMain.getFinReference());
				finRepayQueue.setBranch(financeMain.getFinBranch());
				finRepayQueue.setFinType(financeMain.getFinType());
				finRepayQueue.setCustomerID(financeMain.getCustID());
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
					getRecoveryPostingsUtil().overDueDetailPreparation(finRepayQueue, financeMain.getProfitDaysBasis(),
							curBDay, false, false);
				} catch (IllegalAccessException e) {
					logger.error("Exception: ", e);
				} catch (InvocationTargetException e) {
					logger.error("Exception: ", e);
				}
			}

			// Recalculate Status of Finance using Overdue
			String curFinStatus = getCustomerStatusCodeDAO().getFinanceStatus(financeMain.getFinReference(), true);
			financeMain.setFinStatus(curFinStatus);

			// Suspense Process Check after Overdue Details Recalculation
			suspenseCheckProcess(financeMain, FinanceConstants.FINSER_EVENT_CHGFRQ, curBDay, financeMain.getFinStatus(),
					0);

		}

		// Bundled Product TODO: Need to check why it was calling
		BundledProductsDetail bundledProductTemp = null;
		if (ImplementationConstants.ALLOW_BUNDLEDPRODUCT) {
			bundledProductTemp = getBundledProductsDetailService()
					.getBundledProductsDetailById(financeMain.getFinReference(), "_Temp");
		}

		// Fetch Next Payment Details from Finance for Salaried Postings
		// Verification
		FinanceScheduleDetail orgNextSchd = null;
		if (ImplementationConstants.ALLOW_FIN_SALARY_PAYMENT
				&& StringUtils.isNotEmpty(financeMain.getRcdMaintainSts())) {
			orgNextSchd = getFinanceScheduleDetailDAO().getNextSchPayment(financeMain.getFinReference(), curBDay);
		}
		String recordType = financeMain.getRecordType();

		if (!isWIF) {
			processLimitApprove(aAuditHeader, false);
		}

		tempWorkflowId = financeMain.getWorkflowId();

		List<FinFeeDetail> finFeeDetails = finScheduleData.getFinFeeDetailList();
		String auditTranType = auditHeader.getAuditTranType();

		if (recordType.equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			creditReviewDetailDAO.delete(financeMain.getFinReference(), TableType.MAIN_TAB);
			getFinanceMainDAO().delete(financeMain, TableType.MAIN_TAB, isWIF, true);
			listDeletion(finScheduleData, moduleDefiner, "", isWIF);
			getFinServiceInstructionDAO().deleteList(finScheduleData.getFinReference(), moduleDefiner, "");

			// Delete Finance Premium Details
			if (StringUtils.equals(FinanceConstants.PRODUCT_SUKUK, productCode)) {
				// FIXME : DataSet Removal to be worked on if it requires in
				// future
			}

			// Delete overdraft Details
			if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
					&& finScheduleData.getOverdraftScheduleDetails().size() > 0) {
				getOverdraftScheduleDetailDAO().deleteByFinReference(financeMain.getFinReference(), "", isWIF);
			}

			// Finance Flag Details
			if (financeDetail.getFinFlagsDetails() != null && !financeDetail.getFinFlagsDetails().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinFlagsDetail");
				getFinFlagDetailsDAO().deleteList(financeMain.getFinReference(), FinanceConstants.MODULE_NAME, "");
				auditDetails.addAll(details);
			}

			// Step Details Deletion
			// =======================================
			getFinanceStepDetailDAO().deleteList(financeMain.getFinReference(), isWIF, "");

			if (!isWIF && (!StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)
					|| (auditHeader.getApiHeader() == null) || StringUtils.isNotBlank(financeMain.getServiceName()))) {
				// Additional Field Details Deletion
				// =======================================
				doDeleteAddlFieldDetails(financeDetail, "");
				auditDetails.addAll(jointGuarantorDeletion(financeDetail, "", tranType));
				auditDetails.addAll(checkListDetailService.delete(financeDetail, "", auditTranType));
				auditDetails.addAll(getListAuditDetails(listDeletion_FinContributor(financeDetail, "", auditTranType)));
			}

			// Delete Rolledover Finance Details
			if (financeDetail.getRolledoverFinanceHeader() != null) {
				getRolledoverFinanceDAO().deleteHeader(financeMain.getFinReference(), "");

				// Rolledover Details
				getRolledoverFinanceDAO().deleteListByRef(financeMain.getFinReference(), "");
			}

		} else {
			roleCode = financeMain.getRoleCode();
			financeMain.setRcdMaintainSts("");
			financeMain.setRoleCode("");
			financeMain.setNextRoleCode("");
			financeMain.setTaskId("");
			financeMain.setNextTaskId("");
			financeMain.setNextUserId(null);
			financeMain.setWorkflowId(0);

			// Resetting Maturity Terms & Summary details rendering in case of
			// Reduce maturity cases
			if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
					&& financeMain.getAdvTerms() == 0 && !isSanctionBasedSchd) {
				int size = finScheduleData.getFinanceScheduleDetails().size();
				for (int i = size - 1; i >= 0; i--) {
					FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);
					if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
							&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0
							&& financeMain.getAdvTerms() == 0) {
						financeMain.setMaturityDate(curSchd.getSchDate());
						break;
					} else if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
							&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
						finScheduleData.getFinanceScheduleDetails().remove(i);
					}
				}
			}

			if (recordType.equals(PennantConstants.RECORD_TYPE_NEW)) {

				// Mandate Should be processed first for changes since the
				// mandate id will be updated in the finance
				// main.
				getFinMandateService().doApprove(financeDetail, auditHeader, "");

				tranType = PennantConstants.TRAN_ADD;
				financeMain.setRecordType("");
				if (finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isAllowDownpayPgm()
						&& StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_ORG)) {
					financeMain.setLinkedFinRef(financeMain.getFinReference() + "_DP");
				}
				getFinanceMainDAO().save(financeMain, TableType.MAIN_TAB, isWIF);

				if (financeMain.getOldFinReference() != null && auditHeader.getApiHeader() != null
						&& StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_ORG)) {
					FinanceMainExtension finExtension = new FinanceMainExtension();
					finExtension.setFinreference(financeMain.getFinReference());
					finExtension.setHostreference(financeMain.getOldFinReference());
					finExtension.setOldhostreference(financeMain.getExtReference());
					getFinanceMainDAO().saveHostRef(finExtension);
				}
				// Credit Review Details Saving
				if (financeDetail.getCreditReviewData() != null && creditFinancialService != null) {
					creditFinancialService.doApprove(financeDetail, auditHeader, "");
				}

				// Setting BPI Paid amount to Schedule details
				// =======================================
				FinanceRepayments repayment = null;
				if (financeMain.isAlwBPI()
						&& StringUtils.equals(FinanceConstants.BPI_DISBURSMENT, financeMain.getBpiTreatment())) {
					for (int i = 0; i < finScheduleData.getFinanceScheduleDetails().size(); i++) {
						FinanceScheduleDetail curSchd = finScheduleData.getFinanceScheduleDetails().get(i);

						if (StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday())) {

							// Update Amount on FinExcessAmount as Amount Type :
							// "ADVANCE INTEREST"
							if (SysParamUtil.isAllowed(SMTParameterConstants.BPI_PAID_ON_INSTDATE)) {
								this.advancePaymentService.processBpiAmount(finScheduleData, curSchd);
							} else {
								// Default to PAID
								curSchd.setSchdPftPaid(curSchd.getProfitSchd());
								curSchd.setSchPftPaid(true);
								long linkedTranId = finScheduleData.getDisbursementDetails().get(0).getLinkedTranId();
								repayment = prepareBpiRepayData(financeMain, curSchd.getSchDate(), linkedTranId,
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
				listSave(finScheduleData, "", isWIF, 0, serviceUID);
				getFinServiceInstructionDAO().deleteList(financeMain.getFinReference(), moduleDefiner, "_Temp");

				// IRR Schedule Details
				irrScheduleDetailDAO.saveList(finScheduleData.getIrrSDList());

				// Finance IRR Details
				// =======================================
				saveFinIRR(finScheduleData.getiRRDetails(), financeMain.getFinReference(), TableType.MAIN_TAB);

				// BPI Repayment details saving
				if (repayment != null) {
					getFinanceRepaymentsDAO().save(repayment, "");
				}

				// Save Finance Step Policy Details
				// =======================================
				saveStepDetailList(finScheduleData, isWIF, "");

				// Fee Charge Details
				// =======================================
				saveFeeChargeList(finScheduleData, moduleDefiner, isWIF, "");

				// Secondary Account Details
				// ===========================================
				saveSecondaryAccountList(finScheduleData, moduleDefiner, isWIF, "");

				// update finreferece in finReceiptheader table for upfront
				// receipts case
				if (finScheduleData.getExternalReference() != null
						&& !finScheduleData.getExternalReference().isEmpty()) {
					getFinReceiptHeaderDAO().updateReference(finScheduleData.getExternalReference(),
							financeDetail.getFinReference(), "");
					getFinanceRepaymentsDAO().updateFinReference(financeDetail.getFinReference(),
							finScheduleData.getExternalReference(), "");
				}

				// Save FInance Tax Details
				FinanceTaxDetail financeTaxDetail = financeDetail.getFinanceTaxDetail();
				if (financeTaxDetail != null) {
					FinanceTaxDetail tempTaxDetail = getFinanceTaxDetailDAO()
							.getFinanceTaxDetail(financeTaxDetail.getFinReference(), "_AView");
					if (tempTaxDetail != null) {
						getFinanceTaxDetailDAO().delete(financeTaxDetail, TableType.MAIN_TAB);
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
						getFinanceTaxDetailDAO().save(financeTaxDetail, TableType.MAIN_TAB);
					}
				}

				// Indicative Term Sheet Details Maintenance
				// =======================================
				IndicativeTermDetail termDetail = financeDetail.getIndicativeTermDetail();
				if (termDetail != null) {
					termDetail.setFinReference(financeMain.getFinReference());
					getIndicativeTermDetailDAO().save(termDetail, "", true);
					getIndicativeTermDetailDAO().delete(termDetail, "_Temp", true);
				}

				// Save Finance Premium Details
				// =======================================
				if (StringUtils.equals(FinanceConstants.PRODUCT_SUKUK, productCode)) {
					// FIXME : DataSet Removal to be worked on if it requires in
					// future
				}

				// Save Finance Premium Details
				// =======================================
				if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
						&& !finScheduleData.getOverdraftScheduleDetails().isEmpty()) {

					// Save New list of records
					for (int i = 0; i < finScheduleData.getOverdraftScheduleDetails().size(); i++) {
						finScheduleData.getOverdraftScheduleDetails().get(i)
								.setFinReference(financeMain.getFinReference());
					}
					getOverdraftScheduleDetailDAO().saveList(finScheduleData.getOverdraftScheduleDetails(), "");
				}

				/*
				 * // Save Finance Flag details if(financeDetail.getFinFlagsDetails() != null &&
				 * !financeDetail.getFinFlagsDetails().isEmpty()) {
				 * getFinFlagDetailsDAO().savefinFlagList(financeDetail. getFinFlagsDetails(), ""); }
				 */

				// Save Contributor Header Details
				// =======================================
				if (financeDetail.getFinContributorHeader() != null) {

					FinContributorHeader contributorHeader = financeDetail.getFinContributorHeader();
					String[] fields = PennantJavaUtil.getFieldDetails(new FinContributorHeader());
					getFinContributorHeaderDAO().save(contributorHeader, "");
					auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1],
							financeDetail.getFinContributorHeader().getBefImage(),
							financeDetail.getFinContributorHeader()));

					// Save Contributor Header Details
					// =======================================
					if (contributorHeader.getContributorDetailList() != null
							&& contributorHeader.getContributorDetailList().size() > 0) {
						List<AuditDetail> details = financeDetail.getAuditDetailMap().get("Contributor");
						details = processingContributorList(details, "", contributorHeader.getFinReference());
						auditDetails.addAll(details);
					}
				}

				// Save Rolledover Finance Details
				// =======================================
				if (financeDetail.getRolledoverFinanceHeader() != null) {
					getRolledoverFinanceDAO().saveHeader(financeDetail.getRolledoverFinanceHeader(), "");

					// Rolledover Details
					getRolledoverFinanceDAO().saveDetailList(
							financeDetail.getRolledoverFinanceHeader().getRolledoverFinanceDetails(), "");
				}

				// Vas Recording Details
				if (finScheduleData.getVasRecordingList() != null && !finScheduleData.getVasRecordingList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("VasRecordings");
					details = processingVasRecordngList(financeDetail, details, "");
					auditDetails.addAll(details);

					// Vas Recording Extended Field Details
					List<AuditDetail> exdDetails = financeDetail.getAuditDetailMap().get("VasExtendedDetails");
					exdDetails = extendedFieldDetailsService.processingExtendedFieldDetailList(exdDetails,
							VASConsatnts.MODULE_NAME, null, "", serviceUID);
					auditDetails.addAll(exdDetails);

					insuranceDetailService.doApproveVASInsurance(finScheduleData.getVasRecordingList(),
							financeMain.getUserDetails());

				}

				saveFinExpenseDetails(financeMain);

			} else {

				tranType = PennantConstants.TRAN_UPD;
				financeMain.setRecordType("");

				if (!isWIF) {

					// Fetch Existing data before Modification
					FinScheduleData oldFinSchdData = null;
					if (finScheduleData.getFinanceMain().isScheduleRegenerated()) {
						oldFinSchdData = getFinSchDataByFinRef(finScheduleData.getFinReference(), "", -1);
						oldFinSchdData.setFinReference(finScheduleData.getFinReference());
					}

					// Overdraft Movements are set for every Overdraft Schedule
					// Built,and limit change is calculated by
					// sub old fin asset and current Fin asset
					// =======================================
					if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_OVERDRAFTSCHD)
							&& finScheduleData.getFinanceMain().isScheduleRegenerated()) {

						BigDecimal limitChange = financeMain.getFinAssetValue()
								.subtract(oldFinSchdData.getFinanceMain().getFinAssetValue());

						OverdraftMovements odMovements = new OverdraftMovements();
						odMovements.setFinReference(financeMain.getFinReference());
						odMovements.setDroplineDate(financeMain.getFirstDroplineDate());
						odMovements.setDroplineFrq(financeMain.getDroplineFrq());
						odMovements.setTenor(financeMain.getNumberOfTerms());
						odMovements.setODExpiryDate(financeMain.getMaturityDate());
						odMovements.setODLimit(financeMain.getFinAssetValue());
						odMovements.setLimitChange(limitChange);
						odMovements.setValueDate(Calendar.getInstance().getTime());

						// Saving the OverdraftMovements in table
						getOverdraftScheduleDetailDAO().saveOverdraftMovement(odMovements);

					}

					// Create log entry for Action for Schedule Modification
					// =======================================
					FinLogEntryDetail entryDetail = new FinLogEntryDetail();
					entryDetail.setFinReference(finScheduleData.getFinReference());
					entryDetail.setEventAction(StringUtils.isBlank(financeDetail.getAccountingEventCode())
							? AccountEventConstants.ACCEVENT_ADDDBSN : financeDetail.getAccountingEventCode());
					entryDetail.setSchdlRecal(finScheduleData.getFinanceMain().isScheduleRegenerated());
					entryDetail.setPostDate(curBDay);
					entryDetail.setReversalCompleted(false);
					long logKey = getFinLogEntryDetailDAO().save(entryDetail);

					// Save Schedule Details For Future Modifications
					if (finScheduleData.getFinanceMain().isScheduleRegenerated()) {
						listSave(oldFinSchdData, "_Log", isWIF, logKey, serviceUID);
					}
				}

				// Save Finance Main after Saving the oldFinSchdData
				// =======================================
				getFinanceMainDAO().update(financeMain, TableType.MAIN_TAB, isWIF);

				// Save Finance Premium Details
				// =======================================
				if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
						&& !finScheduleData.getOverdraftScheduleDetails().isEmpty()) {
					getOverdraftScheduleDetailDAO().deleteByFinReference(financeMain.getFinReference(), "", isWIF);

					// Save New list of records
					for (int i = 0; i < finScheduleData.getOverdraftScheduleDetails().size(); i++) {
						finScheduleData.getOverdraftScheduleDetails().get(i)
								.setFinReference(financeMain.getFinReference());
					}
					getOverdraftScheduleDetailDAO().saveList(finScheduleData.getOverdraftScheduleDetails(), "");
				}

				// ScheduleDetails delete and save
				// =======================================

				saveLMSServiceLogs(finScheduleData, "");

				listDeletion(finScheduleData, moduleDefiner, "", isWIF);
				getFinServiceInstructionDAO().deleteList(financeMain.getFinReference(), moduleDefiner, "_Temp");
				listSave(finScheduleData, "", isWIF, 0, serviceUID);

				// Fee Charge Details
				// =======================================
				saveFeeChargeList(finScheduleData, moduleDefiner, isWIF, "");

				// Secondary Account Details
				// =======================================
				saveSecondaryAccountList(finScheduleData, moduleDefiner, isWIF, "");

				// Update Finance Premium Details
				// =======================================
				if (productCode.equals(FinanceConstants.PRODUCT_SUKUK)) {
					// FIXME : DataSet Removal to be worked on if it requires in
					// future
				}

			}

			// LowerTax Deductions
			// ===============
			if (CollectionUtils.isNotEmpty(financeDetail.getFinScheduleData().getLowerTaxDeductionDetails())
					&& FinanceConstants.FINSER_EVENT_ORG.equals(financeDetail.getModuleDefiner())) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("LowerTaxDeductionDetails");
				details = processLowerTaxDeductionDetails(details, TableType.MAIN_TAB, financeDetail);
				auditDetails.addAll(details);
			}
			if (financeDetail.getFinScheduleData().getLowerTaxDeductionDetails() != null
					&& financeDetail.getFinScheduleData().getLowerTaxDeductionDetails().size() >= 1) {
				getLowerTaxDeductionDAO()
						.delete(financeDetail.getFinScheduleData().getLowerTaxDeductionDetails().get(0), "_temp");
			}
			// Save Cheque Header Details
			// =======================================
			if (financeDetail.getChequeHeader() != null) {
				String[] fields = PennantJavaUtil.getFieldDetails(new ChequeHeader());
				finChequeHeaderService.doApprove(auditHeader);
				auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1],
						financeDetail.getChequeHeader().getBefImage(), financeDetail.getChequeHeader()));
			}

			// set Customer Details Audit
			if (!StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)) {
				if (financeDetail.getCustomerDetails() != null
						&& StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_ORG)) {
					auditDetails.addAll(getCustomerDetailsService().saveOrUpdate(financeDetail, ""));
				}
			}

			if (!financeDetail.isExtSource() && !isWIF) {

				// Contractor Asset Details
				// =======================================
				List<ContractorAssetDetail> contractorAssetDetails = financeDetail.getContractorAssetDetails();
				if (contractorAssetDetails != null && !contractorAssetDetails.isEmpty()) {
					auditDetails
							.addAll(getContractorAssetDetailService().doApprove(contractorAssetDetails, "", tranType));
				}

				List<DocumentDetails> documents = financeDetail.getDocumentDetailsList();
				if (CollectionUtils.isNotEmpty(documents)) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
					details = processingDocumentDetailsList(details, "", financeMain, moduleDefiner, serviceUID);
					auditDetails.addAll(details);
				}

				// set Check list details Audit
				// =======================================
				if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {
					auditDetails.addAll(checkListDetailService.doApprove(financeDetail, "", serviceUID));
				}

				// set the Audit Details & Save / Update Guarantor Details
				// =======================================
				if (financeDetail.getGurantorsDetailList() != null
						&& !financeDetail.getGurantorsDetailList().isEmpty()) {
					auditDetails.addAll(getGuarantorDetailService().doApprove(financeDetail.getGurantorsDetailList(),
							"", tranType, financeMain.getFinSourceID(), auditHeader.getApiHeader(),
							financeMain.getServiceName()));
				}

				// set the Audit Details & Save / Update JountAccount Details
				// =======================================
				if (financeDetail.getJountAccountDetailList() != null
						&& !financeDetail.getJountAccountDetailList().isEmpty()) {
					auditDetails.addAll(getJointAccountDetailService().doApprove(
							financeDetail.getJountAccountDetailList(), "", tranType, financeMain.getFinSourceID(),
							auditHeader.getApiHeader(), financeMain.getServiceName()));
				}

				// set Finance Collateral Details Audit
				// =======================================
				if (financeDetail.getFinanceCollaterals() != null && !financeDetail.getFinanceCollaterals().isEmpty()) {
					auditDetails.addAll(getFinCollateralService().doApprove(financeDetail.getFinanceCollaterals(), "",
							tranType, financeMain.getFinSourceID(), auditHeader.getApiHeader(),
							financeMain.getServiceName()));
				}

				// Finance Eligibility Rule Details
				// =======================================
				auditDetails.addAll(getEligibilityDetailService().saveOrUpdate(financeDetail));

				// Finance Scoring Module Details List Saving
				// =======================================
				auditDetails.addAll(getScoringDetailService().saveOrUpdate(financeDetail));

				// Dedup Details
				// =======================================
				auditDetails.addAll(saveDedupDetails(financeDetail));

				// Additional Field Details Save / Update
				// =======================================
				doSaveAddlFieldDetails(financeDetail, "");

				// Etihad Credit Bureau Details
				// =======================================
				if (financeDetail.getEtihadCreditBureauDetail() != null) {
					getEtihadCreditBureauDetailService().doApprove(financeDetail.getEtihadCreditBureauDetail(), "",
							tranType);
				}

				// Bundled Products Details
				// =======================================
				if (bundledProductTemp != null) {
					getBundledProductsDetailService().doApprove(bundledProductTemp, "", tranType);
				}
				// Agreement Field Details
				// =======================================
				if (financeDetail.getAgreementFieldDetails() != null) {
					getAgreementFieldsDetailService().doApprove(financeDetail.getAgreementFieldDetails(), "", tranType);
				}

				// Advance Payment Details
				// =======================================
				// Payment Order Issue Details
				// =======================================
				// Quick disbursement
				if (StringUtils.equals(FinanceConstants.FINSER_EVENT_ORG, financeDetail.getModuleDefiner())) {
					if (financeMain.isQuickDisb()) {
						if (!StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)
								|| auditHeader.getApiHeader() == null) {
							auditDetails.addAll(getFinAdvancePaymentsService().processQuickDisbursment(financeDetail,
									"", auditTranType));
						} else {
							auditDetails.addAll(getFinAdvancePaymentsService().processAPIQuickDisbursment(financeDetail,
									"", auditTranType));
						}

					} else {
						if (financeDetail.getAdvancePaymentsList() != null
								&& !financeDetail.getAdvancePaymentsList().isEmpty()) {
							auditDetails.addAll(
									getFinAdvancePaymentsService().saveOrUpdate(financeDetail.getAdvancePaymentsList(),
											"", auditTranType, financeDetail.isDisbStp()));
						}
					}
				} else {
					if (StringUtils.equals(PennantConstants.RCD_STATUS_SUBMITTED, financeMain.getRecordStatus())) {
						if (financeDetail.getFinScheduleData().getFinServiceInstructions().size() > 0
								&& StringUtils.equals(FinanceConstants.FINSER_EVENT_ADDDISB, financeDetail
										.getFinScheduleData().getFinServiceInstructions().get(0).getFinEvent())) {
							for (FinanceDisbursement disbursements : financeDetail.getFinScheduleData()
									.getDisbursementDetails()) {
								for (FinAdvancePayments payments : financeDetail.getAdvancePaymentsList()) {
									if (payments.getLlDate().compareTo(disbursements.getDisbDate()) == 0) {
										if (disbursements.isQuickDisb()
												&& StringUtils.equalsIgnoreCase("New", payments.getStatus())) {
											auditDetails.addAll(getFinAdvancePaymentsService()
													.processQuickDisbursment(financeDetail, "", auditTranType));
										} else {
											// do nothing
										}
									}
								}
							}
						}
					}
					auditDetails.addAll(getFinAdvancePaymentsService().saveOrUpdate(
							financeDetail.getAdvancePaymentsList(), "", auditTranType, financeDetail.isDisbStp()));
				}

				// Asset Evaluation Details
				// =======================================
				if (financeDetail.getFinAssetEvaluation() != null) {
					getFinAssetEvaluationService().doApprove(financeDetail.getFinAssetEvaluation(), "", tranType);
				}

				// PSL details
				if (financeDetail.getPslDetail() != null) {
					getpSLDetailService().doApprove(financeDetail.getPslDetail(), TableType.MAIN_TAB, tranType);
				}
				List<RisksAndMitigants> risksAndMitigants = financeDetail.getRisksAndMitigantsList();
				if (CollectionUtils.isNotEmpty(risksAndMitigants)) {
					auditDetails.addAll(
							risksAndMitigantsService.doApprove(risksAndMitigants, TableType.MAIN_TAB, tranType));
				}
				List<SanctionConditions> sanctionConditions = financeDetail.getSanctionDetailsList();
				if (CollectionUtils.isNotEmpty(sanctionConditions)) {
					auditDetails.addAll(
							sanctionConditionsService.doApprove(sanctionConditions, TableType.MAIN_TAB, tranType));
				}
				List<DealRecommendationMerits> dealRecommendationMerits = financeDetail
						.getDealRecommendationMeritsDetailsList();
				if (CollectionUtils.isNotEmpty(dealRecommendationMerits)) {
					auditDetails.addAll(dealRecommendationMeritsService.doApprove(dealRecommendationMerits,
							TableType.MAIN_TAB, tranType));
				}
				List<DueDiligenceDetails> dueDiligenceDetails = financeDetail.getDueDiligenceDetailsList();
				if (CollectionUtils.isNotEmpty(dueDiligenceDetails)) {
					auditDetails.addAll(
							dueDiligenceDetailsService.doApprove(dueDiligenceDetails, TableType.MAIN_TAB, tranType));
				}
				List<RecommendationNotes> recommendationNotesList = financeDetail.getRecommendationNoteList();

				if (CollectionUtils.isNotEmpty(recommendationNotesList)) {
					auditDetails.addAll(recommendationNotesDetailsService.doApprove(recommendationNotesList,
							TableType.MAIN_TAB, tranType));
				}
				// Synopsis Details
				if (financeDetail.getSynopsisDetails() != null) {
					getSynopsisDetailsService().doApprove(financeDetail.getSynopsisDetails(), TableType.MAIN_TAB,
							tranType);
				}

				//Fin OCR Details
				//=========================
				if (financeDetail.getFinOCRHeader() != null && finOCRHeaderService != null) {
					auditDetails.addAll(finOCRHeaderService.processFinOCRHeader(aAuditHeader, "doApprove"));
				}

				//PMAY
				//=========================
				if (financeDetail.getPmay() != null && pmayService != null) {
					auditDetails.add(pmayService.doApprove(financeDetail.getPmay(), TableType.MAIN_TAB, auditTranType));
				}

				// Verifications
				saveOrUpdateVerifications(auditDetails, financeDetail, financeMain, tranType);

				//calling post hoot
				if (postExteranalServiceHook != null) {
					postExteranalServiceHook.doProcess(aAuditHeader, "doApprove");
				}

				// Advance Payment Details
				// =======================================
				if (financeDetail.getAdvancePaymentsList() != null) {
					if (StringUtils.trimToEmpty(recordMainStatus).equals(FinanceConstants.FINSER_EVENT_CANCELDISB)) {
						getFinAdvancePaymentsService().doCancel(financeDetail);
					} else {
						getFinAdvancePaymentsService().doApprove(financeDetail.getAdvancePaymentsList(), "", tranType,
								financeDetail.isDisbStp());
					}

					getFinAdvancePaymentsService().processDisbursments(financeDetail);
				}

				if (CollectionUtils.isNotEmpty(financeDetail.getCovenantTypeList())) {
					finCovenantTypeService.doApprove(financeDetail.getCovenantTypeList(), "", tranType);
				}

				List<Covenant> covenants = financeDetail.getCovenants();
				int docSize = 0;
				if (CollectionUtils.isNotEmpty(financeDetail.getDocumentDetailsList())) {
					docSize = financeDetail.getDocumentDetailsList().size();
				}
				if (CollectionUtils.isNotEmpty(covenants)) {
					auditDetails.addAll(covenantsService.doApprove(covenants, TableType.MAIN_TAB, tranType, docSize));
				}

				List<FinOption> finOptions = financeDetail.getFinOptions();
				if (CollectionUtils.isNotEmpty(finOptions)) {
					auditDetails.addAll(finOptionService.doApprove(finOptions, TableType.MAIN_TAB, tranType));
				}

				// Collateral Assignments Details
				// =======================================
				if (financeDetail.getCollateralAssignmentList() != null
						&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
					details = processingCollateralAssignmentList(details, "", finScheduleData.getFinanceMain());
					auditDetails.addAll(details);
				}

				// Save Collateral setup Details
				List<CollateralSetup> collateralSetupList = financeDetail.getCollaterals();
				if (collateralSetupList != null && !collateralSetupList.isEmpty()) {
					List<AuditDetail> details = getCollateralSetupService().processCollateralSetupList(aAuditHeader,
							"doApprove");
					auditDetails.addAll(details);
				}

				// Save LegalDetails
				List<LegalDetail> legalDetailsList = financeDetail.getLegalDetailsList();
				if (CollectionUtils.isNotEmpty(legalDetailsList)) {
					List<AuditDetail> details = getLegalDetailService().processLegalDetails(aAuditHeader, "doApprove");
					auditDetails.addAll(details);
				}

				// FinAssetTypes
				if (financeDetail.getFinAssetTypesList() != null && !financeDetail.getFinAssetTypesList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinAssetTypes");
					details = processingFinAssetTypesList(details, "", finScheduleData.getFinanceMain());
					auditDetails.addAll(details);
				}

				// Fin Flag Details
				if (financeDetail.getFinFlagsDetails() != null && financeDetail.getFinFlagsDetails().size() > 0) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinFlagsDetail");
					details = processingFinFlagDetailList(details, "");
					auditDetails.addAll(details);
				}

				// AssetType Extended field Details
				if (financeDetail.getExtendedFieldRenderList() != null
						&& financeDetail.getExtendedFieldRenderList().size() > 0) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");

					for (int i = 0; i < details.size(); i++) {
						ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) details.get(i).getModelData();
						extendedFieldRender.setReference(financeMain.getFinReference());
					}
					details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
							AssetConstants.EXTENDEDFIELDS_MODULE, null, "", serviceUID);
					auditDetails.addAll(details);
				}

				// Extended field Details
				if (financeDetail.getExtendedFieldRender() != null) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("LoanExtendedFieldDetails");
					details = extendedFieldDetailsService.processingExtendedFieldDetailList(details,
							ExtendedFieldConstants.MODULE_LOAN, financeDetail.getExtendedFieldHeader().getEvent(), "",
							serviceUID);
					auditDetails.addAll(details);
				}

			}

			// Finance Insurance Details
			// =======================================
			if (finScheduleData.getFinInsuranceList() != null && !finScheduleData.getFinInsuranceList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinInsuranceDetails");
				details = processFinInsuranceDetails(details, "", financeDetail, isWIF);
				auditDetails.addAll(details);
			}

			// Finance Fee Details
			if (!financeDetail.isExtSource()) {
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
			 * FinanceConstants.FINSER_EVENT_ORG) ||
			 * FinanceConstants.FINSER_EVENT_ADDDISB.equalsIgnoreCase(financeDetail.getModuleDefiner())) { if
			 * (financeDetail.getFinScheduleData().getFinFeeReceipts() == null ||
			 * financeDetail.getFinScheduleData().getFinFeeReceipts().isEmpty()) {
			 * getFinFeeDetailService().createExcessAmount(financeMain.getFinReference(), null,
			 * financeMain.getCustID()); } else {
			 * getFinFeeDetailService().doApproveFinFeeReceipts(financeDetail.getFinScheduleData().getFinFeeReceipts(),
			 * "", tranType,financeMain.getFinReference(), financeMain.getCustID(), financeDetail.getModuleDefiner()); }
			 * }
			 */

			// Plan EMI Holiday Details Deletion, if exists on Old image
			// =======================================
			if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_PLANNEDEMI)) {
				if (financeMain != null && financeMain.isPlanEMIHAlw()) {
					if (StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
						getFinPlanEmiHolidayDAO().deletePlanEMIHMonths(financeMain.getFinReference(), "");
					} else if (StringUtils.equals(financeMain.getPlanEMIHMethod(),
							FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
						getFinPlanEmiHolidayDAO().deletePlanEMIHDates(financeMain.getFinReference(), "");
					}
				}
			}
		}

		// Plan EMI Holiday Details
		// =======================================
		if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_ORG)
				|| StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_PLANNEDEMI)) {
			if (financeMain.isPlanEMIHAlw()) {
				if (StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
					if (finScheduleData.getPlanEMIHmonths() != null && !finScheduleData.getPlanEMIHmonths().isEmpty()) {

						List<FinPlanEmiHoliday> holidayList = new ArrayList<>();
						for (int i = 0; i < finScheduleData.getPlanEMIHmonths().size(); i++) {
							int planEMIHMonth = finScheduleData.getPlanEMIHmonths().get(i);
							FinPlanEmiHoliday emiHoliday = new FinPlanEmiHoliday();
							emiHoliday.setFinReference(financeMain.getFinReference());
							emiHoliday.setPlanEMIHMonth(planEMIHMonth);
							holidayList.add(emiHoliday);
						}

						getFinPlanEmiHolidayDAO().savePlanEMIHMonths(holidayList, "");
					}
				} else if (StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
					if (finScheduleData.getPlanEMIHDates() != null && !finScheduleData.getPlanEMIHDates().isEmpty()) {

						List<FinPlanEmiHoliday> holidayList = new ArrayList<>();
						for (int i = 0; i < finScheduleData.getPlanEMIHDates().size(); i++) {
							Date planEMIHDate = finScheduleData.getPlanEMIHDates().get(i);
							FinPlanEmiHoliday emiHoliday = new FinPlanEmiHoliday();
							emiHoliday.setFinReference(financeMain.getFinReference());
							emiHoliday.setPlanEMIHDate(planEMIHDate);
							holidayList.add(emiHoliday);
						}

						getFinPlanEmiHolidayDAO().savePlanEMIHDates(holidayList, "");
					}
				}
			}
		}

		// Save New Finance Object created with DownPayment Support program
		// =======================================
		if (finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isAllowDownpayPgm()
				&& StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_ORG)) {
			downpayFinApprove(aAuditHeader, serviceUID);
		}

		// Save Finance Schedule Snapshot
		// ===============================
		// TODO commented below line which is leading to column miss match
		// exception
		// getFinanceMainDAO().saveFinanceSnapshot(financeMain);

		// Update Task_log and Task_Owners tables
		// =======================================
		financeMain.setRoleCode(roleCode);
		updateTaskLog(financeMain, false);

		// Send Handling instruction to ICCS interface
		doHandlingInstructionProcess(financeDetail);

		// Save Salaried Posting Details
		if (ImplementationConstants.ALLOW_FIN_SALARY_PAYMENT
				&& StringUtils.isNotEmpty(financeMain.getRcdMaintainSts())) {
			saveFinSalPayment(finScheduleData, orgNextSchd, false);
		}
		if (!isWIF) {
			getFinStageAccountingLogDAO().update(financeMain.getFinReference(), moduleDefiner, false);
		}

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());

		if (!financeDetail.isExtSource()) {

			// Delete Rolledover Finance Details
			// =======================================
			if (financeDetail.getRolledoverFinanceHeader() != null) {
				getRolledoverFinanceDAO().deleteHeader(financeMain.getFinReference(), "_Temp");

				// Rolledover Details
				getRolledoverFinanceDAO().deleteListByRef(financeMain.getFinReference(), "_Temp");
			}

			// Save FInance Tax Details
			if (financeDetail.getFinanceTaxDetail() != null) {
				FinanceTaxDetail tempTaxDetail = getFinanceTaxDetailDAO()
						.getFinanceTaxDetail(financeDetail.getFinanceTaxDetail().getFinReference(), "_TView");
				if (tempTaxDetail != null) {
					getFinanceTaxDetailDAO().delete(financeDetail.getFinanceTaxDetail(), TableType.TEMP_TAB);
				}
			}

			// ScheduleDetails delete
			// =======================================
			if (!StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)
					|| (auditHeader.getApiHeader() == null) || StringUtils.isNotBlank(financeMain.getServiceName())) {
				listDeletion(finScheduleData, moduleDefiner, "_Temp", isWIF);
			}

			// Plan EMI Holiday Details Deletion, if exists on Old image
			// =======================================
			FinanceMain befFinMain = finScheduleData.getFinanceMain().getBefImage();
			if (befFinMain != null && befFinMain.isPlanEMIHAlw()) {
				if (StringUtils.equals(befFinMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
					getFinPlanEmiHolidayDAO().deletePlanEMIHMonths(financeMain.getFinReference(), "_Temp");
				} else if (StringUtils.equals(befFinMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
					getFinPlanEmiHolidayDAO().deletePlanEMIHDates(financeMain.getFinReference(), "_Temp");
				}
			}

			if (!isWIF && (!StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)
					|| (auditHeader.getApiHeader() == null) || StringUtils.isNotBlank(financeMain.getServiceName()))) {
				// Additional Field Details Deletion in _Temp Table
				// =======================================
				doDeleteAddlFieldDetails(financeDetail, "_Temp");

				auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
				auditDetailList.addAll(checkListDetailService.delete(financeDetail, "_Temp", auditTranType));
				auditDetailList.addAll(
						getListAuditDetails(listDeletion_FinContributor(financeDetail, "_Temp", auditTranType)));
				auditDetailList.addAll(getContractorAssetDetailService()
						.delete(financeDetail.getContractorAssetDetails(), "_Temp", auditTranType));
				if (financeDetail.getEtihadCreditBureauDetail() != null) {
					auditDetailList.add(getEtihadCreditBureauDetailService()
							.delete(financeDetail.getEtihadCreditBureauDetail(), "_Temp", auditTranType));
				}
				if (bundledProductTemp != null) {
					auditDetailList
							.add(getBundledProductsDetailService().delete(bundledProductTemp, "_Temp", auditTranType));
				}
				if (financeDetail.getAdvancePaymentsList() != null) {
					auditDetailList.addAll(getFinAdvancePaymentsService().delete(financeDetail.getAdvancePaymentsList(),
							"_Temp", auditTranType));
				}

				List<FinCovenantType> finCovenantTypes = financeDetail.getCovenantTypeList();
				if (CollectionUtils.isNotEmpty(finCovenantTypes)) {
					// auditDetailList.addAll(finCovenantTypeService.delete(finCovenantTypes,
					// "_Temp", auditTranType));
					finCovenantTypeService.delete(finCovenantTypes, "_Temp", auditTranType);
				}

				List<Covenant> covenants = financeDetail.getCovenants();
				if (CollectionUtils.isNotEmpty(covenants)) {
					// auditDetailList.addAll(covenantsService.delete(covenants,
					// TableType.TEMP_TAB, auditTranType));
					covenantsService.delete(covenants, TableType.TEMP_TAB, auditTranType);
				}

				List<DocumentDetails> documents = financeDetail.getDocumentDetailsList();
				if (!financeDetail.isExtSource() && !isWIF) {
					if (CollectionUtils.isNotEmpty(documents)) {
						listDocDeletion(financeDetail, "_Temp");
					}
				}

				if (financeDetail.getFinAssetEvaluation() != null) {
					auditDetailList.add(getFinAssetEvaluationService().delete(financeDetail.getFinAssetEvaluation(),
							"_Temp", auditTranType));
				}

				if (financeDetail.getPslDetail() != null) {
					auditDetailList.add(getpSLDetailService().delete(financeDetail.getPslDetail(), TableType.TEMP_TAB,
							auditTranType));
				}

				// Collateral assignment Details
				if (financeDetail.getCollateralAssignmentList() != null
						&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
					auditDetailList.addAll(details);
					getCollateralAssignmentDAO().deleteByReference(financeMain.getFinReference(), "_Temp");
				}

				// FinAssetTypes details
				if (financeDetail.getFinAssetTypesList() != null && !financeDetail.getFinAssetTypesList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinAssetTypes");
					getFinAssetTypeDAO().deleteByReference(financeMain.getFinReference(), "_Temp");
					auditDetailList.addAll(details);
				}

				// AssetType Extended field Details
				if (financeDetail.getExtendedFieldRenderList() != null
						&& financeDetail.getExtendedFieldRenderList().size() > 0) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");
					auditDetailList.addAll(extendedFieldDetailsService.delete(details,
							AssetConstants.EXTENDEDFIELDS_MODULE, financeMain.getFinReference(), null, "_Temp"));
				}

				// Vas Recording Details details Prasad
				if (finScheduleData.getVasRecordingList() != null && !finScheduleData.getVasRecordingList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("VasRecordings");
					getVasRecordingDAO().deleteByPrimaryLinkRef(financeMain.getFinReference(), "_Temp");
					auditDetailList.addAll(details);

					// Vas Recording Extended field Details
					List<AuditDetail> vasExtDetails = financeDetail.getAuditDetailMap().get("VasExtendedDetails");
					if (CollectionUtils.isNotEmpty(vasExtDetails)) {
						for (AuditDetail auditDetail : vasExtDetails) {
							ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) auditDetail.getModelData();
							extendedFieldDetailsService.delete(vasExtDetails, VASConsatnts.MODULE_NAME,
									extendedFieldRender.getReference(), null, "_Temp");
						}
					}
				}

				// Loan Extended field Details
				if (financeDetail.getExtendedFieldRender() != null) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("LoanExtendedFieldDetails");
					auditDetailList.addAll(extendedFieldDetailsService.delete(details,
							ExtendedFieldConstants.MODULE_LOAN, financeMain.getFinReference(),
							financeDetail.getExtendedFieldHeader().getEvent(), "_Temp"));
				}

				// Deleting Finance Insurance Details
				if (finScheduleData.getFinInsuranceList() != null && !finScheduleData.getFinInsuranceList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinInsuranceDetails");
					getFinInsurancesDAO().deleteFinInsurancesList(financeMain.getFinReference(), isWIF, "_Temp");
					// delete from finschedule temp
					for (int i = 0; i < finScheduleData.getFinInsuranceList().size(); i++) {
						FinInsurances insurance = finScheduleData.getFinInsuranceList().get(i);
						getFinInsurancesDAO().deleteFreqBatch(insurance.getInsId(), isWIF, "_Temp");
					}
					auditDetailList.addAll(details);
				}

				if (financeDetail.getFinanceCollaterals() != null) {
					auditDetailList.addAll(getFinCollateralService().delete(financeDetail.getFinanceCollaterals(),
							"_Temp", auditTranType));
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
				 * auditDetailList.add(getSynopsisDetailsService().delete( financeDetail.getSynopsisDetails(),
				 * TableType.TEMP_TAB, auditTranType)); }
				 */

			}

			if (!StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)
					|| auditHeader.getApiHeader() == null || StringUtils.isNotBlank(financeMain.getServiceName())) {

				// Fin Fee Details Deletion
				if (CollectionUtils.isNotEmpty(finFeeDetails)) {
					auditDetailList
							.addAll(getFinFeeDetailService().delete(finFeeDetails, "_Temp", auditTranType, isWIF));
				}

				// Fin Fee Receipt Details Deletion
				/*
				 * //TODO:GANESH NEED TO REMOVE if (financeDetail.getFinScheduleData().getFinFeeReceipts() != null) {
				 * auditDetailList.addAll(getFinFeeDetailService().deleteFinFeeReceipts(
				 * financeDetail.getFinScheduleData().getFinFeeReceipts(), "_Temp", auditHeader.getAuditTranType())); }
				 */

				// Step Details Deletion
				// =======================================
				getFinanceStepDetailDAO().deleteList(financeMain.getFinReference(), isWIF, "_Temp");

				// Delete Finance Premium Details
				// =======================================
				if (StringUtils.equals(FinanceConstants.PRODUCT_SUKUK, productCode)) {
					// FIXME : DataSet Removal to be worked on if it requires in
					// future
				}

				// Delete Finance Overdraft Details
				// =======================================
				if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
						&& finScheduleData.getOverdraftScheduleDetails().size() > 0) {
					getOverdraftScheduleDetailDAO().deleteByFinReference(financeMain.getFinReference(), "_Temp", isWIF);
				}

				// Finance Flag Details
				if (financeDetail.getFinFlagsDetails() != null && !financeDetail.getFinFlagsDetails().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinFlagsDetail");
					getFinFlagDetailsDAO().deleteList(financeMain.getFinReference(), FinanceConstants.MODULE_NAME,
							"_Temp");
					auditDetailList.addAll(details);
				}

				// Delete Finance IRR Values
				deleteFinIRR(financeMain.getFinReference(), TableType.TEMP_TAB);

				// Finance Main Details
				// =======================================
				getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, isWIF, true);
			}

			// tasks # >>Start Advance EMI and DSF
			String grcAdvType = financeMain.getGrcAdvType();
			String repayAdvType = financeMain.getAdvType();

			AdvancePaymentDetail advPay = financeDetail.getAdvancePaymentDetail();
			if ((grcAdvType != null || repayAdvType != null) && advPay != null) {
				advPay.setInstructionUID(serviceUID);

				// If Advance Payment updation Required
				if (AdvancePaymentUtil.advPayUpdateReq(moduleDefiner)) {
					advancePaymentService.processAdvancePayment(advPay, moduleDefiner, financeMain.getLastMntBy());
				}

				// Saving of Advance Payment Detail
				advancePaymentService.save(financeDetail.getAdvancePaymentDetail());

			}

			if ((grcAdvType != null || repayAdvType != null)) {
				if (FinanceConstants.FINSER_EVENT_ORG.equals(moduleDefiner)
						|| FinanceConstants.FINSER_EVENT_ADDDISB.equals(moduleDefiner)) {
					processAdvancePayment(finFeeDetails, finScheduleData);
				} else if (ImplementationConstants.ALW_ADV_INTEMI_ADVICE_CREATION) {
					if (FinanceConstants.FINSER_EVENT_RATECHG.equals(moduleDefiner)
							|| FinanceConstants.BULK_RATE_CHG.equals(moduleDefiner)
							|| FinanceConstants.FINSER_EVENT_ADDTERM.equals(moduleDefiner)
							|| FinanceConstants.FINSER_EVENT_RMVTERM.equals(moduleDefiner)
							|| FinanceConstants.FINSER_EVENT_CANCELDISB.equals(moduleDefiner)
							|| FinanceConstants.FINSER_EVENT_CHGPFT.equals(moduleDefiner)
							|| FinanceConstants.FINSER_EVENT_CHGFRQ.equals(moduleDefiner)
							|| FinanceConstants.FINSER_EVENT_CANCELFIN.equals(moduleDefiner)
							|| FinanceConstants.FINSER_EVENT_PLANNEDEMI.equals(moduleDefiner)
							|| FinanceConstants.FINSER_EVENT_UNPLANEMIH.equals(moduleDefiner)
							|| FinanceConstants.FINSER_EVENT_RESCHD.equals(moduleDefiner)
							|| FinanceConstants.FINSER_EVENT_RECALCULATE.equals(moduleDefiner)
							|| FinanceConstants.FINSER_EVENT_CHGRPY.equals(moduleDefiner)) {
						processAdvancePayment(finScheduleData);
					}
				}
			}
			// tasks # >>End Advance EMI and DSF

			// Mail Alert Notification for Customer/Dealer/Provider...etc
			Notification notification = new Notification();
			notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_AE);
			notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_CN);
			notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_SP);
			notification.getTemplates().add(NotificationConstants.TEMPLATE_FOR_DSAN);
			notification.setModule("LOAN_ORG");
			String finEvent = StringUtils.isEmpty(moduleDefiner) ? FinanceConstants.FINSER_EVENT_ORG : moduleDefiner;
			notification.setSubModule(finEvent);
			notification.setKeyReference(financeMain.getFinReference());
			notification.setStage(PennantConstants.REC_ON_APPR);
			notification.setReceivedBy(financeMain.getLastMntBy());
			financeMain.setWorkflowId(tempWorkflowId);
			try {

				if (notificationService != null) {
					notificationService.sendNotifications(notification, financeDetail, financeMain.getFinType(),
							financeDetail.getDocumentDetailsList());
				}

			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
			if (!recordType.equals(PennantConstants.RECORD_TYPE_DEL)) {
				financeMain.setWorkflowId(0);
			}

			// Saving the reasons
			saveReasonDetails(financeDetail);

			// Calling External CMS API system.
			processPayments(financeDetail);

			// Auto Payable creation for Cash back process (DBD/MBD)
			cashBackProcessService.createCashBackAdvice(financeMain, financeDetail.getPromotion(), curBDay);

			FinanceDetail tempfinanceDetail = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
			FinanceMain tempfinanceMain = tempfinanceDetail.getFinScheduleData().getFinanceMain();
			auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
					tempfinanceMain.getBefImage(), tempfinanceMain));
			auditHeader.setAuditDetails(auditDetailList);

			// Adding audit as deleted from Temp table
			if (!isWIF) {
				getAuditHeaderDAO().addAudit(auditHeader);
			}
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(
				new AuditDetail(auditTranType, 1, fields[0], fields[1], financeMain.getBefImage(), financeMain));
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));
		// Adding audit as Insert/Update/deleted into main table
		if (!isWIF) {
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(financeDetail);

		if (ImplementationConstants.PERC_REQ_FOR_FINTYPE_FEE
				&& StringUtils.equals(FinanceConstants.FINSER_EVENT_ORG, financeDetail.getModuleDefiner())) {

			List<FinFeeConfig> calculateFees = feeCalculator.convertToFinanceFees(financeDetail);
			if (CollectionUtils.isNotEmpty(calculateFees)) {
				getFinFeeConfigService().saveList(calculateFees, "");
			}
		}

		// Save GCDCustomer'/////
		// processgcdCustomer(financeDetail, "insert");
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
		String taskId = engine.getUserTaskId(role);

		// Execute service tasks.
		List<ServiceTask> serviceTasks = engine.getServiceTasks(taskId, afinanceMain);
		ServiceTask task;
		List<String> finishedTasks = new ArrayList<>();
		String finalOperation = null;
		auditHeader.setProcessCompleted(false);

		while (!serviceTasks.isEmpty()) {
			task = serviceTasks.get(0);

			if (ProcessUtil.isPersistentTask(task)) {
				finalOperation = task.getOperation();

				break;
			}

			auditHeader = execute(auditHeader, task, role, engine);

			// Check whether to proceed with next service tasks.
			auditHeader = nextProcess(auditHeader);

			if (!auditHeader.isNextProcess()) {
				break;
			}

			// Get the next service tasks.
			finishedTasks.add(task.getOperation());
			serviceTasks = getRemainingServiceTasks(engine, taskId, afinanceMain, finishedTasks);
		}

		// Save the data.
		if (!auditHeader.isProcessCompleted()) {
			// Set work-flow details.
			setNextTaskDetails(taskId, afinanceMain, engine, usrAction, role);

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
	private AuditHeader execute(AuditHeader auditHeader, ServiceTask task, String role, WorkflowEngine engine)
			throws Exception {
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = financeDetail.getFinScheduleData().getFinanceMain();

		switch (task.getOperation()) {
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
			getDeviationDetailsService().doCheckDeviationApproval(auditHeader);

			break;
		// ### Query Management Validations
		case PennantConstants.METHOD_DO_VALIDATE_QUERYMGMT_APPROVAL:
			getQueryDetailService().getQueryMgmtList(auditHeader, task, role);
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
			if (null != extBreService) {
				boolean breReq = SysParamUtil.isAllowed(SMTParameterConstants.EXT_BRE_REQ);
				if (breReq) {
					extBreService.executeBRE(auditHeader);
				}
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

		default:
			// Execute any other custom service tasks
			if (StringUtils.isNotBlank(task.getOperation())) {
				boolean taskExecuted = getCustomServiceTask().executeExternalServiceTask(auditHeader, task);
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

	private AuditHeader save(String operation, AuditHeader auditHeader, String recordType) throws Exception {
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
				customerDetails.getReturnStatus().setReturnCode(InterfaceConstants.SUCCESS_CODE);
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
	 * Method for process Finance Maintenance and sending handling instruction request to ICCS interface
	 * 
	 * @param financeDetail
	 * @throws InterfaceException
	 * 
	 */
	private void doHandlingInstructionProcess(FinanceDetail financeDetail) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		HandlingInstruction handlingInstruction = new HandlingInstruction();
		String narration = "";

		if (StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_POSTPONEMENT)) {// Postponement
			handlingInstruction.setMaintenanceCode(FinanceConstants.INSTCODE_POSTPONEMNT);
			Date installmentDate = financeDetail.getFinScheduleData().getFinanceMain().getEventFromDate();
			handlingInstruction.setInstallmentDate(installmentDate);
			narration = "Postponement";
		} else if (StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_CHGFRQ)) {
			handlingInstruction.setMaintenanceCode(FinanceConstants.INSTCODE_AMENDINSTLMNT); // Modify
																								// installment
																								// date
			narration = "Amend Installment date";
		} else if (StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_RATECHG)
				|| StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_CHGRPY)
				|| StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ADDDISB)
				|| StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_CHGPFT)
				|| StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_RESCHD)
				|| StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_CHGGRCEND)
				|| StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ADDTERM)
				|| StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_RMVTERM)) {
			handlingInstruction.setMaintenanceCode(FinanceConstants.INSTCODE_RESCHDPAY);
			narration = "Re-Schedule";
		}

		if (!StringUtils.isBlank(handlingInstruction.getMaintenanceCode())) {
			handlingInstruction.setFinanceRef(financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
			handlingInstruction.setRemarks(narration);

			// Send Handling instruction to ICCS interface
			getHandlingInstructionService().sendFinanceMaintenanceRequest(handlingInstruction);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * doApprove method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) based on the Record type do
	 * following actions a) DELETE Delete the record from the main table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"" b) NEW Add new record in to main table by using getFinanceMainDAO().save with
	 * parameters financeMain,"" c) EDIT Update record in the main table by using getFinanceMainDAO().update with
	 * parameters financeMain,"" 3) Delete the record from the workFlow table by using getFinanceMainDAO().delete with
	 * parameters financeMain,"_Temp" 4) Audit the record in to AuditHeader and AdtFinanceMain by using
	 * auditHeaderDAO.addAudit(auditHeader) for Work flow 5) Audit the record in to AuditHeader and AdtFinanceMain by
	 * using auditHeaderDAO.addAudit(auditHeader) based on the transaction Type.
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 * @throws AccountNotFoundException
	 */
	@Override
	public AuditHeader doPreApprove(AuditHeader aAuditHeader, boolean isWIF) throws InterfaceException {
		logger.debug(Literal.ENTERING);

		String tranType = "";
		String roleCode = "";
		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		aAuditHeader = businessValidation(aAuditHeader, "doApprove", isWIF, false);
		if (!aAuditHeader.isNextProcess()) {
			return aAuditHeader;
		}

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		Date curBDay = SysParamUtil.getAppDate();

		// Execute Accounting Details Process
		// =======================================
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		String productCode = finScheduleData.getFinanceType().getFinCategory();

		List<FinServiceInstruction> serviceInstructions = getServiceInstructions(financeDetail);

		long serviceUID = Long.MIN_VALUE;
		for (FinServiceInstruction finServInst : serviceInstructions) {
			serviceUID = finServInst.getInstructionUID();
		}

		if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
			financeMain.setFinApprovedDate(curBDay);

			if (financeMain.getFinContractDate() == null) {
				financeMain.setFinContractDate(financeMain.getFinStartDate());
			}

			if (financeMain.getFeeChargeAmt() == null) {
				financeMain.setFeeChargeAmt(BigDecimal.ZERO);
			}
			if (financeMain.getInsuranceAmt() == null) {
				financeMain.setInsuranceAmt(BigDecimal.ZERO);
			}
		}

		// Validation Checking for All Finance Detail data
		// =======================================
		if (auditHeader.getErrorMessage() != null && auditHeader.getErrorMessage().size() > 0) {
			return auditHeader;
		}

		String preApprovalTableType = PennantConstants.PREAPPROVAL_TABLE_TYPE;

		List<FinFeeDetail> finFeeDetailActuals = finScheduleData.getFinFeeDetailList();

		String moduleDefiner = financeDetail.getModuleDefiner();
		String auditTranType = auditHeader.getAuditTranType();
		if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
			tranType = PennantConstants.TRAN_DEL;
			getFinanceMainDAO().delete(financeMain, TableType.MAIN_TAB, isWIF, true);
			listDeletion(finScheduleData, moduleDefiner, "", isWIF);

			// Delete Finance Premium Details
			if (productCode.equals(FinanceConstants.PRODUCT_SUKUK)) {
				// FIXME : DataSet Removal to be worked on if it requires in
				// future
			}

			// Step Details Deletion
			// =======================================
			getFinanceStepDetailDAO().deleteList(financeMain.getFinReference(), isWIF, "_Temp");

			if (!isWIF) {
				// Additional Field Details Deletion
				// =======================================
				doDeleteAddlFieldDetails(financeDetail, "");
				auditDetails.addAll(jointGuarantorDeletion(financeDetail, "", tranType));
				auditDetails.addAll(checkListDetailService.delete(financeDetail, "", auditTranType));
				auditDetails.addAll(getListAuditDetails(listDeletion_FinContributor(financeDetail, "", auditTranType)));
			}

		} else {
			roleCode = financeMain.getRoleCode();
			financeMain.setRcdMaintainSts("");
			financeMain.setRoleCode("");
			financeMain.setNextRoleCode("");
			financeMain.setTaskId("");
			financeMain.setNextTaskId("");
			financeMain.setNextUserId(null);
			financeMain.setWorkflowId(0);

			if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {

				tranType = PennantConstants.TRAN_ADD;
				financeMain.setRecordType("");
				if (finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isAllowDownpayPgm()
						&& StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_ORG)) {
					financeMain.setLinkedFinRef(financeMain.getFinReference() + "_DP");
				}
				getFinanceMainDAO().save(financeMain, TableType.PRE_APPR_TAB, isWIF);

				// Schedule Details
				// =======================================
				listSave(finScheduleData, preApprovalTableType, isWIF, 0, serviceUID);

				// Save Finance Step Policy Details
				// =======================================
				saveStepDetailList(finScheduleData, isWIF, preApprovalTableType);

				// Fee Charge Details
				// =======================================
				saveFeeChargeList(finScheduleData, moduleDefiner, isWIF, preApprovalTableType);

				// Secondary Account Details
				// =======================================
				saveSecondaryAccountList(finScheduleData, moduleDefiner, isWIF, preApprovalTableType);

				// Save Finance Premium Details
				// =======================================
				if (productCode.equals(FinanceConstants.PRODUCT_SUKUK)) {
					// FIXME : DataSet Removal to be worked on if it requires in
					// future
				}

				// Save Contributor Header Details
				// =======================================
				if (financeDetail.getFinContributorHeader() != null) {

					FinContributorHeader contributorHeader = financeDetail.getFinContributorHeader();
					String[] fields = PennantJavaUtil.getFieldDetails(new FinContributorHeader());
					getFinContributorHeaderDAO().save(contributorHeader, preApprovalTableType);
					auditDetails.add(new AuditDetail(auditTranType, 1, fields[0], fields[1],
							financeDetail.getFinContributorHeader().getBefImage(),
							financeDetail.getFinContributorHeader()));

					// Save Contributor Header Details
					// =======================================
					if (contributorHeader.getContributorDetailList() != null
							&& contributorHeader.getContributorDetailList().size() > 0) {
						List<AuditDetail> details = financeDetail.getAuditDetailMap().get("Contributor");
						details = processingContributorList(details, preApprovalTableType,
								contributorHeader.getFinReference());
						auditDetails.addAll(details);
					}
				}

			} else {

				tranType = PennantConstants.TRAN_UPD;
				financeMain.setRecordType("");
				getFinanceMainDAO().update(financeMain, TableType.MAIN_TAB, isWIF);

				if (!isWIF) {

					// Fetch Existing data before Modification
					FinScheduleData oldFinSchdData = null;
					if (finScheduleData.getFinanceMain().isScheduleRegenerated()) {
						oldFinSchdData = getFinSchDataByFinRef(finScheduleData.getFinReference(), "", -1);
						oldFinSchdData.setFinReference(finScheduleData.getFinReference());
					}

					// Create log entry for Action for Schedule Modification
					// =======================================
					FinLogEntryDetail entryDetail = new FinLogEntryDetail();
					entryDetail.setFinReference(finScheduleData.getFinReference());
					entryDetail.setEventAction(financeDetail.getAccountingEventCode());
					entryDetail.setSchdlRecal(finScheduleData.getFinanceMain().isScheduleRegenerated());
					entryDetail.setPostDate(curBDay);
					entryDetail.setReversalCompleted(false);
					long logKey = getFinLogEntryDetailDAO().save(entryDetail);

					// Save Schedule Details For Future Modifications
					if (finScheduleData.getFinanceMain().isScheduleRegenerated()) {
						listSave(oldFinSchdData, "_Log", isWIF, logKey, serviceUID);
					}
				}

				// ScheduleDetails delete and save
				// =======================================
				listDeletion(finScheduleData, moduleDefiner, "", isWIF);
				listSave(finScheduleData, "", isWIF, 0, serviceUID);

				// Fee Charge Details
				// =======================================

				// Secondary Account Details
				// =======================================
				saveSecondaryAccountList(finScheduleData, moduleDefiner, isWIF, "");

				// Update Finance Premium Details
				// =======================================
				if (productCode.equals(FinanceConstants.PRODUCT_SUKUK)) {
					// FIXME : DataSet Removal to be worked on if it requires in
					// future
				}

			}

			if (!financeDetail.isExtSource() && !isWIF) {

				// Asset Details Process
				// =======================================

				// Save Document Details
				// =======================================
				if (financeDetail.getDocumentDetailsList() != null
						&& financeDetail.getDocumentDetailsList().size() > 0) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("DocumentDetails");
					details = processingDocumentDetailsList(details, preApprovalTableType,
							finScheduleData.getFinanceMain(), moduleDefiner, serviceUID);
					auditDetails.addAll(details);
				}

				// set Check list details Audit
				// =======================================
				if (financeDetail.getFinanceCheckList() != null && !financeDetail.getFinanceCheckList().isEmpty()) {
					auditDetails
							.addAll(checkListDetailService.doApprove(financeDetail, preApprovalTableType, serviceUID));
				}

				// set the Audit Details & Save / Update Guarantor Details
				// =======================================
				if (financeDetail.getGurantorsDetailList() != null
						&& !financeDetail.getGurantorsDetailList().isEmpty()) {
					auditDetails.addAll(getGuarantorDetailService().doApprove(financeDetail.getGurantorsDetailList(),
							preApprovalTableType, tranType, "", auditHeader.getApiHeader(),
							financeMain.getServiceName()));
				}

				// set the Audit Details & Save / Update JountAccount Details
				// =======================================
				if (financeDetail.getJountAccountDetailList() != null
						&& !financeDetail.getJountAccountDetailList().isEmpty()) {
					auditDetails.addAll(getJointAccountDetailService().doApprove(
							financeDetail.getJountAccountDetailList(), preApprovalTableType, tranType, "",
							auditHeader.getApiHeader(), financeMain.getServiceName()));
				}

				// set Finance Collateral Details Audit
				// =======================================
				if (financeDetail.getFinanceCollaterals() != null && !financeDetail.getFinanceCollaterals().isEmpty()) {
					auditDetails.addAll(getFinCollateralService().doApprove(financeDetail.getFinanceCollaterals(),
							preApprovalTableType, tranType, "", auditHeader.getApiHeader(),
							financeMain.getServiceName()));
				}

				// set Finance Insurance Details Audit
				// =======================================
				if (finScheduleData.getFinInsuranceList() != null && !finScheduleData.getFinInsuranceList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinInsuranceDetails");
					details = processFinInsuranceDetails(details, preApprovalTableType, financeDetail, isWIF);
					auditDetails.addAll(details);
				}

				// Finance Eligibility Rule Details
				// =======================================
				String finReference = financeMain.getFinReference();
				auditDetails.addAll(getEligibilityDetailService().saveOrUpdate(financeDetail));
				// move to pre-approval table
				List<FinanceEligibilityDetail> list = getEligibilityDetailService().getFinElgDetailList(finReference);
				if (list != null && !list.isEmpty()) {
					getEligibilityDetailService().saveList(list, preApprovalTableType);
					getEligibilityDetailService().deleteByFinRef(finReference);
				}

				// Finance Scoring Module Details List Saving
				// =======================================
				auditDetails.addAll(getScoringDetailService().saveOrUpdate(financeDetail));
				// move to pre-approval table
				List<FinanceScoreHeader> hearlist = getScoringDetailService().getFinScoreHeaderList(finReference, "");
				if (hearlist != null && !hearlist.isEmpty()) {
					List<Long> headerIds = new ArrayList<Long>();
					for (FinanceScoreHeader header : hearlist) {
						headerIds.add(header.getHeaderId());
						getScoringDetailService().saveHeader(header, preApprovalTableType);
					}
					List<FinanceScoreDetail> detailslist = getScoringDetailService().getFinScoreDetailList(headerIds,
							"");
					getScoringDetailService().saveDetailList(detailslist, preApprovalTableType);
					// delete from maintable

					getScoringDetailService().deleteDetailList(headerIds, "");
					getScoringDetailService().deleteHeaderList(finReference, "");

				}

				// Dedup Details
				// =======================================
				auditDetails.addAll(saveDedupDetails(financeDetail));
				moveDedupsPreApproval(finReference, preApprovalTableType);

				// Etihad Credit Bureau Details
				// =======================================
				if (financeDetail.getEtihadCreditBureauDetail() != null) {
					getEtihadCreditBureauDetailService().doApprove(financeDetail.getEtihadCreditBureauDetail(),
							preApprovalTableType, tranType);
				}

				// Bundled Products Details
				// =======================================
				if (financeDetail.getBundledProductsDetail() != null) {
					getBundledProductsDetailService().doApprove(financeDetail.getBundledProductsDetail(),
							preApprovalTableType, tranType);
				}

				// Advance Payment Details
				// =======================================
				if (financeDetail.getAdvancePaymentsList() != null) {
					getFinAdvancePaymentsService().doApprove(financeDetail.getAdvancePaymentsList(),
							preApprovalTableType, tranType, financeDetail.isDisbStp());
				}

				// Covenant Type Details
				// =======================================
				if (financeDetail.getCovenantTypeList() != null) {
					getFinCovenantTypeService().doApprove(financeDetail.getCovenantTypeList(), preApprovalTableType,
							tranType);
				}

				List<Covenant> covenants = financeDetail.getCovenants();
				if (SysParamUtil.isAllowed(SMTParameterConstants.NEW_COVENANT_MODULE)
						&& CollectionUtils.isNotEmpty(covenants)) {
					int docSize = 0;
					if (CollectionUtils.isNotEmpty(financeDetail.getDocumentDetailsList())) {
						docSize = financeDetail.getDocumentDetailsList().size();
					}
					covenantsService.doApprove(covenants, TableType.PRE_APPR_TAB, tranType, docSize);
				}

				List<FinOption> finOptions = financeDetail.getFinOptions();
				if (CollectionUtils.isNotEmpty(finOptions)) {
					finOptionService.doApprove(finOptions, TableType.PRE_APPR_TAB, tranType);
				}

				// set Finance Collateral Details Audit
				// =======================================
				if (financeDetail.getCollateralAssignmentList() != null
						&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
					details = processingCollateralAssignmentList(details, preApprovalTableType,
							finScheduleData.getFinanceMain());
					auditDetails.addAll(details);
				}

				if (financeDetail.getFinAssetTypesList() != null && !financeDetail.getFinAssetTypesList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinAssetTypes");
					details = processingFinAssetTypesList(details, preApprovalTableType,
							finScheduleData.getFinanceMain());
					auditDetails.addAll(details);
				}

				// Asset Evaluation Details
				// =======================================
				if (financeDetail.getFinAssetEvaluation() != null) {
					getFinAssetEvaluationService().doApprove(financeDetail.getFinAssetEvaluation(),
							preApprovalTableType, tranType);
				}
			}
			// Finance Fee Details
			if (!financeDetail.isExtSource()) {
				if (finScheduleData.getFinFeeDetailList() != null) {
					for (FinFeeDetail fee : finFeeDetailActuals) {
						fee.setInstructionUID(serviceUID);
					}

					finFeeDetailService.doApprove(finFeeDetailActuals, preApprovalTableType, tranType, isWIF);
				}
			}
		}

		List<AuditDetail> auditDetailList = new ArrayList<AuditDetail>();
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());

		if (!financeDetail.isExtSource()) {

			// ScheduleDetails delete
			// =======================================
			listDeletion(finScheduleData, moduleDefiner, "_Temp", isWIF);

			if (!isWIF) {
				// Additional Field Details Deletion in _Temp Table
				// =======================================
				doDeleteAddlFieldDetails(financeDetail, "_Temp");

				auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
				auditDetailList.addAll(checkListDetailService.delete(financeDetail, "_Temp", auditTranType));
				auditDetailList.addAll(
						getListAuditDetails(listDeletion_FinContributor(financeDetail, "_Temp", auditTranType)));
				auditDetailList.addAll(getContractorAssetDetailService()
						.delete(financeDetail.getContractorAssetDetails(), "_Temp", auditTranType));
				if (financeDetail.getEtihadCreditBureauDetail() != null) {
					auditDetails.add(getEtihadCreditBureauDetailService()
							.delete(financeDetail.getEtihadCreditBureauDetail(), "_Temp", auditTranType));
				}
				if (financeDetail.getBundledProductsDetail() != null) {
					auditDetails.add(getBundledProductsDetailService().delete(financeDetail.getBundledProductsDetail(),
							"_Temp", auditTranType));
				}
				if (financeDetail.getAdvancePaymentsList() != null) {
					auditDetails.addAll(getFinAdvancePaymentsService().delete(financeDetail.getAdvancePaymentsList(),
							"_Temp", auditTranType));
				}

				List<FinCovenantType> finCovenantTypes = financeDetail.getCovenantTypeList();
				if (CollectionUtils.isNotEmpty(finCovenantTypes)) {
					// auditDetails.addAll(getFinCovenantTypeService().delete(finCovenantTypes,
					// "_Temp", auditTranType));
					getFinCovenantTypeService().delete(finCovenantTypes, "_Temp", auditTranType);
				}

				List<Covenant> covenants = financeDetail.getCovenants();
				if (SysParamUtil.isAllowed(SMTParameterConstants.NEW_COVENANT_MODULE)
						&& CollectionUtils.isNotEmpty(covenants)) {
					// auditDetails.addAll(covenantsService.delete(covenants,
					// TableType.TEMP_TAB, auditTranType));
					covenantsService.delete(covenants, TableType.TEMP_TAB, auditTranType);
				}

				List<DocumentDetails> documents = financeDetail.getDocumentDetailsList();
				if (!financeDetail.isExtSource() && !isWIF) {
					if (CollectionUtils.isNotEmpty(documents)) {
						listDocDeletion(financeDetail, "_Temp");
					}
				}

				List<FinOption> finOptions = financeDetail.getFinOptions();
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
				 * auditDetailList.add(getSynopsisDetailsService().delete( financeDetail.getSynopsisDetails(),
				 * TableType.TEMP_TAB, auditTranType)); }
				 */

				// Collateral assignment Details
				if (financeDetail.getCollateralAssignmentList() != null
						&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
					auditDetails.addAll(details);
					getCollateralAssignmentDAO().deleteByReference(financeMain.getFinReference(), "_Temp");
				}

				// FinAssetTypes details
				if (financeDetail.getFinAssetTypesList() != null && !financeDetail.getFinAssetTypesList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinAssetTypes");
					getFinAssetTypeDAO().deleteByReference(financeMain.getFinReference(), "_Temp");
					auditDetails.addAll(details);
				}

				// AssetType Extended field Details
				if (financeDetail.getExtendedFieldRenderList() != null
						&& financeDetail.getExtendedFieldRenderList().size() > 0) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");

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
						extendedFieldRenderDAO.deleteList(financeMain.getFinReference(), tableName.toString(), "_Temp");
					}
					auditDetails.addAll(details);
				}

				// Vas Recording Details details
				if (finScheduleData.getVasRecordingList() != null && !finScheduleData.getVasRecordingList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("VasRecordings");
					getVasRecordingDAO().deleteByPrimaryLinkRef(financeMain.getFinReference(), "_Temp");
					auditDetails.addAll(details);

					// Vas Recording Extended field Details
					List<AuditDetail> vasExtDetails = financeDetail.getAuditDetailMap().get("VasExtendedDetails");

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

				// Deleting Finance Insurance Details
				if (finScheduleData.getFinInsuranceList() != null && !finScheduleData.getFinInsuranceList().isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinInsuranceDetails");
					getFinInsurancesDAO().deleteFinInsurancesList(financeMain.getFinReference(), isWIF, "_Temp");

					// delete from finschedule temp
					for (int i = 0; i < finScheduleData.getFinInsuranceList().size(); i++) {
						FinInsurances insurance = finScheduleData.getFinInsuranceList().get(i);
						getFinInsurancesDAO().deleteFreqBatch(insurance.getInsId(), isWIF, "_Temp");
					}
					auditDetails.addAll(details);
				}

				if (financeDetail.getFinAssetEvaluation() != null) {
					auditDetails.add(getFinAssetEvaluationService().delete(financeDetail.getFinAssetEvaluation(),
							"_Temp", auditTranType));
				}
			}

			if (finScheduleData.getFinFeeDetailList() != null) {
				auditDetails
						.addAll(getFinFeeDetailService().delete(finFeeDetailActuals, "_Temp", auditTranType, isWIF));
			}

			// Step Details Deletion
			// =======================================
			getFinanceStepDetailDAO().deleteList(financeMain.getFinReference(), isWIF, "_Temp");

			// Delete Finance Premium Details
			// =======================================
			if (productCode.equals(FinanceConstants.PRODUCT_SUKUK)) {
				// FIXME : DataSet Removal to be worked on if it requires in
				// future
			}

			// Finance Main Details
			// =======================================
			getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, isWIF, true);

			FinanceDetail tempfinanceDetail = (FinanceDetail) aAuditHeader.getAuditDetail().getModelData();
			FinanceMain tempfinanceMain = tempfinanceDetail.getFinScheduleData().getFinanceMain();
			auditHeader.setAuditDetail(new AuditDetail(aAuditHeader.getAuditTranType(), 1, fields[0], fields[1],
					tempfinanceMain.getBefImage(), tempfinanceMain));
			auditHeader.setAuditDetails(auditDetailList);

			// Adding audit as deleted from Temp table
			if (!isWIF) {
				getAuditHeaderDAO().addAudit(auditHeader);
			}
		}

		auditHeader.setAuditTranType(tranType);
		auditHeader.setAuditDetail(
				new AuditDetail(auditTranType, 1, fields[0], fields[1], financeMain.getBefImage(), financeMain));
		auditHeader.setAuditDetails(getListAuditDetails(auditDetails));

		// Adding audit as Insert/Update/deleted into main table
		if (!isWIF) {
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		// Reset Finance Detail Object for Service Task Verifications
		auditHeader.getAuditDetail().setModelData(financeDetail);

		// Save New Finance Object created with DownPayment Support program
		// =======================================
		if (finScheduleData.getFinanceType() != null && finScheduleData.getFinanceType().isAllowDownpayPgm()) {
			downpayFinApprove(aAuditHeader, serviceUID);
		}

		// Save Finance Schedule Snapshot
		// ===============================
		// TODO commented below line which is leading to column miss match
		// exception
		// getFinanceMainDAO().saveFinanceSnapshot(financeMain);

		// Update Task_log and Task_Owners tables
		// =======================================
		financeMain.setRoleCode(roleCode);
		updateTaskLog(financeMain, false);

		// TODO: confirm limits is required for pre approve
		logger.debug(Literal.LEAVING);
		return auditHeader;

	}

	private List<FinServiceInstruction> getServiceInstructions(FinanceDetail financeDetail) {
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		List<FinServiceInstruction> serviceInstructions = finScheduleData.getFinServiceInstructions();

		if (CollectionUtils.isEmpty(serviceInstructions)) {
			FinServiceInstruction finServInst = new FinServiceInstruction();
			finServInst.setFinReference(financeMain.getFinReference());
			finServInst.setFinEvent(financeDetail.getModuleDefiner());

			finScheduleData.setFinServiceInstruction(finServInst);
		}

		for (FinServiceInstruction serviceInstruction : serviceInstructions) {
			if (serviceInstruction.getInstructionUID() == Long.MIN_VALUE) {
				serviceInstruction.setInstructionUID(Long.valueOf(ReferenceGenerator.generateNewServiceUID()));
			}

			if (StringUtils.isEmpty(financeDetail.getModuleDefiner())
					|| StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ORG)) {

				if (!StringUtils.equals(serviceInstruction.getFinEvent(), FinanceConstants.FINSER_EVENT_ORG)
						&& !StringUtils.contains(serviceInstruction.getFinEvent(), "_O")) {
					serviceInstruction.setFinEvent(serviceInstruction.getFinEvent().concat("_O"));
				}
			}
		}

		return finScheduleData.getFinServiceInstructions();
	}

	/**
	 * @param finReference
	 * @param type
	 */
	private void moveDedupsPreApproval(String finReference, String type) {
		logger.debug(" Entering ");
		getBlacklistCustomerDAO().moveData(finReference, type);
		getFinanceDedupeDAO().moveData(finReference, type);
		getPoliceCaseDAO().moveData(finReference, type);
		getCustomerDedupDAO().moveData(finReference, type);
		logger.debug(" Leaving ");
	}

	/**
	 * doReject method do the following steps. 1) Do the Business validation by using businessValidation(auditHeader)
	 * method if there is any error or warning message then return the auditHeader. 2) Delete the record from the
	 * workFlow table by using getFinanceMainDAO().delete with parameters financeMain,"_Temp" 3) Audit the record in to
	 * AuditHeader and AdtFinanceMain by using auditHeaderDAO.addAudit(auditHeader) for Work flow
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
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

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		boolean apiCall = false;
		if (StringUtils.equals(PennantConstants.FINSOURCE_ID_API, financeMain.getFinSourceID())) {
			apiCall = true;
		}
		if (isAutoReject) {
			financeMain = financeMainDAO.getFinanceMainById(financeMain.getFinReference(), "_Temp", false);
			// TODO for API Fix me Issue in Reject Loan
			if (apiCall) {
				if (financeMain == null) {
					financeMain = financeDetail.getFinScheduleData().getFinanceMain();
				}
				financeMain.setFinSourceID(PennantConstants.FINSOURCE_ID_API);
			}
			financeMain.setRecordStatus(PennantConstants.RCD_STATUS_REJECTED);
			financeMain.setNextTaskId("");
			financeMain.setNextRoleCode("");

			FinScheduleData finScheduleData = new FinScheduleData();
			finScheduleData.setFinanceMain(financeMain);
			finScheduleData.setFinReference(financeMain.getFinReference());
			financeDetail.setFinScheduleData(finScheduleData);
			financeDetail.setModuleDefiner(FinanceConstants.FINSER_EVENT_ORG);
			financeDetail = getAutoRejDetails(financeDetail, "_Temp", false);
		}
		auditHeader = businessValidation(auditHeader, "doReject", isWIF, isAutoReject);
		if (!isAutoReject) {
			if (!auditHeader.isNextProcess()) {
				logger.debug(Literal.LEAVING);
				return auditHeader;
			}
		}
		String tranType = PennantConstants.TRAN_DEL;
		// PSD #139669 - Rejection of Loan under loan queue gives 900 error
		financeMain.setFinIsActive(false);
		//TODO for API Fix me
		FinanceMain financeMainAvl = financeMainDAO.getFinanceMainById(financeMain.getFinReference(), "_Temp", false);
		financeMain.setLastMntOn(new Timestamp(SysParamUtil.getAppDate().getTime()));
		if (financeMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW) && financeMainAvl != null) {
			financeMainDAO.updateRejectFinanceMain(financeMain, TableType.TEMP_TAB, isWIF);
		}

		// Cancel All Transactions done by Finance Reference
		// =======================================
		cancelStageAccounting(financeMain.getFinReference(), financeDetail.getModuleDefiner());

		if (StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ORG)) {
			getPostingsPreparationUtil().postReveralsExceptFeePay(financeMain.getFinReference());

			auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
			String[] fieldsArray = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
			auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fieldsArray[0],
					fieldsArray[1], financeMain.getBefImage(), financeMain));

			// Saving the reasons
			saveReasonDetails(financeDetail);

			getFinanceMainDAO().saveRejectFinanace(financeMain);

			// Update Task_log and Task_Owners tables
			// =======================================
			updateTaskLog(financeMain, false);

			if (StringUtils.isEmpty(financeMain.getRcdMaintainSts())) {
				if (ImplementationConstants.LIMIT_INTERNAL) {
					getLimitManagement().processLoanLimitOrgination(financeDetail, false, LimitConstants.UNBLOCK,
							false);
				} else {
					getLimitCheckDetails().doProcessLimits(financeMain, FinanceConstants.CANCEL_RESERVE);
				}
			} else if (StringUtils.equals(financeMain.getRcdMaintainSts(), FinanceConstants.FINSER_EVENT_ADDDISB)) {
				if (ImplementationConstants.LIMIT_INTERNAL) {
					getLimitManagement().processLoanDisbursments(financeDetail, false, LimitConstants.CANCIL, false);
				}
			}

			if ((StringUtils.containsIgnoreCase(financeMain.getRecordStatus(), "Reject")
					|| StringUtils.containsIgnoreCase(financeMain.getRecordStatus(), "Cancel")
					|| StringUtils.containsIgnoreCase(financeMain.getRecordStatus(), "Rejected"))) {
				TaskOwners taskOwner = new TaskOwners();
				taskOwner.setReference(financeMain.getFinReference());
				taskOwner.setProcessed(true);
				taskOwner.setRoleCode(financeMain.getRoleCode());
				getTaskOwnersDAO().updateTaskOwner(taskOwner, true);
				if (!isWIF) {
					getAuditHeaderDAO().addAudit(auditHeader);
				}
				return auditHeader;
			}

		}

		String productCode = StringUtils
				.trimToEmpty(financeDetail.getFinScheduleData().getFinanceMain().getFinCategory());

		// Save Finance Details Data on Reject Tables
		// =======================================
		if (!isWIF && StringUtils.equals(financeMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			getFinanceMainDAO().saveRejectFinanceDetails(financeMain);
		}

		// OverDraft Schedule Details Deletion
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
				&& financeDetail.getFinScheduleData().getOverdraftScheduleDetails().size() > 0) {
			getOverdraftScheduleDetailDAO().deleteByFinReference(financeMain.getFinReference(), "_Temp", isWIF);
		}

		// Finance Details deletion
		// =======================================
		listDeletion(financeDetail.getFinScheduleData(), financeDetail.getModuleDefiner(), "_Temp", isWIF);
		getFinServiceInstructionDAO().deleteList(financeDetail.getFinScheduleData().getFinReference(),
				financeDetail.getModuleDefiner(), "_Temp");

		// Document Details
		// =======================================
		if (financeDetail.getDocumentDetailsList() != null && !financeDetail.getDocumentDetailsList().isEmpty()) {
			// getDocumentDetailsDAO().deleteList(financeDetail.getDocumentDetailsList(),
			// "_Temp");
		}

		// Secondary Account deletion
		getSecondaryAccountDAO().delete(financeMain.getFinReference(), "_Temp");

		if (!isWIF) {
			// Additional Field Details Deletion
			// =======================================
			doDeleteAddlFieldDetails(financeDetail, "_Temp");
		}

		// Indicative Term Sheet Details Maintenance
		// =======================================
		IndicativeTermDetail termDetail = financeDetail.getIndicativeTermDetail();
		if (termDetail != null) {
			termDetail.setFinReference(financeMain.getFinReference());
			getIndicativeTermDetailDAO().delete(termDetail, "_Temp", true);
		}

		// Delete Rolledover Finance Details
		// =======================================
		if (financeDetail.getRolledoverFinanceHeader() != null) {
			getRolledoverFinanceDAO().deleteHeader(financeMain.getFinReference(), "_Temp");

			// Rolledover Details
			getRolledoverFinanceDAO().deleteListByRef(financeMain.getFinReference(), "_Temp");
		}

		// Plan EMI Holiday Details Deletion, if exists on Old image
		// =======================================
		FinanceMain befFinMain = financeDetail.getFinScheduleData().getFinanceMain().getBefImage();
		if (befFinMain != null && befFinMain.isPlanEMIHAlw()) {
			if (StringUtils.equals(befFinMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				getFinPlanEmiHolidayDAO().deletePlanEMIHMonths(financeMain.getFinReference(), "_Temp");
			} else if (StringUtils.equals(befFinMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
				getFinPlanEmiHolidayDAO().deletePlanEMIHDates(financeMain.getFinReference(), "_Temp");
			}
		}

		// Save Collateral setup Details
		List<CollateralSetup> collateralSetupList = financeDetail.getCollaterals();
		if (collateralSetupList != null && !collateralSetupList.isEmpty()) {
			List<AuditDetail> details = getCollateralSetupService().processCollateralSetupList(auditHeader, "doReject");
			if (details != null) {
				auditDetails.addAll(details);
			}
		}

		//Fin OCR Details
		//=========================
		if (financeDetail.getFinOCRHeader() != null && finOCRHeaderService != null) {
			auditDetails.addAll(finOCRHeaderService.processFinOCRHeader(auditHeader, "doReject"));
		}

		// Legal Details
		List<LegalDetail> legalDetailsList = financeDetail.getLegalDetailsList();
		if (CollectionUtils.isNotEmpty(legalDetailsList)) {
			List<AuditDetail> details = getLegalDetailService().processLegalDetails(auditHeader, "doReject");
			if (details != null) {
				auditDetails.addAll(details);
			}
		}

		if (financeMain.isLegalRequired()) {
			getLegalDetailService().deleteList(financeMain.getFinReference(), TableType.TEMP_TAB);
		}

		// Cheque Details
		if (financeDetail.getChequeHeader() != null) {
			String[] fields = PennantJavaUtil.getFieldDetails(new ChequeHeader());
			finChequeHeaderService.doReject(auditHeader);
			auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
					financeDetail.getChequeHeader().getBefImage(), financeDetail.getChequeHeader()));
		}

		// Delete Finance IRR values
		deleteFinIRR(financeMain.getFinReference(), TableType.TEMP_TAB);

		// Delete Tax Details
		if (financeDetail.getFinanceTaxDetail() != null) {
			FinanceTaxDetail tempTaxDetail = getFinanceTaxDetailDAO()
					.getFinanceTaxDetail(financeDetail.getFinanceTaxDetail().getFinReference(), "_TView");
			if (tempTaxDetail != null) {
				getFinanceTaxDetailDAO().delete(financeDetail.getFinanceTaxDetail(), TableType.TEMP_TAB);
			}
		}

		// Finance Main Details Deletion
		// =======================================
		FinanceMain dbFinanceMain = getFinanceMain(financeDetail.getFinScheduleData().getFinReference(), "_Temp");
		if (null != dbFinanceMain) {
			getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, isWIF, isAutoReject);
		}

		auditHeader.setAuditTranType(PennantConstants.TRAN_WF);
		String[] fields = PennantJavaUtil.getFieldDetails(new FinanceMain(), financeMain.getExcludeFields());
		auditHeader.setAuditDetail(new AuditDetail(auditHeader.getAuditTranType(), 1, fields[0], fields[1],
				financeMain.getBefImage(), financeMain));

		// Step Details
		// =======================================
		getFinanceStepDetailDAO().deleteList(financeMain.getFinReference(), isWIF, "_Temp");

		// Asset deletion
		if (!isWIF) {

			// Finance Eligibility Rule Details
			// =======================================
			List<FinanceEligibilityDetail> elgList = getEligibilityDetailService()
					.getFinElgDetailList(financeMain.getFinReference());
			FinanceEligibilityDetail eligibilityDetail = new FinanceEligibilityDetail();
			String[] elgFields = PennantJavaUtil.getFieldDetails(eligibilityDetail,
					eligibilityDetail.getExcludeFields());
			for (int i = 0; i < elgList.size(); i++) {
				elgList.get(i).setLastMntBy(financeMain.getLastMntBy());
				elgList.get(i).setLastMntOn(financeMain.getLastMntOn());
				elgList.get(i).setRoleCode(financeMain.getRoleCode());
				elgList.get(i).setRecordStatus(financeMain.getRecordStatus());
				auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), i + 1, elgFields[0], elgFields[1],
						null, elgList.get(i)));
			}
			getEligibilityDetailService().deleteByFinRef(financeMain.getFinReference());

			// Finance Scoring Module Details List Saving
			// =======================================
			List<Object> scoreObjectList = getScoringDetailService()
					.getFinScoreDetailList(financeMain.getFinReference());

			// Finance Score Headers
			if (scoreObjectList != null) {
				List<FinanceScoreHeader> headerList = (List<FinanceScoreHeader>) scoreObjectList.get(0);
				FinanceScoreHeader tempHeader = new FinanceScoreHeader();
				String[] headerFields = PennantJavaUtil.getFieldDetails(tempHeader, tempHeader.getExcludeFields());
				tempHeader = null;
				List<Long> headerIdList = new ArrayList<Long>();
				for (int i = 0; i < headerList.size(); i++) {
					headerIdList.add(headerList.get(i).getHeaderId());
					headerList.get(i).setLastMntBy(financeMain.getLastMntBy());
					headerList.get(i).setRoleCode(financeMain.getRoleCode());
					headerList.get(i).setRecordStatus(financeMain.getRecordStatus());
					auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), i + 1, headerFields[0],
							headerFields[1], null, headerList.get(i)));
				}

				HashMap<Long, List<FinanceScoreDetail>> scoreMap = (HashMap<Long, List<FinanceScoreDetail>>) scoreObjectList
						.get(1);
				List<FinanceScoreDetail> detailList = new ArrayList<FinanceScoreDetail>();
				for (int i = 0; i < headerList.size(); i++) {
					detailList.addAll((Collection<FinanceScoreDetail>) scoreMap.get(headerIdList.get(i)));
				}
				FinanceScoreDetail tempDetail = new FinanceScoreDetail();
				String[] detailfields = PennantJavaUtil.getFieldDetails(tempDetail, tempDetail.getExcludeFields());
				tempDetail = null;

				for (int i = 0; i < detailList.size(); i++) {
					detailList.get(i).setLastMntBy(financeMain.getLastMntBy());
					detailList.get(i).setRoleCode(financeMain.getRoleCode());
					detailList.get(i).setRecordStatus(financeMain.getRecordStatus());
					auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), i + 1, detailfields[0],
							detailfields[1], null, detailList.get(i)));
				}

				// Deletion of Scoring Details
				getScoringDetailService().deleteHeaderList(financeMain.getFinReference(), "");
				getScoringDetailService().deleteDetailList(headerIdList, "");
			}

			// Delete Black List Customer Data
			// =======================================
			List<FinBlacklistCustomer> blackListData = getBlacklistCustomerDAO()
					.fetchFinBlackList(financeMain.getFinReference());
			FinBlacklistCustomer blData = new FinBlacklistCustomer();
			String[] blFields = PennantJavaUtil.getFieldDetails(blData, blData.getExcludeFields());
			for (int i = 0; i < blackListData.size(); i++) {
				blackListData.get(i).setLastMntBy(financeMain.getLastMntBy());
				blackListData.get(i).setRoleCode(financeMain.getRoleCode());
				blackListData.get(i).setRecordStatus(financeMain.getRecordStatus());
				auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), i + 1, blFields[0], blFields[1], null,
						blackListData.get(i)));
			}
			getBlacklistCustomerDAO().deleteList(financeMain.getFinReference());

			// Delete Finance DeDup List Data
			// =======================================
			getFinanceDedupeDAO().deleteList(financeMain.getFinReference());

			// Delete Dedup PoliceCase Data
			// =======================================
			List<PoliceCase> policeCaseData = getPoliceCaseDAO().fetchFinPoliceCase(financeMain.getFinReference());
			PoliceCase policeCaselistData = new PoliceCase();
			String[] pcFields = PennantJavaUtil.getFieldDetails(policeCaselistData,
					policeCaselistData.getExcludeFields());
			for (int i = 0; i < policeCaseData.size(); i++) {
				policeCaseData.get(i).setLastMntBy(financeMain.getLastMntBy());
				policeCaseData.get(i).setRoleCode(financeMain.getRoleCode());
				policeCaseData.get(i).setRecordStatus(financeMain.getRecordStatus());
				auditDetails.add(new AuditDetail(auditHeader.getAuditTranType(), i + 1, pcFields[0], pcFields[1], null,
						policeCaseData.get(i)));
			}

			getPoliceCaseDAO().deleteList(financeMain.getFinReference());

			// Cancel commodity inventory details
			doCheckCommodityInventory(financeDetail);

			auditDetails.addAll(jointGuarantorDeletion(financeDetail, "_Temp", auditHeader.getAuditTranType()));
			auditDetails.addAll(checkListDetailService.delete(financeDetail, "_Temp", auditHeader.getAuditTranType()));
			auditDetails.addAll(getListAuditDetails(
					listDeletion_FinContributor(financeDetail, "_Temp", auditHeader.getAuditTranType())));
			if (financeDetail.getEtihadCreditBureauDetail() != null) {
				auditDetails.add(getEtihadCreditBureauDetailService()
						.delete(financeDetail.getEtihadCreditBureauDetail(), "_Temp", auditHeader.getAuditTranType()));
			}
			if (financeDetail.getBundledProductsDetail() != null) {
				auditDetails.add(getBundledProductsDetailService().delete(financeDetail.getBundledProductsDetail(),
						"_Temp", auditHeader.getAuditTranType()));
			}
			if (financeDetail.getAgreementFieldDetails() != null) {
				auditDetails.add(getAgreementFieldsDetailService().delete(financeDetail.getAgreementFieldDetails(),
						"_Temp", auditHeader.getAuditTranType()));
			}
			if (financeDetail.getAdvancePaymentsList() != null) {
				auditDetails.addAll(getFinAdvancePaymentsService().delete(financeDetail.getAdvancePaymentsList(),
						"_Temp", auditHeader.getAuditTranType()));
			}

			String auditTranType = auditHeader.getAuditTranType();

			List<FinCovenantType> finCovenantTyps = financeDetail.getCovenantTypeList();
			if (CollectionUtils.isNotEmpty(finCovenantTyps)) {
				// auditDetails.addAll(finCovenantTypeService.delete(finCovenantTyps,
				// "_Temp", auditTranType));
				finCovenantTypeService.delete(finCovenantTyps, "_Temp", auditTranType);
			}

			List<Covenant> covenants = financeDetail.getCovenants();
			if (CollectionUtils.isNotEmpty(covenants)) {
				// auditDetails.addAll(covenantsService.delete(covenants,
				// TableType.TEMP_TAB, auditTranType));
				covenantsService.delete(covenants, TableType.TEMP_TAB, auditTranType);
			}

			List<DocumentDetails> documents = financeDetail.getDocumentDetailsList();
			if (!financeDetail.isExtSource() && !isWIF) {
				if (CollectionUtils.isNotEmpty(documents)) {
					listDocDeletion(financeDetail, "_Temp");
				}
			}

			if (financeDetail.getFinAssetEvaluation() != null) {
				auditDetails.add(getFinAssetEvaluationService().delete(financeDetail.getFinAssetEvaluation(), "_Temp",
						auditHeader.getAuditTranType()));
			}

			// Collateral assignment Details
			if (financeDetail.getCollateralAssignmentList() != null
					&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
				if (details != null) {
					auditDetails.addAll(details);
				}
				getCollateralAssignmentDAO().deleteByReference(financeMain.getFinReference(), "_Temp");
			}

			// FinAssetTypes details
			if (financeDetail.getFinAssetTypesList() != null && !financeDetail.getFinAssetTypesList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinAssetTypes");
				getFinAssetTypeDAO().deleteByReference(financeMain.getFinReference(), "_Temp");
				if (details != null) {
					auditDetails.addAll(details);
				}
			}

			// AssetType Extended field Details
			if (financeDetail.getExtendedFieldRenderList() != null
					&& financeDetail.getExtendedFieldRenderList().size() > 0) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");
				auditDetails.addAll(extendedFieldDetailsService.delete(details, AssetConstants.EXTENDEDFIELDS_MODULE,
						financeMain.getFinReference(), null, "_Temp"));
			}

			// Vas Recording Details details Prasad
			if (financeDetail.getFinScheduleData().getVasRecordingList() != null
					&& !financeDetail.getFinScheduleData().getVasRecordingList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("VasRecordings");
				getVasRecordingDAO().deleteByPrimaryLinkRef(financeMain.getFinReference(), "_Temp");
				if (details != null) {
					auditDetails.addAll(details);
				}

				// Vas Recording Extended field Details
				List<AuditDetail> vasExtDetails = financeDetail.getAuditDetailMap().get("VasExtendedDetails");
				if (CollectionUtils.isNotEmpty(vasExtDetails)) {
					for (AuditDetail auditDetail : vasExtDetails) {
						ExtendedFieldRender extendedFieldRender = (ExtendedFieldRender) auditDetail.getModelData();
						extendedFieldDetailsService.delete(vasExtDetails, VASConsatnts.MODULE_NAME,
								extendedFieldRender.getReference(), null, "_Temp");
					}
				}
			}

			// Loan Extended field Details
			if (financeDetail.getExtendedFieldRender() != null) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("LoanExtendedFieldDetails");
				auditDetails.addAll(extendedFieldDetailsService.delete(details, ExtendedFieldConstants.MODULE_LOAN,
						financeMain.getFinReference(), financeDetail.getExtendedFieldHeader().getEvent(), "_Temp"));
			}

			// Deleting Finance Insurance Details
			if (financeDetail.getFinScheduleData().getFinInsuranceList() != null
					&& !financeDetail.getFinScheduleData().getFinInsuranceList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinInsuranceDetails");
				getFinInsurancesDAO().deleteFinInsurancesList(financeMain.getFinReference(), isWIF, "_Temp");
				// delete from finschedule temp
				for (int i = 0; i < financeDetail.getFinScheduleData().getFinInsuranceList().size(); i++) {
					FinInsurances insurance = financeDetail.getFinScheduleData().getFinInsuranceList().get(i);
					getFinInsurancesDAO().deleteFreqBatch(insurance.getInsId(), isWIF, "_Temp");
				}
				if (details != null) {
					auditDetails.addAll(details);
				}
			}

			if (financeDetail.getFinanceCollaterals() != null) {
				auditDetails.addAll(getFinCollateralService().delete(financeDetail.getFinanceCollaterals(), "_Temp",
						auditHeader.getAuditTranType()));
			}

			// Finance Flag Details
			if (financeDetail.getFinFlagsDetails() != null && !financeDetail.getFinFlagsDetails().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinFlagsDetail");
				getFinFlagDetailsDAO().deleteList(financeMain.getFinReference(), FinanceConstants.MODULE_NAME, "_Temp");
				if (details != null) {
					auditDetails.addAll(details);
				}
			}

		}

		List<FinFeeDetail> fees = financeDetail.getFinScheduleData().getFinFeeDetailList();
		if (fees != null) {
			auditDetails.addAll(getFinFeeDetailService().delete(fees, "_Temp", auditHeader.getAuditTranType(), isWIF));
		}

		/*
		 * //TODO:GANESH NEED TO REMOVE if (financeDetail.getFinScheduleData().getFinFeeReceipts() != null) {
		 * auditDetails.addAll(getFinFeeDetailService().deleteFinFeeReceipts(
		 * financeDetail.getFinScheduleData().getFinFeeReceipts(), "_Temp", auditHeader.getAuditTranType())); }
		 */

		auditHeader.setAuditDetails(auditDetails);

		// Saving the reasons
		saveReasonDetails(financeDetail);

		if (!isWIF) {
			getAuditHeaderDAO().addAudit(auditHeader);
		}

		// Reset Finance Detail Object for Service Task Verifications
		// =======================================
		auditHeader.getAuditDetail().setModelData(financeDetail);

		// Update Task_log and Task_Owners tables
		// =======================================
		updateTaskLog(financeMain, false);

		// Send Cancel DDA Registration request to interface
		// ==================================================
		// getDdaControllerService().cancelDDARegistration(financeMain.getFinReference());

		getFinMandateService().doRejct(financeDetail, auditHeader);

		// Send Collateral DeMark request to interface
		// ==================================================
		/**
		 * Check whether Collateral Marked or not if Marked then send DeMark collateral request to interface
		 * 
		 */
		if (financeDetail.getFinanceCollaterals() != null) {
			getCollateralMarkProcess().deMarkCollateral(financeDetail.getFinanceCollaterals());
		}

		// send Cancel Reservation Request to ACP Interface and save log details
		// ======================================================================
		// Maker limit unblock should happen in the origination only

		logger.debug(Literal.LEAVING);
		return auditHeader;
	}

	private FinanceDetail getAutoRejDetails(FinanceDetail financeDetail, String type, boolean isWIF) {
		logger.debug(Literal.ENTERING);

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();

		String finReference = financeMain.getFinReference();
		CustomerDetails customerDetails = new CustomerDetails();

		finScheduleData.setDisbursementDetails(
				financeDisbursementDAO.getFinanceDisbursementDetails(finReference, type, false));
		financeDetail
				.setAdvancePaymentsList(finAdvancePaymentsService.getFinAdvancePaymentsById(finReference, "_View"));
		finScheduleData.setFinanceType(financeTypeDAO.getFinanceTypeByID(financeMain.getFinType(), "_AView"));
		customerDetails.setCustomer(customerDAO.getCustomerByID(financeMain.getCustID()));
		financeDetail.setCustomerDetails(customerDetails);
		finScheduleData
				.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finReference, type, isWIF));
		finScheduleData.setOverdraftScheduleDetails(
				overdraftScheduleDetailDAO.getOverdraftScheduleDetails(finReference, type, isWIF));
		financeDetail.setDocumentDetailsList(
				documentDetailsDAO.getDocumentDetailsByRef(finReference, FinanceConstants.MODULE_NAME, type));
		financeDetail.setCollaterals(collateralSetupService.getCollateralDetails(finReference, true));
		financeDetail.setCollateralAssignmentList(collateralAssignmentDAO.getCollateralAssignmentByFinRef(finReference,
				FinanceConstants.MODULE_NAME, type));
		financeDetail
				.setCovenantTypeList(finCovenantTypeService.getFinCovenantDocTypeByFinRef(finReference, type, false));

		if (SysParamUtil.isAllowed(SMTParameterConstants.NEW_COVENANT_MODULE)) {
			financeDetail.setCovenants(covenantsService.getCovenants(finReference, "Loan", TableType.TEMP_TAB));
		}

		financeDetail.setFinanceCheckList(checkListDetailService.getCheckListByFinRef(finReference, type));
		financeDetail.setFinAssetTypesList(finAssetTypeDAO.getFinAssetTypesByFinRef(finReference, type));
		financeDetail.setExtendedFieldRenderList(
				getExtendedAssetDetails(finReference, financeDetail.getFinAssetTypesList()));
		financeDetail.setFinanceCollaterals(finCollateralService.getFinCollateralsByRef(finReference, type));
		finScheduleData.setFinFeeDetailList(finFeeDetailDAO.getFinFeeDetailByFinRef(finReference, false, type));
		List<Long> feeIds = new ArrayList<Long>();
		for (FinFeeDetail finFeeDetail : finScheduleData.getFinFeeDetailList()) {
			feeIds.add(finFeeDetail.getFeeID());
		}

		if (!feeIds.isEmpty()) {
			finScheduleData.setFinFeeReceipts(finFeeDetailService.getFinFeeReceiptsById(feeIds, type));
		}
		if (financeMain.getMandateID() != 0) {
			financeDetail.setMandate(finMandateService.getMnadateByID(financeMain.getMandateID()));
		}
		financeDetail.setFinFlagsDetails(
				getFinFlagDetailsDAO().getFinFlagsByFinRef(finReference, FinanceConstants.MODULE_NAME, type));
		financeDetail.setFinanceTaxDetail(financeTaxDetailDAO.getFinanceTaxDetail(finReference, type));
		financeDetail.setChequeHeader(finChequeHeaderService.getChequeHeaderByRef(finReference));
		/*
		 * List<LegalDetail> ligelDetailsList = legalDetailService.getLegalDetailByFinreference(finReference); if
		 * (CollectionUtils.isNotEmpty(ligelDetailsList)) { financeDetail.setLegalDetailsList(ligelDetailsList); }
		 */

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	/**
	 * Method for Auto Rejection of Loans After X days
	 */
	@Override
	public void executeAutoFinRejectProcess() {
		logger.debug(Literal.ENTERING);

		List<FinanceMain> finances = financeMainDAO.getUnApprovedFinances();
		StepUtil.AUTO_CANCELLATION.setTotalRecords(finances.size());
		int processedRecords = 0;
		int failureRecords = 0;

		AuditDetail auditDetail = null;
		AuditHeader auditHeader = null;

		for (FinanceMain fm : finances) {
			StepUtil.AUTO_CANCELLATION.setProcessedRecords(processedRecords++);
			int maxDaysForFinRejection = fm.getAutoRejectionDays();
			Date effctiveDate = DateUtility.addDays(SysParamUtil.getAppDate(), -(maxDaysForFinRejection - 1));
			Date finStartDate = fm.getFinStartDate();
			String finReference = fm.getFinReference();

			if (((DateUtility.compare(finStartDate, effctiveDate) < 0)
					|| DateUtility.compare(finStartDate, effctiveDate) == 0)) {
				FinanceDetail financeDetail = new FinanceDetail();
				FinanceMain financeMain = new FinanceMain();

				financeMain.setFinReference(finReference);
				financeDetail.getFinScheduleData().setFinanceMain(financeMain);
				auditDetail = new AuditDetail(PennantConstants.TRAN_WF, 1, null, financeDetail);
				auditHeader = new AuditHeader(financeDetail.getFinReference(), null, null, null, auditDetail, null,
						new HashMap<String, ArrayList<ErrorDetail>>());
				try {
					doReject(auditHeader, false, true);
				} catch (Exception e) {
					StepUtil.AUTO_CANCELLATION.setProcessedRecords(failureRecords++);
					logger.error("Finance Reference for Rejection Failed : " + finReference);
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void doCheckCommodityInventory(FinanceDetail financeDetail) {
		// Cancel Finance Commodity Inventory details
	}

	/**
	 * businessValidation method do the following steps. 1) get the details from the auditHeader. 2) fetch the details
	 * from the tables 3) Validate the Record based on the record details. 4) Validate for any business validation. 5)
	 * for any mismatch conditions Fetch the error details from getFinanceMainDAO().getErrorDetail with Error ID and
	 * language as parameters. 6) if any error/Warnings then assign the to auditHeader
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * @return auditHeader
	 */
	private AuditHeader businessValidation(AuditHeader auditHeader, String method, boolean isWIF,
			boolean isAutoReject) {
		logger.debug(Literal.ENTERING);

		List<AuditDetail> auditDetails = new ArrayList<AuditDetail>();
		AuditDetail auditDetail = validation(auditHeader.getAuditDetail(), auditHeader.getUsrLanguage(), method, isWIF);
		doPostHookValidation(auditHeader, isWIF);

		String auditTranType = auditHeader.getAuditTranType();
		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String usrLanguage = PennantConstants.default_Language;
		if (financeMain.getUserDetails() == null) {
			financeMain.setUserDetails(new LoggedInUser());
			usrLanguage = financeMain.getUserDetails().getLanguage();
		}

		auditHeader.setAuditDetail(auditDetail);
		auditHeader.setErrorList(auditDetail.getErrorDetails());

		// Additional validations for CovanentTypes
		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();
		if (!isWIF) {
			auditHeader = getAuditDetails(auditHeader, method);
			if (!isAutoReject) {
				errorDetails = covValidations(auditHeader);
			}
		}

		// =======================================
		if (errorDetails != null) {
			errorDetails = ErrorUtil.getErrorDetails(errorDetails, auditHeader.getUsrLanguage());
			auditHeader.setErrorList(errorDetails);
		}

		// Finance Insurance Details
		// =======================================
		if (financeDetail.getFinScheduleData().getFinInsuranceList() != null
				&& financeDetail.getFinScheduleData().getFinInsuranceList().size() > 0) {
			financeDetail.getAuditDetailMap().put("FinInsuranceDetails",
					setInsuranceDetailsAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(financeDetail.getAuditDetailMap().get("FinInsuranceDetails"));
		}
		// Finance vas recording
		List<VASRecording> vasRecordingList = financeDetail.getFinScheduleData().getVasRecordingList();
		if (vasRecordingList != null && !vasRecordingList.isEmpty()) {
			financeDetail.getAuditDetailMap().put("VasRecordings",
					setVasAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(financeDetail.getAuditDetailMap().get("VasRecordings"));

			List<AuditDetail> details = new ArrayList<AuditDetail>();
			for (int i = 0; i < vasRecordingList.size(); i++) {
				VASRecording recording = vasRecordingList.get(i);
				ExtendedFieldRender extendedFieldRender = recording.getExtendedFieldRender();
				extendedFieldRender.setTypeCode(recording.getProductCode());
				extendedFieldRender.setTypeCodeDesc(recording.getProductDesc());
				extendedFieldRender.setReference(recording.getVasReference());
				extendedFieldRender.setWorkflowId(financeDetail.getFinScheduleData().getFinanceMain().getWorkflowId());
				if (extendedFieldDetailsService.setExtendedFieldAuditData(extendedFieldRender, auditTranType, method,
						i + 1, null) != null) {
					details.add(extendedFieldDetailsService.setExtendedFieldAuditData(extendedFieldRender,
							auditTranType, method, i + 1, null));
				}

			}
			financeDetail.getAuditDetailMap().put("VasExtendedDetails", details);
			auditDetails.addAll(financeDetail.getAuditDetailMap().get("VasExtendedDetails"));

		}

		if (!isWIF && !financeDetail.isExtSource()) {

			String rcdType = financeMain.getRecordType();
			if (!financeDetail.isLovDescIsQDE() && rcdType.equals(PennantConstants.RECORD_TYPE_NEW)) {

				// Customer Details Validation
				// =======================================
				if (financeDetail.getCustomerDetails() != null && !isAutoReject) {
					financeDetail.getCustomerDetails().setUserDetails(financeDetail.getUserDetails());
					auditDetails.addAll(getCustomerDetailsService().validate(financeDetail.getCustomerDetails(),
							financeMain.getWorkflowId(), method, usrLanguage));
				}

				// Contractor Asset Details
				// =======================================
				List<ContractorAssetDetail> contractorAssetDetails = financeDetail.getContractorAssetDetails();
				if (contractorAssetDetails != null && !contractorAssetDetails.isEmpty()) {
					auditDetails.addAll(getContractorAssetDetailService().validate(financeDetail, method, usrLanguage));
				}
			}

			// Cheque Header Details (TODO : Temporary Validation addition)
			// =======================================
			if (financeDetail.getChequeHeader() != null) {
				auditDetail = finChequeHeaderService.validation(auditDetail, usrLanguage);
				auditHeader.setAuditDetail(auditDetail);
				auditHeader.setErrorList(auditDetail.getErrorDetails());
			}

			// Finance Check List Details
			// =======================================
			List<FinanceCheckListReference> financeCheckList = financeDetail.getFinanceCheckList();
			if (financeCheckList != null && !financeCheckList.isEmpty()) {
				auditDetails.addAll(checkListDetailService
						.validate(financeDetail.getAuditDetailMap().get("checkListDetails"), method, usrLanguage));
			}

			// Guaranteer Details Validation
			// =======================================
			List<GuarantorDetail> gurantorsDetailList = financeDetail.getGurantorsDetailList();
			if (gurantorsDetailList != null && !gurantorsDetailList.isEmpty()) {
				// 10-Jul-2018 BUG FIX related to TktNo:127415
				List<AuditDetail> details = getGuarantorDetailService().validate(gurantorsDetailList,
						financeMain.getWorkflowId(), method, auditTranType, usrLanguage);
				financeDetail.getAuditDetailMap().put("Guarantors", details);
				auditDetails.addAll(details);
			}

			// Joint Account Details Validation
			// =======================================
			List<JointAccountDetail> jountAccountDetailList = financeDetail.getJountAccountDetailList();
			if (jountAccountDetailList != null && !jountAccountDetailList.isEmpty()) {
				// 10-Jul-2018 BUG FIX related to TktNo:127415
				List<AuditDetail> details = getJointAccountDetailService().validate(jountAccountDetailList,
						financeMain.getWorkflowId(), method, auditTranType, usrLanguage);
				financeDetail.getAuditDetailMap().put("JountAccountDetails", details);
				auditDetails.addAll(details);
			}

			FinanceTaxDetail taxDetail = financeDetail.getFinanceTaxDetail();

			if (taxDetail != null && !auditDetails.isEmpty()) {
				if (!financeDetail.isActionSave()) {
					long custId = taxDetail.getTaxCustId();
					String taxNumber = taxDetail.getTaxNumber();
					boolean idExist = false;

					if (custId != 0) {
						// GST Number Validation
						if (StringUtils.isNotBlank(taxNumber)) {
							getFinanceTaxDetailService().gstNumbeValidation(auditDetails.get(0), taxDetail);
						}

						if (PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT.equals(taxDetail.getApplicableFor())) {
							for (JointAccountDetail jointAccountDetail : jountAccountDetailList) {
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
			}

			// set Finance Collateral Details Audit
			// =======================================
			if (financeDetail.getFinanceCollaterals() != null && !financeDetail.getFinanceCollaterals().isEmpty()) {
				auditDetails.addAll(getFinCollateralService().validate(financeDetail.getFinanceCollaterals(),
						financeMain.getWorkflowId(), method, auditTranType, usrLanguage));
			}

			// Etihad Credit Bureau details
			// =======================================
			if (financeDetail.getEtihadCreditBureauDetail() != null) {
				financeDetail.getEtihadCreditBureauDetail().setWorkflowId(financeMain.getWorkflowId());
				auditDetails.add(getEtihadCreditBureauDetailService()
						.validate(financeDetail.getEtihadCreditBureauDetail(), method, auditTranType, usrLanguage));
			}

			// Bundled Products details
			// =======================================
			if (financeDetail.getBundledProductsDetail() != null) {
				financeDetail.getBundledProductsDetail().setWorkflowId(financeMain.getWorkflowId());
				auditDetails.add(getBundledProductsDetailService().validate(financeDetail.getBundledProductsDetail(),
						method, auditTranType, usrLanguage));
			}
			// Agreement Field details
			// =======================================
			if (financeDetail.getAgreementFieldDetails() != null) {
				financeDetail.getAgreementFieldDetails().setWorkflowId(financeMain.getWorkflowId());
				auditDetails.add(getAgreementFieldsDetailService().validate(financeDetail.getAgreementFieldDetails(),
						method, auditTranType, usrLanguage));
			}

			// Advance Payment details
			// =======================================
			if (financeDetail.getAdvancePaymentsList() != null) {
				boolean isApi = false;

				if (auditHeader.getApiHeader() != null) {
					isApi = true;
				}

				auditDetails.addAll(getFinAdvancePaymentsService().validate(financeDetail.getAdvancePaymentsList(),
						financeMain.getWorkflowId(), method, auditTranType, usrLanguage, financeDetail, isApi));
			}

			// Covenant Type details
			// =======================================
			if (financeDetail.getCovenantTypeList() != null) {
				auditDetails.addAll(getFinCovenantTypeService().validate(financeDetail.getCovenantTypeList(),
						financeMain.getWorkflowId(), method, auditTranType, usrLanguage));
				validateDisbursements(financeDetail, auditDetails);

			}

			List<Covenant> covenats = financeDetail.getCovenants();
			if (SysParamUtil.isAllowed(SMTParameterConstants.NEW_COVENANT_MODULE)
					&& CollectionUtils.isNotEmpty(covenats)) {
				auditDetails.addAll(covenantsService.validate(covenats, financeMain.getWorkflowId(), method,
						auditTranType, usrLanguage));
				//commenting the below line since we are calling  covenantsService.validateOTC(financeDetail) method for new covenant module
				//validateDisbursements(financeDetail, auditDetails);

			}

			if ((StringUtils.equals(financeDetail.getModuleDefiner(), FinanceConstants.FINSER_EVENT_ORG)
					|| FinanceConstants.FINSER_EVENT_ADDDISB.equals(financeDetail.getModuleDefiner()))
					&& !financeDetail.isActionSave()) {//skipping the validations while resubmit/cancel/back word cases

				if (SysParamUtil.isAllowed(SMTParameterConstants.NEW_COVENANT_MODULE)) {
					//Adding the audit details to display error's
					auditDetails.addAll(covenantsService.validateOTC(financeDetail));
				} else {
					validateOtcPayment(auditDetails, financeDetail);
				}

			}
			// Collateral Assignments details
			// =======================================
			if (financeDetail.getCollateralAssignmentList() != null
					&& !financeDetail.getCollateralAssignmentList().isEmpty()) {

				// Collateral Assignments Validation
				List<CollateralAssignment> assignments = financeDetail.getCollateralAssignmentList();
				if (assignments != null && !assignments.isEmpty()) {
					List<AuditDetail> details = financeDetail.getAuditDetailMap().get("CollateralAssignments");
					details = getCollateralAssignmentValidation().vaildateDetails(details, method, usrLanguage);
					auditDetails.addAll(details);
				}
			}

			// Collateral Setup details Business validations
			// ========================
			if (CollectionUtils.isNotEmpty(financeDetail.getCollaterals())) {
				auditDetails.addAll(getCollateralSetupService().validateDetails(financeDetail, auditTranType, method));
			}

			// FinAssetType Detail
			if (financeDetail.getFinAssetTypesList() != null && !financeDetail.getFinAssetTypesList().isEmpty()) {

				// FinAssetType Validation
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinAssetTypes");
				if (details != null) {
					details = getFinAssetTypesValidation().vaildateDetails(details, method, usrLanguage);
					auditDetails.addAll(details);
				}
			}
			// Extended field details Validation
			if (financeDetail.getExtendedFieldRenderList() != null
					&& !financeDetail.getExtendedFieldRenderList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("ExtendedFieldDetails");

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
						auditDetails.add(assetDetail);
					}
				}
			}

			// Asset Evaluation details
			// =======================================
			if (financeDetail.getFinAssetEvaluation() != null) {
				financeDetail.getFinAssetEvaluation().setWorkflowId(financeMain.getWorkflowId());
				auditDetails.add(getFinAssetEvaluationService().validate(financeDetail.getFinAssetEvaluation(), method,
						auditTranType, usrLanguage));
			}

			// PSL details
			// =======================================
			if (financeDetail.getPslDetail() != null) {
				financeDetail.getPslDetail().setWorkflowId(financeMain.getWorkflowId());
				auditDetails.add(getpSLDetailService().validate(financeDetail.getPslDetail(), method, auditTranType,
						usrLanguage));
			}

			// Vas Recording Details
			if (financeDetail.getFinScheduleData().getVasRecordingList() != null
					&& !financeDetail.getFinScheduleData().getVasRecordingList().isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("VasRecordings");
				if (details != null) {
					details = getVasRecordingValidation().vaildateDetails(details, method, usrLanguage);
					auditDetails.addAll(details);
				}

				// Extended field details Validation
				List<AuditDetail> vasExtDetails = financeDetail.getAuditDetailMap().get("VasExtendedDetails");
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
			List<FinFlagsDetail> finFlagsDetailList = financeDetail.getFinFlagsDetails();
			if (finFlagsDetailList != null && !finFlagsDetailList.isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinFlagsDetail");
				details = getFlagDetailValidation().vaildateDetails(details, method, usrLanguage);
				auditDetails.addAll(details);
			}

			// Legal details from loan business validation
			// ========================
			if (CollectionUtils.isNotEmpty(financeDetail.getLegalDetailsList())) {
				auditDetails
						.addAll(getLegalDetailService().validateDetailsFromLoan(financeDetail, auditTranType, method));
			}
		}

		// Finance Fee details
		if (!financeDetail.isExtSource()) {
			List<FinFeeDetail> finFeeDeatailsList = financeDetail.getFinScheduleData().getFinFeeDetailList();

			if (finFeeDeatailsList != null) {
				List<AuditDetail> auditDetailsList = getFinFeeDetailService().validate(
						financeDetail.getFinScheduleData().getFinFeeDetailList(), financeMain.getWorkflowId(), method,
						auditTranType, usrLanguage, isWIF);

				auditDetails.addAll(auditDetailsList);

				if (StringUtils.isNotBlank(financeMain.getWifReference())
						&& !StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)
						&& (StringUtils.isBlank(financeDetail.getModuleDefiner())
								|| FinanceConstants.FINSER_EVENT_ORG.equals(financeDetail.getModuleDefiner()))) {
					List<FinTypeFees> finTypeFeesList = financeDetail.getFinTypeFeesList();
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

		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_ORG, financeDetail.getModuleDefiner())) {
			// Finance Fee Receipts
			if (financeDetail.getFinScheduleData().getFinFeeReceipts() == null) {
				financeDetail.getFinScheduleData().setFinFeeReceipts(new ArrayList<FinFeeReceipt>());
			}
			auditDetails.addAll(getFinFeeDetailService().validateFinFeeReceipts(financeDetail,
					financeMain.getWorkflowId(), method, auditTranType, usrLanguage, auditDetails));
		}

		// Finance Insurance details
		// =======================================
		if (financeDetail.getFinScheduleData().getFinInsuranceList() != null
				&& !financeDetail.getFinScheduleData().getFinInsuranceList().isEmpty()) {
			List<FinInsurances> finInsurance = financeDetail.getFinScheduleData().getFinInsuranceList();

			if (finInsurance != null && !finInsurance.isEmpty()) {
				List<AuditDetail> details = financeDetail.getAuditDetailMap().get("FinInsuranceDetails");
				details = getFinInsuranceValidation().vaildateDetails(details, method, usrLanguage);
				auditDetails.addAll(details);
			}
		}

		// Extended field details Validation
		if (financeDetail.getExtendedFieldRender() != null) {
			List<AuditDetail> details = financeDetail.getAuditDetailMap().get("LoanExtendedFieldDetails");
			ExtendedFieldHeader extendedFieldHeader = financeDetail.getExtendedFieldHeader();
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
			if ("saveOrUpdate".equals(method) && isForwardCase(financeMain)) {
				String finReference = financeMain.getFinReference();
				String currentRole = financeMain.getRoleCode();
				List<QueryDetail> qrysList = getQueryDetailService().getUnClosedQurysForGivenRole(finReference,
						currentRole);
				if (CollectionUtils.isNotEmpty(qrysList)) {
					String[] errParm = new String[1];
					String[] valueParm = new String[1];
					valueParm[0] = financeMain.getFinReference();
					errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ": " + valueParm[0];
					List<ErrorDetail> errorDetailsList = new ArrayList<ErrorDetail>(1);
					ErrorDetail errorDetail = ErrorUtil.getErrorDetail(ErrorUtil
							.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "Q003", null, null), "EN"));
					errorDetailsList.add(errorDetail);
					auditHeader.setErrorList(errorDetailsList);
				}
			}
		}

		//OCR Details Validation

		if (financeMain.isFinOcrRequired()) {
			if (financeDetail.getFinOCRHeader() != null) {
				FinOCRHeader finOCRHeader = financeDetail.getFinOCRHeader();
				AuditDetail finOCRHeaderAudit = new AuditDetail(auditTranType, 1, finOCRHeader.getBefImage(),
						finOCRHeader);
				auditDetails.add(finOCRHeaderService.validate(finOCRHeaderAudit, auditTranType, method));
			}
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

			if (errorDetails != null) {
				errorDetails = ErrorUtil.getErrorDetails(errorDetails, auditHeader.getUsrLanguage());
				auditHeader.setErrorList(errorDetails);
			}
		}
	}

	// ### 22-06-2018 -End

	/*
	 * validates to allow for disbursements in case of Otc or PDD
	 */
	private List<AuditDetail> validateDisbursements(FinanceDetail financeDetail, List<AuditDetail> auditDetails) {
		FinanceMain financemain = financeDetail.getFinScheduleData().getFinanceMain();
		if (FinanceConstants.FINSER_EVENT_ADDDISB.equals(financeDetail.getModuleDefiner())
				&& financemain.getRecordStatus().equals(PennantConstants.RCD_STATUS_SUBMITTED)) {
			validateOtcPayment(auditDetails, financeDetail);

		}
		return auditDetails;
	}

	/**
	 * Method for Validate Finance Object
	 * 
	 * @param auditDetail
	 * @param usrLanguage
	 * @param method
	 * @param isWIF
	 * @return
	 */
	private AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method, boolean isWIF) {
		logger.debug(Literal.ENTERING);

		FinanceDetail financeDetail = (FinanceDetail) auditDetail.getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String[] errParm = new String[1];
		String[] valueParm = new String[1];
		valueParm[0] = financeMain.getId();
		errParm[0] = PennantJavaUtil.getLabel("label_FinReference") + ": " + valueParm[0];

		// Checking , if Customer is in EOD process or not. if Yes, not allowed
		// to do an action
		if (!StringUtils.equals(PennantConstants.RECORD_TYPE_NEW, financeMain.getRecordType())) {
			int eodProgressCount = getCustomerQueuingDAO().getProgressCountByCust(financeMain.getCustID());

			// If Customer Exists in EOD Processing, Not allowed to Maintenance
			// till completion
			if (eodProgressCount > 0) {
				auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
						new ErrorDetail(PennantConstants.KEY_FIELD, "60203", errParm, valueParm), usrLanguage));
			}
		}

		if (StringUtils.equals(PennantConstants.RECORD_TYPE_NEW, financeMain.getRecordType()) && !isWIF
				&& ((financeMain.getRecordStatus().contains(PennantConstants.RCD_STATUS_SUBMITTED)
						|| financeMain.getRecordStatus().contains(PennantConstants.RCD_STATUS_SAVED))
						|| (StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)))) {
			String befAppNo = getFinanceMainDAO().getApplicationNoById(financeMain.getFinReference(), "_Temp");
			if (befAppNo == null || !StringUtils.equals(befAppNo, financeMain.getApplicationNo())) {
				if (StringUtils.isNotBlank(financeMain.getApplicationNo())) {
					boolean isDuplicate = getFinanceMainDAO().isAppNoExists(financeMain.getApplicationNo(),
							TableType.BOTH_TAB);
					if (isDuplicate) {
						String[] errParmFinMain = new String[1];
						String[] valueParmFinMain = new String[1];
						valueParmFinMain[0] = financeMain.getApplicationNo();
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
					&& !financeMain.getRecordStatus().contains(PennantConstants.RCD_STATUS_RESUBMITTED)
					&& !financeMain.getRecordStatus().contains(PennantConstants.RCD_STATUS_SAVED)
					&& !financeMain.getRecordStatus().equals(PennantConstants.RCD_STATUS_DECLINED)) {

				// Eligibility Details
				// =======================================
				// FIX ME: Eligibility Rules should be executed before coming
				// here
				if (!StringUtils.equals("MoveLoanStage", financeMain.getServiceName())) {
					getEligibilityDetailService().validate(financeDetail.getElgRuleList(), auditDetail, errParm,
							valueParm, usrLanguage);
				}

				// Scoring Details
				// =======================================
				getScoringDetailService().validate(financeDetail, auditDetail, errParm, valueParm, usrLanguage);

				// Collateral Details
				FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
				if (financeType.isFinCollateralReq() && (!financeType.isPartiallySecured())
						&& ImplementationConstants.COLLATERAL_INTERNAL
						&& financeDetail.getCollateralAssignmentList() != null
						&& !financeDetail.getCollateralAssignmentList().isEmpty()) {
					BigDecimal totAssignAmt = BigDecimal.ZERO;
					for (CollateralAssignment collateralAssignment : financeDetail.getCollateralAssignmentList()) {
						totAssignAmt = totAssignAmt.add(
								collateralAssignment.getCollateralValue().multiply(collateralAssignment.getAssignPerc())
										.divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN));
					}
					if (totAssignAmt.compareTo(financeMain.getFinAmount()) < 0) {
						int foramtter = CurrencyUtil.getFormat(financeMain.getFinCcy());
						String[] errParmCollateral = new String[2];
						String[] valueParmCollateral = new String[2];
						valueParmCollateral[0] = String
								.valueOf(PennantApplicationUtil.amountFormate(totAssignAmt, foramtter));
						valueParmCollateral[1] = String
								.valueOf(PennantApplicationUtil.amountFormate(financeMain.getFinAmount(), foramtter));
						errParmCollateral[0] = PennantJavaUtil.getLabel("label_CollateralAmount") + ":"
								+ valueParmCollateral[0];
						errParmCollateral[1] = PennantJavaUtil.getLabel("label_FinanceAmount") + ":"
								+ valueParmCollateral[1];
						auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD,
								"65012", errParmCollateral, valueParmCollateral), usrLanguage));
					}
				}
			}

			// Finance Insurance Validation
			// validate the Insurances against the finance Type having list of
			// configured insurances
			List<String> mandPolicyList;
			if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
				mandPolicyList = getFinTypeInsuranceDAO().getFinTypeInsurances(financeMain.getPromotionCode(),
						FinanceConstants.MODULEID_PROMOTION);
			} else {
				mandPolicyList = getFinTypeInsuranceDAO().getFinTypeInsurances(financeMain.getFinType(),
						FinanceConstants.MODULEID_FINTYPE);
			}

			if (mandPolicyList != null && !mandPolicyList.isEmpty()) {

				boolean notValidEntry = false;
				String mandPolicy = "";
				for (int i = 0; i < mandPolicyList.size(); i++) {

					List<FinInsurances> finInsurances = financeDetail.getFinScheduleData().getFinInsuranceList();
					mandPolicy = mandPolicyList.get(i);
					if (finInsurances != null && !finInsurances.isEmpty()) {

						boolean isMandRcdFound = false;
						for (int j = 0; j < finInsurances.size(); j++) {
							FinInsurances finInsurance = finInsurances.get(j);
							if (StringUtils.equals(finInsurance.getPolicyCode(), mandPolicy)) {
								isMandRcdFound = true;
								break;
							}
						}

						if (!isMandRcdFound) {
							notValidEntry = true;
							break;
						}

					} else {
						notValidEntry = true;
						break;
					}
				}

				if (notValidEntry) {

					String[] errParmInsurance = new String[2];
					String[] valueParmInsurace = new String[2];
					valueParmInsurace[0] = financeDetail.getFinScheduleData().getFinanceMain().getFinType();
					valueParmInsurace[1] = mandPolicy;

					errParmInsurance[0] = PennantJavaUtil.getLabel("label_FinType") + ":" + valueParmInsurace[0];
					errParmInsurance[1] = PennantJavaUtil.getLabel("label_InsurancePolicy") + ":"
							+ valueParmInsurace[1];
					auditDetail.setErrorDetail(ErrorUtil.getErrorDetail(
							new ErrorDetail(PennantConstants.KEY_FIELD, "91132", errParmInsurance, valueParmInsurace),
							usrLanguage));
				}
			}
		}

		getFinMandateService().validateMandate(auditDetail, financeDetail);
		if (!StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)) {
			// getFinMandateService().promptMandate(auditDetail,
			// financeDetail);//FIXME: Override issue to be fixed
		}

		// Drawing power validations.
		auditDetail = this.drawingPowerService.validate(auditDetail, financeDetail);

		auditDetail.setErrorDetails(ErrorUtil.getErrorDetails(auditDetail.getErrorDetails(), usrLanguage));

		if ("doApprove".equals(method) && !PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())) {
			auditDetail.setBefImage(getFinanceMainDAO().getFinanceMainById(financeMain.getId(), "", isWIF));
		}
		if (financeDetail.isTvApprovalTab()
				&& !StringUtils.contains(financeMain.getRecordStatus(), (PennantConstants.RCD_STATUS_SAVED))
				&& !StringUtils.contains(financeMain.getRecordStatus(), (PennantConstants.RCD_STATUS_RESUBMITTED))
				&& !StringUtils.contains(financeMain.getRecordStatus(), (PennantConstants.RCD_STATUS_REJECTED))
				&& !StringUtils.contains(financeMain.getRecordStatus(), (PennantConstants.RCD_STATUS_CANCELLED))) {
			/*
			 * AuditDetail aAuditDetail = technicalVerificationService.validateTVCount(financeDetail); if
			 * (CollectionUtils.isNotEmpty(aAuditDetail.getErrorDetails())) {
			 * auditDetail.setErrorDetail(aAuditDetail.getErrorDetails().get(0)) ; }
			 */
		}

		return auditDetail;
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
		HashMap<String, List<AuditDetail>> auditDetailMap = new HashMap<String, List<AuditDetail>>();

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		String auditTranType = "";
		if ("saveOrUpdate".equals(method) || "doApprove".equals(method) || "doReject".equals(method)) {
			if (financeMain.isWorkflow()) {
				auditTranType = PennantConstants.TRAN_WF;
			}
		}

		// Finance Flag details
		if (financeDetail.getFinFlagsDetails() != null && financeDetail.getFinFlagsDetails().size() > 0) {
			auditDetailMap.put("FinFlagsDetail", setFinFlagAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("FinFlagsDetail"));
		}

		if (!financeDetail.isExtSource()) {

			// Finance Contribution Details
			// =======================================
			if (financeDetail.getFinContributorHeader() != null
					&& financeDetail.getFinContributorHeader().getContributorDetailList() != null
					&& financeDetail.getFinContributorHeader().getContributorDetailList().size() > 0) {
				auditDetailMap.put("Contributor",
						setContributorAuditData(financeDetail.getFinContributorHeader(), auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("Contributor"));
			}

			// Finance Document Details
			// =======================================
			if (financeDetail.getDocumentDetailsList() != null && financeDetail.getDocumentDetailsList().size() > 0) {
				auditDetailMap.put("DocumentDetails",
						setDocumentDetailsAuditData(financeDetail, auditTranType, method));
				auditDetails.addAll(auditDetailMap.get("DocumentDetails"));
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

		// Asset Type Extended Field Details
		List<ExtendedFieldRender> renderList = financeDetail.getExtendedFieldRenderList();
		if (renderList != null && !renderList.isEmpty()) {
			auditDetailMap.put("ExtendedFieldDetails",
					extendedFieldDetailsService.setExtendedFieldsAuditData(renderList, auditTranType, method, null));
			auditDetails.addAll(auditDetailMap.get("ExtendedFieldDetails"));
		}

		// Loan Field Details
		if (financeDetail.getExtendedFieldRender() != null) {
			auditDetailMap.put("LoanExtendedFieldDetails",
					extendedFieldDetailsService.setExtendedFieldsAuditData(financeDetail.getExtendedFieldHeader(),
							financeDetail.getExtendedFieldRender(), auditTranType, method,
							ExtendedFieldConstants.MODULE_LOAN));
			auditDetails.addAll(auditDetailMap.get("LoanExtendedFieldDetails"));
		}

		if (CollectionUtils.isNotEmpty(financeDetail.getFinScheduleData().getLowerTaxDeductionDetails())) {
			auditDetailMap.put("LowerTaxDeductionDetails", setLTDAuditData(financeDetail, auditTranType, method));
			auditDetails.addAll(auditDetailMap.get("LowerTaxDeductionDetails"));
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
	 * Method for Rate changes for IJARAH Finances by Applying Actual rates
	 * 
	 * @param bulkRateChangeFinances
	 * @param fromDate
	 * @param toDate
	 * @param recalType
	 * @param rateChange
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws DatatypeConfigurationException
	 * @throws AccountNotFoundException
	 */
	@Override
	public boolean bulkRateChangeFinances(List<BulkProcessDetails> bulkRateChangeFinances, String recalType,
			BigDecimal rateChange) throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		// Bulk Rate Changes applied for fetched list
		for (BulkProcessDetails rateChangeFinance : bulkRateChangeFinances) {

			// Get Total Finance Details to particular Finance
			FinanceDetail financeDetail = getFinSchdDetailById(rateChangeFinance.getFinReference(), "_AView", true);

			// Reset Before Image for Auditing
			FinanceDetail befImage = new FinanceDetail();
			BeanUtils.copyProperties(financeDetail, befImage);
			financeDetail.setBefImage(befImage);

			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

			financeMain.setEventFromDate(rateChangeFinance.getLovDescEventFromDate());
			financeMain.setEventToDate(rateChangeFinance.getLovDescEventToDate());
			financeMain.setRecalType(recalType);

			// Schedule Details Log Maintenance
			final List<FinanceScheduleDetail> oldScheduleDetails = financeDetail.getFinScheduleData()
					.getFinanceScheduleDetails();
			getFinanceScheduleDetailDAO().saveList(oldScheduleDetails, "_Log", false);

			// Schedule Re-calculation based on Applied parameters
			financeDetail.setFinScheduleData(ScheduleCalculator.changeRate(financeDetail.getFinScheduleData(), "", "",
					BigDecimal.ZERO, rateChange == null ? BigDecimal.ZERO : rateChange, true));

			// Record proceed through WorkFlow defined Process
			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_UPD, 1, financeDetail.getBefImage(),
					financeDetail);
			AuditHeader auditHeader = new AuditHeader(financeDetail.getFinScheduleData().getFinReference(), null, null,
					null, auditDetail, financeDetail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());

			// Changed Finance Save in Database
			saveOrUpdate(auditHeader, false);
		}

		logger.debug(Literal.LEAVING);
		return true;
	}

	/**
	 * Method for Processing Bulk Finance for Deferment Process
	 * 
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws DatatypeConfigurationException
	 * 
	 * @throws AccountNotFoundException
	 */
	@Override
	public boolean bulkDefermentChanges(List<BulkDefermentChange> defermentChangeFinances, String recalType,
			boolean excludeDeferment, String addTermAfter, Date calFromDate, Date calToDate)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);

		// Bulk Deferment Changes applied for fetched list
		for (BulkDefermentChange defermentFinance : defermentChangeFinances) {

			// Get Total Finance Details to particular Finance
			FinanceDetail financeDetail = getFinSchdDetailById(defermentFinance.getFinReference(), "_AView", true);

			// Reset Before Image for Auditing
			FinanceDetail befImage = new FinanceDetail();
			BeanUtils.copyProperties(financeDetail, befImage);
			financeDetail.setBefImage(befImage);

			FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

			financeMain.setEventFromDate(defermentFinance.getEventFromDate());
			financeMain.setEventToDate(defermentFinance.getEventFromDate());
			financeMain.setRecalType(recalType);
			financeMain.setRecalFromDate(calFromDate);
			financeMain.setRecalToDate(calToDate);

			// Schedule Details Log Maintenance
			final List<FinanceScheduleDetail> oldScheduleDetails = befImage.getFinScheduleData()
					.getFinanceScheduleDetails();
			getFinanceScheduleDetailDAO().saveList(oldScheduleDetails, "_Log", false);

			// Record proceed through WorkFlow defined Process
			AuditDetail auditDetail = new AuditDetail(PennantConstants.TRAN_UPD, 1, financeDetail.getBefImage(),
					financeDetail);
			AuditHeader auditHeader = new AuditHeader(financeDetail.getFinScheduleData().getFinReference(), null, null,
					null, auditDetail, financeDetail.getUserDetails(), new HashMap<String, ArrayList<ErrorDetail>>());

			// Changed Finance Save in Database
			saveOrUpdate(auditHeader, false);
		}
		logger.debug(Literal.LEAVING);
		return false;
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
			String dftCcy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);
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
			List<FinanceProfitDetail> financeProfitDetailsList = getCustomerDAO()
					.getCustFinAmtDetails(customer.getCustID(), eligibilityCheck);

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
			eligibilityCheck.setCustRepayBank(getCustomerDAO().getCustRepayBankTotal(customer.getCustID()));

			// Customer Current Processing Finance Amounts
			if (StringUtils.isNotEmpty(finReference)) {
				eligibilityCheck.setCustProcRepayBank(
						getCustomerDAO().getCustRepayProcBank(customer.getCustID(), finReference));
			}

			// Get Co-Applicants Repay Totals On Bank
			BigDecimal totalCoAppRepayBank = BigDecimal.ZERO;
			BigDecimal totalCoAppIncome = BigDecimal.ZERO;
			BigDecimal totalCoAppExpense = BigDecimal.ZERO;
			BigDecimal totalCoAppCurFinEMI = BigDecimal.ZERO;
			if (jointAccountDetails != null && !jointAccountDetails.isEmpty()) {
				for (JointAccountDetail accountDetail : jointAccountDetails) {
					FinanceExposure exposure = getCustomerDAO().getCoAppRepayBankTotal(accountDetail.getCustCIF());
					if (exposure == null) {
						continue;
					}
					totalCoAppRepayBank = totalCoAppRepayBank
							.add(PennantApplicationUtil.unFormateAmount(exposure.getCurrentExpoSure(),
									SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));

					totalCoAppIncome = totalCoAppIncome.add(
							CalculationUtil.getConvertedAmount(exposure.getFinCCY(), finCcy, exposure.getFinanceAmt()));
					totalCoAppExpense = totalCoAppExpense.add(
							CalculationUtil.getConvertedAmount(exposure.getFinCCY(), finCcy, exposure.getOverdueAmt()));
					BigDecimal curFinEMI = CalculationUtil.getConvertedAmount(exposure.getFinCCY(), finCcy,
							exposure.getCurrentExpoSureinBaseCCY());
					totalCoAppCurFinEMI = totalCoAppCurFinEMI.add(PennantApplicationUtil.unFormateAmount(curFinEMI,
							SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT)));
				}
			}
			eligibilityCheck.setCoAppRepayBank(totalCoAppRepayBank);
			eligibilityCheck.setCoAppIncome(totalCoAppIncome);
			eligibilityCheck.setCoAppExpense(totalCoAppExpense);
			eligibilityCheck.setCoAppCurFinEMI(totalCoAppCurFinEMI);

			// Get Customer Repay Totals by Other Commitments
			eligibilityCheck.setCustRepayOther(BigDecimal.ZERO);// getCustomerDAO().getCustRepayOtherTotal(customer.getCustID())

			// Get Customer Worst Status From Finances
			eligibilityCheck.setCustWorstSts(getCustomerDAO().getCustWorstSts(customer.getCustID()));

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
					.setActiveLoansOnFinType(getFinanceMainDAO().getActiveCount(finType, custID));
			detail.getCustomerEligibilityCheck()
					.setTotalLoansOnFinType(getFinanceMainDAO().getODLoanCount(finType, custID));
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
				List<FinanceProfitDetail> financeProfitDetailsList = getCustomerDAO()
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
				eligibilityCheck.setCustEmpDesg(getCustomerDAO().getCustEmpDesg(customer.getExistCustID()));
				eligibilityCheck.setCustEmpSts(customer.getCustEmpSts());

				// Get Customer Worst Status From Finances
				eligibilityCheck.setCustWorstSts(getCustomerDAO().getCustWorstSts(customer.getExistCustID()));
				eligibilityCheck.setCustTotalIncome(customer.getTotalIncome());
				eligibilityCheck.setCustTotalExpense(customer.getTotalExpense());
				// Get Customer Repay Totals On Bank
				eligibilityCheck.setCustRepayBank(getCustomerDAO().getCustRepayBankTotal(customer.getCustID()));

			} else {

				// Get Customer Worst Status From Finances
				eligibilityCheck.setCustWorstSts(getCustStatusByMinDueDays());
			}

			// DSR Calculation
			Rule rule = getRuleDAO().getRuleByID(RuleConstants.ELGRULE_DSRCAL, RuleConstants.MODULE_ELGRULE,
					RuleConstants.EVENT_ELGRULE, "");
			if (rule != null) {
				Object dscr = getRuleExecutionUtil().executeRule(rule.getSQLRule(),
						eligibilityCheck.getDeclaredFieldValues(), finCcy, RuleReturnType.DECIMAL);
				eligibilityCheck.setDSCR(PennantApplicationUtil.getDSR(dscr));
			}
		}
		logger.debug(Literal.LEAVING);
		return eligibilityCheck;
	}

	/**
	 * Method for Deletion of Contributor Details
	 * 
	 * @param finDetail
	 * @param tableType
	 * @param auditTranType
	 * @return
	 */
	private List<AuditDetail> listDeletion_FinContributor(FinanceDetail finDetail, String tableType,
			String auditTranType) {
		logger.debug("Entering ");

		List<AuditDetail> auditList = new ArrayList<AuditDetail>();
		if (finDetail.getFinContributorHeader() != null) {
			String[] fields = PennantJavaUtil.getFieldDetails(new FinContributorHeader(), "");
			FinContributorHeader contributorHeader = finDetail.getFinContributorHeader();
			auditList.add(new AuditDetail(auditTranType, 1, fields[0], fields[1], contributorHeader.getBefImage(),
					contributorHeader));

			getFinContributorHeaderDAO().delete(contributorHeader.getFinReference(), tableType);

			String[] fields1 = PennantJavaUtil.getFieldDetails(new FinContributorDetail(), "");
			if (contributorHeader.getContributorDetailList() != null
					&& contributorHeader.getContributorDetailList().size() > 0) {

				for (int i = 0; i < contributorHeader.getContributorDetailList().size(); i++) {
					FinContributorDetail contributorDetail = contributorHeader.getContributorDetailList().get(i);
					auditList.add(new AuditDetail(auditTranType, i + 1, fields1[0], fields1[1],
							contributorDetail.getBefImage(), contributorDetail));
				}
				getFinContributorDetailDAO().deleteByFinRef(
						contributorHeader.getContributorDetailList().get(0).getFinReference(), tableType);
			}
		}
		logger.debug("Leaving ");
		return auditList;
	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 **/
	public FinScheduleData getFinSchDataByFinRef(String finReference, String type, long logKey) {
		logger.debug(Literal.ENTERING);

		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, false));
		finSchData.setFinanceScheduleDetails(
				getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, false));

		// Fee Details
		finSchData.setFinFeeDetailList(getFinFeeDetailDAO().getFinFeeDetailByFinRef(finReference, false, "_View"));

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
					HashMap<Long, List<FinFeeScheduleDetail>> schFeeMap = new HashMap<>();
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
					getOverdraftScheduleDetailDAO().getOverdraftScheduleDetails(finReference, odType, false));
		}
		finSchData.setDisbursementDetails(
				getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type, false));
		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));

		if (logKey == 0) {
			finSchData.setFinanceType(
					getFinanceTypeDAO().getFinanceTypeByID(finSchData.getFinanceMain().getFinType(), "_AView"));
			finSchData = getFinMaintainenceDetails(finSchData);
			finSchData.setAccrueValue(getAccrueAmount(finReference));
		}
		logger.debug(Literal.LEAVING);
		return finSchData;
	}

	@Override
	public FinScheduleData getFinMaintainenceDetails(FinScheduleData finSchData) {
		logger.debug(Literal.ENTERING);
		String finReference = finSchData.getFinanceMain().getFinReference();
		finSchData.setRepayDetails(getFinanceRepaymentsByFinRef(finReference, false));
		finSchData.setPenaltyDetails(getFinancePenaltysByFinRef(finReference));
		logger.debug(Literal.LEAVING);
		return finSchData;
	}

	/**
	 * Method for Get the Accrue Details
	 * 
	 * @param finReference
	 * @return
	 */
	@Override
	public BigDecimal getAccrueAmount(String finReference) {
		return getProfitDetailsDAO().getAccrueAmount(finReference);
	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 **/
	public FinScheduleData getFinSchDataById(String finReference, String type, boolean summaryRequired) {
		logger.debug(Literal.ENTERING);

		FinScheduleData finSchData = new FinScheduleData();
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finReference, type, false);
		setDasAndDmaData(financeMain);
		if (financeMain == null) {
			return finSchData;
		}

		finSchData.setFinReference(financeMain.getFinReference());
		finSchData.setFinanceMain(financeMain);

		// Overdraft Schedule Details
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			finSchData.setOverdraftScheduleDetails(
					getOverdraftScheduleDetailDAO().getOverdraftScheduleDetails(finReference, "_Temp", false));
		}

		// Schedule details
		finSchData.setFinanceScheduleDetails(
				getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, false));

		// Disbursement Details
		finSchData.setDisbursementDetails(
				getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type, false));

		// Repay instructions
		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));

		// od penality details
		finSchData.setFinODPenaltyRate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference, type));

		if (summaryRequired) {

			// Finance Type
			// finSchData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByFinType(financeMain.getFinType()));
			// Finance Type Details
			FinanceType financeType = getFinanceTypeDAO().getFinanceTypeByFinType(financeMain.getFinType());
			if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
				// Fetching Promotion Details
				Promotion promotion = this.promotionDAO.getPromotionByReferenceId(financeMain.getPromotionSeqId(),
						"_AView");
				financeType.setFInTypeFromPromotiion(promotion);
			}
			finSchData.setFinanceType(financeType);

			// Suspense
			finSchData.setFinPftSuspended(false);
			FinanceSuspHead financeSuspHead = getFinanceSuspHeadDAO().getFinanceSuspHeadById(finReference, "");
			if (financeSuspHead != null && financeSuspHead.isFinIsInSusp()) {
				finSchData.setFinPftSuspended(true);
				finSchData.setFinSuspDate(financeSuspHead.getFinSuspDate());
			}

			// Finance Summary Details Preparation
			final Date curBussDate = SysParamUtil.getAppDate();
			FinanceSummary summary = new FinanceSummary();
			summary.setFinReference(financeMain.getFinReference());
			summary.setSchDate(curBussDate);

			if (financeMain.isAllowGrcPeriod() && curBussDate.compareTo(financeMain.getNextGrcPftDate()) <= 0) {
				summary.setNextSchDate(financeMain.getNextGrcPftDate());
			} else if (financeMain.getNextRepayDate().compareTo(financeMain.getNextRepayPftDate()) < 0) {
				summary.setNextSchDate(financeMain.getNextRepayDate());
			} else {
				summary.setNextSchDate(financeMain.getNextRepayPftDate());
			}

			// commented because we are fetching total fees from FinfeeDeatail
			// table
			/*
			 * summary = getFinanceScheduleDetailDAO().getFinanceSummaryDetails(summary); summary =
			 * getFinFeeDetailDAO().getTotalFeeCharges(summary);
			 */
			summary.setFinCurODDays(getProfitDetailsDAO().getCurOddays(finReference, ""));
			finSchData.setFinanceSummary(summary);

			FinODDetails finODDetails = getFinODDetailsDAO().getFinODSummary(finReference);
			if (finODDetails != null) {
				summary.setFinODTotPenaltyAmt(finODDetails.getTotPenaltyAmt());
				summary.setFinODTotWaived(finODDetails.getTotWaived());
				summary.setFinODTotPenaltyPaid(finODDetails.getTotPenaltyPaid());
				summary.setFinODTotPenaltyBal(finODDetails.getTotPenaltyBal());
			}

			if (ImplementationConstants.ALLOW_NPA_PROVISION) {
				Provision provision = provisionDAO.getProvisionById(finReference, TableType.VIEW, false);
				if (provision != null) {
					summary.setAssetCode(provision.getAssetCode());
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return finSchData;
	}

	/**
	 * Method to get Schedule related data.
	 * 
	 * @param finReference
	 *            (String)
	 * @param isWIF
	 *            (boolean)
	 **/
	@Override
	public FinScheduleData getFinSchDataForReceipt(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		FinScheduleData scheduleData = new FinScheduleData();
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finReference, type, false);
		scheduleData.setFinReference(financeMain.getFinReference());
		scheduleData.setFinanceMain(financeMain);

		// Schedule details
		scheduleData
				.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finReference, type, false));

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			scheduleData.setOverdraftScheduleDetails(
					overdraftScheduleDetailDAO.getOverdraftScheduleDetails(finReference, "", false));
		}

		FinanceMain finMain = getFinanceMainDAO().getEntityNEntityDesc(finReference, "_Aview", false);

		if (finMain != null) {
			financeMain.setEntityCode(finMain.getEntityCode());
			financeMain.setEntityDesc(finMain.getEntityDesc());
		}

		if (financeMain.isAllowSubvention()) {
			// Disbursement Details
			List<FinanceDisbursement> disbursementDetails = financeDisbursementDAO
					.getFinanceDisbursementDetails(finReference, type, false);
			scheduleData.setDisbursementDetails(disbursementDetails);
			if (CollectionUtils.isNotEmpty(disbursementDetails)) {
				for (FinanceDisbursement disbursement : disbursementDetails) {
					List<SubventionScheduleDetail> subventionScheduleDetails = subventionDetailDAO
							.getSubventionScheduleDetails(disbursement.getFinReference(), disbursement.getDisbSeq(),
									type);
					disbursement.setSubventionSchedules(subventionScheduleDetails);
				}
			}

			scheduleData.setSubventionDetail(
					subventionDetailDAO.getSubventionDetail(financeMain.getFinReference(), "_View"));
		} else {
			scheduleData.setSubventionDetail(null);
		}

		// Repay instructions
		scheduleData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));

		// Finance Disbursement Details
		scheduleData.setDisbursementDetails(
				financeDisbursementDAO.getFinanceDisbursementDetails(finReference, type, false));

		// Finance Type
		scheduleData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(financeMain.getFinType(), type));
		if (StringUtils.isNotBlank(financeMain.getPromotionCode())
				&& (financeMain.getPromotionSeqId() != null && financeMain.getPromotionSeqId() == 0)) {
			// Fetching Promotion Details
			Promotion promotion = this.promotionDAO.getPromotionByCode(financeMain.getPromotionCode(), type);
			scheduleData.getFinanceType().setFInTypeFromPromotiion(promotion);
		}

		// Fee Details
		/*
		 * scheduleData.setFinFeeDetailList(getFinFeeDetailDAO(). getFinScheduleFees(finReference, false, "_View"));
		 * 
		 * // Finance Fee Schedule Details if (scheduleData.getFinFeeDetailList() != null &&
		 * !scheduleData.getFinFeeDetailList().isEmpty()) {
		 * 
		 * List<Long> feeIDList = new ArrayList<>(); for (int i = 0; i < scheduleData.getFinFeeDetailList().size(); i++)
		 * { FinFeeDetail feeDetail = scheduleData.getFinFeeDetailList().get(i); feeDetail.setRcdVisible(false);
		 * feeIDList.add(feeDetail.getFeeID()); }
		 * 
		 * if(!feeIDList.isEmpty()){ List<FinFeeScheduleDetail> feeScheduleList =
		 * getFinFeeScheduleDetailDAO().getFeeScheduleByFinID(feeIDList, false, "");
		 * 
		 * if(feeScheduleList != null && !feeScheduleList.isEmpty()){ HashMap<Long, List<FinFeeScheduleDetail>>
		 * schFeeMap = new HashMap<>(); for (int i = 0; i < feeScheduleList.size(); i++) { FinFeeScheduleDetail schdFee
		 * = feeScheduleList.get(i);
		 * 
		 * List<FinFeeScheduleDetail> schList = new ArrayList<>(); if (schFeeMap.containsKey(schdFee.getFeeID())) {
		 * schList = schFeeMap.get(schdFee.getFeeID()); schFeeMap.remove(schdFee.getFeeID()); } schList.add(schdFee);
		 * schFeeMap.put(schdFee.getFeeID(), schList);
		 * 
		 * }
		 * 
		 * for (int i = 0; i < scheduleData.getFinFeeDetailList().size(); i++) { FinFeeDetail feeDetail =
		 * scheduleData.getFinFeeDetailList().get(i); if (schFeeMap.containsKey(feeDetail.getFeeID())) {
		 * feeDetail.setFinFeeScheduleDetailList(schFeeMap.get(feeDetail. getFeeID())); } } } } }
		 */

		List<FinFeeDetail> fees = finFeeDetailDAO.getFinScheduleFees(finReference, false, "_View");
		scheduleData.setFinFeeDetailList(fees);

		FinanceProfitDetail profitDetail = profitDetailsDAO.getFinProfitDetailsById(finReference);
		scheduleData.setFinPftDeatil(profitDetail);

		// Finance Fee Schedule Details
		List<Long> feeIDList = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(fees)) {
			for (FinFeeDetail fee : fees) {
				if (StringUtils.equals(fee.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)
						|| StringUtils.equals(fee.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)
						|| StringUtils.equals(fee.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)) {
					feeIDList.add(fee.getFeeID());
				}
			}

			List<FinFeeScheduleDetail> feeSchedules;
			if (CollectionUtils.isNotEmpty(feeIDList)) {
				feeSchedules = finFeeScheduleDetailDAO.getFeeScheduleByFinID(feeIDList, false, "");

				if (CollectionUtils.isNotEmpty(feeSchedules)) {
					HashMap<Long, List<FinFeeScheduleDetail>> schFeeMap = new HashMap<>();
					for (FinFeeScheduleDetail schdFee : feeSchedules) {
						List<FinFeeScheduleDetail> schList = new ArrayList<>();
						if (schFeeMap.containsKey(schdFee.getFeeID())) {
							schList = schFeeMap.get(schdFee.getFeeID());
							schFeeMap.remove(schdFee.getFeeID());
						}
						schList.add(schdFee);
						schFeeMap.put(schdFee.getFeeID(), schList);

					}

					for (FinFeeDetail fee : fees) {
						if (schFeeMap.containsKey(fee.getFeeID())) {
							fee.setFinFeeScheduleDetailList(schFeeMap.get(fee.getFeeID()));
						}
					}
				}
			}
		}

		// Insurance Details
		if (ImplementationConstants.ALLOW_INSURANCE) {

			scheduleData
					.setFinInsuranceList(getFinInsurancesDAO().getFinInsuranceListByRef(finReference, "_AView", false));

			// FinSchFrqInsurance Details
			if (scheduleData.getFinInsuranceList() != null && !scheduleData.getFinInsuranceList().isEmpty()) {

				List<FinSchFrqInsurance> finSchFrqInsurances = getFinInsurancesDAO()
						.getFinSchFrqInsuranceFinRef(finReference, false, "_AView");

				if (finSchFrqInsurances != null && !finSchFrqInsurances.isEmpty()) {

					HashMap<Long, List<FinSchFrqInsurance>> schInsMap = new HashMap<>();
					for (int i = 0; i < finSchFrqInsurances.size(); i++) {
						FinSchFrqInsurance finSchFrqInsurance = finSchFrqInsurances.get(i);

						List<FinSchFrqInsurance> schList = new ArrayList<>();
						if (schInsMap.containsKey(finSchFrqInsurance.getInsId())) {
							schList = schInsMap.get(finSchFrqInsurance.getInsId());
							schInsMap.remove(finSchFrqInsurance.getInsId());
						}
						schList.add(finSchFrqInsurance);
						schInsMap.put(finSchFrqInsurance.getInsId(), schList);

					}

					for (int i = 0; i < scheduleData.getFinInsuranceList().size(); i++) {
						FinInsurances finInsurance = scheduleData.getFinInsuranceList().get(i);
						if (schInsMap.containsKey(finInsurance.getInsId())) {
							finInsurance.setFinSchFrqInsurances(schInsMap.get(finInsurance.getInsId()));
						}
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
		return scheduleData;
	}

	/**
	 * Method for Fetching Profit Details for Particular Finance Reference
	 * 
	 * @param finReference
	 * @return
	 */
	public FinanceProfitDetail getFinProfitDetailsById(String finReference) {
		return getProfitDetailsDAO().getFinProfitDetailsById(finReference);
	}

	/**
	 * Method for Fetching Profit Details
	 */
	public FinanceSummary getFinanceProfitDetails(String finRef) {
		return getFinanceMainDAO().getFinanceProfitDetails(finRef);
	}

	/**
	 * Method for getting Finance Contributor Header Details
	 */
	public FinContributorHeader getFinContributorHeaderById(String finReference) {
		logger.debug(Literal.ENTERING);
		FinContributorHeader header = getFinContributorHeaderDAO().getFinContributorHeaderById(finReference, "_AView");
		if (header != null) {
			header.setContributorDetailList(
					getFinContributorDetailDAO().getFinContributorDetailByFinRef(finReference, "_AView"));
		}
		logger.debug(Literal.LEAVING);
		return header;
	}

	@Override
	public List<ReturnDataSet> getPostingsByFinRefAndEvent(String finReference, String finEvent, boolean showZeroBal,
			String postingGroupBy, String type) {
		return getPostingsDAO().getPostingsByFinRefAndEvent(finReference, finEvent, showZeroBal, postingGroupBy, type);
	}

	@Override
	public List<ReturnDataSet> getInsurancePostings(String finReference) {
		return getPostingsDAO().getPostingsByFinRef(finReference);
	}

	/**
	 * Method for fetching list of entries executed based on Linked Transaction ID
	 */
	@Override
	public List<ReturnDataSet> getPostingsByLinkTransId(long linkedTranid) {
		return getPostingsDAO().getPostingsByLinkTransId(linkedTranid);
	}

	/**
	 * Method to CheckLimits
	 * 
	 * @param AuditHeader
	 * 
	 *            1. Check limit category exists or not for the account type, if not exists set limitValid = true other
	 *            wise goto next step. 2. Fetch customer limits from core banking. 3. If the limits not available set
	 *            the ErrMessage. 4. If available limit is less than finance amount, set warning message if the user
	 *            have the permission 'override Limits' otherwise set Error message.
	 * 
	 */
	public AuditHeader doCheckLimits(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		CustomerLimit custLimit = null;
		String[] errParm = new String[2];
		String[] valueParm = new String[2];

		List<CustomerLimit> list = null;

		FinanceDetail finDetails = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain financeMain = finDetails.getFinScheduleData().getFinanceMain();
		AccountType accountType = getAccountTypeDAO()
				.getAccountTypeById(finDetails.getFinScheduleData().getFinanceType().getFinAcType(), "");

		if (StringUtils.isNotBlank(accountType.getAcLmtCategory())) {
			custLimit = new CustomerLimit();
			custLimit.setCustMnemonic(financeMain.getLovDescCustCIF());
			custLimit.setLimitCategory(accountType.getAcLmtCategory());
			custLimit.setCustLocation(" ");
		} else {
			financeMain.setLimitValid(true);
			auditHeader.getAuditDetail().setModelData(finDetails);
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		try {
			list = getCustLimitIntefaceService().fetchLimitDetails(custLimit);
		} catch (Exception e) {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, e.getMessage(), null));
			logger.debug("Exception: ", e);
			logger.debug(Literal.LEAVING);
			return auditHeader;
		}

		if (list == null || list.size() == 0) {

			valueParm[0] = accountType.getAcLmtCategory();
			errParm[0] = PennantJavaUtil.getLabel("label_LimtCategorys") + ":" + valueParm[0];

			valueParm[1] = financeMain.getLovDescCustCIF();
			errParm[1] = "For " + PennantJavaUtil.getLabel("label_IdCustID") + ":" + valueParm[1];

			auditHeader.setErrorDetails(ErrorUtil.getErrorDetail(new ErrorDetail("Limit", "41002", errParm, valueParm),
					finDetails.getUserDetails().getLanguage()));
			logger.debug(Literal.LEAVING);
			return auditHeader;

		} else {

			for (CustomerLimit customerLimit : list) {

				if (customerLimit.getLimitAmount() == null || customerLimit.getLimitAmount().equals(BigDecimal.ZERO)) {

					valueParm[0] = customerLimit.getLimitCategory();
					errParm[0] = PennantJavaUtil.getLabel("label_LimtCategory") + ":" + valueParm[0];
					valueParm[1] = financeMain.getLovDescCustCIF();
					errParm[1] = "For " + PennantJavaUtil.getLabel("label_IdCustID") + ":" + valueParm[1];

					auditHeader.setErrorDetails(
							ErrorUtil.getErrorDetail(new ErrorDetail("Limit", "41002", errParm, valueParm),
									finDetails.getUserDetails().getLanguage()));

					return auditHeader;

				} else {

					Currency fCurrency = CurrencyUtil.getCurrencyObject(financeMain.getFinCcy());
					BigDecimal finAmount = calculateExchangeRate(financeMain.getFinAmount(), fCurrency);

					Currency lCurrency = null;
					if (!StringUtils.trimToEmpty(customerLimit.getLimitCurrency()).equals(fCurrency.getCcyCode())) {
						lCurrency = CurrencyUtil.getCurrencyObject(customerLimit.getLimitCurrency());
					} else {
						lCurrency = fCurrency;
					}

					BigDecimal availAmount = calculateExchangeRate(customerLimit.getAvailAmount(), lCurrency);

					if (availAmount != null && availAmount.compareTo(finAmount) < 0) {
						int dftFormatter = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);

						valueParm[0] = PennantApplicationUtil.amountFormate(financeMain.getFinAmount(),
								lCurrency.getCcyEditField() == 0 ? dftFormatter : lCurrency.getCcyEditField());
						errParm[0] = valueParm[0];

						valueParm[1] = PennantApplicationUtil.amountFormate(customerLimit.getLimitAmount(),
								fCurrency.getCcyEditField() == 0 ? dftFormatter : fCurrency.getCcyEditField());
						errParm[1] = valueParm[1];

						String errorCode = "30532";
						if (finDetails.getFinScheduleData().getFinanceType().isOverrideLimit()) {
							errorCode = "65006";
						}

						auditHeader.setErrorDetails(
								ErrorUtil.getErrorDetail(new ErrorDetail("Limit", errorCode, errParm, valueParm),
										finDetails.getUserDetails().getLanguage()));
						logger.debug(Literal.LEAVING);
						return auditHeader;
					}
				}
			}
			financeMain.setLimitValid(true);
			auditHeader.getAuditDetail().setModelData(finDetails);
		}
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

		String custCRCPR = getCustomerDAO().getCustCRCPRById(custId, "");
		if (StringUtils.isNotBlank(custCRCPR)) {
			Date blackListDate = getCustomerDAO().getCustBlackListedDate(custCRCPR, "");
			if (blackListDate != null) {
				logger.debug(Literal.LEAVING);
				return true;
			}
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	/**
	 * Method for Filling Amounts in USD & BHD Format to FinanceMain Object
	 * 
	 * @param financeMain
	 * @return
	 */
	@Override
	public FinanceMain fetchConvertedAmounts(FinanceMain financeMain, boolean calAllAmounts) {
		logger.debug(Literal.ENTERING);
		String dftCcy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);

		int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
		if (calAllAmounts) {
			if (dftCcy.equals(financeMain.getFinCcy())) {
				financeMain.setAmountBD(PennantApplicationUtil.formateAmount(financeMain.getFinAmount(), formatter));
			} else {
				Currency fCurrency = CurrencyUtil.getCurrencyObject(financeMain.getFinCcy());
				BigDecimal actualAmount = calculateExchangeRate(financeMain.getFinAmount(), fCurrency);
				financeMain.setAmountBD(actualAmount);
			}
		}

		if ("USD".equals(financeMain.getFinCcy())) {
			financeMain.setAmountUSD(PennantApplicationUtil.formateAmount(financeMain.getFinAmount(), formatter));
		} else {
			Currency fCurrency = CurrencyUtil.getCurrencyObject("USD");
			BigDecimal actualAmount = calculateExchangeRate(financeMain.getFinAmount(), fCurrency);
			financeMain.setAmountUSD(actualAmount);
		}

		logger.debug(Literal.LEAVING);
		return financeMain;
	}

	public List<ErrorDetail> getDiscrepancies(FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		List<ErrorDetail> errorDetails = new ArrayList<ErrorDetail>();

		long oDDays = getFinODDetailsDAO()
				.checkCustPastDue(financeDetail.getFinScheduleData().getFinanceMain().getCustID());
		int allowedDays = SysParamUtil.getValueAsInt("MAX_ALLOW_ODDAYS");
		if (oDDays > 0) {
			if (oDDays <= allowedDays) {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "60201",
						new String[] { String.valueOf(oDDays) }, null), ""));
			} else {
				errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "60202",
						new String[] { String.valueOf(oDDays) }, null), ""));
			}
		}

		// Check Limit Status of Customer
		CustomerLimit custLimit = null;
		List<CustomerLimit> customerLimitList = null;
		AccountType accountType = getAccountTypeDAO()
				.getAccountTypeById(financeDetail.getFinScheduleData().getFinanceType().getFinAcType(), "");
		if (StringUtils.isNotBlank(accountType.getAcLmtCategory())) {
			custLimit = new CustomerLimit();
			custLimit.setCustMnemonic(financeDetail.getFinScheduleData().getFinanceMain().getLovDescCustCIF());
			custLimit.setLimitCategory(accountType.getAcLmtCategory());
			custLimit.setCustLocation(" ");
		} else {
			logger.debug(Literal.LEAVING);
			return errorDetails;
		}

		try {
			customerLimitList = getCustLimitIntefaceService().fetchLimitDetails(custLimit);
		} catch (Exception e) {
			logger.debug("Exception: ", e);
			logger.debug(Literal.LEAVING);
			return errorDetails;
		}

		if (customerLimitList != null && customerLimitList.size() > 0) {
			Date curBussDate = SysParamUtil.getAppDate();
			String limitType = "";

			BigDecimal finAmount = financeDetail.getFinScheduleData().getFinanceMain().getFinAmount();
			BigDecimal downpaybank = financeDetail.getFinScheduleData().getFinanceMain().getDownPayBank();
			BigDecimal downpaySuppl = financeDetail.getFinScheduleData().getFinanceMain().getDownPaySupl();
			finAmount = finAmount.subtract(downpaybank == null ? BigDecimal.ZERO : downpaybank)
					.subtract(downpaySuppl == null ? BigDecimal.ZERO : downpaySuppl);
			BigDecimal calcFinAmount = BigDecimal.ZERO;
			for (CustomerLimit limit : customerLimitList) {
				if (StringUtils.isNotEmpty(limit.getCustCountry())) {
					limitType = " ( " + limit.getCustCountry() + " - " + limit.getCustCountryDesc() + " )";
				} else if (StringUtils.isNotEmpty(limit.getCustGrpCode())) {
					limitType = " ( " + limit.getCustGrpCode() + " - " + limit.getCustGrpDesc() + " ) ";
				} else {
					limitType = "Customer";
				}

				if (StringUtils.isEmpty(limit.getLimitCategory())) {
					if (StringUtils.isNotEmpty(limit.getCustGrpCode())) {
						errorDetails.add(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "60304", new String[] { limitType }, null),
								""));
					} else if (StringUtils.isNotEmpty(limit.getCustCountry())) {
						errorDetails.add(ErrorUtil.getErrorDetail(
								new ErrorDetail(PennantConstants.KEY_FIELD, "60307", new String[] { limitType }, null),
								""));
					} else {
						errorDetails.add(ErrorUtil
								.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "60301", null, null), ""));
					}
					continue;
				}

				if (limit.getLimitExpiry() != null && limit.getLimitExpiry().compareTo(curBussDate) < 0) {

					if (StringUtils.isNotEmpty(limit.getCustGrpCode())) {
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "60305",
								new String[] { limitType, limit.getLimitCategoryDesc() }, null), ""));
					} else if (StringUtils.isNotEmpty(limit.getCustCountry())) {
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "60308",
								new String[] { limitType, limit.getLimitCategoryDesc() }, null), ""));
					} else {
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "60302",
								new String[] { limit.getLimitCategoryDesc() }, null), ""));
					}
					logger.debug(Literal.LEAVING);
				}

				if (StringUtils.isNotBlank(limit.getLimitCurrency())) {
					if (!StringUtils.trimToEmpty(limit.getLimitCurrency())
							.equals(financeDetail.getFinScheduleData().getFinanceMain().getFinCcy())) {
						Currency lCurrency = CurrencyUtil.getCurrencyObject(limit.getLimitCurrency());
						Currency fCurrency = CurrencyUtil
								.getCurrencyObject(financeDetail.getFinScheduleData().getFinanceMain().getFinCcy());
						calcFinAmount = CalculationUtil.getConvertedAmount(fCurrency, lCurrency, finAmount);
					} else {
						calcFinAmount = finAmount;
					}
				}
				BigDecimal excessAmount = limit.getRiskAmount().add(calcFinAmount).subtract(limit.getLimitAmount());
				if (excessAmount.compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal excessPerc;

					int formatter = CurrencyUtil
							.getFormat(financeDetail.getFinScheduleData().getFinanceMain().getFinCcy());
					;

					if (limit.getLimitAmount().compareTo(BigDecimal.ZERO) == 0) {
						excessPerc = new BigDecimal(100);
					} else {
						excessPerc = excessAmount.multiply(new BigDecimal(100)).divide(limit.getLimitAmount(), 2,
								RoundingMode.HALF_DOWN);
					}

					if (StringUtils.isNotEmpty(limit.getCustCountry())) {
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "60309",
								new String[] { excessPerc.toString(),
										PennantApplicationUtil.amountFormate(excessAmount, formatter), limitType,
										limit.getLimitCategoryDesc() },
								null), ""));
					} else if (StringUtils.isNotEmpty(limit.getCustGrpCode())) {
						errorDetails.add(ErrorUtil.getErrorDetail(new ErrorDetail(PennantConstants.KEY_FIELD, "60306",
								new String[] { excessPerc.toString(),
										PennantApplicationUtil.amountFormate(excessAmount, formatter), limitType,
										limit.getLimitCategoryDesc() },
								null), ""));
					} else {
						errorDetails
								.add(ErrorUtil
										.getErrorDetail(
												new ErrorDetail(PennantConstants.KEY_FIELD, "60303",
														new String[] { excessPerc.toString(),
																PennantApplicationUtil.amountFormate(excessAmount,
																		formatter),
																limit.getLimitCategoryDesc() },
														null),
												""));
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return errorDetails;
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
		String dftCcy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);
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
		String dftCcy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);

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

	/**
	 * Method For Preparing List of AuditDetails for Contributor Details
	 * 
	 * @param auditDetails
	 * @param type
	 * @param custId
	 * @return
	 */
	private List<AuditDetail> processingContributorList(List<AuditDetail> auditDetails, String type,
			String finReference) {
		logger.debug(Literal.ENTERING);

		boolean saveRecord = false;
		boolean updateRecord = false;
		boolean deleteRecord = false;
		boolean approveRec = false;

		for (int i = 0; i < auditDetails.size(); i++) {

			FinContributorDetail contributorDetail = (FinContributorDetail) auditDetails.get(i).getModelData();
			saveRecord = false;
			updateRecord = false;
			deleteRecord = false;
			approveRec = false;
			String rcdType = "";
			String recordStatus = "";

			if (StringUtils.isEmpty(type)) {
				approveRec = true;
				contributorDetail.setRoleCode("");
				contributorDetail.setNextRoleCode("");
				contributorDetail.setTaskId("");
				contributorDetail.setNextTaskId("");
			}

			contributorDetail.setWorkflowId(0);

			if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				deleteRecord = true;
			} else if (contributorDetail.isNewRecord()) {
				saveRecord = true;
				if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_ADD)) {
					contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_DEL)) {
					contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RCD_UPD)) {
					contributorDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}

			} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_NEW)) {
				if (approveRec) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_UPD)) {
				updateRecord = true;
			} else if (contributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_DEL)) {
				if (approveRec) {
					deleteRecord = true;
				} else if (contributorDetail.isNew()) {
					saveRecord = true;
				} else {
					updateRecord = true;
				}
			}
			if (approveRec) {
				rcdType = contributorDetail.getRecordType();
				recordStatus = contributorDetail.getRecordStatus();
				contributorDetail.setRecordType("");
				contributorDetail.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
			}
			if (saveRecord) {
				if (StringUtils.isBlank(contributorDetail.getFinReference())) {
					contributorDetail.setFinReference(finReference);
				}
				finContributorDetailDAO.save(contributorDetail, type);
			}

			if (updateRecord) {
				finContributorDetailDAO.update(contributorDetail, type);
			}

			if (deleteRecord) {
				finContributorDetailDAO.delete(contributorDetail, type);
			}

			if (approveRec) {
				contributorDetail.setRecordType(rcdType);
				contributorDetail.setRecordStatus(recordStatus);
			}
			auditDetails.get(i).setModelData(contributorDetail);
		}

		logger.debug(Literal.LEAVING);
		return auditDetails;

	}

	@Override
	public void updateCustCIF(long custID, String finReference) {
		getFinanceMainDAO().updateCustCIF(custID, finReference);

	}

	// Document Details List Maintainance
	public void listDocDeletion(FinanceDetail financeDetail, String tableType) {
		getDocumentDetailsDAO().deleteList(new ArrayList<DocumentDetails>(financeDetail.getDocumentDetailsList()),
				tableType);
	}

	@Override
	public List<DocumentDetails> getFinDocByFinRef(String finReference, String finEvent, String type) {
		return getDocumentDetailsDAO().getDocumentDetailsByRef(finReference, FinanceConstants.MODULE_NAME, finEvent,
				type);
	}

	@Override
	public DocumentDetails getFinDocDetailByDocId(long docId) {
		return getDocumentDetailsDAO().getDocumentDetailsById(docId, "");
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
	public FinanceDetail fetchFinCustDetails(FinanceDetail financeDetail, String ctgType, String finType,
			String userRole, String procEdtEvent) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		// Finance Commitment Accounting Posting Details
		if (PennantConstants.RECORD_TYPE_NEW
				.equals(financeDetail.getFinScheduleData().getFinanceMain().getRecordType())) {
			if (financeDetail.getFinScheduleData().getFinanceType().isFinCommitmentReq() && StringUtils
					.isNotBlank(financeDetail.getFinScheduleData().getFinanceMain().getFinCommitmentRef())) {

				long accountingSetId = getAccountingSetDAO().getAccountingSetId(AccountEventConstants.ACCEVENT_CMTDISB,
						AccountEventConstants.ACCEVENT_CMTDISB);
				if (accountingSetId != 0) {
					financeDetail.setCmtFinanceEntries(
							getTransactionEntryDAO().getListTransactionEntryById(accountingSetId, "_AEView", true));
				}
			}
		}

		// Finance Stage Accounting Posting Details
		financeDetail.setStageTransactionEntries(getTransactionEntryDAO().getListTransactionEntryByRefType(finType,
				StringUtils.isEmpty(procEdtEvent) ? FinanceConstants.FINSER_EVENT_ORG : procEdtEvent,
				FinanceConstants.PROCEDT_STAGEACC, userRole, "_AEView", true));

		// Set Eligibility Details to finaceDetail
		financeDetail.setElgRuleList(getEligibilityDetailService().setFinanceEligibilityDetails(
				financeMain.getFinReference(), financeMain.getFinCcy(), financeMain.getFinAmount(),
				financeMain.isNewRecord(), finType, userRole, procEdtEvent));

		// Set Scoring Details to finaceDetail
		getScoringDetailService().setFinanceScoringDetails(financeDetail, finType, userRole, ctgType, procEdtEvent);

		// Reset Finance Document Details
		String preAppref = StringUtils.trimToEmpty(financeMain.getFinPreApprovedRef());
		if (financeMain.isNewRecord()
				&& ("".equals(preAppref) || preAppref.equals(FinanceConstants.FINSER_EVENT_PREAPPROVAL))) {
			financeDetail.setDocumentDetailsList(new ArrayList<DocumentDetails>(1));
		}

		// Set Check List Details to finaceDetail
		checkListDetailService.setFinanceCheckListDetails(financeDetail, finType,
				StringUtils.isEmpty(procEdtEvent) ? FinanceConstants.FINSER_EVENT_ORG : procEdtEvent, userRole);

		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	public FinanceMain getFinanceMainParms(final String finReference) {
		return getFinanceMainDAO().getFinanceMainParms(finReference);
	}

	@Override
	public List<String> getFinanceReferenceList() {
		return getFinanceMainDAO().getFinanceReferenceList();
	}

	@Override
	public List<BulkProcessDetails> getIjaraBulkRateFinList(Date fromDate, Date toDate) {
		return getFinanceMainDAO().getIjaraBulkRateFinList(fromDate, toDate);
	}

	@Override
	public List<BulkDefermentChange> getBulkDefermentFinList(Date fromDate, Date toDate) {
		return getFinanceMainDAO().getBulkDefermentFinList(fromDate, toDate);
	}

	@Override
	public String getCustStatusByMinDueDays() {
		CustomerStatusCode customerStatusCode = getCustomerStatusCodeDAO().getCustStatusByMinDueDays("");
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

	public void downpayFinApprove(AuditHeader aAuditHeader, long serviceUID) {
		logger.debug(Literal.ENTERING);

		Cloner cloner = new Cloner();
		AuditHeader auditHeader = cloner.deepClone(aAuditHeader);

		FinanceDetail financeDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		financeDetail.setFinScheduleData(ScheduleCalculator.getDownPaySchd(financeDetail.getFinScheduleData()));
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();

		Date curBussDate = SysParamUtil.getAppDate();
		financeMain.setFinApprovedDate(curBussDate);

		// Profit Details Data Preparation
		// =======================================
		Date curBDay = SysParamUtil.getAppDate();

		FinanceProfitDetail pftDetail = getAccrualService().calProfitDetails(financeMain,
				financeDetail.getFinScheduleData().getFinanceScheduleDetails(), null, curBDay);

		// Save/Update Finance Profit Details
		// Save/Update Finance Profit Details
		boolean isNew = false;

		if (StringUtils.equals(financeMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			isNew = true;
		}

		doSave_PftDetails(pftDetail, isNew);

		financeMain.setRecordType("");
		financeMain.setRoleCode("");
		financeMain.setNextRoleCode("");
		financeMain.setTaskId("");
		financeMain.setNextTaskId("");
		financeMain.setNextUserId(null);
		financeMain.setWorkflowId(0);
		getFinanceMainDAO().save(financeMain, TableType.MAIN_TAB, false);

		// Schedule Details
		// =======================================
		listSave(financeDetail.getFinScheduleData(), "", false, 0, serviceUID);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to update Task_log table
	 * 
	 * @param financeMain
	 */
	private void updateTaskLog(FinanceMain financeMain, boolean isSaveorUpdate) {
		logger.debug(Literal.ENTERING);

		if (StringUtils.equalsIgnoreCase("Y", SysParamUtil.getValueAsString("ALLOW_LOAN_APP_LOCK"))) {
			if (!PennantConstants.RCD_STATUS_SAVED.equals(financeMain.getRecordStatus())) {
				Map<String, String> roleUsers = new HashMap<>();
				roleUsers.put(financeMain.getRoleCode(), String.valueOf(financeMain.getLastMntBy()));

				String[] nextRoles = StringUtils.split(financeMain.getNextRoleCode(), ",");

				for (String nextRole : nextRoles) {
					if (!financeMain.getNextRoleCode().contains(financeMain.getRoleCode())) {
						roleUsers.remove(financeMain.getRoleCode());
						roleUsers.put(nextRole, String.valueOf("0"));
					}
				}

				financeMain.setLovDescNextUsersRolesMap(roleUsers);

				if (financeMain.getLovDescNextUsersRolesMap() != null) {
					saveUserActivityDetails(financeMain);
				}

				getUserActivityLogDAO().updateFinStatus(financeMain.getFinReference(),
						PennantConstants.WORFLOW_MODULE_FINANCE);

				financeMain.setNextUserId(null);
			}

			return;
		}

		List<QueueAssignment> queueAssignList = new ArrayList<QueueAssignment>();
		List<TaskOwners> taskOwnerList = new ArrayList<TaskOwners>();
		TaskOwners taskOwner = null;
		boolean addRecord = false;
		String roleCode = null;
		String userId = "";

		if (PennantConstants.RCD_STATUS_RESUBMITTED.equals(financeMain.getRecordStatus())
				&& !financeMain.isNewRecord()) {
			TaskOwners owner = fetchTaskOwner(financeMain.getFinReference(), financeMain.getRoleCode());
			if (owner.getCurrentOwner() == 0) {
				owner.setCurrentOwner(financeMain.getLastMntBy());
				owner.setActualOwner(financeMain.getLastMntBy());
			}
			owner.setNewRecord(false);
			owner.setProcessed(true);
			taskOwnerList.add(owner);

			// Update resubmitting task owner in queue assignment
			queueAssignList = getResubmitQueueDetails(financeMain, owner.isProcessed());

			roleCode = financeMain.getNextRoleCode();
		} else {
			roleCode = financeMain.getRoleCode();
			userId = String.valueOf(financeMain.getLastMntBy());
		}

		List<TaskOwners> existingTaskOwners = getTaskOwnersDAO().getTaskOwnerList(financeMain.getFinReference(),
				roleCode);
		if (existingTaskOwners.size() == 0) {
			taskOwner = new TaskOwners();
			taskOwner.setReference(financeMain.getFinReference());
			taskOwner.setRoleCode(financeMain.getRoleCode());
			taskOwner.setActualOwner(financeMain.getLastMntBy());
			taskOwner.setCurrentOwner(financeMain.getLastMntBy());
			if (!PennantConstants.RCD_STATUS_SAVED.equals(financeMain.getRecordStatus())) {
				taskOwner.setProcessed(true);
			}
			taskOwner.setNewRecord(true);
			existingTaskOwners.add(taskOwner);
			addRecord = true;
		} else {
			for (int i = 0; i < existingTaskOwners.size(); i++) {
				taskOwner = existingTaskOwners.get(i);
				taskOwner.setNewRecord(false);

				if (PennantConstants.RCD_STATUS_SAVED.equals(financeMain.getRecordStatus())
						|| (taskOwner.getCurrentOwner() != 0
								&& PennantConstants.RCD_STATUS_RESUBMITTED.equals(financeMain.getRecordStatus()))) {
					taskOwner.setProcessed(false);
				} else {
					taskOwner.setProcessed(true);
				}

				if (taskOwner.getCurrentOwner() == 0 && taskOwner.getRoleCode().equals(financeMain.getRoleCode())) {
					taskOwner.setActualOwner(financeMain.getLastMntBy());
					taskOwner.setCurrentOwner(financeMain.getLastMntBy());
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

		if (financeMain.isNewRecord() || !PennantConstants.RCD_STATUS_SAVED.equals(financeMain.getRecordStatus())
				|| addRecord) {
			queueAssignList.addAll(addQueueAssignmentDetails(existingTaskOwners, financeMain, userId, roleCode,
					taskOwner.isProcessed()));
			taskOwnerList.addAll(existingTaskOwners);
		}

		String nextUsers = null;
		if (isSaveorUpdate && StringUtils.isNotEmpty(financeMain.getNextTaskId())) {
			addTaskQueueDetails(financeMain, queueAssignList, taskOwnerList);
			nextUsers = financeMain.getNextUserId();
		}

		if (financeMain.getLovDescNextUsersRolesMap() != null) {
			saveUserActivityDetails(financeMain);
			getTaskOwnersDAO().saveOrUpdateList(taskOwnerList);
			if (queueAssignList.size() > 0 && StringUtils.isNotBlank(financeMain.getLovDescAssignMthd())
					&& (!StringUtils.equals(financeMain.getFinSourceID(), PennantConstants.FINSOURCE_ID_API)
							|| (financeMain.isQuickDisb()))) {
				getQueueAssignmentDAO().saveOrUpdate(queueAssignList);
			}
		}

		if (isSaveorUpdate) {
			List<String> refList = new ArrayList<String>();
			refList.add(financeMain.getFinReference());
			getFinanceMainDAO().updateNextUserId(refList, "", nextUsers, true); // Update
																				// nextuserid
																				// value
																				// in
																				// finance
																				// table
		} else {
			getUserActivityLogDAO().updateFinStatus(financeMain.getFinReference(),
					PennantConstants.WORFLOW_MODULE_FINANCE);
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
		return getTaskOwnersDAO().getTaskOwner(finRef, roleCode);
	}

	@Override
	public List<AvailFinance> getFinanceDetailByCmtRef(String cmtRef, long custId) {
		return getFinanceMainDAO().getFinanceDetailByCmtRef(cmtRef, custId);
	}

	@Override
	public BigDecimal getCustRepayBankTotal(long custId) {
		return getCustomerDAO().getCustRepayBankTotal(custId);
	}

	@Override
	public String getApprovedRepayMethod(String finReference, String type) {
		return getFinanceMainDAO().getApprovedRepayMethod(finReference, type);
	}

	@Override
	public FeeRule getFeeChargesByFinRefAndFeeCode(String finReference, String feeCode, String tableType) {
		return getFinFeeChargesDAO().getFeeChargesByFinRefAndFee(finReference, feeCode, tableType);
	}

	@Override
	public boolean updateFeeChargesByFinRefAndFeeCode(FeeRule feeRule, String tableType) {
		return getFinFeeChargesDAO().updateFeeChargesByFinRefAndFee(feeRule, tableType);
	}

	@Override
	public List<FinanceSummary> getFinExposureByCustId(long custId) {
		return getFinanceMainDAO().getFinExposureByCustId(custId);
	}

	@Override
	public List<String> getRollOverLimitRefList() {
		return getFinanceMainDAO().getRollOverLimitRefList();
	}

	@Override
	public List<String> getRollOverFinTypeList(String limitRef) {
		return getFinanceMainDAO().getRollOverFinTypeList(limitRef);
	}

	@Override
	public List<Date> getRollOverNextDateList(String limitRef, String finType) {
		return getFinanceMainDAO().getRollOverDateList(limitRef, finType);
	}

	@Override
	public List<RolledoverFinanceDetail> getRolloverFinanceList(String limitRef, String finType, Date rolloverDate) {
		return getFinanceMainDAO().getFinanceList(limitRef, finType, rolloverDate);
	}

	@Override
	public BigDecimal getTotalRepayAmount(String finReference) {
		return getFinanceScheduleDetailDAO().getTotalRepayAmount(finReference);
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

	@Override
	public void updateFinancePriority() {
		getFinanceMainDAO().updateFinancePriority();
	}

	/**
	 * @param finReference
	 * @return
	 */
	@Override
	public FinanceDetail getPreApprovalFinanceDetailsById(String finReference) {
		logger.debug(" Entering ");
		String type = PennantConstants.PREAPPROVAL_TABLE_TYPE;

		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		scheduleData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, false));

		// Step Policy Details List
		if (scheduleData.getFinanceMain().isStepFinance()) {
			scheduleData.setStepPolicyDetails(
					getFinanceStepDetailDAO().getFinStepDetailListByFinRef(finReference, type, false));
		}

		// Finance Schedule Details
		scheduleData.setFinanceScheduleDetails(
				getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, false));

		// Finance Disbursement Details
		scheduleData.setDisbursementDetails(
				getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference, type, false));

		// Finance Repayments Instruction Details
		scheduleData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, false));

		// Finance Overdue Penalty Rate Details
		scheduleData.setFinODPenaltyRate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference, type));

		// Fetch Finance Premium Details
		// financeDetail.setPremiumDetail(getFinancePremiumDetailDAO().getFinPremiumDetailsById(finReference,
		// type));

		// scheduleData.setFeeRules(getFinFeeChargesDAO().getFeeChargesByFinRef(finReference,FinanceConstants.FINSER_EVENT_PREAPPROVAL,
		// false, type));

		// Finance Fee Schedule Details
		scheduleData.setFinFeeDetailList(getFinFeeDetailService().getFinFeeDetailById(finReference, false, type));

		// Finance Guaranteer Details
		financeDetail.setGurantorsDetailList(getGuarantorDetailService().getGuarantorDetail(finReference, type));

		// Finance Joint Account Details
		financeDetail
				.setJountAccountDetailList(getJointAccountDetailService().getJoinAccountDetail(finReference, type));

		// Asset Type Details
		financeDetail.setFinAssetTypesList(getFinAssetTypeDAO().getFinAssetTypesByFinRef(finReference, "_Temp"));

		// Extended Field Details for Assets
		financeDetail.setExtendedFieldRenderList(
				getExtendedAssetDetails(finReference, financeDetail.getFinAssetTypesList()));

		// Collateral Details
		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			financeDetail.setCollateralAssignmentList(getCollateralAssignmentDAO()
					.getCollateralAssignmentByFinRef(finReference, FinanceConstants.MODULE_NAME, "_TView"));
		} else {
			financeDetail.setFinanceCollaterals(getFinCollateralService().getFinCollateralsByRef(finReference, type));
		}

		// document details
		financeDetail.setDocumentDetailsList(getDocumentDetailsDAO().getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, FinanceConstants.FINSER_EVENT_PREAPPROVAL, type));

		logger.debug(" Leaving ");
		return financeDetail;
	}

	// ******************************************************//
	// ************ ROLLED OVER FINANCE DETAIL *************//
	// ******************************************************//

	@SuppressWarnings("unchecked")
	private void doRolledOverFinancePostings(String finReference, BigDecimal custPayAmount, String accNumber)
			throws InterfaceException, IllegalAccessException, InvocationTargetException {

		// Finance Details
		FinanceMain main = getFinanceMainDAO().getFinanceMainById(finReference, "", false);
		FinanceType financeType = getFinanceTypeDAO().getFinanceTypeByID(main.getFinType(), "_AView");
		List<FinanceScheduleDetail> scheduleDetails = getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference,
				"", false);
		FinanceProfitDetail financeProfitDetail = getProfitDetailsDAO().getFinProfitDetailsById(finReference);

		List<Object> returnList = new ArrayList<Object>();
		try {
			String actRepayAccountId = main.getRepayAccountId();
			main.setRepayAccountId(accNumber);

			// Check Finance is RIA Finance Type or Not
			BigDecimal totRpyPri = BigDecimal.ZERO;
			BigDecimal totRpyPft = BigDecimal.ZERO;
			BigDecimal totSchdFee = BigDecimal.ZERO;
			BigDecimal totSchdIns = BigDecimal.ZERO;
			BigDecimal totSchdSuplRent = BigDecimal.ZERO;
			BigDecimal totSchdIncrCost = BigDecimal.ZERO;

			List<FinRepayQueue> finRepayQueues = new ArrayList<FinRepayQueue>();
			Map<String, BigDecimal> totalsMap = new HashMap<String, BigDecimal>();
			FinRepayQueue finRepayQueue = null;
			Date curBDay = SysParamUtil.getAppDate();

			for (int i = 0; i < scheduleDetails.size(); i++) {

				finRepayQueue = new FinRepayQueue();
				finRepayQueue.setFinReference(main.getFinReference());
				finRepayQueue.setRpyDate(scheduleDetails.get(i).getSchDate());
				finRepayQueue.setFinRpyFor(FinanceConstants.SCH_TYPE_SCHEDULE);
				finRepayQueue.setRcdNotExist(true);
				finRepayQueue = doWriteDataToBean(finRepayQueue, main, scheduleDetails.get(i), custPayAmount);

				// Check Balance
				BigDecimal totalPay = finRepayQueue.getSchdInsPayNow().add(finRepayQueue.getSchdSuplRentPayNow())
						.add(finRepayQueue.getSchdIncrCostPayNow()).add(finRepayQueue.getSchdFeePayNow())
						.add(finRepayQueue.getSchdPftPayNow()).add(finRepayQueue.getSchdPriPayNow());

				custPayAmount = custPayAmount.subtract(totalPay);

				// Overdue Details preparation
				List<Object> odObjDetails = getRecoveryPostingsUtil().recoveryCalculation(finRepayQueue,
						main.getProfitDaysBasis(), curBDay, false, false);
				OverdueChargeRecovery recovery = (OverdueChargeRecovery) odObjDetails.get(1);

				finRepayQueue.setRefundAmount(BigDecimal.ZERO);
				finRepayQueue.setPenaltyPayNow(BigDecimal.ZERO);
				finRepayQueue.setWaivedAmount(BigDecimal.ZERO);
				finRepayQueue.setPenaltyBal(recovery.getPenalty());
				finRepayQueue.setChargeType(recovery.getPenaltyType());

				if (totalPay.compareTo(BigDecimal.ZERO) == 0 && recovery.getPenalty().compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}

				// Total Repayments Calculation for Principal, Profit & Refunds
				totRpyPri = totRpyPri.add(finRepayQueue.getSchdPriPayNow());
				totRpyPft = totRpyPft.add(finRepayQueue.getSchdPftPayNow());

				// Fee Details
				totSchdFee = totSchdFee.add(finRepayQueue.getSchdFeePayNow());
				totSchdIns = totSchdIns.add(finRepayQueue.getSchdInsPayNow());
				totSchdSuplRent = totSchdSuplRent.add(finRepayQueue.getSchdSuplRentPayNow());
				totSchdIncrCost = totSchdIncrCost.add(finRepayQueue.getSchdIncrCostPayNow());

				finRepayQueues.add(finRepayQueue);

				// If Balance is Nill then Process Closed
				if (custPayAmount.compareTo(BigDecimal.ZERO) <= 0) {
					break;
				}
			}

			totalsMap.put("totRpyTot", totRpyPri.add(totRpyPft));
			totalsMap.put("totRpyPri", totRpyPri);
			totalsMap.put("totRpyPft", totRpyPft);
			totalsMap.put("totRefund", BigDecimal.ZERO);
			totalsMap.put("INSREFUND", BigDecimal.ZERO);

			// Fee Details
			totalsMap.put("insPay", totSchdIns);
			totalsMap.put("schFeePay", totSchdFee);
			totalsMap.put("suplRentPay", totSchdSuplRent);
			totalsMap.put("incrCostPay", totSchdIncrCost);

			// Repayment process
			returnList = getRepayPostingUtil().postingsScreenRepayProcess(main, scheduleDetails, financeProfitDetail,
					finRepayQueues, totalsMap, AccountEventConstants.ACCEVENT_REPAY, null,
					financeType.getFinDivision());

			if ((Boolean) returnList.get(0)) {
				returnList.add(finRepayQueues);
			}

			if (!(Boolean) returnList.get(0)) {
				String errParm = (String) returnList.get(1);
				throw new InterfaceException("9999", errParm);
			}

			long linkedTranId = (Long) returnList.get(1);
			boolean partialPay = (Boolean) returnList.get(2);
			AEAmountCodes aeAmountCodes = (AEAmountCodes) returnList.get(3);

			// Repayment Postings Details Process
			returnList = getRepayPostingUtil().UpdateScreenPaymentsProcess(main, scheduleDetails, financeProfitDetail,
					finRepayQueues, linkedTranId, partialPay, aeAmountCodes);

			// Save Finance Repay Header Details
			FinRepayHeader finRepayHeader = new FinRepayHeader();
			finRepayHeader.setFinReference(finReference);
			finRepayHeader.setValueDate(curBDay);
			finRepayHeader.setRepayAmount(totRpyPri.add(totRpyPft));
			finRepayHeader.setFinEvent(FinanceConstants.FINSER_EVENT_ROLLOVER);
			finRepayHeader.setPriAmount(totRpyPri);
			finRepayHeader.setPftAmount(totRpyPft);
			finRepayHeader.setTotalIns(totSchdIns);
			finRepayHeader.setTotalSuplRent(totSchdSuplRent);
			finRepayHeader.setTotalIncrCost(totSchdIncrCost);
			finRepayHeader.setTotalSchdFee(totSchdFee);
			finRepayHeader.setTotalRefund(BigDecimal.ZERO);
			finRepayHeader.setTotalWaiver(BigDecimal.ZERO);
			finRepayHeader.setInsRefund(BigDecimal.ZERO);
			finRepayHeader.setRepayAccountId(actRepayAccountId);
			finRepayHeader.setEarlyPayEffMtd(CalculationConstants.EARLYPAY_NOEFCT);
			finRepayHeader.setEarlyPayDate(curBDay);
			finRepayHeader.setSchdRegenerated(false);
			finRepayHeader.setPayApportionment(PennantConstants.List_Select);
			finRepayHeader.setLinkedTranId(linkedTranId);
			getFinanceRepaymentsDAO().saveFinRepayHeader(finRepayHeader, TableType.MAIN_TAB);

			// Finance Main Updation
			// =======================================
			main = (FinanceMain) returnList.get(3);
			getFinanceMainDAO().updateRepaymentAmount(main.getFinReference(),
					main.getFinRepaymentAmount().add(custPayAmount));

			// ScheduleDetails delete and save
			// =======================================
			scheduleDetails = (List<FinanceScheduleDetail>) returnList.get(4);
			getFinanceScheduleDetailDAO().deleteByFinReference(finReference, "", false, 0);
			getFinanceScheduleDetailDAO().saveList(scheduleDetails, "", false);

		} catch (InterfaceException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (IllegalAccessException e) {
			logger.error("Exception: ", e);
			throw e;
		} catch (InvocationTargetException e) {
			logger.error("Exception: ", e);
			throw e;
		}

	}

	/**
	 * Method for prepare RepayQueue data
	 * 
	 * @param resultSet
	 * @return
	 */
	private FinRepayQueue doWriteDataToBean(FinRepayQueue finRepayQueue, FinanceMain financeMain,
			FinanceScheduleDetail curSchd, BigDecimal custPayAmount) {
		logger.debug(Literal.ENTERING);

		finRepayQueue.setBranch(financeMain.getFinBranch());
		finRepayQueue.setFinType(financeMain.getFinType());
		finRepayQueue.setCustomerID(financeMain.getCustID());
		finRepayQueue.setFinPriority(9999);

		finRepayQueue.setSchdPft(curSchd.getProfitSchd());
		finRepayQueue.setSchdPri(curSchd.getPrincipalSchd());
		finRepayQueue.setSchdPftBal(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
		finRepayQueue.setSchdPriBal(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
		finRepayQueue.setSchdPriPaid(curSchd.getSchdPriPaid());
		finRepayQueue.setSchdPftPaid(curSchd.getSchdPftPaid());

		// Fee Details
		// 1. Schedule Fee Amount
		finRepayQueue.setSchdFee(curSchd.getFeeSchd());
		finRepayQueue.setSchdFeePaid(curSchd.getSchdFeePaid());

		// 2. Schedule Insurance Amount
		finRepayQueue.setSchdIns(curSchd.getInsSchd());
		finRepayQueue.setSchdInsPaid(curSchd.getSchdInsPaid());

		// 3. Schedule Supplementary Rent Amount
		finRepayQueue.setSchdSuplRent(curSchd.getSuplRent());
		finRepayQueue.setSchdSuplRentPaid(curSchd.getSuplRentPaid());

		// 4. Schedule Fee Amount
		finRepayQueue.setSchdIncrCost(curSchd.getIncrCost());
		finRepayQueue.setSchdIncrCostPaid(curSchd.getIncrCostPaid());

		// Scheduled Fee Collection Process

		// 1. Insurance Fee Amount
		if (custPayAmount.compareTo(curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid())) > 0) {
			finRepayQueue.setSchdInsPayNow(curSchd.getInsSchd().subtract(curSchd.getSchdInsPaid()));
		} else {
			finRepayQueue.setSchdInsPayNow(custPayAmount);
		}
		custPayAmount = custPayAmount.subtract(finRepayQueue.getSchdInsPayNow());

		// 2. Supplementary Rent Amount
		if (custPayAmount.compareTo(curSchd.getSuplRent().subtract(curSchd.getSuplRentPaid())) > 0) {
			finRepayQueue.setSchdSuplRentPayNow(curSchd.getSuplRent().subtract(curSchd.getSuplRentPaid()));
		} else {
			finRepayQueue.setSchdSuplRentPayNow(custPayAmount);
		}
		custPayAmount = custPayAmount.subtract(finRepayQueue.getSchdSuplRentPayNow());

		// 3. Increased Cost Amount
		if (custPayAmount.compareTo(curSchd.getIncrCost().subtract(curSchd.getIncrCostPaid())) > 0) {
			finRepayQueue.setSchdIncrCostPayNow(curSchd.getIncrCost().subtract(curSchd.getIncrCostPaid()));
		} else {
			finRepayQueue.setSchdIncrCostPayNow(custPayAmount);
		}
		custPayAmount = custPayAmount.subtract(finRepayQueue.getSchdIncrCostPayNow());

		// 4. Scheduled Fee Amount
		if (custPayAmount.compareTo(curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid())) > 0) {
			finRepayQueue.setSchdFeePayNow(curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid()));
		} else {
			finRepayQueue.setSchdFeePayNow(custPayAmount);
		}
		custPayAmount = custPayAmount.subtract(finRepayQueue.getSchdFeePayNow());

		// 5. Scheduled Profit Amount
		if (custPayAmount.compareTo(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid())) > 0) {
			finRepayQueue.setSchdPftPayNow(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()));
		} else {
			finRepayQueue.setSchdPftPayNow(custPayAmount);
		}
		custPayAmount = custPayAmount.subtract(finRepayQueue.getSchdPftPayNow());

		// 6. Scheduled Principal Amount
		if (custPayAmount.compareTo(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid())) > 0) {
			finRepayQueue.setSchdPriPayNow(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()));
		} else {
			finRepayQueue.setSchdPriPayNow(custPayAmount);
		}
		custPayAmount = custPayAmount.subtract(finRepayQueue.getSchdPriPayNow());

		logger.debug(Literal.LEAVING);
		return finRepayQueue;
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

					if ("".equals(moduleType) || FinanceConstants.FINSER_EVENT_ORG.equals(moduleType)) {
						List<ErrorDetail> errorDetails = getLimitManagement().processLoanLimitOrgination(financeDetail,
								aAuditHeader.isOveride(), LimitConstants.BLOCK, validateOnly);
						if (!errorDetails.isEmpty()) {
							aAuditHeader.setErrorList(errorDetails);
						}
					} else if (moduleType.equals(FinanceConstants.FINSER_EVENT_ADDDISB)
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
			if ("".equals(moduleType) || FinanceConstants.FINSER_EVENT_ORG.equals(moduleType)) {

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

				if (moduleType.equals(FinanceConstants.FINSER_EVENT_OVERDRAFTSCHD)) {
					List<ErrorDetail> errorDetails = getLimitManagement().processLimitIncrease(financeDetail,
							aAuditHeader.isOveride(), validateOnly);
					if (!errorDetails.isEmpty()) {
						aAuditHeader.setErrorList(errorDetails);
					}
				} else {

					String tranType = "";

					if (moduleType.equals(FinanceConstants.FINSER_EVENT_ADDDISB)) {
						tranType = LimitConstants.APPROVE;
					} else if (moduleType.equals(FinanceConstants.FINSER_EVENT_CANCELDISB)) {
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
		repayment.setSchdInsPaid(BigDecimal.ZERO);
		repayment.setSchdSuplRentPaid(BigDecimal.ZERO);
		repayment.setSchdIncrCostPaid(BigDecimal.ZERO);

		logger.debug(Literal.LEAVING);
		return repayment;
	}

	@Override
	public String getFinanceMainByRcdMaintenance(String reference, String type) {
		return getFinanceMainDAO().getFinanceMainByRcdMaintenance(reference, type);
	}

	@Override
	public List<FinanceDisbursement> getFinanceDisbursements(String finReferecne, String type, boolean isWIF) {
		return getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReferecne, type, isWIF);
	}

	@Override
	public Date getFinStartDate(String finReference) {
		return getFinanceMainDAO().getFinStartDate(finReference);
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

	/**
	 * Method for Approval process for LPO Approval Agreement
	 */
	@Override
	public void updateFinApprovalStatus(String finReference, String approvalStatus) {
		getFinanceMainDAO().updateApprovalStatus(finReference, approvalStatus);
	}

	@Override
	public String getNextRoleCodeByRef(String finReference, String type) {
		return getFinanceMainDAO().getNextRoleCodeByRef(finReference, type);

	}

	@Override
	public DocumentDetails getFinDocDetailByDocId(long docId, String type, boolean readAttachment) {
		return getDocumentDetailsDAO().getDocumentDetailsById(docId, type, readAttachment);
	}

	@Override
	public DocumentDetails getDocumentDetails(long docId, String type) {
		return getDocumentDetailsDAO().getDocumentDetails(docId, type);
	}

	@Override
	public List<DocumentDetails> getDocumentDetails(String finReference, String finProcEvent) {
		return getDocumentDetailsDAO().getDocumentDetailsByRef(finReference, FinanceConstants.MODULE_NAME, finProcEvent,
				"_View");
	}

	@Override
	public List<String> getUsersLoginList(List<String> nextRoleCodes) {
		return getFinanceMainDAO().getUsersLoginList(nextRoleCodes);
	}

	@Override
	public FinanceMain getFinanceMainForBatch(String finReference) {
		return getFinanceMainDAO().getFinanceMainForBatch(finReference);
	}

	@Override
	public FinanceScheduleDetail getFinSchduleDetails(String finReference, Date schdDate) {
		return getFinanceScheduleDetailDAO().getFinSchduleDetails(finReference, schdDate, false);
	}

	// ******************************************************//
	// *************** EOD PROCESS Details ******************//
	// ******************************************************//

	@Override
	public int getProgressCountByCust(long custID) {
		return getCustomerQueuingDAO().getProgressCountByCust(custID);
	}

	@Override
	public FinanceDetail getFinanceDetailForCovenants(FinanceMain financeMain) {
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(financeMain.getFinReference());
		scheduleData.setFinanceMain(financeMain);

		if (SysParamUtil.isAllowed(SMTParameterConstants.NEW_COVENANT_MODULE)) {
			financeDetail
					.setCovenants(covenantsService.getCovenants(financeMain.getFinReference(), "Loan", TableType.VIEW));
		} else {
			financeDetail.setCovenantTypeList(
					getFinCovenantTypeService().getFinCovenantTypeById(financeMain.getFinReference(), "_View", false));
		}

		financeDetail.setCustomerDetails(
				getCustomerDetailsService().getCustomerAndCustomerDocsById(financeMain.getCustID(), ""));

		financeDetail.setDocumentDetailsList(getDocumentDetailsDAO()
				.getDocumentDetailsByRef(financeMain.getFinReference(), FinanceConstants.MODULE_NAME, "", ""));

		return financeDetail;
	}

	@Override
	public FinanceDetail getFinanceDetailForFinOptions(FinanceMain financeMain) {
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(financeMain.getFinReference());
		scheduleData.setFinanceMain(financeMain);

		financeDetail.setFinOptions(finOptionService.getFinOptions(financeMain.getFinReference(), TableType.VIEW));

		financeDetail.setCustomerDetails(
				getCustomerDetailsService().getCustomerAndCustomerDocsById(financeMain.getCustID(), ""));

		financeDetail.setDocumentDetailsList(getDocumentDetailsDAO()
				.getDocumentDetailsByRef(financeMain.getFinReference(), FinanceConstants.MODULE_NAME, "", ""));

		return financeDetail;
	}

	/**
	 * Method for Add loan type Expense to the loan
	 */
	private List<FinTypeExpense> saveFinExpenseDetails(FinanceMain financeMain) {

		List<FinTypeExpense> finTypeExpenseList = getFinTypeExpenseDAO()
				.getLoanQueueExpenseListByFinType(financeMain.getFinType(), "");

		if (CollectionUtils.isNotEmpty(finTypeExpenseList)) {

			for (FinTypeExpense finTypeExpense : finTypeExpenseList) {
				// Expense Amount calculation
				BigDecimal txnAmount = getFinExpenseAmount(finTypeExpense, financeMain);

				FinExpenseDetails finExpenseDetails = new FinExpenseDetails();
				finExpenseDetails.setFinReference(financeMain.getFinReference());
				finExpenseDetails.setExpenseTypeId(finTypeExpense.getExpenseTypeID());
				finExpenseDetails.setLastMntOn(financeMain.getLastMntOn());
				finExpenseDetails.setLastMntBy(financeMain.getLastMntBy());
				finExpenseDetails.setAmount(txnAmount);

				getFinExpenseDetailsDAO().saveFinExpenseDetails(finExpenseDetails);
			}
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

	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public FinanceReferenceDetailDAO getFinanceReferenceDetailDAO() {
		return financeReferenceDetailDAO;
	}

	public AccountingSetDAO getAccountingSetDAO() {
		return accountingSetDAO;
	}

	public void setAccountingSetDAO(AccountingSetDAO accountingSetDAO) {
		this.accountingSetDAO = accountingSetDAO;
	}

	public RuleDAO getRuleDAO() {
		return ruleDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
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

	public void setFinContributorHeaderDAO(FinContributorHeaderDAO finContributorHeaderDAO) {
		this.finContributorHeaderDAO = finContributorHeaderDAO;
	}

	public FinContributorHeaderDAO getFinContributorHeaderDAO() {
		return finContributorHeaderDAO;
	}

	public void setFinContributorDetailDAO(FinContributorDetailDAO finContributorDetailDAO) {
		this.finContributorDetailDAO = finContributorDetailDAO;
	}

	public FinContributorDetailDAO getFinContributorDetailDAO() {
		return finContributorDetailDAO;
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

	public IndicativeTermDetailDAO getIndicativeTermDetailDAO() {
		return indicativeTermDetailDAO;
	}

	public void setIndicativeTermDetailDAO(IndicativeTermDetailDAO indicativeTermDetailDAO) {
		this.indicativeTermDetailDAO = indicativeTermDetailDAO;
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

	public DDAControllerService getDdaControllerService() {
		return ddaControllerService;
	}

	public void setDdaControllerService(DDAControllerService ddaControllerService) {
		this.ddaControllerService = ddaControllerService;
	}

	public RolledoverFinanceDAO getRolledoverFinanceDAO() {
		return rolledoverFinanceDAO;
	}

	public void setRolledoverFinanceDAO(RolledoverFinanceDAO rolledoverFinanceDAO) {
		this.rolledoverFinanceDAO = rolledoverFinanceDAO;
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

	public CollateralMarkProcess getCollateralMarkProcess() {
		return collateralMarkProcess;
	}

	public void setCollateralMarkProcess(CollateralMarkProcess collateralMarkProcess) {
		this.collateralMarkProcess = collateralMarkProcess;
	}

	public HandlingInstructionService getHandlingInstructionService() {
		return handlingInstructionService;
	}

	public void setHandlingInstructionService(HandlingInstructionService handlingInstructionService) {
		this.handlingInstructionService = handlingInstructionService;
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
		return getFinanceMainDAO().getScheduleEffectModuleList(schdChangeReq);
	}

	public List<FinTypeFees> getFinTypeFees(String finType, String eventCode, boolean origination, int moduleId) {
		return getFinTypeFeesDAO().getFinTypeFeesList(finType, eventCode, "_AView", origination, moduleId);
	}

	public List<FinTypeFees> getSchemeFeesList(long referenceId, String finEvent, String type, boolean origination,
			int moduleId) {
		return getFinTypeFeesDAO().getSchemeFeesList(referenceId, finEvent, type, origination, moduleId);
	}

	@Override
	public List<FinTypeFees> getSchemeFeesList(long referenceId, String eventCode, boolean origination, int moduleId) {
		return getFinTypeFeesDAO().getSchemeFeesList(referenceId, eventCode, "_AView", origination, moduleId);
	}

	/**
	 * Method for Fetching List of Step Policy Details using Finance Reference
	 * 
	 * @param finReference
	 * @param type
	 * @param isWIF
	 * @return
	 */
	@Override
	public List<FinanceStepPolicyDetail> getFinStepPolicyDetails(String finReference, String type, boolean isWIF) {
		return getFinanceStepDetailDAO().getFinStepDetailListByFinRef(finReference, type, isWIF);
	}

	@Override
	public BigDecimal getOutStandingBalFromFees(String finReference) {
		return getFinanceScheduleDetailDAO().getOutStandingBalFromFees(finReference);
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

	public FinFlagDetailsDAO getFinFlagDetailsDAO() {
		return finFlagDetailsDAO;
	}

	public void setFinFlagDetailsDAO(FinFlagDetailsDAO finFlagDetailsDAO) {
		this.finFlagDetailsDAO = finFlagDetailsDAO;
	}

	public FinTypeInsuranceDAO getFinTypeInsuranceDAO() {
		return finTypeInsuranceDAO;
	}

	public void setFinTypeInsuranceDAO(FinTypeInsuranceDAO finTypeInsuranceDAO) {
		this.finTypeInsuranceDAO = finTypeInsuranceDAO;
	}

	public VASRecordingDAO getVasRecordingDAO() {
		return vasRecordingDAO;
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

	public FinTypeVASProductsDAO getFinTypeVASProductsDAO() {
		return finTypeVASProductsDAO;
	}

	public void setFinTypeVASProductsDAO(FinTypeVASProductsDAO finTypeVASProductsDAO) {
		this.finTypeVASProductsDAO = finTypeVASProductsDAO;
	}

	@Override
	public BigDecimal getFinAssetValue(String finReference) {
		return getFinanceMainDAO().getFinAssetValue(finReference);
	}

	public PromotionDAO getPromotionDAO() {
		return promotionDAO;
	}

	public void setPromotionDAO(PromotionDAO promotionDAO) {
		this.promotionDAO = promotionDAO;
	}

	public FinFeeDetailDAO getFinFeeDetailDAO() {
		return finFeeDetailDAO;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	public FinanceTaxDetailDAO getFinanceTaxDetailDAO() {
		return financeTaxDetailDAO;
	}

	public void setFinanceTaxDetailDAO(FinanceTaxDetailDAO financeTaxDetailDAO) {
		this.financeTaxDetailDAO = financeTaxDetailDAO;
	}

	public FinanceTaxDetailService getFinanceTaxDetailService() {
		return financeTaxDetailService;
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

	public CustomServiceTask getCustomServiceTask() {
		return customServiceTask;
	}

	public void setCustomServiceTask(CustomServiceTask customServiceTask) {
		this.customServiceTask = customServiceTask;
	}

	public CustomerService getCustomerService() {
		return customerService;
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
		financeMain.setDepreciationFrq(financeType.getFinDepreciationFrq());
		financeMain.setTDSApplicable(financeType.isTdsApplicable());
		financeMain.setProductCategory(financeType.getProductCategory());
		financeMain.setFinCategory(financeType.getFinCategory());
		// Step Policy Details
		if (financeType.isSteppingMandatory()) {
			financeMain.setStepFinance(financeType.isStepFinance());
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

		// Default Grace Period Group box UnVisible by setting Allow grace
		// period to FALSE
		if (financeType.getFinCategory().equals(FinanceConstants.PRODUCT_ISTISNA)) {
			financeMain.setAllowGrcPeriod(financeType.isFInIsAlwGrace());
		} else {
			financeMain.setAllowGrcPeriod(false);
		}

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

			financeMain.setGrcAdvBaseRate(financeType.getGrcAdvBaseRate());
			financeMain.setGrcAdvMargin(financeType.getGrcAdvMargin());
			financeMain.setGrcAdvPftRate(financeType.getGrcAdvPftRate());
		}
		//Setting the Gestation Period default values to financemain  
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
		financeMain.setRpyAdvBaseRate(financeType.getRpyAdvBaseRate());
		financeMain.setRpyAdvMargin(financeType.getRpyAdvMargin());
		financeMain.setRpyAdvPftRate(financeType.getRpyAdvPftRate());
		financeMain.setRolloverFrq(StringUtils.trimToEmpty(financeType.getRollOverFrq()));
		if (StringUtils.isNotEmpty(financeType.getRollOverFrq())
				&& FrequencyUtil.validateFrequency(financeType.getRollOverFrq()) == null) {
			financeMain.setNextRolloverDate(FrequencyUtil
					.getNextDate(financeType.getRollOverFrq(), financeMain.getNumberOfTerms(),
							financeMain.getFinStartDate(), "A", false, financeType.getFddLockPeriod())
					.getNextFrequencyDate());
		}

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
				if (!isCovenantReceived(documentList, finCovenantType)) {
					covenantRoles.add(finCovenantType.getMandRole());
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
	public List<FinanceScheduleDetail> getFinScheduleList(String finReference) {
		return getFinanceScheduleDetailDAO().getFinSchdDetailsForBatch(finReference);
	}

	@Override
	public boolean isholdDisbursementProcess(String finReference) {
		return holdDisbursementDAO.isholdDisbursementProcess(finReference, "_View");
	}

	@Override
	public void updateNextUserId(String finReference, String nextUserId) {
		if (nextUserId != null) {
			String currUserId = getFinanceMainDAO().getNextUserId(finReference);

			if (StringUtils.isNotEmpty(currUserId)) {
				throw new AppException("The record was locked by another user.");
			}
		}

		getFinanceMainDAO().updateNextUserId(finReference, nextUserId);
	}

	@Override
	public String getNextUserId(String finReference) {
		return getFinanceMainDAO().getNextUserId(finReference);
	}

	// FinAsset details
	@Override
	public List<FinAssetTypes> getFinAssetTypesByFinRef(String reference, String type) {
		return getFinAssetTypeDAO().getFinAssetTypesByFinRef(reference, type);
	}

	@Override
	public List<Integer> getFinanceDisbSeqs(String finReferecne, boolean isWIF) {
		return getFinanceDisbursementDAO().getFinanceDisbSeqs(finReferecne, "", isWIF);
	}

	@Override
	public List<FinanceProfitDetail> getFinProfitListByFinRefList(List<String> finRefList) {
		return getProfitDetailsDAO().getFinProfitListByFinRefList(finRefList);
	}

	@Override
	public List<FinanceMain> getFinanceMainForLinkedLoans(String finReference) {
		return getFinanceMainDAO().getFinanceMainForLinkedLoans(finReference);
	}

	@Override
	public List<FinanceMain> getFinanceMainForLinkedLoans(long custId) {
		return getFinanceMainDAO().getFinanceMainForLinkedLoans(custId);
	}

	/**
	 * Generating all fin reference which are having dues against the customer.
	 */
	@Override
	public String getCustomerDueFinReferces(long custId) {
		List<FinODDetails> odDetailsList = getFinODDetailsDAO().getCustomerDues(custId);

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

		for (FinServiceInstruction instruction : finServiceInstructions) {
			if (StringUtils.equals(instruction.getFinEvent(), FinanceConstants.FINSER_EVENT_RATECHG)
					&& instruction.getFromDate() != null) {

				BigDecimal oldRate = getFinServiceInstructionDAO().getOldRate(instruction.getFinReference(),
						instruction.getFromDate());
				BigDecimal newRate = getFinServiceInstructionDAO().getNewRate(instruction.getFinReference(),
						instruction.getFromDate());

				LMSServiceLog lmsServiceLog = new LMSServiceLog();
				lmsServiceLog.setOldRate(oldRate);
				lmsServiceLog.setNewRate(newRate);
				lmsServiceLog.setEvent(instruction.getFinEvent());
				lmsServiceLog.setFinReference(instruction.getFinReference());
				lmsServiceLog.setNotificationFlag(PennantConstants.NO);
				lmsServiceLog.setEffectiveDate(instruction.getFromDate());
				lmsServiceLogs.add(lmsServiceLog);
			}
		}

		if (CollectionUtils.isNotEmpty(lmsServiceLogs)) {
			getFinServiceInstructionDAO().saveLMSServiceLOGList(lmsServiceLogs);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Getting Fin schedule details for rate report
	 */
	@Override
	public List<FinanceScheduleDetail> getFinSchdDetailsForRateReport(String finReference) {
		return getFinanceScheduleDetailDAO().getFinSchdDetailsForRateReport(finReference);
	}

	/*
	 * Getting the verification initiation details
	 */
	@Override
	public FinanceDetail getVerificationInitiationDetails(String finReference, VerificationType verificationType,
			String tableType) {

		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finReference, tableType, false);
		// Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		scheduleData.setFinanceMain(financeMain);

		// Finance Type Details
		FinanceType financeType = getFinanceTypeDAO().getOrgFinanceTypeByID(scheduleData.getFinanceMain().getFinType(),
				"_ORGView");
		scheduleData.setFinanceType(financeType);

		// Customer details
		if (financeMain.getCustID() != 0 && financeMain.getCustID() != Long.MIN_VALUE) {
			financeDetail.setCustomerDetails(
					getCustomerDetailsService().getCustomerDetailsById(financeMain.getCustID(), true, "_AView"));
		}

		// Collateral Details
		List<CollateralAssignment> assignmentListMain = null;
		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			assignmentListMain = getCollateralAssignmentDAO().getCollateralAssignmentByFinRef(finReference,
					FinanceConstants.MODULE_NAME, "_TView");
		} else {
			financeDetail
					.setFinanceCollaterals(getFinCollateralService().getFinCollateralsByRef(finReference, "_TView"));
		}

		// Collateral setup details and assignment details
		List<CollateralSetup> collateralSetupList = getCollateralSetupService().getCollateralDetails(finReference);
		List<CollateralAssignment> assignmentListTemp = null;
		if (CollectionUtils.isNotEmpty(collateralSetupList)) {
			if (ImplementationConstants.COLLATERAL_INTERNAL) {
				assignmentListTemp = getCollateralAssignmentDAO().getCollateralAssignmentByFinRef(finReference,
						FinanceConstants.MODULE_NAME, "_CTView");
			}
		}
		financeDetail.setCollaterals(collateralSetupList);
		financeDetail = setCollateralAssignments(financeDetail, assignmentListMain, assignmentListTemp);

		// Document Details
		List<DocumentDetails> documentList = getDocumentDetailsDAO().getDocumentDetailsByRef(finReference,
				FinanceConstants.MODULE_NAME, FinanceConstants.FINSER_EVENT_ORG, "_TView");
		if (financeDetail.getDocumentDetailsList() != null && !financeDetail.getDocumentDetailsList().isEmpty()) {
			financeDetail.getDocumentDetailsList().addAll(documentList);
		} else {
			financeDetail.setDocumentDetailsList(documentList);
		}

		logger.debug("Leaving");
		return financeDetail;
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

		if (finMain.getFinStartDate().compareTo(appDate) >= 0) {
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

	/**
	 * Getting Fin details for rate report
	 */
	@Override
	public FinanceMain getFinanceMainForRateReport(String finReference, String type) {
		return getFinanceScheduleDetailDAO().getFinanceMainForRateReport(finReference, type);
	}

	@Autowired
	public void setFinExcessAmountDAO(FinExcessAmountDAO finExcessAmountDAO) {
		this.finExcessAmountDAO = finExcessAmountDAO;
	}

	@Autowired(required = false)
	public void setSubventionDetailDAO(SubventionDetailDAO subventionDetailDAO) {
		this.subventionDetailDAO = subventionDetailDAO;
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
	public FinanceDetail getFinanceDetailsForPmay(String finReference) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainById(finReference, "_View", false);
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(financeMain.getFinReference());
		scheduleData.setFinanceMain(financeMain);

		financeDetail.setCustomerDetails(
				getCustomerDetailsService().getCustomerDetailsById(financeMain.getCustID(), true, "_AView"));
		financeDetail
				.setJountAccountDetailList(getJointAccountDetailService().getJoinAccountDetail(finReference, "_AView"));
		financeDetail.setPmay(pmayService.getPMAY(finReference, "_AView"));
		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	@Override
	public FinCustomerDetails getDetailsByOfferID(String offerID) {
		logger.debug(Literal.ENTERING);
		logger.debug(Literal.LEAVING);
		return getFinanceMainDAO().getDetailsByOfferID(offerID);
	}

	@Override
	public void saveDisbDetails(List<FinanceDisbursement> disbursementDetails, String finReference) {
		getFinanceDisbursementDAO().deleteByFinReference(finReference, "", false, 0);
		getFinanceDisbursementDAO().saveList(disbursementDetails, "", false);
	}

	@Override
	public void saveFinSchdDetail(List<FinanceScheduleDetail> financeScheduleDetails, String finReference) {
		getFinanceScheduleDetailDAO().deleteByFinReference(finReference, "", false, 0);
		getFinanceScheduleDetailDAO().saveList(financeScheduleDetails, "", false);

	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(String finReference, String string, boolean b) {
		return getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, string, b);
	}

	@Override
	public List<FinanceStepPolicyDetail> getFinStepDetailListByFinRef(String finReference, String type, boolean isWIF) {
		return getFinanceStepDetailDAO().getFinStepDetailListByFinRef(finReference, type, isWIF);
	}

	@Override
	public List<RepayInstruction> getRepayInstructions(String finReference, String type, boolean isWIF) {
		return getRepayInstructionDAO().getRepayInstructions(finReference, type, isWIF);
	}

	/**
	 * Method to fetch finance details by id from given table type
	 * 
	 * @param finReference
	 *            (String)
	 * @param type
	 *            (String)
	 * @return FinanceDetail
	 */
	@Override
	public FinanceDetail getFinSchdDetailByRef(String finReference, String type, boolean isWIF) {
		logger.debug(Literal.ENTERING);

		// Finance Details
		FinanceDetail financeDetail = new FinanceDetail();
		FinScheduleData scheduleData = financeDetail.getFinScheduleData();
		scheduleData.setFinReference(finReference);
		scheduleData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(finReference, type, isWIF));

		if (scheduleData.getFinanceMain() != null) {

			// Finance Type Details
			FinanceType financeType = getFinanceTypeDAO()
					.getOrgFinanceTypeByID(scheduleData.getFinanceMain().getFinType(), "_ORGView");
			if (StringUtils.isNotBlank(scheduleData.getFinanceMain().getPromotionCode())) {
				// Fetching Promotion Details
				Promotion promotion = this.promotionDAO
						.getPromotionById(scheduleData.getFinanceMain().getPromotionCode(), "_AView");
				financeType.setFInTypeFromPromotiion(promotion);
			}
			scheduleData.setFinanceType(financeType);

			// Step Policy Details List
			if (scheduleData.getFinanceMain().isStepFinance()) {
				scheduleData.setStepPolicyDetails(getFinanceStepDetailDAO().getFinStepDetailListByFinRef(finReference,
						isWIF ? "_View" : "_TView", isWIF));
			}

			// Finance Schedule Details
			scheduleData.setFinanceScheduleDetails(
					getFinanceScheduleDetailDAO().getFinScheduleDetails(finReference, type, isWIF));

			//Finance Disbursement Details
			scheduleData.setDisbursementDetails(getFinanceDisbursementDAO().getFinanceDisbursementDetails(finReference,
					isWIF ? "_View" : type, isWIF));

			// Finance Repayments Instruction Details
			scheduleData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(finReference, type, isWIF));

			// Fee Details
			List<FinFeeDetail> finOriginationFeeList = getFinFeeDetailDAO().getFinScheduleFees(finReference, false,
					"_View");
			scheduleData.setFinFeeDetailList(finOriginationFeeList);

			// Finance Fee Schedule Details
			if (finOriginationFeeList != null && !finOriginationFeeList.isEmpty()) {
				List<Long> feeIDList = new ArrayList<>();
				for (FinFeeDetail feeDetail : finOriginationFeeList) {
					feeIDList.add(feeDetail.getFeeID());
					feeDetail.setRcdVisible(false);
				}

				if (!feeIDList.isEmpty()) {
					List<FinFeeScheduleDetail> feeScheduleList = getFinFeeScheduleDetailDAO()
							.getFeeScheduleByFinID(feeIDList, false, "");

					if (feeScheduleList != null && !feeScheduleList.isEmpty()) {
						HashMap<Long, List<FinFeeScheduleDetail>> schFeeMap = new HashMap<>();
						for (int i = 0; i < feeScheduleList.size(); i++) {
							FinFeeScheduleDetail schdFee = feeScheduleList.get(i);

							List<FinFeeScheduleDetail> schList = new ArrayList<>();
							if (schFeeMap.containsKey(schdFee.getFeeID())) {
								schList = schFeeMap.get(schdFee.getFeeID());
								schFeeMap.remove(schdFee.getFeeID());
							}
							schList.add(schdFee);
							schFeeMap.put(schdFee.getFeeID(), schList);

						}

						for (int i = 0; i < finOriginationFeeList.size(); i++) {
							FinFeeDetail feeDetail = finOriginationFeeList.get(i);
							if (schFeeMap.containsKey(feeDetail.getFeeID())) {
								feeDetail.setFinFeeScheduleDetailList(schFeeMap.get(feeDetail.getFeeID()));
							}
						}
					}
				}
			}

			// Finance Overdue Penalty Rate Details
			if (!isWIF) {

				// Finance Flag Details
				financeDetail.setFinFlagsDetails(
						getFinFlagDetailsDAO().getFinFlagsByFinRef(finReference, FinanceConstants.MODULE_NAME, type));

			}

			// insurance Details
			scheduleData.setFinInsuranceList(getFinInsurancesDAO().getFinInsuranceListByRef(finReference, type, isWIF));

			if (scheduleData.getFinanceMain().istDSApplicable()) {
				scheduleData.setLowerTaxDeductionDetails(getLowerTaxDeductionDAO()
						.getLowerTaxDeductionDetails(scheduleData.getFinanceMain().getFinReference(), ""));
			}

			// FinSchFrqInsurance Details
			if (scheduleData.getFinInsuranceList() != null && !scheduleData.getFinInsuranceList().isEmpty()) {
				List<FinSchFrqInsurance> finSchFrqInsurances = getFinInsurancesDAO()
						.getFinSchFrqInsuranceFinRef(finReference, isWIF, "_View");
				HashMap<Long, List<FinSchFrqInsurance>> schInsMap = new HashMap<>();

				for (int i = 0; i < finSchFrqInsurances.size(); i++) {
					FinSchFrqInsurance finSchFrqInsurance = finSchFrqInsurances.get(i);

					List<FinSchFrqInsurance> schList = new ArrayList<>();
					if (schInsMap.containsKey(finSchFrqInsurance.getInsId())) {
						schList = schInsMap.get(finSchFrqInsurance.getInsId());
						schInsMap.remove(finSchFrqInsurance.getInsId());
					}
					schList.add(finSchFrqInsurance);
					schInsMap.put(finSchFrqInsurance.getInsId(), schList);

				}

				for (int i = 0; i < scheduleData.getFinInsuranceList().size(); i++) {
					FinInsurances finInsurance = scheduleData.getFinInsuranceList().get(i);
					if (StringUtils.equals(finInsurance.getPaymentMethod(), InsuranceConstants.PAYTYPE_SCH_FRQ)
							&& schInsMap.containsKey(finInsurance.getInsId())) {
						finInsurance.setFinSchFrqInsurances(schInsMap.get(finInsurance.getInsId()));
					}
				}
				schInsMap = null;
			}

			if (!isWIF) {

				String tableType = "";
				if (StringUtils.isNotBlank(scheduleData.getFinanceMain().getRecordType())) {
					tableType = "_Temp";
				}

				// Overdue Penalty Rates
				scheduleData.setFinODPenaltyRate(getFinODPenaltyRateDAO().getFinODPenaltyRateByRef(finReference,
						StringUtils.equals(tableType, "") ? type : tableType));

				// Overdraft Schedule Detail
				if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
						scheduleData.getFinanceMain().getProductCategory())) {
					scheduleData.setOverdraftScheduleDetails(getOverdraftScheduleDetailDAO()
							.getOverdraftScheduleDetails(finReference, tableType, isWIF));
				}

				// Fetch Secondary account Details
				if (ImplementationConstants.ACCOUNTS_APPLICABLE) {
					scheduleData.getFinanceMain().setSecondaryAccount(getSecondaryAccountDAO()
							.getSecondaryAccountsByFinRef(scheduleData.getFinanceMain().getFinReference(), type));
				}
			}

			// set advancepayments
			financeDetail.setAdvancePaymentsList(
					getFinAdvancePaymentsService().getFinAdvancePaymentsById(finReference, type));

		}
		logger.debug(Literal.LEAVING);
		return financeDetail;
	}

	private void processPricingLoans(FinanceDetail financeDetail) {
		PricingDetail pricingDetail = financeDetail.getPricingDetail();
		FinanceMain main = financeDetail.getFinScheduleData().getFinanceMain();
		if (pricingDetail != null) {

			List<FinanceMain> financeMains = pricingDetail.getFinanceMains();

			BigDecimal totalLoanAmt = main.getFinAssetValue();
			if (CollectionUtils.isNotEmpty(financeMains)) {
				for (FinanceMain financeMain : financeMains) {
					totalLoanAmt = totalLoanAmt.add(financeMain.getFinAssetValue());
				}
			}

			if (CollectionUtils.isNotEmpty(financeMains)) {
				CollateralAssignment collateralAssignment = null;
				if (CollectionUtils.isNotEmpty(financeMains) && pricingDetail.isSplit()) {
					for (FinanceMain financeMain : financeMains) {
						if (CollectionUtils.isNotEmpty(financeDetail.getCollateralAssignmentList())) {
							for (CollateralAssignment parentColAssignment : financeDetail
									.getCollateralAssignmentList()) {
								collateralAssignment = new CollateralAssignment();
								collateralAssignment.setReference(financeMain.getFinReference());
								collateralAssignment.setCollateralRef(parentColAssignment.getCollateralRef());

								collateralAssignment.setAssignPerc(setCollateralAssignmenForChildLoans(financeMain,
										totalLoanAmt, parentColAssignment.getAssignPerc()));
								parentColAssignment.setAssignPerc(parentColAssignment.getAssignPerc()
										.subtract(collateralAssignment.getAssignPerc()));
								collateralAssignment.setModule(FinanceConstants.MODULE_NAME);
								collateralAssignment.setWorkflowId(0);
								collateralAssignment.setVersion(main.getVersion());
								collateralAssignment.setLastMntBy(main.getLastMntBy());
								collateralAssignment.setLastMntOn(main.getLastMntOn());
								collateralAssignment.setRecordStatus(main.getRecordStatus());
								collateralAssignment.setRecordType(main.getRecordType());

								getCollateralAssignmentDAO().save(collateralAssignment, "_Temp");

								getCollateralAssignmentDAO().update(parentColAssignment, "_Temp");

							}
						}
						List<JointAccountDetail> jountAccountDetailList = financeDetail.getJountAccountDetailList();
						if (CollectionUtils.isNotEmpty(jountAccountDetailList)) {
							for (JointAccountDetail details : jountAccountDetailList) {
								Cloner cloner = new Cloner();
								JointAccountDetail jointAccountDetail = cloner.deepClone(details);
								jointAccountDetail.setId(Long.MIN_VALUE);
								jointAccountDetail.setFinReference(financeMain.getFinReference());
								jointAccountDetail.setNewRecord(true);
								if (!StringUtils.equals(details.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
									try {
										getJountAccountDetailDAO().save(jointAccountDetail, "_Temp");
									} catch (Exception e) {
										logger.error(Literal.EXCEPTION, e);
									}
								}
							}
						}
					}

				}

				for (FinanceMain financeMain : financeMains) {
					logger.debug("Ser FinReference :: " + StringUtils.trimToEmpty(financeMain.getFinReference()));
					logger.debug("Ser Investment Ref :: " + StringUtils.trimToEmpty(financeMain.getInvestmentRef()));
					logger.debug("Ser Parent Ref :: " + StringUtils.trimToEmpty(financeMain.getParentRef()));
					logger.debug("Ser Role Code :: " + StringUtils.trimToEmpty(financeMain.getRoleCode()));
					logger.debug("Ser Next RoleCode :: " + StringUtils.trimToEmpty(financeMain.getNextRoleCode()));
					logger.debug("Ser Record Status :: " + StringUtils.trimToEmpty(financeMain.getRecordStatus()));
					financeMain.setFinStartDate(main.getFinStartDate());

					String finType = financeMainDAO.getFinanceTypeFinReference(financeMain.getFinReference(), "_Temp");
					FinanceWorkFlow financeWorkFlow = getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(
							finType, FinanceConstants.FINSER_EVENT_ORG, PennantConstants.WORFLOW_MODULE_FINANCE);
					if (financeWorkFlow != null) {
						WorkFlowDetails workFlowDetails = WorkFlowUtil
								.getDetailsByType(financeWorkFlow.getWorkFlowType());
						if (workFlowDetails != null) {
							WorkflowEngine workflow = new WorkflowEngine(workFlowDetails.getWorkFlowXml());
							String taskid = workflow.getUserTaskId(workflow.firstTaskOwner());
							financeMain.setWorkflowId(workFlowDetails.getWorkFlowId());
							financeMain.setRoleCode(workflow.firstTaskOwner());
							financeMain.setNextRoleCode(workflow.firstTaskOwner());
							financeMain.setTaskId(taskid);
							financeMain.setNextTaskId(taskid + ";");
						}
					} else {
						financeMain.setRoleCode(main.getRoleCode());
						financeMain.setNextRoleCode(main.getNextRoleCode());
						financeMain.setTaskId(main.getTaskId());
						financeMain.setNextTaskId(main.getNextTaskId());
					}
					financeMain.setLastMntBy(main.getLastMntBy());
					financeMain.setLastMntOn(main.getLastMntOn());
					financeMain.setRecordStatus(main.getRecordStatus());
					financeMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					if (StringUtils.isNotBlank(financeMain.getParentRef())) {
						financeMain.setInvestmentRef("");
					}
					if (financeMain.isNewRecord()) {
						getFinanceMainDAO().save(financeMain, TableType.TEMP_TAB, false);
					} else {
						if (StringUtils.equals(financeMain.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
							// getFinanceMainDAO().delete(financeMain,
							// TableType.TEMP_TAB, false, true);
						} else {
							String[] roles = StringUtils.trimToEmpty(SysParamUtil.getValueAsString("BRANCH_OPS_ROLE"))
									.split(",");
							boolean update = false;
							update = true;
							for (String role : roles) {
								if (StringUtils.equals(
										financeDetail.getFinScheduleData().getFinanceMain().getRoleCode(), role)) {
									update = true;
									break;
								}
							}

							if (financeMain.getParentRef() == null || update) {
								getFinanceMainDAO().update(financeMain, TableType.TEMP_TAB, false);
							}
						}
					}
				}

				for (VASRecording vasRecording : pricingDetail.getTopUpVasDetails()) {
					vasRecording.setRecordType(main.getRecordType());
					vasRecording.setRecordStatus(main.getRecordStatus());
					vasRecording.setLastMntOn(main.getLastMntOn());
					vasRecording.setLastMntBy(main.getLastMntBy());

					String finType = financeMainDAO.getFinanceTypeFinReference(vasRecording.getPrimaryLinkRef(),
							"_Temp");
					FinanceWorkFlow financeWorkFlow = getFinanceWorkFlowService().getApprovedFinanceWorkFlowById(
							finType, FinanceConstants.FINSER_EVENT_ORG, PennantConstants.WORFLOW_MODULE_FINANCE);
					if (financeWorkFlow != null) {
						WorkFlowDetails workFlowDetails = WorkFlowUtil
								.getDetailsByType(financeWorkFlow.getWorkFlowType());
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
						getVasRecordingDAO().save(vasRecording, "_Temp");
					} else {
						if (StringUtils.equals(vasRecording.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
							getVasRecordingDAO().delete(vasRecording, "_Temp");
						} else {
							getVasRecordingDAO().update(vasRecording, "_Temp");
						}
					}
				}

				// Delete parent data
				for (FinanceMain financeMain : financeMains) {
					if (!financeMain.isNewRecord()
							&& StringUtils.equals(financeMain.getRecordType(), PennantConstants.RECORD_TYPE_CAN)) {
						getFinanceMainDAO().delete(financeMain, TableType.TEMP_TAB, false, true);
					}
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

	public ReasonDetailDAO getReasonDetailDAO() {
		return reasonDetailDAO;
	}

	public void setReasonDetailDAO(ReasonDetailDAO reasonDetailDAO) {
		this.reasonDetailDAO = reasonDetailDAO;
	}

	public void setFinChequeHeaderService(FinChequeHeaderService finChequeHeaderService) {
		this.finChequeHeaderService = finChequeHeaderService;
	}

	public FinTypeExpenseDAO getFinTypeExpenseDAO() {
		return finTypeExpenseDAO;
	}

	public void setFinTypeExpenseDAO(FinTypeExpenseDAO finTypeExpenseDAO) {
		this.finTypeExpenseDAO = finTypeExpenseDAO;
	}

	public FinExpenseDetailsDAO getFinExpenseDetailsDAO() {
		return finExpenseDetailsDAO;
	}

	public void setFinExpenseDetailsDAO(FinExpenseDetailsDAO finExpenseDetailsDAO) {
		this.finExpenseDetailsDAO = finExpenseDetailsDAO;
	}

	public void setCreditInformation(CreditInformation creditInformation) {
		this.creditInformation = creditInformation;
	}

	public FinIRRDetailsDAO getFinIRRDetailsDAO() {
		return finIRRDetailsDAO;
	}

	public void setFinIRRDetailsDAO(FinIRRDetailsDAO finIRRDetailsDAO) {
		this.finIRRDetailsDAO = finIRRDetailsDAO;
	}

	public void setVehicleDealerService(VehicleDealerService vehicleDealerService) {
		this.vehicleDealerService = vehicleDealerService;
	}

	public CollateralSetupService getCollateralSetupService() {
		return collateralSetupService;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	public PSLDetailService getpSLDetailService() {
		return pSLDetailService;
	}

	public void setpSLDetailService(PSLDetailService pSLDetailService) {
		this.pSLDetailService = pSLDetailService;
	}

	public PSLDetailDAO getpSLDetailDAO() {
		return pSLDetailDAO;
	}

	public void setpSLDetailDAO(PSLDetailDAO pSLDetailDAO) {
		this.pSLDetailDAO = pSLDetailDAO;
	}

	public LegalDetailService getLegalDetailService() {
		return legalDetailService;
	}

	public void setLegalDetailService(LegalDetailService legalDetailService) {
		this.legalDetailService = legalDetailService;
	}

	public FinReceiptHeaderDAO getFinReceiptHeaderDAO() {
		return finReceiptHeaderDAO;
	}

	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}

	public HoldDisbursementDAO getHoldDisbursementDAO() {
		return holdDisbursementDAO;
	}

	public void setHoldDisbursementDAO(HoldDisbursementDAO holdDisbursementDAO) {
		this.holdDisbursementDAO = holdDisbursementDAO;
	}

	public LowerTaxDeductionDAO getLowerTaxDeductionDAO() {
		return lowerTaxDeductionDAO;
	}

	public void setLowerTaxDeductionDAO(LowerTaxDeductionDAO lowerTaxDeductionDAO) {
		this.lowerTaxDeductionDAO = lowerTaxDeductionDAO;
	}

	public void setInsuranceDetailService(InsuranceDetailService insuranceDetailService) {
		this.insuranceDetailService = insuranceDetailService;
	}

	public void setPaymentsProcessService(PaymentsProcessService paymentsProcessService) {
		this.paymentsProcessService = paymentsProcessService;
	}

	public BaseRateCodeDAO getBaseRateCodeDAO() {
		return baseRateCodeDAO;
	}

	public void setBaseRateCodeDAO(BaseRateCodeDAO baseRateCodeDAO) {
		this.baseRateCodeDAO = baseRateCodeDAO;
	}

	public CreditReviewDetailDAO getCreditReviewDetailDAO() {
		return creditReviewDetailDAO;
	}

	public void setCreditReviewDetailDAO(CreditReviewDetailDAO creditReviewDetailDAO) {
		this.creditReviewDetailDAO = creditReviewDetailDAO;
	}

	public void setIrrScheduleDetailDAO(IRRScheduleDetailDAO irrScheduleDetailDAO) {
		this.irrScheduleDetailDAO = irrScheduleDetailDAO;
	}

	public RisksAndMitigantsDAO getRisksAndMitigantsDAO() {
		return risksAndMitigantsDAO;
	}

	public void setRisksAndMitigantsDAO(RisksAndMitigantsDAO risksAndMitigantsDAO) {
		this.risksAndMitigantsDAO = risksAndMitigantsDAO;
	}

	public SanctionConditionsDAO getSanctionConditionsDAO() {
		return sanctionConditionsDAO;
	}

	public void setSanctionConditionsDAO(SanctionConditionsDAO sanctionConditionsDAO) {
		this.sanctionConditionsDAO = sanctionConditionsDAO;
	}

	public DealRecommendationMeritsDAO getDealRecommendationMeritsDAO() {
		return dealRecommendationMeritsDAO;
	}

	public void setDealRecommendationMeritsDAO(DealRecommendationMeritsDAO dealRecommendationMeritsDAO) {
		this.dealRecommendationMeritsDAO = dealRecommendationMeritsDAO;
	}

	public DueDiligenceDetailsDAO getDueDiligenceDetailsDAO() {
		return dueDiligenceDetailsDAO;
	}

	public void setDueDiligenceDetailsDAO(DueDiligenceDetailsDAO dueDiligenceDetailsDAO) {
		this.dueDiligenceDetailsDAO = dueDiligenceDetailsDAO;
	}

	public RecommendationNotesDetailsDAO getRecommendationNotesDetailsDAO() {
		return recommendationNotesDetailsDAO;
	}

	public void setRecommendationNotesDetailsDAO(RecommendationNotesDetailsDAO recommendationNotesDetailsDAO) {
		this.recommendationNotesDetailsDAO = recommendationNotesDetailsDAO;
	}

	public SynopsisDetailsService getSynopsisDetailsService() {
		return synopsisDetailsService;
	}

	public void setSynopsisDetailsService(SynopsisDetailsService synopsisDetailsService) {
		this.synopsisDetailsService = synopsisDetailsService;
	}

	public void setDrawingPowerService(DrawingPowerService drawingPowerService) {
		this.drawingPowerService = drawingPowerService;
	}

	public void setCashBackProcessService(CashBackProcessService cashBackProcessService) {
		this.cashBackProcessService = cashBackProcessService;
	}

	public FinFeeConfigService getFinFeeConfigService() {
		return finFeeConfigService;
	}

	public void setFinFeeConfigService(FinFeeConfigService finFeeConfigService) {
		this.finFeeConfigService = finFeeConfigService;
	}

	public FeeCalculator getFeeCalculator() {
		return feeCalculator;
	}

	public void setFeeCalculator(FeeCalculator feeCalculator) {
		this.feeCalculator = feeCalculator;
	}

	public PMAYService getPmayService() {
		return pmayService;
	}

	public void setPmayService(PMAYService pMAYService) {
		pmayService = pMAYService;
	}

	public JountAccountDetailDAO getJountAccountDetailDAO() {
		return jountAccountDetailDAO;
	}

	public void setJountAccountDetailDAO(JountAccountDetailDAO jountAccountDetailDAO) {
		this.jountAccountDetailDAO = jountAccountDetailDAO;
	}

	public FinanceWorkFlowService getFinanceWorkFlowService() {
		return financeWorkFlowService;
	}

	public void setFinanceWorkFlowService(FinanceWorkFlowService financeWorkFlowService) {
		this.financeWorkFlowService = financeWorkFlowService;
	}

	public void setPostExteranalServiceHook(PostExteranalServiceHook postExteranalServiceHook) {
		this.postExteranalServiceHook = postExteranalServiceHook;
	}

	public void setProvisionDAO(ProvisionDAO provisionDAO) {
		this.provisionDAO = provisionDAO;
	}

}
