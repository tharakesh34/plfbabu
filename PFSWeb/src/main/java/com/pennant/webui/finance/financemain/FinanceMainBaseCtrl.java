/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *
 * FileName    		:  FinanceMainBaseCtrl.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 * 17-04-2018		Vinay					 0.2	   	As per the existing functionality 	*
 * 														developed for AIB, application will *
 * 														automatically set the frequency 	*
 * 														cycle date with the day of the Loan *
 * 														Start Date. Due to this, default 	*
 * 														cycle date provided in the Loan 	*
 * 														Type is not getting defaulted in 	*
 * 														the Loan Origination, if the date 	*
 * 														in the loan start date is not part	*
 * 														of the default frequency cycle 		*
 * 														date.As discussed with Raju, this 	*
 * 														has to be removed for Core 			*
 * 														Functionality and hence the 		*
 * 														Condition is removed and committed. *                                                                                     
 *                                                                                          * 
 * 23-04-2018		Vinay					0.3			As per mail from raju, 				*
 * 														Eligibility Method filed added 		*
 * 														for Profectus.                      * 
 *                                                                                          * 
 *                                                                                          * 
 * 08-05-2019		Srinivasa Varma			0.4			Development Item 81                 *
 *                                                                                          *
 * 10-05-2019		Srinivasa Varma			0.5			Development Item 82                 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.login.AccountNotFoundException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.InputElement;

import com.aspose.words.SaveFormat;
import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.FrequencyBox;
import com.pennant.RateBox;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.Interface.service.DDAInterfaceService;
import com.pennant.Interface.service.NorkamCheckService;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.core.InstallmentDueService;
import com.pennant.app.finance.limits.LimitCheckDetails;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.MailUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.SMSUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.financeservice.ReScheduleService;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.MMAgreement.MMAgreement;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceMainExt;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.OverdraftScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RolledoverFinanceDetail;
import com.pennant.backend.model.finance.RolledoverFinanceHeader;
import com.pennant.backend.model.finance.SecondaryAccount;
import com.pennant.backend.model.finance.TATDetail;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.finance.psl.PSLDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.limits.LimitDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.solutionfactory.DeviationHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.backend.service.applicationmaster.AgreementDefinitionService;
import com.pennant.backend.service.applicationmaster.BaseRateService;
import com.pennant.backend.service.collateral.CollateralMarkProcess;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.dda.DDAControllerService;
import com.pennant.backend.service.dda.DDAProcessService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.service.extendedfields.ExtendedFieldDetailsService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.FinanceMainExtService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.impl.CreditReviewSummaryData;
import com.pennant.backend.service.legal.LegalDetailService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.service.mail.MailTemplateService;
import com.pennant.backend.service.notifications.NotificationsService;
import com.pennant.backend.service.payorderissue.impl.DisbursementPostings;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.service.sms.ShortMessageService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.StageTabConstants;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.component.Uppercasebox;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.core.EventManager;
import com.pennant.core.EventManager.Notify;
import com.pennant.util.AgreementEngine;
import com.pennant.util.AgreementGeneration;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.dedup.dedupparm.DedupValidation;
import com.pennant.webui.delegationdeviation.DeviationExecutionCtrl;
import com.pennant.webui.finance.financemain.stepfinance.StepDetailDialogCtrl;
import com.pennant.webui.finance.financetaxdetail.FinanceTaxDetailDialogCtrl;
import com.pennant.webui.finance.payorderissue.DisbursementInstCtrl;
import com.pennant.webui.finance.psldetails.PSLDetailDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.mandate.mandate.MandateDialogCtrl;
import com.pennant.webui.pdfupload.PdfParserCaller;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.service.sampling.SamplingService;
import com.pennanttech.webui.sampling.FinSamplingDialogCtrl;
import com.pennanttech.webui.verification.FieldVerificationDialogCtrl;
import com.pennanttech.webui.verification.LVerificationCtrl;
import com.pennanttech.webui.verification.RCUVerificationDialogCtrl;
import com.pennanttech.webui.verification.TVerificationDialogCtrl;
import com.rits.cloning.Cloner;

import freemarker.template.TemplateException;

/**
 * Base controller for creating the controllers of the zul files with the spring framework.
 * 
 */
public class FinanceMainBaseCtrl extends GFCBaseCtrl<FinanceMain> {
	private static final long serialVersionUID = -1171206258809472640L;
	private static final Logger logger = Logger.getLogger(FinanceMainBaseCtrl.class);

	protected Label windowTitle;

	protected Datebox finStartDate;
	protected Textbox promotionProduct;
	protected Textbox finDivisionName;
	protected Textbox finType;
	protected Textbox finReference;
	protected Space space_finReference;
	protected ExtendedCombobox finCcy;
	protected Combobox cbProfitDaysBasis;
	protected Longbox custID;
	protected Textbox custCIF;
	protected Label custShrtName;
	protected Button viewCustInfo;
	protected ExtendedCombobox finBranch;
	protected Datebox finContractDate;
	protected CurrencyBox finAmount;
	protected Label netFinAmount;
	protected CurrencyBox finAssetValue;
	protected CurrencyBox finCurrentAssetValue;
	protected Row row_FinAssetValue;
	protected Row row_RolloverPayment;
	protected CurrencyBox downPayBank;
	protected AccountSelectionBox downPayAccount;
	protected Hbox hbox_PromotionProduct;
	protected Label label_FinanceMainDialog_PromotionProduct;
	protected Row row_downPayBank;
	protected Row row_disbAcctId;
	protected Row row_secondaryAccount;
	protected CurrencyBox downPaySupl;
	protected Row row_downPayPercentage;
	protected Label downPayPercentage;
	protected Row row_downPaySupl;
	protected Row defermentsRow;
	protected Intbox defferments;
	protected Intbox planDeferCount;
	protected Hbox hbox_PlanDeferCount;
	protected AccountSelectionBox disbAcctId;
	protected AccountSelectionBox repayAcctId;
	protected FrequencyBox depreciationFrq;
	protected ExtendedCombobox commitmentRef;
	protected Row row_commitment;
	protected Row rowLimitRef;
	protected ExtendedCombobox finLimitRef;
	protected Textbox finRemarks;
	protected Checkbox finIsActive;
	protected ExtendedCombobox finPurpose;
	protected CurrencyBox securityDeposit;
	protected Row row_securityDeposit;
	protected Row row_accountsOfficer;
	//protected Row											row_salesDept;
	protected Row row_ReferralId;
	protected ExtendedCombobox accountsOfficer;
	protected Row row_employeeName;
	protected ExtendedCombobox employeeName;
	protected ExtendedCombobox dsaCode;
	protected Hbox hbox_tdsApplicable;
	protected Checkbox tDSApplicable;
	protected Label label_FinanceMainDialog_TDSApplicable;
	//Facility Details
	protected Row rowFacilityAmounts;
	protected Row rowFacilityDateRate;
	protected Row rowFacilityNotes;
	protected Decimalbox facilityAmount;
	protected Decimalbox facilityUtilizedAmount;
	protected Decimalbox facilityAvaliableAmount;
	protected Datebox facilityExpiryDate;
	protected Decimalbox facilityBaseRate;
	protected Decimalbox facilityMarginRate;
	protected Textbox facilityNotes;
	protected ExtendedCombobox mMAReference;

	// Step Finance Details
	protected Checkbox stepFinance;
	protected ExtendedCombobox stepPolicy;
	protected Label label_FinanceMainDialog_StepPolicy;
	protected Label label_FinanceMainDialog_numberOfSteps;
	protected Checkbox alwManualSteps;
	protected Intbox noOfSteps;
	protected Row row_stepFinance;
	protected Row row_manualSteps;
	protected Space space_StepPolicy;
	protected Space space_noOfSteps;
	protected Hbox hbox_numberOfSteps;
	protected Row row_shariaApproval;
	protected Checkbox shariaApprovalReq;
	protected Combobox stepType;
	protected Space space_stepType;
	protected Row row_stepType;

	protected CurrencyBox custPaymentAmount;
	protected AccountSelectionBox custPayAccId;
	protected CurrencyBox latePayAmount;
	protected CurrencyBox latePayWaiverAmount;
	protected Row row_ManualSchedule;
	protected Checkbox manualSchedule;
	protected Uppercasebox applicationNo;
	protected ExtendedCombobox referralId;
	protected ExtendedCombobox dmaCode;
	protected ExtendedCombobox salesDepartment;
	protected Checkbox quickDisb;
	protected Label label_FinanceMainDialog_QuickDisb;
	protected Label label_FinanceMainDialog_RepayRvwFrq;
	// Finance Main Details Tab---> Rollover Finance Details
	protected Groupbox gb_RolloverFinance;
	protected Listbox listBoxRolledoverFinance;

	//Finance Main Details Tab---> 2. Grace Period Details

	protected Groupbox gb_gracePeriodDetails;

	protected Intbox graceTerms;
	protected Intbox graceTerms_Two;
	protected Checkbox allowGrace;
	protected Space space_grcPeriodEndDate;
	protected Datebox gracePeriodEndDate;
	protected Datebox gracePeriodEndDate_two;
	protected Combobox grcRateBasis;
	protected Decimalbox gracePftRate;
	protected RateBox graceRate;
	protected Row row_FinGrcRates;
	protected Decimalbox finGrcMinRate;
	protected Decimalbox finGrcMaxRate;
	protected Combobox grcPftDaysBasis;
	protected Row grcPftFrqRow;
	protected FrequencyBox gracePftFrq;
	protected Datebox nextGrcPftDate;
	protected Datebox nextGrcPftDate_two;
	protected Row grcPftRvwFrqRow;
	protected FrequencyBox gracePftRvwFrq;
	protected Datebox nextGrcPftRvwDate;
	protected Datebox nextGrcPftRvwDate_two;
	protected Row grcCpzFrqRow;
	protected FrequencyBox graceCpzFrq;
	protected Datebox nextGrcCpzDate;
	protected Datebox nextGrcCpzDate_two;
	protected Row grcRepayRow;
	protected Checkbox allowGrcRepay;
	protected Combobox cbGrcSchdMthd;
	protected Space space_GrcSchdMthd;
	protected Row grcBaseRateRow;
	protected Row row_GrcPftDayBasis;
	protected Row row_GrcMaxAmount;
	protected CurrencyBox grcMaxAmount;

	//Advised Profit Rates
	protected RateBox grcAdvRate;
	protected Decimalbox grcAdvPftRate;
	protected Row row_GrcAdvBaseRate;
	protected Row row_GrcAdvPftRate;

	//Finance Main Details Tab---> 3. Repayment Period Details

	protected Groupbox gb_repaymentDetails;

	protected Intbox numberOfTerms;
	protected Intbox numberOfTerms_two;
	protected CurrencyBox finRepaymentAmount;
	protected Combobox repayRateBasis;
	protected Decimalbox repayProfitRate;
	//protected Decimalbox									repayEffectiveRate;
	protected Row repayBaseRateRow;
	protected RateBox repayRate;
	protected Row row_FinRepRates;
	protected Decimalbox finMinRate;
	protected Decimalbox finMaxRate;
	protected Combobox cbScheduleMethod;
	protected Row rpyPftFrqRow;
	protected FrequencyBox repayPftFrq;
	protected Datebox nextRepayPftDate;
	protected Datebox nextRepayPftDate_two;
	protected Row rpyRvwFrqRow;
	protected FrequencyBox repayRvwFrq;
	protected Datebox nextRepayRvwDate;
	protected Datebox nextRepayRvwDate_two;
	protected Row rpyCpzFrqRow;
	protected FrequencyBox repayCpzFrq;
	protected Datebox nextRepayCpzDate;
	protected Datebox nextRepayCpzDate_two;
	protected FrequencyBox repayFrq;
	protected Datebox nextRepayDate;
	protected Datebox nextRepayDate_two;
	protected Checkbox finRepayPftOnFrq;
	protected Row rpyFrqRow;
	protected Datebox maturityDate;
	protected Datebox maturityDate_two;
	protected Row row_MaturityDate;
	protected Combobox finRepayMethod;
	protected FrequencyBox rolloverFrq;
	protected Datebox nextRollOverDate;
	protected Datebox nextRollOverDate_two;
	protected Row rolloverFrqRow;

	protected Hbox hbox_finRepayPftOnFrq;
	protected Hbox hbox_ScheduleMethod;
	protected Row noOfTermsRow;
	
	protected Row row_advEMITerms;
	protected Intbox advEMITerms;

	//Advised Profit Rates
	protected RateBox rpyAdvRate;
	protected Decimalbox rpyAdvPftRate;
	protected Row row_RpyAdvBaseRate;
	protected Row row_RpyAdvPftRate;

	protected Row row_supplementRent;
	protected CurrencyBox supplementRent;
	protected CurrencyBox increasedCost;

	// Planned Emi Holidays
	protected Row row_BpiTreatment;
	protected Checkbox alwBpiTreatment;
	protected Space space_DftBpiTreatment;
	protected Combobox dftBpiTreatment;
	protected Space space_PftDueSchdOn;
	protected Checkbox alwPlannedEmiHoliday;
	protected Hbox hbox_planEmiMethod;
	protected Combobox planEmiMethod;
	protected Row row_PlannedEMIH;
	protected Row row_MaxPlanEmi;
	protected Row row_UnPlanEmiHLockPeriod;
	protected Row row_MaxUnPlannedEMIH;
	protected Row row_ReAge;
	protected Intbox maxPlanEmiPerAnnum;
	protected Intbox maxPlanEmi;
	protected Row row_PlanEmiHLockPeriod;
	protected Intbox planEmiHLockPeriod;
	protected Checkbox cpzAtPlanEmi;
	protected Intbox unPlannedEmiHLockPeriod;
	protected Intbox maxUnplannedEmi;
	protected Intbox maxReAgeHolidays;
	protected Checkbox cpzAtUnPlannedEmi;
	protected Checkbox cpzAtReAge;
	protected Combobox roundingMode;

	//Finance Main Details Tab---> 4. Overdue Penalty Details
	protected Groupbox gb_OverDuePenalty;
	protected Checkbox applyODPenalty;
	protected Checkbox oDIncGrcDays;
	protected Combobox oDChargeType;
	protected Intbox oDGraceDays;
	protected Combobox oDChargeCalOn;
	protected Decimalbox oDChargeAmtOrPerc;
	protected Checkbox oDAllowWaiver;
	protected Decimalbox oDMaxWaiverPerc;
	//###_0.3
	protected Row row_EligibilityMethod;
	protected ExtendedCombobox eligibilityMethod;

	protected Row row_Connector;
	protected ExtendedCombobox connector;

	protected Row samplingRequiredRow;
	protected Checkbox samplingRequired;
	
	protected Row legalRequiredRow;
	protected Checkbox legalRequired;

	protected Space space_oDChargeAmtOrPerc;
	protected Space space_oDMaxWaiverPerc;
	protected Space space_oDChargeCalOn;
	protected Space space_oDChargeType;

	//Finance Main Details Tab---> 5. DDA Request Details
	protected Groupbox gb_ddaRequest;
	protected ExtendedCombobox bankName;
	protected Textbox iban;
	protected Uppercasebox ifscCode;
	protected Label label_Financemain_IfscCode;
	protected Hbox hbox_Financemain_IfscCode;
	protected Combobox accountType;

	private Label label_FinanceMainDialog_FinType;
	private Label label_FinanceMainDialog_ScheduleMethod;
	private Label label_FinanceMainDialog_FinRepayPftOnFrq;
	private Label label_FinanceMainDialog_CommitRef;
	private Label label_FinanceMainDialog_FinLimitRef;
	private Label label_FinanceMainDialog_DepriFrq;
	private Label label_FinanceMainDialog_PlanDeferCount;
	private Label label_FinanceMainDialog_AlwGrace;
	private Label label_FinanceMainDialog_PromoProduct;
	protected Label label_FinanceMainDialog_PlanEmiHolidayMethod;

	protected Label label_FinanceMainDialog_CustPayAccId;
	protected Label label_FinanceMainDialog_DisbAcctId;
	protected Label label_FinanceMainDialog_RepayAcctId;
	protected Label label_FinanceMainDialog_SecondaryAccount;
	protected Label label_FinanceMainDialog_DownPayAccount;
	protected Label label_FinanceMainDialog_SalesDepartment;

	//DIV Components for Showing Finance basic Details in Each tab
	protected Div basicDetailTabDiv;

	//Search Button for value Selection
	protected Button btnSearchFinType;
	protected Textbox lovDescFinTypeName;

	protected Button btnValidate;
	protected Button btnBuildSchedule;
	protected Button btnSearchCustCIF;

	protected transient BigDecimal oldVar_finAmount;
	protected transient BigDecimal oldVar_utilizedAmount;
	protected transient BigDecimal oldVar_downPayBank;
	protected transient BigDecimal oldVar_downPaySupl;
	protected transient int oldVar_tenureInMonths;
	protected transient int oldVar_finRepayMethod;
	protected transient String oldVar_finType;
	protected transient String oldVar_finCcy;
	protected transient int oldVar_profitDaysBasis;
	protected transient String oldVar_depreciationFrq;
	protected transient Date oldVar_finStartDate;
	protected transient boolean oldVar_tDSApplicable;
	protected transient BigDecimal oldVar_finAssetValue;
	protected transient BigDecimal oldVar_finCurrAssetValue;
	protected transient boolean oldVar_manualSchedule;
	// Step Finance Details
	protected transient boolean oldVar_stepFinance;
	protected transient String oldVar_stepPolicy;
	protected transient boolean oldVar_alwManualSteps;
	protected transient int oldVar_noOfSteps;
	protected transient int oldVar_stepType;
	protected transient int oldVar_planDeferCount;
	protected transient List<FinanceStepPolicyDetail> oldVar_finStepPolicyList;
	protected transient Date oldVar_gracePeriodEndDate;
	protected transient boolean oldVar_allowGrace;
	protected transient int oldVar_graceTerms;
	protected transient int oldVar_grcRateBasis;
	protected transient String oldVar_graceBaseRate;
	protected transient BigDecimal oldVar_finGrcMinRate;
	protected transient BigDecimal oldVar_finGrcMaxRate;
	protected transient String oldVar_graceSpecialRate;
	protected transient BigDecimal oldVar_gracePftRate;
	protected transient String oldVar_gracePftFrq;
	protected transient BigDecimal oldVar_grcMargin;
	protected transient int oldVar_grcPftDaysBasis;
	protected transient Date oldVar_nextGrcPftDate;
	protected transient String oldVar_gracePftRvwFrq;
	protected transient Date oldVar_nextGrcPftRvwDate;
	protected transient String oldVar_graceCpzFrq;
	protected transient Date oldVar_nextGrcCpzDate;
	protected transient boolean oldVar_allowGrcRepay;
	protected transient int oldVar_grcSchdMthd;
	private transient String oldVar_grcAdvBaseRate;
	private transient BigDecimal oldVar_grcAdvMargin;
	private transient BigDecimal oldVar_grcAdvPftRate;
	private transient BigDecimal oldVar_grcMaxAmount;
	protected transient int oldVar_numberOfTerms;
	protected transient BigDecimal oldVar_finRepaymentAmount;
	protected transient String oldVar_repayFrq;
	protected transient String oldVar_repayBaseRate;
	protected transient Date oldVar_nextRepayDate;
	protected transient String oldVar_rolloverFrq;
	protected transient Date oldVar_nextRolloverDate;
	protected transient Date oldVar_maturityDate;
	protected transient boolean oldVar_finRepayPftOnFrq;
	protected transient int oldVar_repayRateBasis;
	protected transient BigDecimal oldVar_finMinRate;
	protected transient BigDecimal oldVar_finMaxRate;
	protected transient String oldVar_repaySpecialRate;
	protected transient BigDecimal oldVar_repayProfitRate;
	protected transient BigDecimal oldVar_repayMargin;
	private transient String oldVar_rpyAdvBaseRate;
	private transient BigDecimal oldVar_rpyAdvMargin;
	private transient BigDecimal oldVar_rpyAdvPftRate;
	private transient BigDecimal oldVar_supplementRent;
	private transient BigDecimal oldVar_increasedCost;
	protected transient int oldVar_scheduleMethod;
	protected transient String oldVar_repayPftFrq;
	protected transient Date oldVar_nextRepayPftDate;
	protected transient String oldVar_repayRvwFrq;
	protected transient Date oldVar_nextRepayRvwDate;
	protected transient String oldVar_repayCpzFrq;
	protected transient Date oldVar_nextRepayCpzDate;
	protected transient boolean oldVar_alwBpiTreatment;
	protected transient int oldVar_dftBpiTreatment;
	protected transient boolean oldVar_alwPlannedEmiHoliday;
	protected transient int oldVar_planEmiMethod;
	protected transient int oldVar_maxPlanEmiPerAnnum;
	protected transient int oldVar_maxPlanEmi;
	protected transient int oldVar_planEmiHLockPeriod;
	protected transient boolean oldVar_cpzAtPlanEmi;

	protected transient List<Integer> oldVar_planEMIMonths;
	protected transient List<Date> oldVar_planEMIDates;

	protected Vbox discrepancies;

	//Main Tab Details

	protected Tabbox tabBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab financeTypeDetailsTab;
	protected Tab custDetailTab;
	protected Rows additionalDetails;

	//External Fields usage for Individuals ---->  Schedule Details

	protected boolean recSave;
	protected boolean buildEvent;

	protected Component childWindow;
	protected Component checkListChildWindow;
	protected Component customerWindow;
	protected Component contributorWindow;
	protected Component etihadCreditBureauWindow;
	protected Component bundledProductsWindow;
	protected Component evaluationWindow;
	protected Component advancePaymentWindow;
	protected Component feeDetailWindow;
	protected Component covenantTypeWindow;
	protected Component agreementfieldsWindow;
	protected Component assetTypeWindow;

	// Temporary Fix for the User Next role Modification On Submit-Fail & Saving the record
	protected String curRoleCode;
	protected String curNextRoleCode;
	protected String curTaskId;
	protected String curNextTaskId;
	protected String curNextUserId;

	protected Checkbox pftServicingODLimit;
	protected FrequencyBox droplineFrq;
	protected Datebox firstDroplineDate;
	protected Intbox odYearlyTerms;
	protected Intbox odMnthlyTerms;
	protected Datebox odMaturityDate;
	protected Space space_DroplineDate;
	protected Row row_DroplineFrq;

	//Finance Flag
	protected Button btnFlagDetails;
	protected Textbox flagDetails;

	// Enquiry Components
	protected Combobox enquiryCombobox;
	protected Label enquiryLabel;

	protected transient boolean oldVar_pftServicingODLimit;
	protected transient String oldVar_droplineFrq;
	protected transient Date oldVar_firstDroplineDate;
	protected transient int oldVar_odYearlyTerms;
	protected transient int oldVar_odMnthlyTerms;

	//Sub Window Child Details Dialog Controllers
	private transient ScheduleDetailDialogCtrl scheduleDetailDialogCtrl;
	private transient ContributorDetailsDialogCtrl contributorDetailsDialogCtrl;
	private transient StepDetailDialogCtrl stepDetailDialogCtrl;
	private transient EligibilityDetailDialogCtrl eligibilityDetailDialogCtrl;
	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl;
	private transient AccountingDetailDialogCtrl accountingDetailDialogCtrl;
	private transient StageAccountingDetailDialogCtrl stageAccountingDetailDialogCtrl;
	private transient JointAccountDetailDialogCtrl jointAccountDetailDialogCtrl;
	private transient AgreementDetailDialogCtrl agreementDetailDialogCtrl;
	private transient AgreementGeneration agreementGeneration;
	private transient ScoringDetailDialogCtrl scoringDetailDialogCtrl;
	private transient FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl;
	private transient Object childWindowDialogCtrl;
	private transient CustomerDialogCtrl customerDialogCtrl;
	private transient DisbursementDetailDialogCtrl disbursementDetailDialogCtrl;
	private transient ChequeDetailDialogCtrl chequeDetailDialogCtrl;
	private transient DeviationDetailDialogCtrl deviationDetailDialogCtrl;
	private transient MandateDialogCtrl mandateDialogCtrl;
	private transient FinanceTaxDetailDialogCtrl financeTaxDetailDialogCtrl;
	private transient EtihadCreditBureauDetailDialogCtrl etihadCreditBureauDetailDialogCtrl;
	private transient BundledProductsDetailDialogCtrl bundledProductsDetailDialogCtrl;
	private transient FinAssetEvaluationDialogCtrl finAssetEvaluationDialogCtrl;
	private transient FinAdvancePaymentsListCtrl finAdvancePaymentsListCtrl;
	private transient FinFeeDetailListCtrl finFeeDetailListCtrl;
	private transient FinCovenantTypeListCtrl finCovenantTypeListCtrl;
	@Autowired
	private transient DeviationExecutionCtrl deviationExecutionCtrl;
	private transient FinCollateralHeaderDialogCtrl finCollateralHeaderDialogCtrl;
	private transient CollateralHeaderDialogCtrl collateralHeaderDialogCtrl;
	private transient FinVasRecordingDialogCtrl finVasRecordingDialogCtrl;
	private transient AgreementFieldsDetailDialogCtrl agreementFieldsDetailDialogCtrl;
	private transient ManualScheduleDetailDialogCtrl manualScheduleDetailDialogCtrl;
	private transient OverdraftScheduleDetailDialogCtrl overdraftScheduleDetailDialogCtrl;
	private transient FieldVerificationDialogCtrl fieldVerificationDialogCtrl;
	private transient TVerificationDialogCtrl tVerificationDialogCtrl;
	private transient LVerificationCtrl lVerificationCtrl;
	private transient RCUVerificationDialogCtrl rcuVerificationDialogCtrl;
	private transient FinSamplingDialogCtrl finSamplingDialogCtrl;
	private transient PSLDetailDialogCtrl pSLDetailDialogCtrl;
	private transient CreditReviewSummaryData creditReviewSummaryData;
	private transient CreditApplicationReviewService creditApplicationReviewService;

	private transient FinBasicDetailsCtrl finBasicDetailsCtrl;
	private transient CustomerInterfaceService customerInterfaceService;
	private LimitCheckDetails limitCheckDetails;
	private DDAControllerService ddaControllerService;
	private DDAInterfaceService ddaInterfaceService;
	private DDAProcessService ddaProcessService;
	private NorkamCheckService norkamCheckService;
	private CustomerDetailsService customerDetailsService;
	private FinanceMainExtService financeMainExtService;
	private CollateralMarkProcess collateralMarkProcess;
	private ReScheduleService reScheduleService;
	private AccrualService accrualService;
	private List<ExtendedFieldRender> extendedFieldRenderList = new ArrayList<ExtendedFieldRender>();

	//Bean Setters  by application Context
	private AccountInterfaceService accountInterfaceService;
	private FinanceDetailService financeDetailService;
	private AccountsService accountsService;
	private AccountEngineExecution engineExecution;
	private CustomerService customerService;
	private CommitmentService commitmentService;
	private MailUtil mailUtil;
	private SMSUtil smsUtil;
	private boolean extMailService;
	private boolean extSMSService;
	private StepPolicyService stepPolicyService;
	private FinanceReferenceDetailService financeReferenceDetailService;
	private RuleExecutionUtil ruleExecutionUtil;
	private RuleService ruleService;
	private DedupParmService dedupParmService;
	private NotificationsService notificationsService;
	private DedupValidation dedupValidation;
	private DisbursementPostings disbursementPostings;
	private InstallmentDueService installmentDueService;
	private ShortMessageService shortMessageService;
	private MailTemplateService mailTemplateService;
	private LegalDetailService legalDetailService;
	private BaseRateService baseRateService;

	protected BigDecimal availCommitAmount = BigDecimal.ZERO;
	protected Commitment commitment;
	protected Tab listWindowTab;
	protected boolean isRIAExist;
	protected String moduleDefiner = "";
	protected String eventCode = "";
	protected String menuItemRightName = null;
	protected String custCtgCode = "";
	protected String finDivision = "";
	protected String old_NextRoleCode = "";
	protected boolean isEnquiry = false;
	protected String selectMethodName = "onSelectTab";

	// Change Frequency Fields
	private Date org_grcPeriodEndDate = null;
	private BigDecimal org_finAssetValue = BigDecimal.ZERO;

	// not auto wired variables
	private transient boolean validationOn;
	private transient Boolean assetDataChanged;
	private transient Boolean finPurposeDataChanged;

	// not auto wired variables
	private FinanceDetail financeDetail = null;
	protected FinScheduleData validFinScheduleData;
	protected transient FinanceMainListCtrl financeMainListCtrl = null;
	protected transient FinanceSelectCtrl financeSelectCtrl = null;
	protected JdbcSearchObject<Customer> custCIFSearchObject;

	private Window mainWindow = null;
	private String productCode = null;
	protected boolean isFeeReExecute;
	private boolean recommendEntered;

	Date appDate = DateUtility.getAppDate();
	Date minReqFinStartDate = DateUtility.addDays(appDate, -SysParamUtil.getValueAsInt("BACKDAYS_STARTDATE") + 1);
	Date maxReqFinStartDate = DateUtility.addDays(appDate, +SysParamUtil.getValueAsInt("FUTUREDAYS_STARTDATE") + 1);
	Date appEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
	Date appStartDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");

	protected transient String nextUserId;
	protected boolean isFinPreApproved;
	private Button btnAddSecondaryAccounts;
	private EventManager eventManager;
	private LimitManagement limitManagement;
	String cbSchddemethod = "";
	String cbGrcSchddemethod = "";
	// Finance Flag Details
	protected String tempflagcode = "";
	private List<FinFlagsDetail> finFlagsDetailList = null;
	protected Map<String, Object> flagTypeDataMap = new HashMap<String, Object>();
	protected transient List<FinInsurances> oldVar_finInsuranceList;
	protected Label label_FinanceMainDialog_FinAssetValue;
	protected Label label_FinanceMainDialog_FinAmount;
	protected Label label_FinanceMainDialog_FinCurrentAssetValue;

	private boolean isBranchanged;
	private String branchSwiftCode;

	//Extended fields
	private ExtendedFieldCtrl extendedFieldCtrl = null;
	//for pdf extraction
	private PdfParserCaller pdfParserCaller;
	private String pdfExtTabPanelId;
	private VehicleDealerService vehicleDealerService;

	@Autowired
	private VerificationService verificationService;
	@Autowired
	private SamplingService samplingService;
	@Autowired
	private ExtendedFieldDetailsService extendedFieldDetailsService;

	private String elgMethodVisible = SysParamUtil.getValueAsString(SMTParameterConstants.ELGMETHOD);
	private String isCreditRevTabReq = SysParamUtil.getValueAsString(SMTParameterConstants.IS_CREDITREVIEW_TAB_REQ);
	private List<String> assignCollateralRef = new ArrayList<>();
	private CollateralSetupService collateralSetupService;
	private Map<String, Object> collateralRuleMap = new HashMap<>();
	//adding new document fields
	private Object financeMainDialogCtrl = null;
	private List<FinanceReferenceDetail> agreementList = null;
	protected Listbox listBox_Agreements; // autoWired
	private List<DocumentDetails> documentDetailsList;
	protected Window window_documentDetailDialog;
	private transient AgreementDefinitionService agreementDefinitionService;
	private Map <String,List> autoDownloadMap = null;
	private Map<String, String> extValuesMap = new HashMap<String, String>();
	/**
	 * default constructor.<br>
	 */
	public FinanceMainBaseCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinanceMain";
		super.pageRightName = "FinanceMainDialog";
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	protected void doCheckRights() {
		logger.debug("Entering");
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnEdit"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnSave"));
		this.btnCancel.setVisible(false);

		// Schedule related buttons
		this.btnValidate.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnValidate"));
		this.btnBuildSchedule.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnBuildSchd"));
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		finDivision = financeType.getFinDivision();
		StringBuilder whereClause = new StringBuilder();

		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		// Finance Basic Details Tab ---> 1. Basic Details
		this.finReference.setMaxlength(LengthConstants.LEN_REF);
		this.finType.setMaxlength(LengthConstants.LEN_MASTER_CODE);
		this.applicationNo.setMaxlength(LengthConstants.LEN_REF);
		this.referralId.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false,
				LengthConstants.LEN_MASTER_CODE);
		if (this.row_employeeName != null && this.row_employeeName.isVisible()) {
			this.employeeName.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false,
					LengthConstants.LEN_MASTER_CODE);
		}
		this.referralId.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false,
				LengthConstants.LEN_MASTER_CODE);
		this.dmaCode.setProperties("DMA", "DealerName", "Code", false, LengthConstants.LEN_MASTER_CODE);
		this.dmaCode.getTextbox().setMaxlength(50);
		this.salesDepartment.setProperties("GeneralDepartment", "GenDepartment", "GenDeptDesc", false,
				LengthConstants.LEN_MASTER_CODE);

		this.finCcy.setProperties("Currency", "CcyCode", "CcyDesc", true, LengthConstants.LEN_CURRENCY);

		if (StringUtils.equals(PennantConstants.YES, elgMethodVisible)) {
			this.eligibilityMethod.setProperties("EligibilityMethod", "FieldCodeValue", "ValueDesc", false, 4);
		}
		this.connector.setProperties("Connector", "DealerName", "Code", false, LengthConstants.LEN_MASTER_CODE);
		this.connector.getTextbox().setMaxlength(50);

		this.finStartDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.finContractDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.finAmount.setProperties(true, finFormatter);
		this.downPayBank.setProperties(true, finFormatter);

		this.finAssetValue.setProperties(false, finFormatter);
		this.finCurrentAssetValue.setProperties(false, finFormatter);
		this.custPaymentAmount.setProperties(false, finFormatter);
		this.custPaymentAmount.setReadonly(true);

		this.latePayAmount.setProperties(false, finFormatter);
		this.latePayAmount.setReadonly(true);

		this.latePayWaiverAmount.setProperties(false, finFormatter);

		this.defferments.setMaxlength(3);
		this.planDeferCount.setMaxlength(3);

		this.commitmentRef.setProperties("Commitment", "CmtReference", "CmtTitle", false, 20);
		this.commitmentRef.setFilters(new Filter[] { new Filter("CustID", financeMain.getCustID(), Filter.OP_EQUAL) });

		if (!financeType.isFinCommitmentReq()) {
			readOnlyComponent(true, this.commitmentRef);
			this.commitmentRef.setMandatoryStyle(false);
			this.row_commitment.setVisible(false);
		}

		this.finLimitRef.setProperties("CustomerLimit", "LimitRef", "LimitDesc", false, 20);
		// set CustomerReference as Filter for finLimitRef 
		Filter finLimitFilter[] = new Filter[3];
		finLimitFilter[0] = new Filter("ProductCode", financeType.getFinType(), Filter.OP_EQUAL);
		finLimitFilter[1] = new Filter("Category", financeType.getFinCategory(), Filter.OP_EQUAL);
		finLimitFilter[2] = new Filter("CustomerReference", financeMain.getCustID(), Filter.OP_EQUAL);
		Filter.and(finLimitFilter);
		this.finLimitRef.setFilters(finLimitFilter);
		//Facility Details
		this.facilityAmount.setMaxlength(LengthConstants.LEN_AMOUNT);
		this.facilityAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.facilityAmount.setScale(finFormatter);

		this.facilityUtilizedAmount.setMaxlength(LengthConstants.LEN_AMOUNT);
		this.facilityUtilizedAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.facilityUtilizedAmount.setScale(finFormatter);

		this.facilityAvaliableAmount.setMaxlength(LengthConstants.LEN_AMOUNT);
		this.facilityAvaliableAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.facilityAvaliableAmount.setScale(finFormatter);

		this.facilityExpiryDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.facilityBaseRate.setMaxlength(LengthConstants.LEN_RATE);
		this.facilityBaseRate.setFormat(PennantConstants.rateFormate9);
		this.facilityBaseRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.facilityBaseRate.setScale(LengthConstants.LEN_RATE_SCALE);

		this.facilityMarginRate.setMaxlength(LengthConstants.LEN_RATE);
		this.facilityMarginRate.setFormat(PennantConstants.rateFormate9);
		this.facilityMarginRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.facilityMarginRate.setScale(LengthConstants.LEN_RATE_SCALE);

		this.mMAReference.setProperties("MMAgreement", "MMAId", "MMAReference", false, 3, 161);

		boolean limitRequired = financeType.isLimitRequired();

		if (ImplementationConstants.LIMIT_INTERNAL) {
			limitRequired = false;
		}
		if (limitRequired
				&& (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory()))) {
			this.rowLimitRef.setVisible(true);
			this.rowFacilityAmounts.setVisible(true);
			this.rowFacilityDateRate.setVisible(true);
			this.rowFacilityNotes.setVisible(true);
			this.finLimitRef.setMandatoryStyle(true);
			readOnlyComponent(true, this.mMAReference);
		} else {
			this.rowLimitRef.setVisible(false);
			this.rowFacilityAmounts.setVisible(false);
			this.rowFacilityDateRate.setVisible(false);
			this.rowFacilityNotes.setVisible(false);
			readOnlyComponent(true, this.finLimitRef);
			readOnlyComponent(true, this.mMAReference);
		}
		this.finPurpose.setProperties("LoanPurpose", "LoanPurposeCode", "LoanPurposeDesc", false, 8);
		this.finBranch.setProperties("UserDivBranch", "UserBranch", "UserBranchDesc", true, LengthConstants.LEN_BRANCH);
		whereClause.append("usrID = ");
		whereClause.append(getUserWorkspace().getLoggedInUser().getUserId());
		whereClause.append(" AND ");
		whereClause.append("UserDivision = '");
		whereClause.append(this.finDivision);
		whereClause.append("'");
		this.finBranch.setWhereClause(whereClause.toString());

		this.downPayBank.setProperties(true, finFormatter);
		this.downPaySupl.setProperties(false, finFormatter);
		this.securityDeposit.setProperties(false, finFormatter);

		this.firstDroplineDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		// Step Finance Field Properties       		
		this.noOfSteps.setMaxlength(2);
		this.stepType.setReadonly(true);

		this.stepPolicy.setProperties("StepPolicyHeader", "PolicyCode", "PolicyDesc", true, 8);
		String[] alwdStepPolices = StringUtils.trimToEmpty(financeType.getAlwdStepPolicies()).split(",");
		Filter filter[] = new Filter[1];
		filter[0] = new Filter("PolicyCode", Arrays.asList(alwdStepPolices), Filter.OP_IN);
		this.stepPolicy.setFilters(filter);

		this.accountsOfficer.setProperties("SourceOfficer", "DealerName", "DealerCity", false, 8);
		this.accountsOfficer.getTextbox().setMaxlength(50);
		this.dsaCode.setProperties("DSA", "DealerName", "Code", false, 8);
		this.dsaCode.getTextbox().setMaxlength(50);

		// Finance Basic Details Tab ---> 2. Grace Period Details

		this.gracePeriodEndDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.gracePeriodEndDate_two.setFormat(DateFormat.LONG_DATE.getPattern());
		this.graceTerms.setMaxlength(4);
		this.graceTerms.setStyle("text-align:right;");
		this.graceTerms_Two.setStyle("text-align:right;");

		this.graceRate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.graceRate.setSpecialProperties("SplRateCode", "SRType", "SRTypeDesc");
		this.graceRate.getEffRateComp().setVisible(true);

		this.gracePftRate.setMaxlength(LengthConstants.LEN_RATE);
		this.gracePftRate.setFormat(PennantConstants.rateFormate9);
		this.gracePftRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.gracePftRate.setScale(LengthConstants.LEN_RATE_SCALE);

		this.grcAdvRate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.grcAdvRate.setEffectiveRateVisible(true);

		this.finGrcMinRate.setMaxlength(LengthConstants.LEN_RATE);
		this.finGrcMinRate.setFormat(PennantConstants.rateFormate9);
		this.finGrcMinRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.finGrcMinRate.setScale(LengthConstants.LEN_RATE_SCALE);

		this.finGrcMaxRate.setMaxlength(LengthConstants.LEN_RATE);
		this.finGrcMaxRate.setFormat(PennantConstants.rateFormate9);
		this.finGrcMaxRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.finGrcMaxRate.setScale(LengthConstants.LEN_RATE_SCALE);

		this.graceRate.getEffRateComp().setMaxlength(LengthConstants.LEN_RATE);
		this.graceRate.getEffRateComp().setFormat(PennantConstants.rateFormate9);
		this.graceRate.getEffRateComp().setRoundingMode(BigDecimal.ROUND_DOWN);
		this.graceRate.getEffRateComp().setScale(LengthConstants.LEN_RATE_SCALE);

		this.nextGrcPftDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextGrcPftDate_two.setFormat(DateFormat.LONG_DATE.getPattern());
		this.nextGrcPftRvwDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextGrcPftRvwDate_two.setFormat(DateFormat.LONG_DATE.getPattern());
		this.nextGrcCpzDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextGrcCpzDate_two.setFormat(DateFormat.LONG_DATE.getPattern());

		this.grcMaxAmount.setProperties(false, finFormatter);
		
		// Finance Basic Details Tab ---> 3. Repayment Period Details

		this.numberOfTerms.setMaxlength(4);
		this.numberOfTerms.setStyle("text-align:right;");
		this.numberOfTerms_two.setStyle("text-align:right;");

		this.finRepaymentAmount.setProperties(false, finFormatter);

		this.repayRate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.repayRate.setSpecialProperties("SplRateCode", "SRType", "SRTypeDesc");
		this.repayRate.setMandatoryStyle(true);
		this.repayRate.getEffRateComp().setVisible(true);

		this.repayRate.getEffRateComp().setMaxlength(13);
		this.repayRate.getEffRateComp().setFormat(PennantConstants.rateFormate9);
		this.repayRate.getEffRateComp().setRoundingMode(BigDecimal.ROUND_DOWN);
		this.repayRate.getEffRateComp().setScale(9);

		this.repayProfitRate.setMaxlength(LengthConstants.LEN_RATE);
		this.repayProfitRate.setFormat(PennantConstants.rateFormate9);
		this.repayProfitRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.repayProfitRate.setScale(LengthConstants.LEN_RATE_SCALE);

		this.repayPftFrq.setMandatoryStyle(true);
		this.repayPftFrq.setAlwFrqDays(financeType.getFrequencyDays());
		this.depreciationFrq.setMandatoryStyle(true);
		this.gracePftFrq.setMandatoryStyle(true);
		this.gracePftFrq.setAlwFrqDays(financeType.getFrequencyDays());
		this.gracePftRvwFrq.setMandatoryStyle(true);
		this.gracePftRvwFrq.setAlwFrqDays(financeType.getFrequencyDays());
		this.graceCpzFrq.setMandatoryStyle(true);
		this.graceCpzFrq.setAlwFrqDays(financeType.getFrequencyDays());
		this.repayRvwFrq.setMandatoryStyle(true);
		this.repayRvwFrq.setAlwFrqDays(financeType.getFrequencyDays());
		this.repayCpzFrq.setMandatoryStyle(true);
		this.repayCpzFrq.setAlwFrqDays(financeType.getFrequencyDays());
		this.repayFrq.setMandatoryStyle(true);
		this.repayFrq.setAlwFrqDays(financeType.getFrequencyDays());
		this.rolloverFrq.setMandatoryStyle(true);
		this.rolloverFrq.setAlwFrqDays(financeType.getFrequencyDays());

		this.rpyAdvRate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.rpyAdvRate.setEffectiveRateVisible(true);

		this.finMinRate.setMaxlength(LengthConstants.LEN_RATE);
		this.finMinRate.setFormat(PennantConstants.rateFormate9);
		this.finMinRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.finMinRate.setScale(LengthConstants.LEN_RATE_SCALE);

		this.finMaxRate.setMaxlength(LengthConstants.LEN_RATE);
		this.finMaxRate.setFormat(PennantConstants.rateFormate9);
		this.finMaxRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.finMaxRate.setScale(LengthConstants.LEN_RATE_SCALE);

		this.nextRepayDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRepayDate_two.setFormat(DateFormat.LONG_DATE.getPattern());
		this.nextRepayPftDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRepayPftDate_two.setFormat(DateFormat.LONG_DATE.getPattern());
		this.nextRepayRvwDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRepayRvwDate_two.setFormat(DateFormat.LONG_DATE.getPattern());
		this.nextRepayCpzDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRepayCpzDate_two.setFormat(DateFormat.LONG_DATE.getPattern());
		this.nextRollOverDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRollOverDate_two.setFormat(DateFormat.LONG_DATE.getPattern());
		this.maturityDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.maturityDate_two.setFormat(DateFormat.LONG_DATE.getPattern());

		this.grcAdvPftRate.setMaxlength(LengthConstants.LEN_RATE);
		this.grcAdvPftRate.setFormat(PennantConstants.rateFormate9);
		this.grcAdvPftRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.grcAdvPftRate.setScale(LengthConstants.LEN_RATE_SCALE);

		this.rpyAdvPftRate.setMaxlength(LengthConstants.LEN_RATE);
		this.rpyAdvPftRate.setFormat(PennantConstants.rateFormate9);
		this.rpyAdvPftRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.rpyAdvPftRate.setScale(LengthConstants.LEN_RATE_SCALE);

		this.supplementRent.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.supplementRent.setScale(finFormatter);
		this.increasedCost.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.increasedCost.setScale(finFormatter);

		this.planEmiHLockPeriod.setMaxlength(3);
		this.maxPlanEmiPerAnnum.setMaxlength(2);
		this.maxPlanEmi.setMaxlength(3);
		this.unPlannedEmiHLockPeriod.setMaxlength(3);
		this.maxReAgeHolidays.setMaxlength(3);
		this.maxUnplannedEmi.setMaxlength(3);

		this.bankName.setProperties("BankDetail", "BankCode", "BankName", true, 8);

		this.iban.setMaxlength(LengthConstants.LEN_IBAN);
		this.label_Financemain_IfscCode.setVisible(false);
		this.hbox_Financemain_IfscCode.setVisible(false);
		this.ifscCode.setReadonly(true);
		this.ifscCode.setMaxlength(9);

		if (StringUtils.isEmpty(moduleDefiner)) {
			deviationExecutionCtrl.setFormat(finFormatter);
			deviationExecutionCtrl.setUserRole(getRole());
			deviationExecutionCtrl.setUserid(getUserWorkspace().getUserDetails().getUserId());
			deviationExecutionCtrl.setApprovedFinanceDeviations(getFinanceDetail().getApprovedFinanceDeviations());
		}

		if (ImplementationConstants.ACCOUNTS_APPLICABLE) {
			//Account box Properties 
			this.custPayAccId.setAccountDetails(financeType.getFinType(), AccountConstants.FinanceAccount_REPY,
					financeType.getFinCcy());
			this.custPayAccId.setFormatter(finFormatter);
			this.custPayAccId.setBranchCode(StringUtils.trimToEmpty(financeMain.getFinBranch()));
			this.custPayAccId.setTextBoxWidth(165);

			this.disbAcctId.setAccountDetails(financeType.getFinType(), AccountConstants.FinanceAccount_DISB,
					financeType.getFinCcy());
			this.disbAcctId.setFormatter(finFormatter);
			this.disbAcctId.setBranchCode(StringUtils.trimToEmpty(financeMain.getFinBranch()));
			this.disbAcctId.setTextBoxWidth(165);

			this.repayAcctId.setAccountDetails(financeType.getFinType(), AccountConstants.FinanceAccount_REPY,
					financeType.getFinCcy());
			this.repayAcctId.setFormatter(finFormatter);
			this.repayAcctId.setBranchCode(StringUtils.trimToEmpty(financeMain.getFinBranch()));
			this.repayAcctId.setTextBoxWidth(165);

			this.downPayAccount.setAccountDetails(financeType.getFinType(), AccountConstants.FinanceAccount_DWNP,
					financeType.getFinCcy());
			this.downPayAccount.setFormatter(finFormatter);
			this.downPayAccount.setBranchCode(StringUtils.trimToEmpty(financeMain.getFinBranch()));
			this.downPayAccount.setTextBoxWidth(165);
		}

		if (ImplementationConstants.ALLOW_PLANNED_EMIHOLIDAY) {
			fillComboBox(this.planEmiMethod, FinanceConstants.PLANEMIHMETHOD_FRQ,
					PennantStaticListUtil.getPlanEmiHolidayMethod(), "");
		} else {
			fillComboBox(this.planEmiMethod, null, new ArrayList<ValueLabel>(), "");
		}
		if (StringUtils.equals(FinanceConstants.FIN_DIVISION_CORPORATE, this.finDivision)) {
			this.row_accountsOfficer.setVisible(false);
			this.row_ReferralId.setVisible(false);
			if (this.row_employeeName != null) {
				this.row_employeeName.setVisible(false);
			}
			label_FinanceMainDialog_SalesDepartment.setVisible(false);
			this.salesDepartment.setVisible(false);
		}
		if (financeType.isQuickDisb()) {
			this.quickDisb.setVisible(true);
			this.label_FinanceMainDialog_QuickDisb.setVisible(true);
		} else {
			this.quickDisb.setVisible(false);
			this.label_FinanceMainDialog_QuickDisb.setVisible(false);
		}

		//Field visibility & Naming for FinAsset value and finCurrent asset value by  OD/NONOD.
		setFinAssetFieldVisibility(financeType);

		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			isOverdraft = true;
		}
		if (isOverdraft) {
			this.odMaturityDate.setFormat(DateFormat.SHORT_DATE.getPattern());
			if (financeType.isDroplineOD() || StringUtils.isNotEmpty(financeMain.getDroplineFrq())) {
				this.row_DroplineFrq.setVisible(true);
				this.droplineFrq.setMandatoryStyle(true);
				this.droplineFrq.setVisible(true);
				this.firstDroplineDate.setVisible(true);
				this.firstDroplineDate.setFormat(DateFormat.SHORT_DATE.getPattern());
				this.space_DroplineDate.setSclass(PennantConstants.mandateSclass);
			} else {
				this.row_DroplineFrq.setVisible(false);
				this.droplineFrq.setMandatoryStyle(false);
				this.droplineFrq.setVisible(false);
				this.firstDroplineDate.setVisible(false);
				this.space_DroplineDate.setSclass("");
			}
		}
		
		if(financeType.isAlwAdvEMI()) {
			this.row_advEMITerms.setVisible(true);
			this.advEMITerms.setMaxlength(3);
			this.advEMITerms.setStyle("text-align:right;");
		}

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}
		logger.debug("Leaving");
	}

	private void setFinAssetFieldVisibility(FinanceType financeType) {

		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		if (financeType.isAlwMaxDisbCheckReq()) {

			if (isOverdraft) {

				this.label_FinanceMainDialog_FinAssetValue
						.setValue(Labels.getLabel("label_FinanceMainDialog_FinOverDftLimit.value"));
				this.label_FinanceMainDialog_FinCurrentAssetValue.setValue("");
				this.finCurrentAssetValue.setVisible(false);
			} else {
				if (!isOverdraft && financeType.isAlwMaxDisbCheckReq()) {
					readOnlyComponent(isReadOnly("FinanceMainDialog_finAssetValue"), this.finAssetValue);
					this.row_FinAssetValue.setVisible(true);
					this.finAssetValue.setMandatory(true);
					this.finCurrentAssetValue.setReadonly(true);
					this.label_FinanceMainDialog_FinAssetValue
							.setValue(Labels.getLabel("label_FinanceMainDialog_FinMaxDisbAmt.value"));
					this.label_FinanceMainDialog_FinCurrentAssetValue
							.setValue(Labels.getLabel("label_FinanceMainDialog_TotalDisbAmt.value"));
				} else {
					this.label_FinanceMainDialog_FinAssetValue.setVisible(false);
					this.finAssetValue.setVisible(false);
					this.label_FinanceMainDialog_FinCurrentAssetValue
							.setValue(Labels.getLabel("label_FinanceMainDialog_TotalDisbAmt.value"));
					this.label_FinanceMainDialog_FinCurrentAssetValue.setVisible(true);
					this.finCurrentAssetValue.setVisible(true);
				}
			}
		} else {
			this.row_FinAssetValue.setVisible(false);
			if (this.label_FinanceMainDialog_FinAmount != null) {
				this.label_FinanceMainDialog_FinAmount
						.setValue(Labels.getLabel("label_FinanceMainDialog_FinMaxDisbAmt.value"));
			}
		}
	}

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws Exception
	 * 
	 */
	protected void doClose() throws Exception {
		logger.debug("Entering ");
		boolean isClosed = doClose(this.btnSave.isVisible());
		if (isClosed && extendedFieldCtrl != null && financeDetail.getExtendedFieldHeader() != null) {
			extendedFieldCtrl.deAllocateAuthorities();
		}
		logger.debug("Leaving ");
	}

	/**
	 * Method for Adding Flags into Multi Selection Extended box
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onClick$btnFlagDetails(Event event) {
		logger.debug("Entering  " + event.toString());

		Object dataObject = MultiSelectionSearchListBox.show(this.window, "Flag", this.flagDetails.getValue(), null);
		if (dataObject instanceof String) {
			this.flagDetails.setValue(dataObject.toString());
			this.flagDetails.setTooltiptext("");
		} else {
			HashMap<String, Object> details = (HashMap<String, Object>) dataObject;
			if (details != null) {
				String tempflagcode = "";
				List<String> flagKeys = new ArrayList<>(details.keySet());
				for (int i = 0; i < flagKeys.size(); i++) {
					if (StringUtils.isEmpty(flagKeys.get(i))) {
						continue;
					}
					if (i == 0) {
						tempflagcode = flagKeys.get(i);
					} else {
						tempflagcode = tempflagcode + "," + flagKeys.get(i);
					}
				}
				this.flagDetails.setValue(tempflagcode);
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method Used for set list of values been class to components finance flags list
	 * 
	 * @param Collateral
	 */
	private void doFillFinFlagsList(List<FinFlagsDetail> finFlagsDetailList) {
		logger.debug("Entering");
		setFinFlagsDetailList(finFlagsDetailList);
		if (finFlagsDetailList == null || finFlagsDetailList.isEmpty()) {
			return;
		}

		String tempflagcode = "";
		for (FinFlagsDetail finFlagsDetail : finFlagsDetailList) {
			if (!StringUtils.equals(finFlagsDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
				if (StringUtils.isEmpty(tempflagcode)) {
					tempflagcode = finFlagsDetail.getFlagCode();
				} else {
					tempflagcode = tempflagcode.concat(",").concat(finFlagsDetail.getFlagCode());
				}
			}
		}
		this.flagDetails.setValue(tempflagcode);
		logger.debug("Entering");
	}

	private void doFillEnquiryList(ArrayList<Object> finBasicDetail) {
		logger.debug(Literal.ENTERING);
		boolean isEnquiryVisible = false;
		List<ValueLabel> enquiryList = new ArrayList<>();
		enquiryList.add(new ValueLabel("1", "Verifications"));
		List<Integer> verificationTypes = verificationService
				.getVerificationTypes(StringUtils.trimToEmpty((finBasicDetail.get(3).toString())));

		for (Integer verificationType : verificationTypes) {
			if (verificationType == VerificationType.FI.getKey()
					&& (!(financeDetail.isFiInitTab() || financeDetail.isFiApprovalTab()))) {
				isEnquiryVisible = true;
			} else if (verificationType == VerificationType.TV.getKey()
					&& (!(financeDetail.isTvInitTab() || financeDetail.isTvApprovalTab()))) {
				isEnquiryVisible = true;
			} else if (verificationType == VerificationType.LV.getKey()
					&& (!(financeDetail.isLvInitTab() || financeDetail.isLvApprovalTab()))) {
				isEnquiryVisible = true;
			} else if (verificationType == VerificationType.RCU.getKey()
					&& !(financeDetail.isRcuInitTab() || financeDetail.isRcuApprovalTab())) {
				isEnquiryVisible = true;
			}

			//Check whether enquiry is visible or not.
			if (isEnquiryVisible && StringUtils.isEmpty(moduleDefiner)) {
				this.enquiryLabel.setValue("Enquiry");
				this.enquiryCombobox.setVisible(true);
				fillComboBox(this.enquiryCombobox, "", enquiryList, "");
				break;
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Used for render the Data from List
	 * 
	 * @param finFlagsDetailList
	 */
	private void fetchFlagDetals() {
		logger.debug("Entering");

		List<String> finFlagList = Arrays.asList(this.flagDetails.getValue().split(","));

		if (this.finFlagsDetailList == null) {
			this.finFlagsDetailList = new ArrayList<>();
		}

		Map<String, FinFlagsDetail> flagMap = new HashMap<>();
		for (int i = 0; i < finFlagsDetailList.size(); i++) {
			FinFlagsDetail finFlagsDetail = finFlagsDetailList.get(i);
			flagMap.put(finFlagsDetail.getFlagCode(), finFlagsDetail);
		}

		for (String flagCode : finFlagList) {

			if (StringUtils.isEmpty(flagCode)) {
				continue;
			}

			// Check object is already exists in saved list or not
			if (flagMap.containsKey(flagCode)) {
				// Do Nothing

				//Removing from map to identify existing modifications
				boolean isDelete = false;
				if (this.userAction.getSelectedItem() != null) {
					if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							|| this.userAction.getSelectedItem().getLabel().contains("Reject")
							|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
						isDelete = true;
					}
				}

				if (!isDelete) {
					flagMap.remove(flagCode);
				}
			} else {
				FinFlagsDetail afinFlagsDetail = new FinFlagsDetail();
				afinFlagsDetail.setFlagCode(flagCode);
				afinFlagsDetail.setModuleName(FinanceConstants.MODULE_NAME);
				afinFlagsDetail.setNewRecord(true);
				afinFlagsDetail.setVersion(1);
				afinFlagsDetail.setRecordType(PennantConstants.RCD_ADD);

				this.finFlagsDetailList.add(afinFlagsDetail);
			}
		}

		//Removing unavailable records from DB by using Workflow details
		if (flagMap.size() > 0) {
			for (int i = 0; i < finFlagsDetailList.size(); i++) {
				FinFlagsDetail finFlagsDetail = finFlagsDetailList.get(i);
				if (flagMap.containsKey(finFlagsDetail.getFlagCode())) {

					if (StringUtils.isBlank(finFlagsDetail.getRecordType())) {
						finFlagsDetail.setNewRecord(true);
						finFlagsDetail.setVersion(finFlagsDetail.getVersion() + 1);
						finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else {
						if (!StringUtils.equals(finFlagsDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
							finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
						}
					}
				}
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method to invoke data filling method for eligibility tab, Scoring tab, fee charges tab, accounting tab,
	 * agreements tab and additional field details tab.
	 * 
	 * @param aFinanceDetail
	 * @throws ParseException
	 * @throws InterruptedException
	 * 
	 */
	@SuppressWarnings("unused")
	protected void doFillTabs(FinanceDetail aFinanceDetail, boolean onLoad, boolean isReqToLoad)
			throws ParseException, InterruptedException {
		logger.debug("Entering");

		FinanceType financeType = aFinanceDetail.getFinScheduleData().getFinanceType();

		//Customer Details   
		if (onLoad || StringUtils.isEmpty(moduleDefiner)) {
			appendCustomerDetailTab(onLoad);
		}

		if (StringUtils.isEmpty(moduleDefiner)){
			//FI Initiation Tab
			appendFIInitiationTab(onLoad);
	
			//FI Approval Tab
			appendFIApprovalTab(onLoad);
	
			//TV Initiation Tab
			appendTVInitiationTab(onLoad);
	
			//TV Approval Tab
			appendTVApprovalTab(onLoad);
	
			//LV Initiation Tab
			appendLVInitiationTab(onLoad);
	
			//LV Initiation Tab
			appendLVApprovalTab(onLoad);
	
			//RCU Initiation Tab
			appendRCUInitiationTab(onLoad);
	
			//RCU Approval Tab
			appendRCUApprovalTab(onLoad);
		}
		if (isReadOnly("FinanceMainDialog_NoScheduleGeneration")) {

			//Step Policy Details
			appendStepDetailTab(onLoad, true);

			//Contributor details Tab Addition
			if (financeType.isAllowRIAInvestment()) {
				isRIAExist = true;
				appendContributorDetailsTab(onLoad);
			}

			//Disbursement Detail Tab
			if (getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_ISTISNA)) {
				appendDisbursementDetailTab(onLoad);
			}

			//Fee Details Tab Addition
			appendFeeDetailTab(onLoad);
		}

		//Advance Payment Detail Tab Addition
		
		  if ((StringUtils.isEmpty(moduleDefiner) && !(StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
		  aFinanceDetail.getFinScheduleData().getFinanceMain().getProductCategory()))) ||
		  StringUtils.equals(FinanceConstants.FINSER_EVENT_ADDDISB, moduleDefiner) ||
		  StringUtils.equals(FinanceConstants.FINSER_EVENT_CANCELDISB, moduleDefiner)) {
		   
		  if (isTabVisible(StageTabConstants.AdvancePayment)) {
				appendAdvancePaymentsDetailTab(onLoad);
			}
		  }
		 
		
		//Asset Evaluation Tab Addition
		if (StringUtils.isEmpty(moduleDefiner) && StringUtils.equals(finDivision, FinanceConstants.FIN_DIVISION_RETAIL)
				&& (StringUtils.equals(FinanceConstants.PRODUCT_IJARAH, financeType.getFinCategory())
						|| StringUtils.equals(FinanceConstants.PRODUCT_FWIJARAH, financeType.getFinCategory()))
				&& getUserWorkspace().isAllowed("FinanceMainDialog_showAssetEvaluation")) {
			appendAssetEvaluationTab(onLoad);
		}

		//Schedule Details Tab Adding
		if (isReadOnly("FinanceMainDialog_NoScheduleGeneration")) {
			if (onLoad) {
				appendScheduleDetailTab(true, false);
			}
		}
		if (StringUtils.isEmpty(moduleDefiner)){
		//Joint Account and Guaranteer  Tab Addition
			if (isTabVisible(StageTabConstants.CoApplicants)) {
				appendJointGuarantorDetailTab(onLoad);
			}

			// Finance Tax Details
			if (isTabVisible(StageTabConstants.TaxDetail)) {
				appendTaxDetailTab(onLoad);
			}
		}

		if (StringUtils.isEmpty(moduleDefiner) || isFinPreApproved) {
			//Eligibility Details Tab Adding
			appendEligibilityDetailTab(onLoad);

			//Scoring Detail Tab Addition
			appendFinScoringDetailTab(onLoad);
		}

		//Agreements Detail Tab Addition
		appendAgreementsDetailTab(onLoad);

		//CheckList Details Tab Addition
		appendCheckListDetailTab(aFinanceDetail, onLoad);

		// Document Detail Tab Addition
		if (isTabVisible(StageTabConstants.Documents)) {
			appendDocumentDetailTab(onLoad);
		}
		if (StringUtils.isEmpty(moduleDefiner)) {
		// Covenant Type Tab Addition
			if (ImplementationConstants.ALLOW_COVENANT_TYPES && isTabVisible(StageTabConstants.Covenant)) {
				appendCovenantTypeTab(onLoad);
			}
			// Deviation Detail Tab 
			if (ImplementationConstants.ALLOW_DEVIATIONS) {
					boolean allowed = deviationExecutionCtrl.deviationAllowed(financeType.getFinCategory());
					if (allowed) {
						appendDeviationDetailTab(onLoad);
					}
			}

			//Mandate Details Tab 
			if (isTabVisible(StageTabConstants.Mandate)) {
				appendMandateDetailTab(onLoad);
			}

			if (isTabVisible(StageTabConstants.Cheque)) {
				appendChequeDetailTab(onLoad);
			}

			// Collateral Detail Tab
			if (isTabVisible(StageTabConstants.Collaterals)) {
				appendFinCollateralTab(onLoad);	
			}

			// VAS Recording Detail Tab
			if (isTabVisible(StageTabConstants.VAS)) {
				appendVasRecordingTab(onLoad);
			}
		}else{
			// Collateral Detail Tab
			if (StringUtils.equals(FinanceConstants.FINSER_EVENT_ADDDISB,moduleDefiner)) {
				 appendFinCollateralTab(onLoad);
			 }
			
		}

		//Stage Accounting details Tab Addition
		appendStageAccountingDetailsTab(onLoad);

		//Credit Review Tab
		if (StringUtils.equals(isCreditRevTabReq, PennantConstants.YES) && 
				!StringUtils.equals(PennantConstants.PFF_CUSTCTG_INDIV, financeDetail.getCustomerDetails().getCustomer().getCustCtgCode())) {
			appendCreditReviewDetailTab(false);
		}

		//Etihad Credit Bureau Detail Tab Addition
		if (ImplementationConstants.ALLOW_CREDITBUREAU && StringUtils.isEmpty(moduleDefiner)
				&& getUserWorkspace().isAllowed("FinanceMainDialog_showEtihadCreditBureau")) {
			appendEtihadCreditBureauDetailTab(onLoad);
		}

		//Bundled Products Detail Tab Addition
		if (ImplementationConstants.ALLOW_BUNDLEDPRODUCT && StringUtils.isEmpty(moduleDefiner)
				&& StringUtils.equals(finDivision, FinanceConstants.FIN_DIVISION_RETAIL)) {
			if (onLoad) {
				appendBundledProductsDetailTab();
			}
		}

		//Agreement Arabic Field Detail Tab Addition
		if (StringUtils.isEmpty(moduleDefiner)
				&& (financeType.getFinCategory().equals(FinanceConstants.PRODUCT_IJARAH)
						|| financeType.getFinCategory().equals(FinanceConstants.PRODUCT_FWIJARAH))
				&& financeType.getFinDivision().equals(FinanceConstants.FIN_DIVISION_RETAIL)) {
			appendAgreementFieldsTab(onLoad);
		}

		//Show Accounting Tab Details Based upon Role Condition using Work flow
		if ("Accounting".equals(getTaskTabs(getTaskId(getRole()))) && isReqToLoad
				&& !StringUtils.equals(FinanceConstants.FINSER_EVENT_HOLDEMI, moduleDefiner)) {
			//Accounting Details Tab Addition
			appendAccountingDetailTab(onLoad);
		}

		//Recommend & Comments Details Tab Addition
		appendRecommendDetailTab(onLoad);
		
		//Extended Field Tab Addition
		if(isTabVisible(StageTabConstants.ExtendedField)){
			if (onLoad) {
				appendExtendedFieldDetails(aFinanceDetail, moduleDefiner);
			}
		}

		if (StringUtils.isEmpty(moduleDefiner)) {
			//Sampling Approval Details
			appendSamplingApprovalTab(onLoad);
			//Query Mangement Tab 
			if (isTabVisible(StageTabConstants.QueryMangement)) {
				appendQueryMangementTab(false);
			}
		}

		if (isTabVisible(StageTabConstants.PSLDetails) && StringUtils.isEmpty(moduleDefiner)) {
			appendPslDetailsTab(onLoad);
		}
		

		logger.debug("Leaving");
	}

	/**
	 * Method for Checking Conditions for Displaying Tab on particular Stage or not
	 * 
	 * @param tabID
	 * @return
	 */
	private boolean isTabVisible(long tabID){
		String strTabId = StringUtils.leftPad(String.valueOf(tabID), 3, "0");
		boolean showTab = true;
		String roles="";

		if(getFinanceDetail().getShowTabDetailMap().containsKey(strTabId)){
			roles= getFinanceDetail().getShowTabDetailMap().get(strTabId);
			if(!StringUtils.contains(roles, getRole()+",")){
				showTab = false;
			}
		}
		return showTab;
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendScheduleDetailTab(Boolean onLoadProcess, Boolean isFeeRender) {
		logger.debug("Entering");
		Tabpanel tabpanel = getTabpanel(AssetConstants.UNIQUE_ID_SCHEDULE);
		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		if (onLoadProcess) {
			if (tabpanel == null) {
				Tab tab = new Tab("Schedule");
				tab.setId(getTabID(AssetConstants.UNIQUE_ID_SCHEDULE));
				tabsIndexCenter.appendChild(tab);
				if (StringUtils.isEmpty(moduleDefiner)) {
					tab.addForward(Events.ON_SELECT, this.window, selectMethodName);
				}
				if (isOverdraft && getFinanceDetail().getFinScheduleData().getFinanceType().isDroplineOD()) {
					if ((getFinanceDetail().getFinScheduleData().getOverdraftScheduleDetails() == null
							|| getFinanceDetail().getFinScheduleData().getOverdraftScheduleDetails().isEmpty())
							|| (!getFinanceDetail().getFinScheduleData().getFinanceType().isDroplineOD())) {

						tab.setDisabled(true);
						tab.setVisible(false);
					}
				} else {
					if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() == null
							|| getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().isEmpty()) {

						tab.setDisabled(true);
						tab.setVisible(false);
					}
				}

				tabpanel = new Tabpanel();
				tabpanel.setId(getTabpanelID(AssetConstants.UNIQUE_ID_SCHEDULE));
				tabpanel.setStyle("overflow:auto;");
				tabpanel.setParent(tabpanelsBoxIndexCenter);
				tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");
			} else if (tabpanel != null) {
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_SCHEDULE);
		}

		//Open Window For maintenance 
		if (StringUtils.isNotEmpty(moduleDefiner)) {
			if ((getFinanceDetail().getFinScheduleData().getFeeRules() != null
					&& !getFinanceDetail().getFinScheduleData().getFeeRules().isEmpty())
					|| (getFinanceDetail().getFeeCharges() != null && !getFinanceDetail().getFeeCharges().isEmpty())) {
				if (isFeeRender) {
					onLoadProcess = false;
				}
			} else {
				onLoadProcess = false;
			}
		}

		// Schedule Preparation without calculation , In case of Overdraft Schedule when no disbursements happened
		FinScheduleData finSchdData = getFinanceDetail().getFinScheduleData();
		if (StringUtils.isNotBlank(moduleDefiner) && isOverdraft && (finSchdData.getFinanceScheduleDetails() == null
				|| finSchdData.getFinanceScheduleDetails().isEmpty())) {
			getFinanceDetail()
					.setFinScheduleData(ScheduleGenerator.getNewSchd(getFinanceDetail().getFinScheduleData()));
			if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() != null
					&& getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0) {
				for (FinanceScheduleDetail curSchd : getFinanceDetail().getFinScheduleData()
						.getFinanceScheduleDetails()) {
					curSchd.setSchdMethod(getComboboxValue(this.cbScheduleMethod));
					if (StringUtils.isNotEmpty(curSchd.getBaseRate())) {
						BigDecimal rate = RateUtil.rates(curSchd.getBaseRate(),
								finSchdData.getFinanceMain().getFinCcy(), curSchd.getSplRate(), curSchd.getMrgRate(),
								finSchdData.getFinanceMain().getRpyMinRate(),
								finSchdData.getFinanceMain().getRpyMaxRate()).getNetRefRateLoan();
						curSchd.setCalculatedRate(rate);
					} else {
						curSchd.setCalculatedRate(finSchdData.getFinanceMain().getRepayProfitRate());
					}
				}
				getFinanceDetail().getFinScheduleData().getFinanceMain()
						.setRecalSchdMethod(getComboboxValue(this.cbScheduleMethod));
			}
		}

		// Schedule Tab Rendering
		if (!onLoadProcess) {
			final HashMap<String, Object> map = getDefaultArguments();
			map.put("menuItemRightName", menuItemRightName);
			map.put("moduleDefiner", moduleDefiner);
			map.put("isEnquiry", isEnquiry);
			FinanceType fintype = getFinanceDetail().getFinScheduleData().getFinanceType();
			if (manualSchedule.isChecked() && getUserWorkspace().isAllowed("button_FinanceMainDialog_btnBuildSchd")) {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ManualScheduleDetailDialog.zul",
						tabpanel, map);
			} else if (StringUtils.isEmpty(moduleDefiner) && isOverdraft && fintype.isDroplineOD()) {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/OverdraftScheduleDetailDialog.zul",
						tabpanel, map);
			} else if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() != null
					&& getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0) {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScheduleDetailDialog.zul", tabpanel,
						map);
			}
			Tab tab = getTab(AssetConstants.UNIQUE_ID_SCHEDULE);
			if (tab != null) {
				tab.setDisabled(false);
				tab.setVisible(true);
				tab.removeForward(Events.ON_SELECT, this.window, selectMethodName);
				if (StringUtils.isNotEmpty(moduleDefiner) && !isEnquiry) {
					tab.setSelected(true);
				}
			}
			logger.debug("Leaving");
		}
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendEligibilityDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");
		List<FinanceEligibilityDetail> elgRuleList = getFinanceDetail().getElgRuleList();
		if (onLoadProcess) {
			createTab(AssetConstants.UNIQUE_ID_ELIGIBLITY, false);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_ELIGIBLITY);
		}
		if (!onLoadProcess && (elgRuleList != null && !elgRuleList.isEmpty())) {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/EligibilityDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_ELIGIBLITY), getDefaultArguments());
		} else {
			setEligibilityDetailDialogCtrl(null);
		}
		Tab tab = getTab(AssetConstants.UNIQUE_ID_ELIGIBLITY);
		if (tab != null) {
			tab.setVisible((elgRuleList != null && !elgRuleList.isEmpty()));
		}
		elgRuleList = null;
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Scoring Details Data in finance
	 */
	protected void appendFinScoringDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");
		List<FinanceReferenceDetail> scoringGroupList = getFinanceDetail().getScoringGroupList();
		if (onLoadProcess) {
			createTab(AssetConstants.UNIQUE_ID_SCORING, false);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_SCORING);
		}
		if (!onLoadProcess && (scoringGroupList != null && !scoringGroupList.isEmpty())) {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScoringDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_SCORING), getDefaultArguments());
		} else {
			setScoringDetailDialogCtrl(null);
		}
		Tab tab = getTab(AssetConstants.UNIQUE_ID_SCORING);
		if (tab != null) {
			tab.setVisible((scoringGroupList != null && !scoringGroupList.isEmpty()));
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Joint account and Guaranteer Details Data in finance
	 */
	protected void appendJointGuarantorDetailTab(boolean onLoad) {
		logger.debug("Entering");
		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_JOINTGUARANTOR, true);
		} else {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/JointAccountDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_JOINTGUARANTOR), getDefaultArguments());
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Joint account and guaranteer Details Data in finance
	 */
	protected void appendAgreementsDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");
		boolean createTab = false;
		if (getFinanceDetail().getAggrementList() == null || getFinanceDetail().getAggrementList().isEmpty()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_AGREEMENT) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_AGREEMENT, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_AGREEMENT);
		}
		if (getFinanceDetail().getAggrementList() != null && getFinanceDetail().getAggrementList().size() > 0) {
			final HashMap<String, Object> map = getDefaultArguments();
			map.put("finHeaderList", getFinBasicDetails());
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AgreementDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_AGREEMENT), map);
		}
		logger.debug("Leaving");
	}

	public void setAgreementDetailTab(Window window) {
		Tab tab = getTab(AssetConstants.UNIQUE_ID_AGREEMENT);
		if (tab != null) {
			if (!getFinanceDetail().getAggrementList().isEmpty()) {
				tab.setVisible(true);
				ComponentsCtrl.applyForward(tab, selectMethodName);
			} else {
				tab.setVisible(false);
			}
		}
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendStepDetailTab(boolean screenOpening, boolean onLoadProcess) {
		logger.debug("Entering");
		if (screenOpening) {
			createTab(AssetConstants.UNIQUE_ID_STEPDETAILS, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_STEPDETAILS);
		}
		Tab tab = getTab(AssetConstants.UNIQUE_ID_STEPDETAILS);
		if (!onLoadProcess || (getFinanceDetail().getFinScheduleData().getFinanceMain().isStepFinance()
				&& (!getFinanceDetail().getFinScheduleData().getStepPolicyDetails().isEmpty()
						|| getFinanceDetail().getFinScheduleData().getFinanceMain().isNew()))) {
			final HashMap<String, Object> map = getDefaultArguments();
			map.put("isWIF", false);
			map.put("alwManualSteps", this.alwManualSteps.isChecked());
			map.put("isAlwNewStep", isReadOnly("FinanceMainDialog_btnFinStepPolicy"));
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/StepDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_STEPDETAILS), map);
			if (tab != null) {
				tab.setDisabled(false);
				tab.setVisible(true);
			}
		} else {
			if (tab != null) {
				tab.setDisabled(true);
				tab.setVisible(false);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Disbursement Details Data in finance
	 */
	private void appendDisbursementDetailTab(boolean onLoad) {
		logger.debug("Entering");
		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_DISBURSMENT, true);
		} else {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DisbursementDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_DISBURSMENT), getDefaultArguments());
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Disbursement Details Data in finance
	 */
	private void appendChequeDetailTab(boolean onLoad) {
		logger.debug("Entering");
		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_CHEQUE, false);
		} else {
			final HashMap<String, Object> map = getDefaultArguments();
			ChequeHeader chequeHeader = null;
			if (getFinanceMain().isNew() || financeDetail.getChequeHeader() == null) {
				chequeHeader = new ChequeHeader();
				chequeHeader.setNewRecord(true);
			} else {
				chequeHeader = financeDetail.getChequeHeader();
			}
			map.put("roleCode", getRole());
			map.put("financeMainDialogCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("financeDetail", getFinanceDetail());
			map.put("tab", getTab(AssetConstants.UNIQUE_ID_CHEQUE));
			map.put("fromLoan", true);
			map.put("ccyFormatter",
					CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));
			map.put("chequeHeader", chequeHeader);
			Executions.createComponents("/WEB-INF/pages/Finance/PDC/ChequeDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_CHEQUE), map);
		}
		logger.debug("Leaving");
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	protected void appendFeeDetailTab(boolean onLoad) throws InterruptedException {
		logger.debug("Entering");
		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_FEE, true);
			} else {
				HashMap<String, Object> map = getDefaultArguments();
				map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_FEE));
				map.put("moduleDefiner", this.moduleDefiner);
				map.put("eventCode", eventCode);
				map.put("numberOfTermsLabel", Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"));
				feeDetailWindow = Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinFeeDetailList.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_FEE), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendStageAccountingDetailsTab(boolean onLoadProcess) {
		logger.debug("Entering");
		boolean createTab = false;
		if (getFinanceDetail().getStageTransactionEntries() != null
				&& getFinanceDetail().getStageTransactionEntries().size() > 0) {
			if (getTab(AssetConstants.UNIQUE_ID_STAGEACCOUNTING) == null) {
				createTab = true;
			}
		} else if (onLoadProcess) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_STAGEACCOUNTING, false);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_STAGEACCOUNTING);
		}
		if (getFinanceDetail().getStageTransactionEntries() != null
				&& getFinanceDetail().getStageTransactionEntries().size() > 0) {
			final HashMap<String, Object> map = getDefaultArguments();
			map.put("moduleDefiner", moduleDefiner);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/StageAccountingDetailsDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_STAGEACCOUNTING), map);
			Tab tab = getTab(AssetConstants.UNIQUE_ID_STAGEACCOUNTING);
			if (tab != null) {
				tab.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendContributorDetailsTab(boolean onLoadProcess) {
		logger.debug("Entering");
		if (onLoadProcess) {
			createTab(AssetConstants.UNIQUE_ID_CONTRIBUTOR, true);
		} else {
			contributorWindow = Executions.createComponents(
					"/WEB-INF/pages/Finance/FinanceMain/ContributorDetailsDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_CONTRIBUTOR), getDefaultArguments());
		}
		logger.debug("Leaving");
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	protected void appendEtihadCreditBureauDetailTab(boolean onLoad) throws InterruptedException {
		logger.debug("Entering");
		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_ETIHADCB, true);
			} else {
				HashMap<String, Object> map = getDefaultArguments();
				map.put("tab", getTab(AssetConstants.UNIQUE_ID_ETIHADCB));
				etihadCreditBureauWindow = Executions.createComponents(
						"/WEB-INF/pages/Finance/EtihadCreditBureau/EtihadCreditBureauDetailDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_ETIHADCB), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	protected void appendBundledProductsDetailTab() throws InterruptedException {
		logger.debug("Entering");
		try {
			createTab(AssetConstants.UNIQUE_ID_BUNDLEDPRODUCTS, true);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	protected void appendAgreementFieldsTab(boolean onLoad) throws InterruptedException {
		logger.debug("Entering");
		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_AGREEMENTFIELDS, true);
			} else {
				HashMap<String, Object> map = getDefaultArguments();
				map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_AGREEMENTFIELDS));
				agreementfieldsWindow = Executions.createComponents(
						"/WEB-INF/pages/Finance/AgreementFields/AgreementFieldsDetailDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_AGREEMENTFIELDS), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	protected void appendAssetEvaluationTab(boolean onLoad) throws InterruptedException {
		logger.debug("Entering");
		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_ASSETEVALUATION, true);
			} else {
				evaluationWindow = Executions.createComponents(
						"/WEB-INF/pages/Finance/FinAssetEvaluation/FinAssetEvaluationDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_ASSETEVALUATION), getDefaultArguments());
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	protected void appendAdvancePaymentsDetailTab(boolean onLoad) throws InterruptedException {
		logger.debug("Entering");
		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_ADVANCEPAYMENTS, true);
			} else {
				HashMap<String, Object> map = getDefaultArguments();
				map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_ADVANCEPAYMENTS));
				map.put("moduleDefiner", moduleDefiner);
				if (StringUtils.equals(FinanceConstants.FINSER_EVENT_ADDDISB, moduleDefiner)
						|| StringUtils.equals(FinanceConstants.FINSER_EVENT_CANCELDISB, moduleDefiner)) {
					FinanceMain finmain = getFinanceDetail().getFinScheduleData().getFinanceMain();
					map.put("approvedDisbursments",
							getFinanceDetailService().getFinanceDisbursements(finmain.getFinReference(), "", false));
				}

				advancePaymentWindow = Executions.createComponents(
						"/WEB-INF/pages/Finance/FinanceMain/FinAdvancePaymentsList.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_ADVANCEPAYMENTS), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	protected void appendCovenantTypeTab(boolean onLoad) throws InterruptedException {
		logger.debug("Entering");
		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_COVENANTTYPE, true);
			} else {

				HashMap<String, Object> map = getDefaultArguments();
				map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_COVENANTTYPE));
				//remove the current role and display allowed roles.
				map.put("allowedRoles",
						StringUtils.join(getWorkFlow().getActors(false), ';').replace(getRole().concat(";"), ""));
				covenantTypeWindow = Executions.createComponents(
						"/WEB-INF/pages/Finance/FinanceMain/FinCovenantTypeList.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_COVENANTTYPE), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	protected void appendCustomerDetailTab(boolean onLoad) throws InterruptedException {
		logger.debug("Entering");
		try {
			if (onLoad) {
				custDetailTab = new Tab(Labels.getLabel("lable_FinanceCustomer"));
				custDetailTab.setId(getTabID(AssetConstants.UNIQUE_ID_CUSTOMERS));
				tabsIndexCenter.appendChild(custDetailTab);
				Tabpanel tabpanel = new Tabpanel();
				tabpanel.setId(getTabpanelID(AssetConstants.UNIQUE_ID_CUSTOMERS));
				tabpanel.setStyle("overflow:auto;");
				tabpanel.setParent(tabpanelsBoxIndexCenter);
				tabpanel.setHeight(this.borderLayoutHeight - 100 - 20 + "px");
				ComponentsCtrl.applyForward(custDetailTab, selectMethodName);
			} else {
				HashMap<String, Object> map = getDefaultArguments();
				//In Servicing the Customer Details are not been Editable
				if (StringUtils.isNotBlank(moduleDefiner)) {
					map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
					map.put("isEnqProcess", isEnquiry);
				}
				map.put("rcuVerificationDialogCtrl", rcuVerificationDialogCtrl);
				customerWindow = Executions.createComponents(
						"/WEB-INF/pages/CustomerMasters/Customer/CustomerDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_CUSTOMERS), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Preparation of Check List Details Window
	 * 
	 * @param financeDetail
	 * @param finIsNewRecord
	 * @param map
	 */
	protected void appendCheckListDetailTab(FinanceDetail financeDetail, boolean onLoadProcess) {
		logger.debug("Entering");
		boolean createTab = false;
		if (financeDetail.getCheckList() != null && financeDetail.getCheckList().size() > 0) {
			if (getTab(AssetConstants.UNIQUE_ID_CHECKLIST) == null) {
				createTab = true;
			}
		} else if (onLoadProcess) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_CHECKLIST, false);
		}
		if (onLoadProcess && financeDetail.getCheckList() != null && financeDetail.getCheckList().size() > 0) {
			boolean createcheckLsitTab = false;
			for (FinanceReferenceDetail chkList : financeDetail.getCheckList()) {
				if (chkList.getShowInStage().contains(getRole())) {
					createcheckLsitTab = true;
					break;
				}
				if (chkList.getAllowInputInStage().contains(getRole())) {
					createcheckLsitTab = true;
					break;
				}
			}
			if (createcheckLsitTab) {
				clearTabpanelChildren(AssetConstants.UNIQUE_ID_CHECKLIST);
				checkListChildWindow = Executions.createComponents(
						"/WEB-INF/pages/LMTMasters/FinanceCheckListReference/FinanceCheckListReferenceDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_CHECKLIST), getDefaultArguments());
				Tab tab = getTab(AssetConstants.UNIQUE_ID_CHECKLIST);
				if (tab != null) {
					tab.setVisible(true);
				}
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendAccountingDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");
		boolean createTab = false;
		if (getTab(AssetConstants.UNIQUE_ID_ACCOUNTING) == null) {
			createTab = true;
		}

		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_ACCOUNTING, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_ACCOUNTING);
		}
		if (!onLoadProcess) {
			final HashMap<String, Object> map = getDefaultArguments();
			map.put("finHeaderList", getFinBasicDetails());

			FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			if (StringUtils.isBlank(eventCode)) {
				eventCode = PennantApplicationUtil.getEventCode(finMain.getFinStartDate());
			}

			long acSetID = Long.MIN_VALUE;
			if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
				acSetID = AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(), eventCode,
						FinanceConstants.MODULEID_PROMOTION);
			} else {
				acSetID = AccountingConfigCache.getAccountSetID(finMain.getFinType(), eventCode,
						FinanceConstants.MODULEID_FINTYPE);
			}
			map.put("acSetID", acSetID);

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_ACCOUNTING), map);
			Tab tab = getTab(AssetConstants.UNIQUE_ID_ACCOUNTING);
			if (tab != null) {
				tab.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	public void setAccountingDetailTab(Window window) {
		Tab tab = getTab(AssetConstants.UNIQUE_ID_ACCOUNTING);
		if (tab != null) {
			tab.setVisible(true);
			ComponentsCtrl.applyForward(tab, selectMethodName);
		}
	}

	/**
	 * Method for Append Recommend Details Tab
	 * 
	 * @throws InterruptedException
	 */
	protected void appendRecommendDetailTab(boolean onLoadProcess) throws InterruptedException {
		logger.debug("Entering");
		if (onLoadProcess) {
			createTab(AssetConstants.UNIQUE_ID_RECOMMENDATIONS, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_RECOMMENDATIONS);
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("isFinanceNotes", true);
			map.put("isRecommendMand", true);
			map.put("userRole", getRole());
			map.put("notes", getNotes(this.financeDetail.getFinScheduleData().getFinanceMain()));
			map.put("control", this);
			map.put("finHeaderList", getFinBasicDetails());
			try {
				Executions.createComponents("/WEB-INF/pages/notes/notes.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_RECOMMENDATIONS), map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Document Details Data in finance
	 */
	protected void appendDocumentDetailTab(boolean onLoad) {
		logger.debug("Entering");
		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_DOCUMENTDETAIL, true);
		} else {
			final HashMap<String, Object> map = getDefaultArguments();
			map.put("moduleDefiner", moduleDefiner);
			map.put("module", DocumentCategories.FINANCE.getKey());
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DocumentDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_DOCUMENTDETAIL), map);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Deviations in finance
	 */
	protected void appendDeviationDetailTab(boolean onLoad) {
		logger.debug("Entering");
		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_DEVIATION, true);
		} else {
			final HashMap<String, Object> map = getDefaultArguments();
			map.put("tab", getTab(AssetConstants.UNIQUE_ID_DEVIATION));
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DeviationDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_DEVIATION), map);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Mandates in finance
	 */
	protected void appendMandateDetailTab(boolean onLoad) {
		logger.debug("Entering");
		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_MANDATE, false);
		} else {
			final HashMap<String, Object> map = getDefaultArguments();
			map.put("tab", getTab(AssetConstants.UNIQUE_ID_MANDATE));
			map.put("fromLoan", true);
			Executions.createComponents("/WEB-INF/pages/Mandate/MandateDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_MANDATE), map);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Appending tab for GST Details in Finance Origination
	 */
	protected void appendTaxDetailTab(boolean onLoad) {
		logger.debug("Entering");
		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_TAX, false);
		} else {
			final HashMap<String, Object> map = getDefaultArguments();
			map.put("tab", getTab(AssetConstants.UNIQUE_ID_TAX));
			map.put("fromLoan", true);
			map.put("panNum", getFinanceDetail().getCustomerDetails().getCustomer().getCustCRCPR());
			FinanceTaxDetail financetaxdetail = getFinanceDetail().getFinanceTaxDetails();
			if (financetaxdetail == null) {
				financetaxdetail = new FinanceTaxDetail();
				financetaxdetail.setNewRecord(true);
			}
			map.put("financeTaxDetail", financetaxdetail);
			map.put("financeDetail", getFinanceDetail());
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceTaxDetail/FinanceTaxDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_TAX), map);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Collateral Details Data in finance
	 */
	protected void appendFinCollateralTab(boolean onLoad) {
		logger.debug("Entering");
		if (!ImplementationConstants.COLLATERAL_INTERNAL) {
			if (!getFinanceDetail().getFinScheduleData().getFinanceType().isFinCollateralReq()) {
				return;
			}
		}

		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_COLLATERAL, true);
		} else {
			final HashMap<String, Object> map = getDefaultArguments();
			FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
			BigDecimal utilizedAmt = BigDecimal.ZERO;
			if (ImplementationConstants.COLLATERAL_INTERNAL) {
				map.put("collateralAssignmentList", getFinanceDetail().getCollateralAssignmentList());
				map.put("assetTypeList", getFinanceDetail().getExtendedFieldRenderList());
				map.put("finassetTypeList", getFinanceDetail().getFinAssetTypesList());	
				
				if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(financeType.getFinLTVCheck())) {
					utilizedAmt = financeMain.getFinAssetValue().subtract(financeMain.getFinRepaymentAmount());
				} else {
					utilizedAmt = financeMain.getFinCurrAssetValue().subtract(financeMain.getFinRepaymentAmount());
				}
				map.put("utilizedAmount",utilizedAmt);
				map.put("finAssetValue",financeMain.getFinAssetValue());
				map.put("finType", financeMain.getFinType());
				map.put("customerId", financeMain.getCustID());
				map.put("assetsReq", true);
				map.put("collateralReq", getFinanceDetail().getFinScheduleData().getFinanceType().isFinCollateralReq()
						|| !getFinanceDetail().getCollateralAssignmentList().isEmpty());

				map.put("assignCollateralRef", assignCollateralRef);

				map.put("finLTVCheck", financeType.getFinLTVCheck());
				map.put("finDivision", financeType.getFinDivision());
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/CollateralHeaderDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_COLLATERAL), map);
			} else {

				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinCollateralHeaderDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_COLLATERAL), map);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Collateral Details Data in finance
	 */
	protected void appendVasRecordingTab(boolean onLoad) {
		logger.debug("Entering");
		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_VAS, true);
		} else {
			final HashMap<String, Object> map = getDefaultArguments();
			map.put("financemainBaseCtrl", this);
			map.put("vasRecordingList", getFinanceDetail().getFinScheduleData().getVasRecordingList());
			map.put("finType", getFinanceDetail().getFinScheduleData().getFinanceMain().getFinType());
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinVasRecordingDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_VAS), map);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug("Entering");
		String tabName = "";
		if (StringUtils.equals(AssetConstants.UNIQUE_ID_JOINTGUARANTOR, moduleID)) {
			tabName = Labels.getLabel("tab_Co-borrower&Gurantors");
		} else if (StringUtils.equals(AssetConstants.UNIQUE_ID_ADDITIONALFIELDS, moduleID)) {
			tabName = getFinanceDetail().getExtendedFieldHeader().getTabHeading();
		} else {
			tabName = Labels.getLabel("tab_label_" + moduleID);
		}
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight("100%");
		ComponentsCtrl.applyForward(tab, ("onSelect=" + selectMethodName));
		logger.debug("Leaving");
	}

	public void onSelectTab(ForwardEvent event)
			throws IllegalAccessException, InvocationTargetException, InterruptedException, ParseException,
			WrongValueException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		Tab tab = (Tab) event.getOrigin().getTarget();
		logger.debug(tab.getId() + " --> " + "Entering");
		String module = getIDbyTab(tab.getId());
		switch (module) {
		case AssetConstants.UNIQUE_ID_ELIGIBLITY:
			eligibilityDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_SCORING:
			scoringDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_CUSTOMERS:
			if (customerDialogCtrl == null && StringUtils.isNotEmpty(moduleDefiner)) {
				appendCustomerDetailTab(false);
			}
			if (customerDialogCtrl != null) {
				customerDialogCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_STEPDETAILS:
			stepDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_JOINTGUARANTOR:
			jointAccountDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_DISBURSMENT:
			disbursementDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_STAGEACCOUNTING:
			stageAccountingDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_DEVIATION:
			deviationDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_MANDATE:
			mandateDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_TAX:
			financeTaxDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_CONTRIBUTOR:
			contributorDetailsDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_ETIHADCB:
			etihadCreditBureauDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_AGREEMENTFIELDS:
			agreementFieldsDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_ASSETEVALUATION:
			finAssetEvaluationDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_ADVANCEPAYMENTS:
			finAdvancePaymentsListCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_FEE:
			finFeeDetailListCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_CHEQUE:
			chequeDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_COVENANTTYPE:
			finCovenantTypeListCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_SCHEDULE:
			tab.removeForward(Events.ON_SELECT, this.window, selectMethodName);
			if (!this.manualSchedule.isChecked() || getManualScheduleDetailDialogCtrl() == null) {
				appendScheduleDetailTab(false, false);
			}
			break;
		case AssetConstants.UNIQUE_ID_BUNDLEDPRODUCTS:
			tab.removeForward(Events.ON_SELECT, (Tab) null, selectMethodName);
			renderBundledProducts();
			break;
		case AssetConstants.UNIQUE_ID_RECOMMENDATIONS:
			tab.removeForward(Events.ON_SELECT, (Tab) null, selectMethodName);
			appendRecommendDetailTab(false);
			break;
		case AssetConstants.UNIQUE_ID_COLLATERAL:
			if (ImplementationConstants.COLLATERAL_INTERNAL) {
				collateralHeaderDialogCtrl.doSetLabels(getFinBasicDetails());

				FinanceMain  financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
				FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
				int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

				BigDecimal utilizedAmt = BigDecimal.ZERO;

				if (!financeMain.isLovDescIsSchdGenerated()) {
					if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(financeType.getFinLTVCheck())) {
						boolean allowDisb = financeType.isAlwMaxDisbCheckReq();
						if (allowDisb) {
							utilizedAmt = PennantAppUtil.unFormateAmount((this.finAssetValue.getActualValue().compareTo(BigDecimal.ZERO) > 0
									? this.finAssetValue.getActualValue() : this.finAmount.getActualValue()).subtract(this.downPayBank.getActualValue()).subtract(
													this.downPaySupl.getActualValue()), formatter).add(financeMain.getFeeChargeAmt()).add(financeMain.getInsuranceAmt());
						} else {
							utilizedAmt = PennantAppUtil.unFormateAmount(this.finAmount.getActualValue().subtract(this.downPayBank.getActualValue())
									.subtract(this.downPaySupl.getActualValue()), formatter).add(financeMain.getFeeChargeAmt()).add(financeMain.getInsuranceAmt());
						}
					} else {
						utilizedAmt = PennantAppUtil.unFormateAmount(this.finAmount.getActualValue().subtract(this.downPayBank.getActualValue())
												.subtract(this.downPaySupl.getActualValue()), formatter).add(financeMain.getFeeChargeAmt()).add(financeMain.getInsuranceAmt());
					}
				} else {
					if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(financeType.getFinLTVCheck())) {
					   boolean allowDisb = 	financeType.isAlwMaxDisbCheckReq();
					   if(allowDisb){
						   utilizedAmt = utilizedAmt.add(BigDecimal.ZERO.compareTo(financeMain.getFinAssetValue()) < 0
								   ? financeMain.getFinAssetValue() : financeMain.getFinAmount()).add(financeMain.getFeeChargeAmt().add(financeMain.getInsuranceAmt()));
					   } else {
						   utilizedAmt = utilizedAmt.add(BigDecimal.ZERO.compareTo(financeMain.getFinAmount()) < 0
								   ? financeMain.getFinAmount() : BigDecimal.ZERO).add(financeMain.getFeeChargeAmt().add(financeMain.getInsuranceAmt()));
					   }
					} else {
						for (FinanceDisbursement curDisb : getFinanceDetail().getFinScheduleData().getDisbursementDetails()) {
							if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus())) {
								continue;
							}
							utilizedAmt = utilizedAmt.add(curDisb.getDisbAmount()).add(curDisb.getFeeChargeAmt().add(curDisb.getInsuranceAmt()));
						}
						utilizedAmt = utilizedAmt.subtract(PennantAppUtil.unFormateAmount(this.downPayBank.getActualValue().subtract(this.downPaySupl.getActualValue()), formatter))
								.subtract(financeMain.getFinRepaymentAmount());
					}
				}
				if (this.oldVar_utilizedAmount != utilizedAmt) {
					collateralHeaderDialogCtrl.updateUtilizedAmount(utilizedAmt);
					this.oldVar_utilizedAmount = utilizedAmt;
				}
			} else {
				finCollateralHeaderDialogCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_VAS:
			finVasRecordingDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_ACCOUNTING:
			tab.removeForward(Events.ON_SELECT, (Tab) null, selectMethodName);
			appendAccountingDetailTab(false);
			if (accountingDetailDialogCtrl != null) {
				accountingDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_AGREEMENT:
			this.doWriteComponentsToBean(getFinanceDetail().getFinScheduleData());
			if (customerDialogCtrl != null && customerDialogCtrl.getCustomerDetails() != null) {
				customerDialogCtrl.doSave_CustomerDetail(getFinanceDetail(), custDetailTab, false);
			}
			// refresh template tab
			if (agreementDetailDialogCtrl != null) {
				agreementDetailDialogCtrl.doSetLabels(getFinBasicDetails());
				agreementDetailDialogCtrl.doShowDialog(false);
			}
			break;
		case AssetConstants.UNIQUE_ID_CHECKLIST:
			this.doWriteComponentsToBean(getFinanceDetail().getFinScheduleData());
			if (customerDialogCtrl != null && customerDialogCtrl.getCustomerDetails() != null) {
				customerDialogCtrl.doSetLabels(getFinBasicDetails());
				customerDialogCtrl.doSave_CustomerDetail(getFinanceDetail(), custDetailTab, false);
			}
			if (financeCheckListReferenceDialogCtrl != null) {
				financeCheckListReferenceDialogCtrl.doSetLabels(getFinBasicDetails());
				financeCheckListReferenceDialogCtrl.doWriteBeanToComponents(getFinanceDetail().getCheckList(),
						getFinanceDetail().getFinanceCheckList(), false);
			}
			break;
		case AssetConstants.UNIQUE_ID_FIINITIATION:
			if (fieldVerificationDialogCtrl != null) {
				fieldVerificationDialogCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_TVINITIATION:
			if (tVerificationDialogCtrl != null) {
				tVerificationDialogCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_LVINITIATION:
			if (lVerificationCtrl != null) {
				lVerificationCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_RCUINITIATION:
			if (rcuVerificationDialogCtrl != null) {
				rcuVerificationDialogCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW:
			appendCreditReviewDetailTab(true);
			break;
		case AssetConstants.UNIQUE_ID_QUERY_MGMT:
			tab.removeForward(Events.ON_SELECT, (Tab) null, selectMethodName);
			if (isTabVisible(StageTabConstants.QueryMangement)) {
				appendQueryMangementTab(true);
			}
			break;
		case AssetConstants.UNIQUE_ID_PSL_DETAILS:
			pSLDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		default:
			break;
		}

		logger.debug(tab.getId() + " --> " + "Leaving");
	}

	public void onSelectAddlDetailTab(ForwardEvent event) {
		finBasicDetailsCtrl.doWriteBeanToComponents(getFinBasicDetails());
	}

	public HashMap<String, Object> getDefaultArguments() {
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("finHeaderList", getFinBasicDetails());
		map.put("financeDetail", getFinanceDetail());
		map.put("isFinanceProcess", true);
		map.put("ccyFormatter",
				CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));
		return map;
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getIDbyTab(String tabID) {
		return tabID.replace("TAB", "");
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	private void clearTabpanelChildren(String id) {
		Tabpanel tabpanel = getTabpanel(id);
		if (tabpanel != null) {
			tabpanel.setStyle("overflow:auto;");
			tabpanel.getChildren().clear();
		}
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            financeMain
	 * @throws ParseException
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail, boolean onLoadProcess) throws ParseException,
			InterruptedException, InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		int format = CurrencyUtil.getFormat(aFinanceMain.getFinCcy());
		Customer customer = null;
		if (aFinanceDetail.getCustomerDetails() != null && aFinanceDetail.getCustomerDetails().getCustomer() != null) {
			customer = aFinanceDetail.getCustomerDetails().getCustomer();
		}
		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		//Showing Product Details for Promotion Type
		this.finDivisionName.setValue(financeType.getFinDivision() + " - " + financeType.getLovDescFinDivisionName());
		if (StringUtils.isNotEmpty(financeType.getPromotionCode())) {
			this.hbox_PromotionProduct.setVisible(true);
			this.label_FinanceMainDialog_PromotionProduct.setVisible(true);
			this.promotionProduct.setValue(financeType.getPromotionCode() + " - " + financeType.getPromotionDesc());
		}

		this.repayAcctId.setMandatoryStyle(!isReadOnly("FinanceMainDialog_ManRepayAcctId"));
		if (getWorkFlow() != null && !"Accounting".equals(getTaskTabs(getTaskId(getRole())))) {
			this.disbAcctId.setMandatoryStyle(!isReadOnly("FinanceMainDialog_MandisbAcctId"));
			this.downPayAccount.setMandatoryStyle(!isReadOnly("FinanceMainDialog_MandownPaymentAcc"));
		} else {
			this.disbAcctId.setMandatoryStyle(true);
			this.downPayAccount.setMandatoryStyle(true);
			if (this.downPayBank.isReadonly() && aFinanceMain.getDownPayBank().compareTo(BigDecimal.ZERO) == 0) {
				this.downPayAccount.setMandatoryStyle(false);
			}
		}

		if (isReadOnly("FinanceMainDialog_ManRepayAcctId")) {
			this.repayAcctId.setMandatoryStyle(false);
		} else {
			this.repayAcctId.setMandatoryStyle(true);
		}

		// Account
		if (isOverdraft) {
			this.repayAcctId.setMandatoryStyle(false);
			this.disbAcctId.setMandatoryStyle(false);
		}

		// Finance MainDetails Tab ---> 1. Basic Details

		this.finType.setValue(aFinanceMain.getFinType());
		this.finCcy.setValue(aFinanceMain.getFinCcy());
		fillComboBox(this.cbProfitDaysBasis, aFinanceMain.getProfitDaysBasis(),
				PennantStaticListUtil.getProfitDaysBasis(), "");
		this.finBranch.setValue(aFinanceMain.getFinBranch());
		if (customer != null) {
			this.custCIF.setValue(customer.getCustCIF());
			this.custShrtName.setValue(customer.getCustShrtName());
		}
		this.custID.setValue(aFinanceMain.getCustID());
		this.disbAcctId.setCustCIF(aFinanceMain.getLovDescCustCIF());
		this.repayAcctId.setCustCIF(aFinanceMain.getLovDescCustCIF());
		this.downPayAccount.setCustCIF(aFinanceMain.getLovDescCustCIF());
		this.finPurpose.setValue(aFinanceMain.getFinPurpose());
		this.finAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAmount(), format));
		this.finAssetValue.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAssetValue(), format));
		this.finCurrentAssetValue.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinCurrAssetValue(), format));
		this.disbAcctId.setValue(aFinanceMain.getDisbAccountId());
		this.repayAcctId.setValue(aFinanceMain.getRepayAccountId());

		String repayMethod = aFinanceMain.getFinRepayMethod();
		if (StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_AHB)) {
			if (StringUtils.isEmpty(repayMethod)) {
				if (!getFinanceDetail().getCustomerDetails().getCustomer().isSalariedCustomer()) {
					repayMethod = FinanceConstants.REPAYMTH_AUTODDA;
				} else {
					repayMethod = FinanceConstants.REPAYMTH_AUTO;
				}
			}
		}

		List<ValueLabel> rpyMethodList = new ArrayList<>();
		if (StringUtils.isNotEmpty(financeType.getAlwdRpyMethods())) {
			String[] rpMthds = financeType.getAlwdRpyMethods().trim().split(",");
			if (rpMthds.length > 0) {
				List<String> list = Arrays.asList(rpMthds);
				for (ValueLabel rpyMthd : PennantStaticListUtil.getRepayMethods()) {
					if (list.contains(rpyMthd.getValue().trim())) {
						rpyMethodList.add(rpyMthd);
					}
				}
			}
		}

		fillComboBox(this.finRepayMethod, repayMethod, rpyMethodList, "");
		fillComboBox(this.accountType, "", PennantStaticListUtil.getAccountTypes(), "");
		doCheckDDA();

		this.commitmentRef.setValue(aFinanceMain.getFinCommitmentRef(),
				StringUtils.trimToEmpty(aFinanceMain.getFinCommitmentRef()));

		this.finLimitRef.setValue(aFinanceMain.getFinLimitRef(),
				StringUtils.trimToEmpty(aFinanceMain.getFinLimitRef()));
		this.mMAReference.setValue(String.valueOf(aFinanceMain.getMMAId()));
		this.mMAReference.setDescription(aFinanceMain.getLovDescMMAReference());

		if (!aFinanceMain.isNew() && !StringUtils.isBlank(aFinanceMain.getFinLimitRef())) {
			processLimitData();
		}
		if (aFinanceMain.isNew() && financeType.isLimitRequired()) {
			if (!ImplementationConstants.LIMIT_INTERNAL) {
				checkLimitDetailsForSingleLimit();
			}
		}

		this.accountsOfficer.setValue(StringUtils.trimToEmpty(aFinanceMain.getLovDescAccountsOfficer()));
		this.accountsOfficer.setDescription(StringUtils.trimToEmpty(aFinanceMain.getLovDescSourceCity()));
		this.accountsOfficer.setAttribute("DealerId", aFinanceMain.getAccountsOfficer());

		if (!aFinanceMain.isNewRecord()) {
			this.dsaCode.setValue(StringUtils.trimToEmpty((aFinanceMain.getDsaName())),
					StringUtils.trimToEmpty(aFinanceMain.getDsaCodeDesc()));
			if (aFinanceMain.getDsaCode() != null) {
				this.dsaCode.setAttribute("DSAdealerID", aFinanceMain.getDsaCode());
			} else {
				this.dsaCode.setAttribute("DSAdealerID", null);
			}
		}

		if (!aFinanceMain.isNewRecord()) {
			this.dmaCode.setValue(StringUtils.trimToEmpty((aFinanceMain.getDmaName())),
					StringUtils.trimToEmpty(aFinanceMain.getDmaCodeDesc()));
			if (aFinanceMain.getDsaCode() != null) {
				this.dmaCode.setAttribute("DMAdealerID", aFinanceMain.getDmaCode());
			} else {
				this.dmaCode.setAttribute("DMAdealerID", null);
			}
		}
		
		if (!aFinanceMain.isNewRecord()) {
			this.connector.setValue(StringUtils.trimToEmpty((aFinanceMain.getConnectorCode())),
					StringUtils.trimToEmpty(aFinanceMain.getConnectorDesc()));
			if (aFinanceMain.getConnector() > 0) {
				this.connector.setAttribute("DealerId", aFinanceMain.getConnector());
			} else {
				this.connector.setAttribute("DealerId", 0);
			}
		}

		if (financeType.isFinDepreciationReq()) {
			this.depreciationFrq.setVisible(true);
			this.label_FinanceMainDialog_DepriFrq.setVisible(true);
			this.depreciationFrq.setDisabled(isReadOnly("FinanceMainDialog_depreciationFrq"));
			this.depreciationFrq.setValue(aFinanceMain.getDepreciationFrq());
		} else {
			this.label_FinanceMainDialog_DepriFrq.setVisible(false);
			this.depreciationFrq.setMandatoryStyle(false);
			this.depreciationFrq.setDisabled(true);
		}

		this.finIsActive.setChecked(aFinanceMain.isFinIsActive());
		this.tDSApplicable.setChecked(aFinanceMain.isTDSApplicable());
		//TDSApplicable Visiblitly based on Financetype Selection
		if (financeType.isTDSApplicable()) {
			this.hbox_tdsApplicable.setVisible(true);
			this.label_FinanceMainDialog_TDSApplicable.setVisible(true);
		} else {
			this.hbox_tdsApplicable.setVisible(false);
			this.label_FinanceMainDialog_TDSApplicable.setVisible(false);
		}

		this.lovDescFinTypeName.setValue(aFinanceMain.getFinType() + "-" + aFinanceMain.getLovDescFinTypeName());
		this.finCcy.setValue(aFinanceMain.getFinCcy(), CurrencyUtil.getCcyDesc(aFinanceMain.getFinCcy()));
		if (StringUtils.isNotBlank(aFinanceMain.getFinBranch())) {
			this.finBranch.setDescription(aFinanceMain.getLovDescFinBranchName());
		}

		if (aFinanceMain.getFinStartDate() != null) {
			this.finStartDate.setValue(aFinanceMain.getFinStartDate());
		}

		if (aFinanceMain.getFinContractDate() != null) {
			this.finContractDate.setValue(aFinanceMain.getFinContractDate());
		} else {
			this.finContractDate.setValue(aFinanceMain.getFinStartDate());
		}

		setDownpaymentRulePercentage(false);

		if (financeType.isFinIsDwPayRequired() && aFinanceMain.getMinDownPayPerc().compareTo(BigDecimal.ZERO) >= 0) {
			this.row_downPayBank.setVisible(true);
			this.row_downPaySupl.setVisible(true);
			if (getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_ISTISNA)
					|| getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_SUKUK)) {
				this.downPayPercentage.setVisible(true);
			} else {
				this.row_downPayPercentage.setVisible(false);
			}
			this.downPayAccount.setValue(aFinanceMain.getDownPayAccount());
			this.downPayBank.setValue(PennantAppUtil.formateAmount(aFinanceMain.getDownPayBank(), format));
			this.downPaySupl.setValue(PennantAppUtil.formateAmount(aFinanceMain.getDownPaySupl(), format));
			if (aFinanceMain.isNewRecord()) {
				this.downPayAccount.setValue("");
			} else {
				this.downPayAccount.setValue(aFinanceMain.getDownPayAccount());
			}

			if (this.downPayBank.isReadonly() && aFinanceMain.getDownPayBank().compareTo(BigDecimal.ZERO) == 0) {
				this.downPayAccount.setMandatoryStyle(false);
				this.downPayAccount.setReadonly(true);
				this.row_downPayBank.setVisible(false);
			}

			if (this.downPayBank.isReadonly() && this.downPaySupl.isReadonly()
					&& aFinanceMain.getDownPayment().compareTo(BigDecimal.ZERO) == 0) {
				if (getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_ISTISNA)
						|| getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_SUKUK)) {
					this.downPayPercentage.setVisible(false);
				} else {
					this.row_downPayPercentage.setVisible(false);
				}
			}

			if (this.downPaySupl.isReadonly() && aFinanceMain.getDownPaySupl().compareTo(BigDecimal.ZERO) == 0) {
				this.row_downPaySupl.setVisible(false);
			}
		} else {
			this.downPayAccount.setMandatoryStyle(false);
		}

		if (getFinanceDetail().getFinScheduleData().getFinanceMain().getMinDownPayPerc()
				.compareTo(BigDecimal.ZERO) == 0) {
			this.downPayBank.setMandatory(false);
			this.downPaySupl.setMandatory(false);
		}

		if (financeType.isAllowDownpayPgm()) {
			this.row_downPaySupl.setVisible(true);
			this.downPayAccount.setReadonly(true);
			this.downPayBank.setReadonly(true);
			this.downPayAccount.setValue(SysParamUtil.getValueAsString("AHB_DOWNPAY_AC"));
		}
		setDownPayPercentage();
		setNetFinanceAmount(true);
		//Setting DownPayment Supplier to Invisible state to some of the Products
		if (getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_MUSHARAKA)
				|| getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_SUKUKNRM)
				|| getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_ISTISNA)) {
			this.row_downPaySupl.setVisible(false);
			this.downPaySupl.setReadonly(true);
		}
		if (getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_ISTISNA)) {
			this.disbAcctId.setReadonly(true);
		}

		this.finPurpose.setValue(aFinanceMain.getFinPurpose());
		if (StringUtils.isNotBlank(aFinanceMain.getFinPurpose())) {
			this.finPurpose.setDescription(aFinanceMain.getLovDescFinPurposeName());
		}

		if (financeType.isManualSchedule()) {
			this.row_ManualSchedule.setVisible(true);
			this.manualSchedule.setChecked(aFinanceMain.isManualSchedule());
		} else {
			this.row_ManualSchedule.setVisible(false);
		}

		// Step Finance
		if ((aFinanceMain.isNewRecord() || !aFinanceMain.isStepFinance()) && !financeType.isStepFinance()) {
			this.row_stepFinance.setVisible(false);
		}
		this.stepFinance.setChecked(aFinanceMain.isStepFinance());
		doStepPolicyCheck(false);
		this.stepPolicy.setValue(aFinanceMain.getStepPolicy());
		this.stepPolicy.setDescription(aFinanceMain.getLovDescStepPolicyName());
		this.alwManualSteps.setChecked(aFinanceMain.isAlwManualSteps());
		doAlwManualStepsCheck(false);
		this.noOfSteps.setValue(aFinanceMain.getNoOfSteps());
		fillComboBox(this.stepType, aFinanceMain.getStepType(), PennantStaticListUtil.getStepType(), "");

		if (aFinanceMain.isNewRecord()) {
			if (aFinanceMain.isAlwManualSteps()
					&& getFinanceDetail().getFinScheduleData().getStepPolicyDetails() == null) {
				getFinanceDetail().getFinScheduleData().resetStepPolicyDetails(null);
			}
		}

		if (StringUtils.isNotEmpty(aFinanceMain.getShariaStatus())
				&& !StringUtils.equals(PennantConstants.SHARIA_STATUS_NOTREQUIRED, aFinanceMain.getShariaStatus())) {
			this.shariaApprovalReq.setChecked(true);
		} else {
			this.shariaApprovalReq.setChecked(false);
		}
		if (StringUtils.equals(PennantConstants.SHARIA_STATUS_DECLINED, aFinanceMain.getShariaStatus())) {
			this.shariaApprovalReq.setDisabled(true);
		}

		// fill the components with the Finance Flags Data and Display
		doFillFinFlagsList(aFinanceDetail.getFinFlagsDetails());
		this.applicationNo.setValue(aFinanceMain.getApplicationNo());

		if (aFinanceMain.getReferralId() != null) {
			this.referralId.setValue(aFinanceMain.getReferralId());
			this.referralId.setDescription(aFinanceMain.getReferralIdDesc());
		}
		if (this.row_employeeName != null && this.row_employeeName.isVisible()) {
			this.employeeName.setValue(aFinanceMain.getEmployeeName());
			this.employeeName.setDescription(aFinanceMain.getEmployeeNameDesc());
		}

		if (aFinanceMain.getSalesDepartment() != null) {
			this.salesDepartment.setValue(aFinanceMain.getSalesDepartment());
			this.salesDepartment.setDescription(aFinanceMain.getSalesDepartmentDesc());
		}

		this.quickDisb.setChecked(aFinanceMain.isQuickDisb());

		// Finance MainDetails Tab ---> 2. Grace Period Details
		if (financeType.isFInIsAlwGrace()) {

			if (aFinanceMain.getGrcPeriodEndDate() == null) {
				aFinanceMain.setGrcPeriodEndDate(aFinanceMain.getFinStartDate());
			}

			this.allowGrace.setChecked(aFinanceMain.isAllowGrcPeriod());
			this.gb_gracePeriodDetails.setVisible(true);

			if (this.manualSchedule.isChecked()) {
				this.gracePeriodEndDate.setValue(aFinanceMain.getGrcPeriodEndDate());
			} else {
				if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_CHGGRCEND)) {
					this.gracePeriodEndDate.setValue(aFinanceMain.getGrcPeriodEndDate());
				} else {
					this.gracePeriodEndDate.setText("");
				}
			}

			this.gracePeriodEndDate_two.setValue(aFinanceMain.getGrcPeriodEndDate());
			fillComboBox(this.grcRateBasis, aFinanceMain.getGrcRateBasis(),
					PennantStaticListUtil.getInterestRateType(!aFinanceMain.isMigratedFinance()), ",C,D,");

			fillComboBox(this.cbGrcSchdMthd, aFinanceMain.getGrcSchdMthd(), PennantStaticListUtil.getScheduleMethods(),
					",EQUAL,PRI_PFT,PRI,");
			if (aFinanceMain.isAllowGrcRepay()) {
				this.graceTerms.setVisible(true);
				this.grcRepayRow.setVisible(true);
				this.allowGrcRepay.setChecked(aFinanceMain.isAllowGrcRepay());
			}

			this.graceTerms.setText("");
			this.graceRate.setMarginValue(aFinanceMain.getGrcMargin());
			fillComboBox(this.grcPftDaysBasis, aFinanceMain.getGrcProfitDaysBasis(),
					PennantStaticListUtil.getProfitDaysBasis(), "");
			if (StringUtils.isNotEmpty(aFinanceMain.getGraceBaseRate()) && StringUtils.equals(
					CalculationConstants.RATE_BASIS_R, this.grcRateBasis.getSelectedItem().getValue().toString())) {
				this.grcBaseRateRow.setVisible(true);
				this.graceRate.setVisible(true);
				this.graceRate.setBaseValue(aFinanceMain.getGraceBaseRate());
				this.graceRate.setSpecialValue(aFinanceMain.getGraceSpecialRate());
				if ((financeType.getFInGrcMinRate() == null
						|| BigDecimal.ZERO.compareTo(financeType.getFInGrcMinRate()) == 0)
						&& (financeType.getFinGrcMaxRate() == null
								|| BigDecimal.ZERO.compareTo(financeType.getFinGrcMaxRate()) == 0)) {
					this.row_FinGrcRates.setVisible(false);
					this.finGrcMinRate.setValue(BigDecimal.ZERO);
					this.finGrcMaxRate.setValue(BigDecimal.ZERO);
				} else {
					this.row_FinGrcRates.setVisible(true);
					if (aFinanceMain.isNewRecord()) {
						this.finGrcMinRate.setValue(financeType.getFInGrcMinRate());
						this.finGrcMaxRate.setValue(financeType.getFinGrcMaxRate());
					} else {
						this.finGrcMinRate.setValue(aFinanceMain.getGrcMinRate());
						this.finGrcMaxRate.setValue(aFinanceMain.getGrcMaxRate());
					}
				}

				RateDetail rateDetail = RateUtil.rates(aFinanceMain.getGraceBaseRate(), aFinanceMain.getFinCcy(),
						aFinanceMain.getGraceSpecialRate(), aFinanceMain.getGrcMargin(), aFinanceMain.getGrcMinRate(),
						aFinanceMain.getGrcMaxRate());

				if (rateDetail.getErrorDetails() == null) {
					this.graceRate.setEffRateText(
							PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
				}
				readOnlyComponent(true, this.gracePftRate);

			} else {
				this.row_FinGrcRates.setVisible(false);
				this.grcBaseRateRow.setVisible(false);
				this.graceRate.setVisible(false);
				this.graceRate.setBaseValue("");
				this.graceRate.setBaseDescription("");
				this.graceRate.setBaseReadonly(true);
				this.graceRate.setSpecialValue("");
				this.graceRate.setSpecialDescription("");
				this.graceRate.setSpecialReadonly(true);
				this.graceRate.setMarginReadonly(true);
				this.graceRate.setMarginValue(BigDecimal.ZERO);
				readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
				this.gracePftRate.setValue(aFinanceMain.getGrcPftRate());

				if (aFinanceMain.getGrcPftRate().compareTo(BigDecimal.ZERO) > 0) {
					this.graceRate.setEffRateValue(BigDecimal.ZERO);
					this.graceRate.setEffRateText("0.00");
				}

				this.finGrcMinRate.setValue(BigDecimal.ZERO);
				this.finGrcMaxRate.setValue(BigDecimal.ZERO);

				this.grcAdvRate.setBaseValue("");
				this.grcAdvRate.setBaseDescription("");
				this.grcAdvRate.setMarginText("");
				this.grcAdvPftRate.setText("");
			}

			// Effective Rate Setting
			if (StringUtils.isNotEmpty(this.graceRate.getBaseValue())) {
				calculateRate(this.graceRate.getBaseValue(), this.finCcy.getValue(), this.graceRate.getSpecialComp(),
						this.graceRate.getBaseComp(), this.graceRate.getMarginValue(), this.graceRate.getEffRateComp(),
						this.finGrcMinRate, this.finMaxRate);
			}

			//Advised profit Rates
			doCheckAdviseRates(aFinanceMain.getGrcAdvBaseRate(), aFinanceMain.getRpyAdvBaseRate(), true,
					financeType.getFinCategory());
			this.grcAdvRate.setBaseValue(aFinanceMain.getGrcAdvBaseRate());
			this.grcAdvRate.setMarginValue(aFinanceMain.getGrcAdvMargin());
			this.grcAdvPftRate.setValue(aFinanceMain.getGrcAdvPftRate());
			calAdvPftRate(this.grcAdvRate.getBaseValue(), this.finCcy.getValue(), this.grcAdvRate.getMarginValue(),
					BigDecimal.ZERO, BigDecimal.ZERO, this.grcAdvRate.getEffRateComp());

			this.grcPftFrqRow.setVisible(true);
			this.gracePftFrq.setValue(aFinanceMain.getGrcPftFrq());

			readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftDate"), this.nextGrcPftDate);
			if (aFinanceMain.isAllowGrcPftRvw()) {
				if (StringUtils.isNotBlank(aFinanceMain.getGrcPftRvwFrq())
						&& !StringUtils.equals(aFinanceMain.getGrcPftRvwFrq(), PennantConstants.List_Select)) {
					this.grcPftRvwFrqRow.setVisible(true);
					this.gracePftRvwFrq.setValue(aFinanceMain.getGrcPftRvwFrq());
				}
				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"), this.nextGrcPftRvwDate);
			} else {
				this.gracePftRvwFrq.setDisabled(true);
				this.nextGrcPftRvwDate.setValue(SysParamUtil.getValueAsDate("APP_DFT_ENDDATE"));
				readOnlyComponent(true, this.nextGrcPftRvwDate);

			}

			if (aFinanceMain.isAllowGrcCpz()) {
				this.graceCpzFrq.setDisabled(isReadOnly("FinanceMainDialog_graceCpzFrq"));
				if (StringUtils.isNotBlank(aFinanceMain.getGrcCpzFrq())
						|| !StringUtils.trimToEmpty(aFinanceMain.getGrcCpzFrq()).equals(PennantConstants.List_Select)) {
					this.grcCpzFrqRow.setVisible(true);
					this.graceCpzFrq.setValue(aFinanceMain.getGrcCpzFrq());
				}
				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcCpzDate"), this.nextGrcCpzDate);
			} else {
				this.graceCpzFrq.setDisabled(true);
				this.nextGrcCpzDate.setValue(SysParamUtil.getValueAsDate("APP_DFT_ENDDATE"));
				readOnlyComponent(true, this.nextGrcCpzDate);
			}

			if (!this.allowGrace.isChecked()) {
				doAllowGraceperiod(false);
			}
			
			onChangeGrcSchdMthd();
			this.grcMaxAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getGrcMaxAmount(), format));

		} else {
			this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			this.gb_gracePeriodDetails.setVisible(false);
			this.allowGrace.setVisible(false);
			this.label_FinanceMainDialog_AlwGrace.setVisible(false);
		}
		if (this.allowGrace.isDisabled() && !this.allowGrace.isChecked()) {
			this.label_FinanceMainDialog_AlwGrace.setVisible(false);
			this.allowGrace.setVisible(false);
		}

		// Show default date values beside the date components
		this.graceTerms_Two.setValue(0);
		if (aFinanceMain.isAllowGrcPeriod()) {
			this.graceTerms_Two.setValue(aFinanceMain.getGraceTerms());
			this.nextGrcPftDate_two.setValue(aFinanceMain.getNextGrcPftDate());
			this.nextGrcPftRvwDate_two.setValue(aFinanceMain.getNextGrcPftRvwDate());
			this.nextGrcCpzDate_two.setValue(aFinanceMain.getNextGrcCpzDate());

			this.nextGrcPftDate.setValue(aFinanceMain.getNextGrcPftDate());
			this.nextGrcPftRvwDate.setValue(aFinanceMain.getNextGrcPftRvwDate());
			this.nextGrcCpzDate.setValue(aFinanceMain.getNextGrcCpzDate());
		}

		// Finance MainDetails Tab ---> 3. Repayment Period Details
		if (isOverdraft) {
			if (aFinanceDetail.getFinScheduleData().getFinanceType().isDroplineOD()
					|| StringUtils.isNotEmpty(aFinanceMain.getDroplineFrq())) {
				if (StringUtils.isNotEmpty(aFinanceMain.getDroplineFrq())) {
					this.droplineFrq.setValue(aFinanceMain.getDroplineFrq());
				} else {
					this.droplineFrq.setValue(aFinanceMain.getRepayFrq());
				}
				this.firstDroplineDate.setValue(aFinanceMain.getFirstDroplineDate());
			} else {
				this.row_DroplineFrq.setVisible(false);
				this.droplineFrq.setMandatoryStyle(false);
				this.droplineFrq.setVisible(false);
				this.firstDroplineDate.setVisible(false);
				this.space_DroplineDate.setSclass("");
			}
			this.pftServicingODLimit.setChecked(aFinanceMain.isPftServicingODLimit());
			if (aFinanceMain.getNumberOfTerms() > 0) {
				int odYearlTerms = aFinanceMain.getNumberOfTerms() / 12;
				this.odYearlyTerms.setValue(odYearlTerms);
				this.odMnthlyTerms.setValue(aFinanceMain.getNumberOfTerms() % 12);
			}
		}

		fillComboBox(this.repayRateBasis, aFinanceMain.getRepayRateBasis(),
				PennantStaticListUtil.getInterestRateType(!aFinanceMain.isMigratedFinance()), "");
		this.finRepaymentAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getReqRepayAmount(), format));

		if (aFinanceMain.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PFT)) {
			this.finRepaymentAmount.setReadonly(true);
		}
		this.numberOfTerms_two.setValue(aFinanceMain.getNumberOfTerms());
		this.numberOfTerms.setText("");
		if (this.numberOfTerms_two.intValue() == 1) {
			this.repayFrq.setMandatoryStyle(false);
		} else {
			this.repayFrq.setMandatoryStyle(true);
		}

		this.finRepayPftOnFrq.setChecked(aFinanceMain.isFinRepayPftOnFrq());
		if (!financeType.isFinRepayPftOnFrq()) {
			this.finRepayPftOnFrq.setDisabled(true);
		}

		if (this.manualSchedule.isChecked()) {
			this.maturityDate.setValue(aFinanceMain.getMaturityDate());
		}
		this.maturityDate_two.setValue(aFinanceMain.getMaturityDate());
		this.advEMITerms.setValue(aFinanceMain.getAdvEMITerms());

		this.repayRate.setMarginValue(aFinanceMain.getRepayMargin());

		if (isOverdraft) {
			fillComboBox(this.cbScheduleMethod, aFinanceMain.getScheduleMethod(),
					PennantStaticListUtil.getScheduleMethods(),
					",EQUAL,GRCNDPAY,MAN_PRI,MANUAL,PRI,PRI_PFT,NO_PAY,PFTCAP,");
		} else {
			fillComboBox(this.cbScheduleMethod, aFinanceMain.getScheduleMethod(),
					PennantStaticListUtil.getScheduleMethods(), ",NO_PAY,GRCNDPAY,PFTCAP,");
		}

		if (StringUtils.isNotEmpty(aFinanceMain.getRepayBaseRate()) && StringUtils.equals(
				CalculationConstants.RATE_BASIS_R, this.repayRateBasis.getSelectedItem().getValue().toString())) {
			this.repayBaseRateRow.setVisible(true);
			this.repayRate.setBaseValue(aFinanceMain.getRepayBaseRate());
			this.repayRate.setSpecialValue(aFinanceMain.getRepaySpecialRate());

			RateDetail rateDetail = RateUtil.rates(this.repayRate.getBaseValue(), aFinanceMain.getFinCcy(),
					this.repayRate.getSpecialValue(), this.repayRate.getMarginValue(), this.finMinRate.getValue(),
					this.finMaxRate.getValue());

			if (rateDetail.getErrorDetails() == null) {
				this.repayRate.setEffRateText(
						PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
			}
			readOnlyComponent(true, this.repayProfitRate);

			if (financeType.getFInMinRate().compareTo(BigDecimal.ZERO) == 0
					&& financeType.getFinMaxRate().compareTo(BigDecimal.ZERO) == 0) {
				this.row_FinRepRates.setVisible(false);
			} else {
				this.row_FinRepRates.setVisible(true);
				if (aFinanceMain.isNewRecord()) {
					this.finMinRate.setValue(financeType.getFInMinRate());
					this.finMaxRate.setValue(financeType.getFinMaxRate());
				} else {
					this.finMinRate.setValue(aFinanceMain.getRpyMinRate());
					this.finMaxRate.setValue(aFinanceMain.getRpyMaxRate());
				}
			}

		} else {
			this.row_FinRepRates.setVisible(false);
			this.repayBaseRateRow.setVisible(true);
			this.repayRate.setMarginReadonly(true);
			this.repayRate.setBaseValue("");
			this.repayRate.setBaseDescription("");
			this.repayRate.setBaseReadonly(true);
			this.repayRate.setSpecialValue("");
			this.repayRate.setSpecialDescription("");
			this.repayRate.setSpecialReadonly(true);
			readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);

			if (aFinanceMain.getRepayProfitRate().compareTo(BigDecimal.ZERO) > 0) {
				this.repayRate.setEffRateValue(BigDecimal.ZERO);
				this.repayRate.setEffRateText("0.00");
			}

			this.finMinRate.setValue(BigDecimal.ZERO);
			this.finMaxRate.setValue(BigDecimal.ZERO);
		}

		// Repay Profit Rate
		this.repayProfitRate.setValue(aFinanceMain.getRepayProfitRate());

		// Effective Rate Setting
		if (StringUtils.isNotBlank(this.repayRate.getBaseValue())) {
			calculateRate(this.repayRate.getBaseValue(), this.finCcy.getValue(), this.repayRate.getSpecialComp(),
					this.repayRate.getBaseComp(), this.repayRate.getMarginValue(), this.repayRate.getEffRateComp(),
					this.finMinRate, this.finMaxRate);
		}

		if (this.odMaturityDate.isVisible()) {
			this.odMaturityDate.setValue(aFinanceMain.getMaturityDate());
		}
		//Advised profit Rates
		doCheckAdviseRates(aFinanceMain.getGrcAdvBaseRate(), aFinanceMain.getRpyAdvBaseRate(), false,
				financeType.getFinCategory());
		this.rpyAdvRate.setBaseValue(aFinanceMain.getRpyAdvBaseRate());
		this.rpyAdvRate.setMarginValue(aFinanceMain.getRpyAdvMargin());
		this.rpyAdvPftRate.setValue(aFinanceMain.getRpyAdvPftRate());
		calAdvPftRate(this.rpyAdvRate.getBaseValue(), this.finCcy.getValue(), this.rpyAdvRate.getMarginValue(),
				BigDecimal.ZERO, BigDecimal.ZERO, this.rpyAdvRate.getEffRateComp());

		this.alwBpiTreatment.setChecked(aFinanceMain.isAlwBPI());
		fillComboBox(this.dftBpiTreatment, aFinanceMain.getBpiTreatment(), PennantStaticListUtil.getDftBpiTreatment(),
				"");
		oncheckalwBpiTreatment(false);

		if (ImplementationConstants.ALLOW_PLANNED_EMIHOLIDAY) {
			this.alwPlannedEmiHoliday.setChecked(aFinanceMain.isPlanEMIHAlw());
			String planEmiHMType = aFinanceMain.getPlanEMIHMethod();
			if (aFinanceMain.isNewRecord()) {
				planEmiHMType = FinanceConstants.PLANEMIHMETHOD_FRQ;
			}
			this.maxPlanEmiPerAnnum.setValue(aFinanceMain.getPlanEMIHMaxPerYear());
			this.maxPlanEmi.setValue(aFinanceMain.getPlanEMIHMax());
			this.planEmiHLockPeriod.setValue(aFinanceMain.getPlanEMIHLockPeriod());
			this.cpzAtPlanEmi.setChecked(aFinanceMain.isPlanEMICpz());
			onCheckPlannedEmiholiday(planEmiHMType, false);
		} else {
			this.row_PlannedEMIH.setVisible(false);
			this.hbox_planEmiMethod.setVisible(false);
			this.row_MaxPlanEmi.setVisible(false);
			this.row_PlanEmiHLockPeriod.setVisible(false);
			this.row_MaxPlanEmi.setVisible(false);
			this.row_PlanEmiHLockPeriod.setVisible(false);
			this.planEmiHLockPeriod.setValue(0);
			this.maxPlanEmiPerAnnum.setValue(0);
			this.maxPlanEmi.setValue(0);
			this.cpzAtPlanEmi.setChecked(false);
			this.maxPlanEmiPerAnnum.setVisible(false);
			this.maxPlanEmi.setVisible(false);
			this.planEmiHLockPeriod.setVisible(false);
			this.cpzAtPlanEmi.setDisabled(true);
		}
		if (ImplementationConstants.ALLOW_UNPLANNED_EMIHOLIDAY) {
			if (financeType.isAlwUnPlanEmiHoliday() || aFinanceMain.getMaxUnplannedEmi() > 0) {
				this.row_UnPlanEmiHLockPeriod.setVisible(true);
				this.row_MaxUnPlannedEMIH.setVisible(true);
				this.unPlannedEmiHLockPeriod.setValue(aFinanceMain.getUnPlanEMIHLockPeriod());
				this.maxUnplannedEmi.setValue(aFinanceMain.getMaxUnplannedEmi());
				this.cpzAtUnPlannedEmi.setChecked(aFinanceMain.isUnPlanEMICpz());
			} else {
				this.row_UnPlanEmiHLockPeriod.setVisible(false);
				this.row_MaxUnPlannedEMIH.setVisible(false);
			}
		} else {
			this.row_UnPlanEmiHLockPeriod.setVisible(false);
			this.row_MaxUnPlannedEMIH.setVisible(false);
		}
		if (ImplementationConstants.ALLOW_REAGE) {
			if (financeType.isAlwReage() || aFinanceMain.getMaxReAgeHolidays() > 0) {
				this.row_ReAge.setVisible(true);
				this.maxReAgeHolidays.setValue(aFinanceMain.getMaxReAgeHolidays());
				this.cpzAtReAge.setChecked(aFinanceMain.isReAgeCpz());
			} else {
				this.row_ReAge.setVisible(false);
			}
		} else {
			this.row_ReAge.setVisible(false);
		}
		fillComboBox(this.roundingMode, aFinanceMain.getCalRoundingMode(), PennantStaticListUtil.getRoundingModes(),
				"");

		// External Charges For Ijarah
		doCheckSuplIncrCost(financeType.getFinCategory());
		this.supplementRent.setValue(PennantAppUtil.formateAmount(aFinanceMain.getSupplementRent(), format));
		this.increasedCost.setValue(PennantAppUtil.formateAmount(aFinanceMain.getIncreasedCost(), format));

		this.repayFrq.setDisabled(isReadOnly("FinanceMainDialog_repayFrq"));

		if (StringUtils.isNotEmpty(aFinanceMain.getRepayFrq())
				|| !aFinanceMain.getRepayFrq().equals(PennantConstants.List_Select)) {
			this.rpyFrqRow.setVisible(true);
			this.repayFrq.setValue(aFinanceMain.getRepayFrq());
		}

		this.repayPftFrq.setDisabled(isReadOnly("FinanceMainDialog_repayPftFrq"));
		if (aFinanceMain.getRepayPftFrq() != null && (StringUtils.isNotEmpty(aFinanceMain.getRepayPftFrq())
				|| !aFinanceMain.getRepayPftFrq().equals(PennantConstants.List_Select))) {
			this.rpyPftFrqRow.setVisible(true);
			this.repayPftFrq.setValue(aFinanceMain.getRepayPftFrq());
		}

		if (aFinanceMain.isAllowRepayRvw()) {
			this.repayRvwFrq.setDisabled(isReadOnly("FinanceMainDialog_repayRvwFrq"));
			if (aFinanceMain.getRepayRvwFrq() != null && (StringUtils.isNotEmpty(aFinanceMain.getRepayRvwFrq())
					|| !aFinanceMain.getRepayRvwFrq().equals(PennantConstants.List_Select))) {
				this.rpyRvwFrqRow.setVisible(true);
				this.repayRvwFrq.setVisible(true);
				this.label_FinanceMainDialog_RepayRvwFrq.setVisible(true);
				this.repayRvwFrq.setValue(aFinanceMain.getRepayRvwFrq());
			}
			readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayRvwDate"), this.nextRepayRvwDate);
		} else {
			this.repayRvwFrq.setDisabled(true);
			readOnlyComponent(true, this.nextRepayRvwDate);
		}

		if (aFinanceMain.isAllowRepayCpz()) {
			if (isReadOnly("FinanceMainDialog_repayCpzFrq")) {
				this.repayCpzFrq.setDisabled(true);
			} else {
				this.repayCpzFrq.setDisabled(false);
				readOnlyComponent(true, this.nextRepayCpzDate);
			}
			if (StringUtils.isNotEmpty(aFinanceMain.getRepayCpzFrq())
					|| !aFinanceMain.getRepayCpzFrq().equals(PennantConstants.List_Select)) {
				this.rpyCpzFrqRow.setVisible(true);
				this.repayCpzFrq.setValue(aFinanceMain.getRepayCpzFrq());
			}
			readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayCpzDate"), this.nextRepayCpzDate);
		} else {
			this.repayCpzFrq.setDisabled(true);
			readOnlyComponent(true, this.nextRepayCpzDate);
		}
		if (this.rolloverFrqRow.isVisible() && (StringUtils.isNotEmpty(aFinanceMain.getRolloverFrq())
				|| !aFinanceMain.getRolloverFrq().equals(PennantConstants.List_Select))) {
			this.rolloverFrq.setValue(aFinanceMain.getRolloverFrq());
		}

		if (!aFinanceMain.isNew() || StringUtils.isNotBlank(aFinanceMain.getFinReference())) {
			if (StringUtils.isBlank(moduleDefiner) || moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CHGGRCEND)) {
				this.nextRepayDate.setValue(aFinanceMain.getNextRepayDate());
				this.nextRollOverDate.setValue(aFinanceMain.getNextRolloverDate());
				this.nextRepayRvwDate.setValue(aFinanceMain.getNextRepayRvwDate());
				this.nextRepayCpzDate.setValue(aFinanceMain.getNextRepayCpzDate());
				this.nextRepayPftDate.setValue(aFinanceMain.getNextRepayPftDate());
			}
		}

		this.nextRepayDate_two.setValue(aFinanceMain.getNextRepayDate());
		this.nextRollOverDate_two.setValue(aFinanceMain.getNextRolloverDate());
		this.nextRepayRvwDate_two.setValue(aFinanceMain.getNextRepayRvwDate());
		this.nextRepayCpzDate_two.setValue(aFinanceMain.getNextRepayCpzDate());
		this.nextRepayPftDate_two.setValue(aFinanceMain.getNextRepayPftDate());

		this.finReference.setValue(aFinanceMain.getFinReference());
		if (financeType.isFinIsAlwDifferment() && aFinanceMain.getPlanDeferCount() == 0) {
			this.defferments.setReadonly(isReadOnly("FinanceMainDialog_defferments"));
		} else {
			this.defferments.setReadonly(true);
		}

		this.defferments.setValue(aFinanceMain.getDefferments());
		if (financeType.isAlwPlanDeferment() && StringUtils.isEmpty(moduleDefiner)) {
			this.planDeferCount.setReadonly(isReadOnly("FinanceMainDialog_planDeferCount"));
		} else {
			this.planDeferCount.setReadonly(true);
			this.hbox_PlanDeferCount.setVisible(false);
			this.label_FinanceMainDialog_PlanDeferCount.setVisible(false);
		}

		if (!financeType.isFinIsAlwDifferment() && !financeType.isAlwPlanDeferment()) {
			this.defermentsRow.setVisible(false);
		}

		this.planDeferCount.setValue(aFinanceMain.getPlanDeferCount());

		if (aFinanceMain.isManualSchedule()) {
			onCheckmanualSchedule();
			this.numberOfTerms.setValue(aFinanceMain.getNumberOfTerms());
			this.numberOfTerms_two.setValue(aFinanceMain.getNumberOfTerms());
		}
		
		// Rollover Finance Details
		if (aFinanceDetail.getRolledoverFinanceHeader() != null) {

			RolledoverFinanceHeader rolledoverFinanceHeader = aFinanceDetail.getRolledoverFinanceHeader();

			this.custPaymentAmount
					.setValue(PennantAppUtil.formateAmount(rolledoverFinanceHeader.getCustPayment(), format));

			if (rolledoverFinanceHeader.getCustPayment().compareTo(BigDecimal.ZERO) > 0) {
				this.custPayAccId.setMandatoryStyle(true);
			}
			this.custPayAccId.setValue(rolledoverFinanceHeader.getPaymentAccount());

			this.latePayAmount
					.setValue(PennantAppUtil.formateAmount(rolledoverFinanceHeader.getLatePayAmount(), format));
			this.latePayWaiverAmount
					.setValue(PennantAppUtil.formateAmount(rolledoverFinanceHeader.getLatePayWaiverAmount(), format));
			BigDecimal totalPriBal = BigDecimal.ZERO;
			BigDecimal totalPftBal = BigDecimal.ZERO;
			for (int i = 0; i < rolledoverFinanceHeader.getRolledoverFinanceDetails().size(); i++) {

				RolledoverFinanceDetail detail = rolledoverFinanceHeader.getRolledoverFinanceDetails().get(i);

				Listitem listitem = new Listitem();
				Listcell lc = new Listcell(detail.getFinReference());
				listitem.appendChild(lc);
				lc = new Listcell(DateUtility.formatToLongDate(detail.getStartDate()));
				listitem.appendChild(lc);
				lc = new Listcell(PennantAppUtil.amountFormate(detail.getFinAmount(), format));
				listitem.appendChild(lc);
				lc = new Listcell(PennantApplicationUtil.formatRate(detail.getProfitRate().doubleValue(), 9) + " %");
				listitem.appendChild(lc);
				lc = new Listcell(PennantAppUtil.amountFormate(detail.getTotalProfit(), format));
				listitem.appendChild(lc);
				lc = new Listcell(PennantAppUtil.amountFormate(detail.getTotalPriBal(), format));
				listitem.appendChild(lc);
				lc = new Listcell(PennantAppUtil.amountFormate(detail.getTotalPftBal(), format));
				listitem.appendChild(lc);
				lc = new Listcell(
						PennantAppUtil.amountFormate(detail.getFinAmount().add(detail.getTotalProfit()), format));
				listitem.appendChild(lc);

				totalPriBal = totalPriBal.add(detail.getTotalPriBal());
				totalPftBal = totalPftBal.add(detail.getTotalPftBal());

				Decimalbox custPayAmount = new Decimalbox();
				custPayAmount.setWidth("100%");
				custPayAmount.setMaxlength(LengthConstants.LEN_AMOUNT);
				custPayAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
				custPayAmount.setSclass("feeWaiver");
				custPayAmount.setDisabled(isReadOnly("FinanceMainDialog_custPayAmount"));
				custPayAmount.setId("custPayAmount_" + detail.getFinReference());
				custPayAmount.setValue(PennantAppUtil.formateAmount(detail.getCustPayment(), format));
				if (custPayAmount != null) {
					custPayAmount.addForward("onChange", "", "onChangecustPayAmount", detail);
				}
				lc = new Listcell();
				custPayAmount.setInplace(true);
				lc.appendChild(custPayAmount);
				lc.setSclass("inlineMargin");
				listitem.appendChild(lc);

				lc = new Listcell(PennantAppUtil.amountFormate(detail.getRolloverAmount(), format));
				listitem.appendChild(lc);

				listBoxRolledoverFinance.appendChild(listitem);
			}
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(Labels.getLabel("label_Total"));
			lc.setParent(item);
			lc.setStyle("font-weight:bold");
			lc.setSpan(5);
			lc = new Listcell(PennantAppUtil.amountFormate(totalPriBal, format));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);
			lc = new Listcell(PennantAppUtil.amountFormate(totalPftBal, format));
			lc.setStyle("text-align:right;font-weight:bold;");
			lc.setParent(item);
			lc = new Listcell();
			lc.setSpan(6);
			lc.setParent(item);
			item.setStyle("background-color: #C0EBDF;");
			this.listBoxRolledoverFinance.appendChild(item);
		}

		//FinanceMain Details Tab ---> 4. Overdue Penalty Details
		if (financeType.isApplyODPenalty()) {

			FinODPenaltyRate penaltyRate = aFinanceDetail.getFinScheduleData().getFinODPenaltyRate();

			if (penaltyRate != null) {
				if (isFinPreApproved) {
					this.gb_OverDuePenalty.setVisible(false);
				} else {
					this.gb_OverDuePenalty.setVisible(true);
				}
				this.applyODPenalty.setChecked(penaltyRate.isApplyODPenalty());
				this.oDIncGrcDays.setChecked(penaltyRate.isODIncGrcDays());
				fillComboBox(this.oDChargeCalOn, penaltyRate.getODChargeCalOn(),
						PennantStaticListUtil.getODCCalculatedOn(), "");
				this.oDGraceDays.setValue(penaltyRate.getODGraceDays());
				fillComboBox(this.oDChargeType, penaltyRate.getODChargeType(), PennantStaticListUtil.getODCChargeType(),
						"");
				if (FinanceConstants.PENALTYTYPE_FLAT.equals(getComboboxValue(this.oDChargeType))
						|| FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					onChangeODChargeType(true);
					this.oDChargeAmtOrPerc
							.setValue(PennantAppUtil.formateAmount(penaltyRate.getODChargeAmtOrPerc(), format));
				} else if (FinanceConstants.PENALTYTYPE_PERC_ONETIME.equals(getComboboxValue(this.oDChargeType))
						|| FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(getComboboxValue(this.oDChargeType))
						|| FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					this.oDChargeAmtOrPerc
							.setValue(PennantAppUtil.formateAmount(penaltyRate.getODChargeAmtOrPerc(), 2));
				}
				this.oDAllowWaiver.setChecked(penaltyRate.isODAllowWaiver());
				this.oDMaxWaiverPerc.setValue(penaltyRate.getODMaxWaiverPerc());
			} else {
				this.applyODPenalty.setChecked(false);
				this.gb_OverDuePenalty.setVisible(false);
				fillComboBox(this.oDChargeCalOn, "", PennantStaticListUtil.getODCCalculatedOn(), "");
				fillComboBox(this.oDChargeType, "", PennantStaticListUtil.getODCChargeType(), "");
			}
		} else {
			this.applyODPenalty.setChecked(false);
			this.gb_OverDuePenalty.setVisible(false);
			fillComboBox(this.oDChargeCalOn, "", PennantStaticListUtil.getODCCalculatedOn(), "");
			fillComboBox(this.oDChargeType, "", PennantStaticListUtil.getODCChargeType(), "");
		}

		// ###_0.3
		this.eligibilityMethod.setValue(aFinanceMain.getLovEligibilityMethod());
		this.eligibilityMethod.setDescription(aFinanceMain.getLovDescEligibilityMethod());
		this.eligibilityMethod.setAttribute("FieldCodeId", aFinanceMain.getEligibilityMethod());

		//FinanceMain Details Tab ---> 5. DDA Registration Details
		if (this.gb_ddaRequest.isVisible()) {
			this.bankName.setValue(aFinanceMain.getBankName());
			this.bankName.setDescription(aFinanceMain.getBankNameDesc());
			this.iban.setValue(aFinanceMain.getIban());
			FinanceMainExt finExt = getFinanceMainExtService().getFinanceMainExtByRef(aFinanceMain.getFinReference());
			if (finExt != null) {
				this.ifscCode.setValue(finExt.getIfscCode());
			}
			fillComboBox(this.accountType, aFinanceMain.getAccountType(), PennantStaticListUtil.getAccountTypes(), "");
		}

		this.availCommitAmount = aFinanceMain.getAvailCommitAmount();
		this.recordStatus.setValue(aFinanceMain.getRecordStatus());

		if (aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() > 0) {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
		}

		if (isOverdraft && aFinanceDetail.getFinScheduleData().getOverdraftScheduleDetails().size() > 0) {
			getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
		}

		setReadOnlyForCombobox();
		setRepayAccMandatory();
		setDownpayPgmDeails(aFinanceMain.isNewRecord());
		setDownPayAcMand();
		checkQDPProcess(getFinanceDetail());

		// Account
		if (isOverdraft) {
			this.repayAcctId.setMandatoryStyle(false);
			this.disbAcctId.setMandatoryStyle(false);
		}

		//Filling Child Window Details Tabs
		aFinanceDetail.setModuleDefiner(
				StringUtils.isEmpty(moduleDefiner) ? FinanceConstants.FINSER_EVENT_ORG : moduleDefiner);
		doFillTabs(aFinanceDetail, true, true);

		// Setting Utilized Amoun for Collateral Assignment purpose calculations
		this.oldVar_utilizedAmount = this.finAmount.getActualValue();

		//Accounts should be displayed only to the Banks
		if (!ImplementationConstants.ACCOUNTS_APPLICABLE) {
			this.custPayAccId.setVisible(false);
			this.disbAcctId.setVisible(false);
			this.repayAcctId.setVisible(false);
			this.downPayAccount.setVisible(false);
			this.btnAddSecondaryAccounts.setVisible(false);
			if (label_FinanceMainDialog_CustPayAccId != null) { //This field is available only in Islamic loan Types
				this.label_FinanceMainDialog_CustPayAccId.setVisible(false);
			}
			this.label_FinanceMainDialog_DisbAcctId.setVisible(false);
			this.label_FinanceMainDialog_RepayAcctId.setVisible(false);
			this.label_FinanceMainDialog_DownPayAccount.setVisible(false);
			this.label_FinanceMainDialog_SecondaryAccount.setVisible(false);
			if (row_disbAcctId != null) { //Components order differernt in few products 
				this.row_disbAcctId.setVisible(false);
			}
			if (!hbox_tdsApplicable.isVisible()) {
				this.row_secondaryAccount.setVisible(false);
			}
		}

		//Sampling Required flag
		this.samplingRequired.setChecked(aFinanceMain.isSamplingRequired());
		
		//Legal Required flag
		this.legalRequired.setChecked(aFinanceMain.isLegalRequired());

		doFillEnquiryList(getFinBasicDetails());

		logger.debug("Leaving");
	}

	/**
	 * Method for Query Management Details Tab in finance
	 */
	protected void appendQueryMangementTab(boolean onLoadProcess) {
		logger.debug("Entering");
		final HashMap<String, Object> map = getDefaultArguments();
		map.put("financeMain", financeDetail.getFinScheduleData().getFinanceMain());
		boolean createTab = false;
		if (getTab(AssetConstants.UNIQUE_ID_QUERY_MGMT) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_QUERY_MGMT, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_QUERY_MGMT);
		}
		if (onLoadProcess) {
			map.put("moduleName", PennantConstants.QUERY_ORIGINATION);
			Executions.createComponents("/WEB-INF/pages/LoanQuery/QueryDetail/FinQueryDetailList.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_QUERY_MGMT), map);
		}
		logger.debug("Leaving");
	}

	
	/**
	 * Method for Credit Review Details Data in finance
	 */
	protected void appendCreditReviewDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		long custId = getFinanceDetail().getFinScheduleData().getFinanceMain().getCustID();
		boolean createTab = false;
		if (getTab(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW);
		}
		if (onLoadProcess) {
			map.put("facility", "");
			map.put("custCIF", this.custCIF.getValue());
			map.put("custID", custId);
			map.put("userRole", getRole());
			map.put("custCtgType", getFinanceDetail().getCustomerDetails().getCustomer().getCustCtgCode());
			map.put("numberOfTerms", getFinanceDetail().getFinScheduleData().getFinanceMain().getNumberOfTerms());
			if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0) {
				map.put("repayProfitRate",
						getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().get(0).getCalculatedRate());
			} else {
				map.put("repayProfitRate", BigDecimal.ZERO);
			}
			map.put("roundingTarget", getFinanceDetail().getFinScheduleData().getFinanceMain().getRoundingTarget());
			map.put("finAssetValue", getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAssetValue());
			map.put("finAmount", getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAmount());
			map.put("firstRepay", getFinanceDetail().getFinScheduleData().getFinanceMain().getFirstRepay());
			map.put("finReference", getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());

			Executions.createComponents(
					"/WEB-INF/pages/FinanceManagement/BankOrCorpCreditReview/CreditApplicationReviewEnquiry.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW), map);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method is for append extended field details
	 */
	private void appendExtendedFieldDetails(FinanceDetail aFinanceDetail, String finEvent) {
		logger.debug("Entering");

		try {
			FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
			if (aFinanceMain == null) {
				return;
			}
			if (finEvent.isEmpty()) {
				finEvent = FinanceConstants.FINSER_EVENT_ORG;
			}

			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = this.extendedFieldCtrl.getExtendedFieldHeader(
					ExtendedFieldConstants.MODULE_LOAN, aFinanceMain.getFinCategory(), finEvent);
			if (extendedFieldHeader == null) {
				return;
			}
			ExtendedFieldRender extendedFieldRender = extendedFieldCtrl
					.getExtendedFieldRender(aFinanceMain.getFinReference());
			extendedFieldCtrl.createTab(tabsIndexCenter, tabpanelsBoxIndexCenter);
			setPdfExtTabPanelId("TabPanel" + ExtendedFieldConstants.MODULE_LOAN + aFinanceMain.getFinCategory());
			aFinanceDetail.setExtendedFieldHeader(extendedFieldHeader);
			aFinanceDetail.setExtendedFieldRender(extendedFieldRender);

			if (aFinanceDetail.getBefImage() != null) {
				aFinanceDetail.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				aFinanceDetail.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}

			extendedFieldCtrl.setCcyFormat(CurrencyUtil.getFormat(aFinanceMain.getFinCcy()));
			extendedFieldCtrl.setReadOnly(/* isReadOnly("CustomerDialog_custFirstName") */false);
			extendedFieldCtrl.setWindow(getMainWindow());
			extendedFieldCtrl.setTabHeight(this.borderLayoutHeight - 100);
			//for getting rights in ExtendeFieldGenerator these two fields required.
			extendedFieldCtrl.setUserWorkspace(getUserWorkspace());
			extendedFieldCtrl.setUserRole(getRole());
			extendedFieldCtrl.render();
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		logger.debug("Leaving");
	}
	
	
	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	protected void appendPslDetailsTab(boolean onLoad) throws InterruptedException {
		logger.debug("Entering");
		String value = "N";
		try {
			value = SysParamUtil.getValueAsString("PSL_DATA_REQUIRED");
		} catch (Exception e) {
		}

		if (!StringUtils.equalsIgnoreCase(PennantConstants.YES, value)) {
			getFinanceDetail().setPslDetail(null);
			return;
		}

		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_PSL_DETAILS, true);
			} else {
				
				PSLDetail pslDetail = getFinanceDetail().getPslDetail();
				if (pslDetail == null) {
					pslDetail = new PSLDetail();
					pslDetail.setNewRecord(true);
				}
				
				HashMap<String, Object> map = getDefaultArguments();
				map.put("finHeaderList", getFinBasicDetails());
				map.put("pSLDetail", pslDetail);
				map.put("tab", getTab(AssetConstants.UNIQUE_ID_PSL_DETAILS));
				map.put("fromLoan", true);
				map.put("financeDetail", getFinanceDetail());
				map.put("financeMainDialogCtrl", this);
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/PslDetailDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_PSL_DETAILS), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void onChangecustPayAmount(Event event) {
		logger.debug("Entering" + event.toString());
		BigDecimal totalCustPayAmt = BigDecimal.ZERO;
		List<RolledoverFinanceDetail> list = getFinanceDetail().getRolledoverFinanceHeader()
				.getRolledoverFinanceDetails();

		int format = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		for (RolledoverFinanceDetail detail : list) {
			Decimalbox custPayAmount = (Decimalbox) this.listBoxRolledoverFinance
					.getFellowIfAny("custPayAmount_" + detail.getFinReference());
			detail.setCustPayment(PennantAppUtil.unFormateAmount(custPayAmount.getValue(), format));

			if (detail.getCustPayment().compareTo(detail.getTotalPriBal().add(detail.getTotalPftBal())) > 0) {
				throw new WrongValueException(custPayAmount, Labels.getLabel("NUMBER_MAXVALUE_EQ", new String[] {
						Labels.getLabel("label_RolloverFinanceMainDialog_CustPaymentAmount"),
						PennantAppUtil.amountFormate(detail.getTotalPriBal().add(detail.getTotalPftBal()), format) }));
			}
			totalCustPayAmt = totalCustPayAmt.add(detail.getCustPayment());
		}
		this.custPaymentAmount.setValue(
				PennantAppUtil.formateAmount(totalCustPayAmt, CurrencyUtil.getFormat(getFinanceMain().getFinCcy())));

		this.custPayAccId.setMandatoryStyle(false);
		if (totalCustPayAmt.compareTo(BigDecimal.ZERO) > 0) {
			this.custPayAccId.setMandatoryStyle(true);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Setting Advised Rates For Structured Murabaha Product
	 * 
	 * @param financeMain
	 * @param isGrace
	 * @param finCategory
	 */
	protected void doCheckAdviseRates(String grcAdvBaseRate, String rpyAdvBaseRate, boolean isGrace,
			String finCategory) {
		if (!StringUtils.equals(finCategory, FinanceConstants.PRODUCT_STRUCTMUR)) {
			return;
		}

		if (isGrace) {
			if (StringUtils.isNotBlank(grcAdvBaseRate)) {
				this.row_GrcAdvBaseRate.setVisible(true);
				this.row_GrcAdvPftRate.setVisible(false);
			} else {
				this.row_GrcAdvPftRate.setVisible(true);
				this.row_GrcAdvBaseRate.setVisible(false);
			}
		} else {
			if (StringUtils.isNotBlank(rpyAdvBaseRate)) {
				this.row_RpyAdvBaseRate.setVisible(true);
				this.row_RpyAdvPftRate.setVisible(false);
			} else {
				this.row_RpyAdvBaseRate.setVisible(false);
				this.row_RpyAdvPftRate.setVisible(true);
			}
		}
	}

	/**
	 * Method for Displaying Supplementary Rent & increased Cost for Ijarah product
	 * 
	 * @param financeMain
	 * @param isGrace
	 * @param finCategory
	 */
	private void doCheckSuplIncrCost(String finCategory) {
		if (!(StringUtils.equals(finCategory, FinanceConstants.PRODUCT_IJARAH)
				|| StringUtils.equals(finCategory, FinanceConstants.PRODUCT_FWIJARAH))
				&& !StringUtils.equals(finCategory, FinanceConstants.PRODUCT_ISTISNA)) {
			this.supplementRent.setDisabled(true);
			this.increasedCost.setDisabled(true);
			return;
		}

		this.row_supplementRent.setVisible(true);
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	protected void doShowDialog(FinanceDetail afinanceDetail) throws InterruptedException {
		logger.debug("Entering");

		// set Read only mode accordingly if the object is new or not.
		if (afinanceDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitNew();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		// ###_0.3
		if (StringUtils.equals(PennantConstants.YES, elgMethodVisible)) {
			this.row_EligibilityMethod.setVisible(true);
		}

		// setFocus
		this.finAmount.focus();

		boolean isOverDraft = false;
		//Reset Maintenance Buttons for finance modification
		if (StringUtils.isNotEmpty(moduleDefiner)) {
			if (!StringUtils.equals(FinanceConstants.FINSER_EVENT_ROLLOVER, moduleDefiner)
					&& !StringUtils.equals(FinanceConstants.FINSER_EVENT_CHGGRCEND, moduleDefiner) && !isFinPreApproved
					&& !StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_OVERDRAFTSCHD)) {
				this.btnValidate.setDisabled(true);
				this.btnBuildSchedule.setDisabled(true);
				this.btnValidate.setVisible(false);
				this.btnBuildSchedule.setVisible(false);
			}
			afinanceDetail.getFinScheduleData().getFinanceMain()
					.setCurDisbursementAmt(afinanceDetail.getFinScheduleData().getFinanceMain().getFinAmount());
		}

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				afinanceDetail.getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverDraft = true;
		}

		if (isOverDraft && !afinanceDetail.getFinScheduleData().getFinanceType().isDroplineOD()
				&& StringUtils.isEmpty(moduleDefiner)) {
			this.btnBuildSchedule.setDisabled(true);
			this.btnBuildSchedule.setVisible(false);
		}

		try {
			// fill the components with the data
			if (StringUtils.isEmpty(moduleDefiner)) {
				deviationExecutionCtrl.setFinanceDeviations(afinanceDetail.getFinanceDeviations());
			}
			doWriteBeanToComponents(afinanceDetail, true);

			setPolicyRate(false, afinanceDetail.isNewRecord());
			if (afinanceDetail.getFinScheduleData().getFinanceMain().isAllowGrcPeriod()) {
				setGrcPolicyRate(false, afinanceDetail.isNewRecord());
			}

			onCheckODPenalty(false);
			if (afinanceDetail.getFinScheduleData().getFinanceMain().isNew()
					&& !afinanceDetail.getFinScheduleData().getFinanceMain().isLovDescIsSchdGenerated()) {
				//####_0.2
				//changeFrequencies();
				this.finAmount.focus();
			}

			FinanceType financeType = afinanceDetail.getFinScheduleData().getFinanceType();
			if (financeType.getFinMinTerm() == 1 && financeType.getFinMaxTerm() == 1) {
				if (!financeType.isFinRepayPftOnFrq()) {
					getLabel_FinanceMainDialog_FinRepayPftOnFrq().setVisible(false);
					this.rpyPftFrqRow.setVisible(false);
					this.hbox_finRepayPftOnFrq.setVisible(false);
				} else {
					this.rpyPftFrqRow.setVisible(true);

					// As of Bank Request below two fields visibility overridden from TRUE to FALSE by default
					getLabel_FinanceMainDialog_FinRepayPftOnFrq().setVisible(false);
					this.hbox_finRepayPftOnFrq.setVisible(false);
				}

				this.rpyFrqRow.setVisible(false);
				this.hbox_ScheduleMethod.setVisible(false);
				getLabel_FinanceMainDialog_ScheduleMethod().setVisible(false);
				this.noOfTermsRow.setVisible(false);
			} else {
				if (!financeType.isFinRepayPftOnFrq()) {
					getLabel_FinanceMainDialog_FinRepayPftOnFrq().setVisible(false);
					this.hbox_finRepayPftOnFrq.setVisible(false);
				}
			}

			/*
			 * if(!financeType.isFinRepayPftOnFrq() && !financeType.isFinIsIntCpz()){
			 * getLabel_FinanceMainDialog_FinRepayPftOnFrq().setVisible(false); this.rpyPftFrqRow.setVisible(false);
			 * this.hbox_finRepayPftOnFrq.setVisible(false); }
			 */

			if (StringUtils.equals(FinanceConstants.PRODUCT_QARDHASSAN, financeType.getFinCategory())) {
				this.rpyPftFrqRow.setVisible(false);
			}

			if (!isReadOnly("FinanceMainDialog_NoScheduleGeneration")) {
				this.gb_gracePeriodDetails.setVisible(false);
				//this.gb_repaymentDetails.setVisible(false);
				this.gb_OverDuePenalty.setVisible(false);
				if (this.numberOfTerms_two.intValue() == 0) {
					this.numberOfTerms_two.setValue(1);
				}
				this.row_stepFinance.setVisible(false);
				this.row_manualSteps.setVisible(false);
			}

			//Saving Gestation Period Next (Pft/Rvw/Cpz) Dates
			if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CHGGRCEND)) {
				this.org_grcPeriodEndDate = this.gracePeriodEndDate_two.getValue();
			}

			// stores the initial data for comparing if they are changed
			// during user action.
			if (StringUtils.isEmpty(moduleDefiner) || isFinPreApproved) {

				//Set Customer Data
				if (afinanceDetail.getFinScheduleData().getFinanceMain().isNewRecord()) {
					this.custCIF.clearErrorMessage();
					setCustomerData(afinanceDetail.getCustomerDetails().getCustomer());
				}
			}

			if (StringUtils.isEmpty(moduleDefiner)) {
				deviationExecutionCtrl.setFinanceMainBaseCtrl(this);
			}

			doSetMMAProperties();
			doStoreDftSchdValues();
			allowQDPBuild(afinanceDetail);

			//Change the button label of secondary account for submitted case 
			if (!getUserWorkspace().isAllowed("FinanceMainDialog_btnAddSecondaryAccounts")) {
				this.btnAddSecondaryAccounts.setLabel(Labels.getLabel("label_View"));
			}

			doStoreServiceIds(afinanceDetail.getFinScheduleData().getFinanceMain());

			// Set Unvisible fields based on Product (OD Facility)
			if (isOverDraft) {
				doVisibleODFacilityFields();
			}

			// Setting tile Name based on Service Action
			if (StringUtils.isNotEmpty(moduleDefiner)) {
				this.windowTitle.setValue(Labels.getLabel(moduleDefiner + "_Window.Title"));
			}

			if (StringUtils.isEmpty(moduleDefiner)
					|| StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_PLANNEDEMI)) {
				this.oldVar_planEMIMonths = getFinanceDetail().getFinScheduleData().getPlanEMIHmonths();
				this.oldVar_planEMIDates = getFinanceDetail().getFinScheduleData().getPlanEMIHDates();
			}
			
			//Prepare credit review details for agreements
			setCreditRevDetails(getFinanceDetail());
			
			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public void onPostWinCreation(Event event) throws ParseException, InterruptedException {
		doFillTabs(getFinanceDetail(), false, false);
	}

	private void doVisibleODFacilityFields() {

		this.row_downPayBank.setVisible(false);
		this.row_accountsOfficer.setVisible(true);
		this.row_downPaySupl.setVisible(false);
		this.row_downPayPercentage.setVisible(false);
		this.rpyPftFrqRow.setVisible(false);
		this.rpyRvwFrqRow.setVisible(false);
		this.rpyCpzFrqRow.setVisible(false);
		this.repayFrq.setMandatoryStyle(true);
	}

	/**
	 * Methods to store Current Record Service Task ID's
	 * 
	 * @param financeMain
	 */
	private void doStoreServiceIds(FinanceMain financeMain) {
		this.curRoleCode = financeMain.getRoleCode();
		this.curNextRoleCode = financeMain.getNextRoleCode();
		this.curTaskId = financeMain.getTaskId();
		this.curNextTaskId = financeMain.getNextTaskId();
		this.curNextUserId = financeMain.getNextUserId();
	}

	/**
	 * Method for Checking Details whether Fees Are re-execute or not
	 */
	protected void doCheckFeeReExecution() {

		isFeeReExecute = false;
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy());

		BigDecimal oldFinAmount = PennantAppUtil.unFormateAmount(this.oldVar_finAmount, formatter);
		BigDecimal newFinAmount = PennantAppUtil.unFormateAmount(this.finAmount.getActualValue(), formatter);
		if (oldFinAmount.compareTo(newFinAmount) != 0) {
			isFeeReExecute = true;
		}

		BigDecimal oldDwnPayBank = PennantAppUtil.unFormateAmount(this.oldVar_downPayBank, formatter);
		BigDecimal newDwnPayBank = PennantAppUtil.unFormateAmount(this.downPayBank.getActualValue(), formatter);
		if (oldDwnPayBank.compareTo(newDwnPayBank) != 0) {
			isFeeReExecute = true;
		}

		BigDecimal oldDwnPaySupl = PennantAppUtil.unFormateAmount(this.oldVar_downPaySupl, formatter);
		BigDecimal newDwnPaySupl = PennantAppUtil.unFormateAmount(this.downPaySupl.getActualValue(), formatter);
		if (oldDwnPaySupl.compareTo(newDwnPaySupl) != 0) {
			isFeeReExecute = true;
		}

		Date maturDate = null;
		if (this.maturityDate.getValue() != null) {
			maturDate = this.maturityDate.getValue();
		} else {
			maturDate = this.maturityDate_two.getValue();
		}

		int months = DateUtility.getMonthsBetween(maturDate, this.finStartDate.getValue(), true);
		if (months != this.oldVar_tenureInMonths) {
			isFeeReExecute = true;
		}

		if (this.finRepayMethod.getSelectedIndex() != this.oldVar_finRepayMethod) {
			isFeeReExecute = true;
		}

		if (customerDialogCtrl != null && customerDialogCtrl.isFeeDataModified()) {
			isFeeReExecute = true;
		}

	}

	/**
	 * Stores the initial values in memory variables. <br>
	 */
	protected void doStoreDftSchdValues() {

		doClearMessage();

		//FinanceMain Details Tab ---> 1. Basic Details

		this.oldVar_finType = this.finType.getValue();
		this.oldVar_finCcy = this.finCcy.getValue();
		this.oldVar_profitDaysBasis = this.cbProfitDaysBasis.getSelectedIndex();
		this.oldVar_finStartDate = this.finStartDate.getValue();
		this.oldVar_finAmount = this.finAmount.getActualValue();
		this.oldVar_finAssetValue = this.finAssetValue.getActualValue();
		this.oldVar_finCurrAssetValue = this.finCurrentAssetValue.getActualValue();
		this.oldVar_downPayBank = this.downPayBank.getActualValue();
		this.oldVar_downPaySupl = this.downPaySupl.getActualValue();
		this.oldVar_planDeferCount = this.planDeferCount.intValue();
		this.oldVar_depreciationFrq = this.depreciationFrq.getValue();
		this.oldVar_tDSApplicable = this.tDSApplicable.isChecked();
		this.oldVar_manualSchedule = this.manualSchedule.isChecked();

		// Step Finance Details
		this.oldVar_stepFinance = this.stepFinance.isChecked();
		this.oldVar_stepPolicy = this.stepPolicy.getValue();
		this.oldVar_alwManualSteps = this.alwManualSteps.isChecked();
		this.oldVar_noOfSteps = this.noOfSteps.intValue();
		this.oldVar_stepType = this.stepType.getSelectedIndex();

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.oldVar_gracePeriodEndDate = this.gracePeriodEndDate_two.getValue();
		this.oldVar_allowGrace = this.allowGrace.isChecked();
		if (this.gb_gracePeriodDetails.isVisible()) {
			this.oldVar_graceTerms = this.graceTerms_Two.intValue();
			this.oldVar_grcSchdMthd = this.cbGrcSchdMthd.getSelectedIndex();
			this.oldVar_grcRateBasis = this.grcRateBasis.getSelectedIndex();
			this.oldVar_allowGrcRepay = this.allowGrcRepay.isChecked();
			this.oldVar_graceBaseRate = this.graceRate.getBaseValue();
			this.oldVar_graceSpecialRate = this.graceRate.getSpecialValue();
			this.oldVar_gracePftRate = this.gracePftRate.getValue() == null ? this.graceRate.getEffRateValue()
					: this.gracePftRate.getValue();
			this.oldVar_gracePftFrq = this.gracePftFrq.getValue();
			this.oldVar_nextGrcPftDate = this.nextGrcPftDate_two.getValue();
			this.oldVar_gracePftRvwFrq = this.gracePftRvwFrq.getValue();
			this.oldVar_nextGrcPftRvwDate = this.nextGrcPftRvwDate_two.getValue();
			this.oldVar_graceCpzFrq = this.graceCpzFrq.getValue();
			this.oldVar_nextGrcCpzDate = this.nextGrcCpzDate_two.getValue();
			this.oldVar_grcMargin = this.graceRate.getMarginValue();
			this.oldVar_grcPftDaysBasis = this.grcPftDaysBasis.getSelectedIndex();
			this.oldVar_finGrcMinRate = this.finGrcMinRate.getValue();
			this.oldVar_finGrcMaxRate = this.finGrcMaxRate.getValue();
			this.oldVar_grcAdvBaseRate = this.grcAdvRate.getBaseValue();
			this.oldVar_grcAdvMargin = this.grcAdvRate.getMarginValue();
			this.oldVar_grcAdvPftRate = this.grcAdvPftRate.getValue();
			this.oldVar_grcMaxAmount = this.grcMaxAmount.getActualValue();
		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		this.oldVar_numberOfTerms = this.numberOfTerms_two.intValue();
		this.oldVar_repayBaseRate = this.repayRate.getBaseValue();
		this.oldVar_repayRateBasis = this.repayRateBasis.getSelectedIndex();
		this.oldVar_repaySpecialRate = this.repayRate.getSpecialValue();
		this.oldVar_repayProfitRate = this.repayProfitRate.getValue() == null ? this.repayRate.getEffRateValue()
				: this.repayProfitRate.getValue();
		this.oldVar_repayMargin = this.repayRate.getMarginValue();
		this.oldVar_finMinRate = this.finMinRate.getValue();
		this.oldVar_finMaxRate = this.finMaxRate.getValue();
		this.oldVar_scheduleMethod = this.cbScheduleMethod.getSelectedIndex();
		this.oldVar_repayFrq = this.repayFrq.getValue();
		this.oldVar_nextRepayDate = this.nextRepayDate_two.getValue();
		this.oldVar_rolloverFrq = this.rolloverFrq.getValue();
		this.oldVar_nextRolloverDate = this.nextRollOverDate_two.getValue();
		this.oldVar_repayPftFrq = this.repayPftFrq.getValue();
		this.oldVar_nextRepayPftDate = this.nextRepayPftDate_two.getValue();
		this.oldVar_repayRvwFrq = this.repayRvwFrq.getValue();
		this.oldVar_nextRepayRvwDate = this.nextRepayRvwDate_two.getValue();
		this.oldVar_repayCpzFrq = this.repayCpzFrq.getValue();
		this.oldVar_nextRepayCpzDate = this.nextRepayCpzDate_two.getValue();
		this.oldVar_maturityDate = this.maturityDate_two.getValue();
		this.oldVar_finRepaymentAmount = this.finRepaymentAmount.getActualValue();
		this.oldVar_finRepayPftOnFrq = this.finRepayPftOnFrq.isChecked();
		this.oldVar_finRepayMethod = this.finRepayMethod.getSelectedIndex();
		this.oldVar_rpyAdvBaseRate = this.rpyAdvRate.getBaseValue();
		this.oldVar_rpyAdvMargin = this.rpyAdvRate.getMarginValue();
		this.oldVar_rpyAdvPftRate = this.rpyAdvPftRate.getValue();
		this.oldVar_supplementRent = this.supplementRent.getActualValue();
		this.oldVar_increasedCost = this.increasedCost.getActualValue();

		this.oldVar_alwBpiTreatment = this.alwBpiTreatment.isChecked();
		this.oldVar_dftBpiTreatment = this.dftBpiTreatment.getSelectedIndex();
		this.oldVar_alwPlannedEmiHoliday = this.alwPlannedEmiHoliday.isChecked();
		this.oldVar_planEmiMethod = this.planEmiMethod.getSelectedIndex();
		this.oldVar_maxPlanEmi = this.maxPlanEmi.intValue();
		this.oldVar_maxPlanEmiPerAnnum = this.maxPlanEmiPerAnnum.intValue();
		this.oldVar_planEmiHLockPeriod = this.planEmiHLockPeriod.intValue();
		this.oldVar_cpzAtPlanEmi = this.cpzAtPlanEmi.isChecked();

		this.oldVar_droplineFrq = this.droplineFrq.getValue();
		this.oldVar_firstDroplineDate = this.firstDroplineDate.getValue();
		this.oldVar_odMnthlyTerms = this.odMnthlyTerms.intValue();
		this.oldVar_odYearlyTerms = this.odYearlyTerms.intValue();
		this.oldVar_pftServicingODLimit = this.pftServicingODLimit.isChecked();
		Date maturDate = null;
		if (this.maturityDate.getValue() != null) {
			maturDate = this.maturityDate.getValue();
		} else {
			maturDate = this.maturityDate_two.getValue();
		}

		int months = DateUtility.getMonthsBetween(maturDate, this.finStartDate.getValue(), true);
		this.oldVar_tenureInMonths = months;
		this.oldVar_finStepPolicyList = getFinanceDetail().getFinScheduleData().getStepPolicyDetails();
		this.oldVar_finInsuranceList = getFinanceDetail().getFinScheduleData().getFinInsuranceList();

		if (finFeeDetailListCtrl != null) {
			finFeeDetailListCtrl.doReSetDataChanged();
		}
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	public boolean isSchdlRegenerate() {

		String recStatus = StringUtils
				.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain().getRecordStatus());
		if (this.userAction.getSelectedItem() != null && !recStatus.equals(PennantConstants.RCD_STATUS_REJECTED)
				&& (this.userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_REJECTED)
						|| this.userAction.getSelectedItem().getValue()
								.equals(PennantConstants.RCD_STATUS_CANCELLED))) {
			return false;
		}

		// To clear the Error Messages
		doClearMessage();

		//FinanceMain Details Tab ---> 1. Basic Details

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy());

		if (!StringUtils.equals(this.oldVar_finType, this.finType.getValue())) {
			return true;
		}
		if (!StringUtils.equals(this.oldVar_finCcy, this.finCcy.getValue())) {
			return true;
		}

		if (this.oldVar_profitDaysBasis != this.cbProfitDaysBasis.getSelectedIndex()) {
			return true;
		}
		if (!StringUtils.equals(this.oldVar_depreciationFrq, this.depreciationFrq.getValue())) {
			return true;
		}
		if (DateUtility.compare(this.oldVar_finStartDate, this.finStartDate.getValue()) != 0) {
			return true;
		}
		if (this.oldVar_tDSApplicable != this.tDSApplicable.isChecked()) {
			return true;
		}
		if (this.oldVar_manualSchedule != this.manualSchedule.isChecked()) {
			return true;
		}

		BigDecimal oldFinAmount = PennantAppUtil.unFormateAmount(this.oldVar_finAmount, formatter);
		BigDecimal newFinAmount = PennantAppUtil.unFormateAmount(this.finAmount.getActualValue(), formatter);
		if (oldFinAmount.compareTo(newFinAmount) != 0) {
			return true;
		}

		BigDecimal oldFinAssetAmount = PennantAppUtil.unFormateAmount(this.oldVar_finAssetValue, formatter);
		BigDecimal newFinAssetAmount = PennantAppUtil.unFormateAmount(this.finAssetValue.getActualValue(), formatter);
		if (oldFinAssetAmount.compareTo(newFinAssetAmount) != 0) {
			return true;
		}

		// Step Finance Details
		if (this.oldVar_stepFinance != this.stepFinance.isChecked()) {
			return true;
		}
		if (!this.oldVar_stepPolicy.equals(this.stepPolicy.getValue())) {
			return true;
		}
		if (this.oldVar_alwManualSteps != this.alwManualSteps.isChecked()) {
			return true;
		}
		if (this.oldVar_noOfSteps != this.noOfSteps.intValue()) {
			return true;
		}
		if (this.oldVar_stepType != this.stepType.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_planDeferCount != this.planDeferCount.intValue()) {
			return true;
		}
		if (this.oldVar_finRepayMethod != this.finRepayMethod.getSelectedIndex()) {
			return true;
		}

		BigDecimal oldDwnPayBank = PennantAppUtil.unFormateAmount(this.oldVar_downPayBank, formatter);
		BigDecimal newDwnPayBank = PennantAppUtil.unFormateAmount(this.downPayBank.getActualValue(), formatter);
		if (oldDwnPayBank.compareTo(newDwnPayBank) != 0) {
			return true;
		}

		BigDecimal oldDwnPaySupl = PennantAppUtil.unFormateAmount(this.oldVar_downPaySupl, formatter);
		BigDecimal newDwnPaySupl = PennantAppUtil.unFormateAmount(this.downPaySupl.getActualValue(), formatter);
		if (oldDwnPaySupl.compareTo(newDwnPaySupl) != 0) {
			return true;
		}

		// Step Finance Details List Validation

		if (stepDetailDialogCtrl != null
				&& stepDetailDialogCtrl.getFinStepPoliciesList() != this.oldVar_finStepPolicyList) {

			return true;
		}

		if (this.gracePeriodEndDate.getValue() != null) {
			if (DateUtility.compare(this.oldVar_gracePeriodEndDate, this.gracePeriodEndDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_gracePeriodEndDate, this.gracePeriodEndDate_two.getValue()) != 0) {
			return true;
		}

		//FinanceMain Details Tab ---> 2. Grace Period Details

		if (this.gb_gracePeriodDetails.isVisible()) {
			if (this.oldVar_allowGrace != this.allowGrace.isChecked()) {
				return true;
			}
			if (this.graceTerms.intValue() != 0) {
				if (this.oldVar_graceTerms != this.graceTerms.intValue()) {
					return true;
				}
			} else if (this.oldVar_graceTerms != this.graceTerms_Two.intValue()) {
				return true;
			}
			if (this.oldVar_grcRateBasis != this.grcRateBasis.getSelectedIndex()) {
				return true;
			}
			if (!StringUtils.equals(this.oldVar_graceBaseRate, this.graceRate.getBaseValue())) {
				return true;
			}
			if (this.row_FinGrcRates.isVisible()) {
				if (this.oldVar_finGrcMinRate != this.finGrcMinRate.getValue()) {
					return true;
				}
				if (this.oldVar_finGrcMaxRate != this.finGrcMaxRate.getValue()) {
					return true;
				}
			}
			if (!StringUtils.equals(this.oldVar_graceSpecialRate, this.graceRate.getSpecialValue())) {
				return true;
			}
			if (this.oldVar_gracePftRate != this.gracePftRate.getValue() && !this.grcBaseRateRow.isVisible()) {
				if (this.oldVar_gracePftRate.compareTo(BigDecimal.ZERO) > 0 || (this.gracePftRate.getValue() != null
						&& this.gracePftRate.getValue().compareTo(BigDecimal.ZERO) > 0)) {
					return true;
				}
			}
			if (!StringUtils.equals(this.oldVar_gracePftFrq, this.gracePftFrq.getValue())) {
				return true;
			}
			if (this.oldVar_grcMargin != this.graceRate.getMarginValue()) {
				return true;
			}
			if (this.oldVar_grcPftDaysBasis != this.grcPftDaysBasis.getSelectedIndex()) {
				return true;
			}
			if (this.nextGrcPftDate.getValue() != null) {
				if (DateUtility.compare(this.oldVar_nextGrcPftDate, this.nextGrcPftDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtility.compare(this.oldVar_nextGrcPftDate, this.nextGrcPftDate_two.getValue()) != 0) {
				return true;
			}
			if (!StringUtils.equals(this.oldVar_gracePftRvwFrq, this.gracePftRvwFrq.getValue())) {
				return true;
			}
			if (this.nextGrcPftRvwDate.getValue() != null) {
				if (DateUtility.compare(this.oldVar_nextGrcPftRvwDate, this.nextGrcPftRvwDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtility.compare(this.oldVar_nextGrcPftRvwDate, this.nextGrcPftRvwDate_two.getValue()) != 0) {
				return true;
			}

			if (!StringUtils.equals(this.oldVar_graceCpzFrq, this.graceCpzFrq.getValue())) {
				return true;
			}
			if (this.nextGrcCpzDate.getValue() != null) {
				if (DateUtility.compare(this.oldVar_nextGrcCpzDate, this.nextGrcCpzDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtility.compare(this.oldVar_nextGrcCpzDate, this.nextGrcCpzDate_two.getValue()) != 0) {
				return true;
			}
			if (this.oldVar_allowGrcRepay != this.allowGrcRepay.isChecked()) {
				return true;
			}
			if (this.oldVar_grcSchdMthd != this.cbGrcSchdMthd.getSelectedIndex()) {
				return true;
			}
			if (!StringUtils.equals(this.oldVar_grcAdvBaseRate, this.grcAdvRate.getBaseValue())) {
				return true;
			}
			if (this.oldVar_grcAdvMargin != this.grcAdvRate.getMarginValue()) {
				return true;
			}
			if (this.oldVar_grcAdvPftRate != this.grcAdvPftRate.getValue()) {
				return true;
			}
			if (this.oldVar_grcMaxAmount != this.grcMaxAmount.getActualValue()) {
				return true;
			}
		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details
		if (!this.manualSchedule.isChecked()) {
			if (this.numberOfTerms.intValue() != 0) {
				if (this.oldVar_numberOfTerms != this.numberOfTerms.intValue()) {
					return true;
				}
			} else if (this.oldVar_numberOfTerms != this.numberOfTerms_two.intValue()) {
				return true;
			}
		}

		BigDecimal oldFinRepayAmount = PennantAppUtil.unFormateAmount(this.oldVar_finRepaymentAmount, formatter);
		BigDecimal newFinRepayAmount = PennantAppUtil.unFormateAmount(this.finRepaymentAmount.getActualValue(),
				formatter);

		if (oldFinRepayAmount.compareTo(newFinRepayAmount) != 0) {
			return true;
		}
		if (!StringUtils.equals(this.oldVar_repayFrq, this.repayFrq.getValue())) {
			return true;
		}
		if (rpyFrqRow.isVisible()) {
			if (this.nextRepayDate.getValue() != null) {
				if (DateUtility.compare(this.oldVar_nextRepayDate, this.nextRepayDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtility.compare(this.oldVar_nextRepayDate, this.nextRepayDate_two.getValue()) != 0) {
				return true;
			}
		}

		if (!StringUtils.equals(this.oldVar_rolloverFrq, this.rolloverFrq.getValue())) {
			return true;
		}
		if (this.nextRollOverDate.getValue() != null) {
			if (DateUtility.compare(this.oldVar_nextRolloverDate, this.nextRollOverDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_nextRolloverDate, this.nextRollOverDate_two.getValue()) != 0) {
			return true;
		}

		if (this.maturityDate.getValue() != null) {
			if (DateUtility.compare(this.oldVar_maturityDate, this.maturityDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_maturityDate, this.maturityDate_two.getValue()) != 0) {
			return true;
		}

		if (this.oldVar_finRepayPftOnFrq != this.finRepayPftOnFrq.isChecked()) {
			return true;
		}
		if (this.oldVar_repayRateBasis != this.repayRateBasis.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_alwBpiTreatment != this.alwBpiTreatment.isChecked()) {
			return true;
		}
		if (this.oldVar_dftBpiTreatment != this.dftBpiTreatment.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_alwPlannedEmiHoliday != this.alwPlannedEmiHoliday.isChecked()) {
			return true;
		}
		if (this.oldVar_planEmiMethod != this.planEmiMethod.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_maxPlanEmi != this.maxPlanEmi.intValue()) {
			return true;
		}
		if (this.oldVar_maxPlanEmiPerAnnum != this.maxPlanEmiPerAnnum.intValue()) {
			return true;
		}
		if (this.oldVar_planEmiHLockPeriod != this.planEmiHLockPeriod.intValue()) {
			return true;
		}
		if (this.oldVar_cpzAtPlanEmi != this.cpzAtPlanEmi.isChecked()) {
			return true;
		}
		if (!StringUtils.equals(this.oldVar_repayBaseRate, this.repayRate.getBaseValue())) {
			return true;
		}
		if (this.row_FinRepRates.isVisible()) {
			if (this.oldVar_finMinRate != this.finMinRate.getValue()) {
				return true;
			}
			if (this.oldVar_finMaxRate != this.finMaxRate.getValue()) {
				return true;
			}
		}
		if (!StringUtils.equals(this.oldVar_repaySpecialRate, this.repayRate.getSpecialValue())) {
			return true;
		}
		if (this.oldVar_repayProfitRate != this.repayProfitRate.getValue()
				&& StringUtils.isEmpty(this.repayRate.getBaseValue()) && !this.manualSchedule.isChecked()) {
			if (this.oldVar_repayProfitRate.compareTo(BigDecimal.ZERO) > 0 || (this.repayProfitRate.getValue() != null
					&& this.repayProfitRate.getValue().compareTo(BigDecimal.ZERO) > 0)) {
				return true;
			}
		}
		if (this.oldVar_repayMargin != this.repayRate.getMarginValue()) {
			return true;
		}

		if (!StringUtils.equals(this.oldVar_rpyAdvBaseRate, this.rpyAdvRate.getBaseValue())) {
			return true;
		}
		if (this.oldVar_rpyAdvMargin != this.rpyAdvRate.getMarginValue()) {
			return true;
		}
		if (this.oldVar_rpyAdvPftRate != this.rpyAdvPftRate.getValue()) {
			return true;
		}
		if (this.oldVar_supplementRent != this.supplementRent.getActualValue()) {
			return true;
		}
		if (this.oldVar_increasedCost != this.increasedCost.getActualValue()) {
			return true;
		}

		if (this.oldVar_scheduleMethod != this.cbScheduleMethod.getSelectedIndex()) {
			return true;
		}
		if (this.rpyPftFrqRow.isVisible()
				&& (!StringUtils.equals(this.oldVar_repayPftFrq, this.repayPftFrq.getValue()))) {
			return true;
		}
		if (this.nextRepayPftDate.getValue() != null) {
			if (DateUtility.compare(this.oldVar_nextRepayPftDate, this.nextRepayPftDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_nextRepayPftDate, this.nextRepayPftDate_two.getValue()) != 0) {
			return true;
		}
		if (!StringUtils.equals(this.oldVar_repayRvwFrq, this.repayRvwFrq.getValue())) {
			return true;
		}
		if (this.nextRepayRvwDate.getValue() != null) {
			if (DateUtility.compare(this.oldVar_nextRepayRvwDate, this.nextRepayRvwDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_nextRepayRvwDate, this.nextRepayRvwDate_two.getValue()) != 0) {
			return true;
		}
		if (!StringUtils.equals(this.oldVar_repayCpzFrq, this.repayCpzFrq.getValue())) {
			return true;
		}
		if (this.nextRepayCpzDate.getValue() != null) {
			if (DateUtility.compare(this.oldVar_nextRepayCpzDate, this.nextRepayCpzDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_nextRepayCpzDate, this.nextRepayCpzDate_two.getValue()) != 0) {
			return true;
		}

		if (!StringUtils.equals(this.oldVar_droplineFrq, this.droplineFrq.getValue())) {
			return true;
		}

		if (this.oldVar_odMnthlyTerms != this.odMnthlyTerms.intValue()) {
			return true;
		}

		if (this.oldVar_odYearlyTerms != this.odYearlyTerms.intValue()) {
			return true;
		}

		if ((DateUtility.compare(this.oldVar_firstDroplineDate, this.firstDroplineDate.getValue()) != 0)) {
			return true;
		}

		boolean noValidation = false;
		if (this.userAction.getSelectedItem() != null) {
			if (this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Cancel")
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
				noValidation = true;
			}
		}

		if (!noValidation && !getFinanceDetail().getFinScheduleData().getFinanceMain().isLovDescIsSchdGenerated()) {
			return true;
		}
		if (!getFinanceDetail().getFinScheduleData().getFinanceMain().isLovDescIsSchdGenerated()) {
			return true;
		}

		/*
		 * if (feeDetailDialogCtrl != null && feeDetailDialogCtrl.isDataChanged(false)) { return true; }
		 * 
		 * if (feeDetailDialogCtrl != null && feeDetailDialogCtrl.getFinInsuranceList() != oldVar_finInsuranceList) {
		 * return true; }
		 */

		if (StringUtils.isEmpty(moduleDefiner) && finFeeDetailListCtrl != null
				&& finFeeDetailListCtrl.isDataChanged()) {
			return true;
		}

		if (StringUtils.isEmpty(moduleDefiner) && finFeeDetailListCtrl != null
				&& (finFeeDetailListCtrl.getFinInsuranceList().size() > 0 || oldVar_finInsuranceList.size() > 0)
				&& finFeeDetailListCtrl.getFinInsuranceList() != oldVar_finInsuranceList) {
			return true;
		}

		if (customerDialogCtrl != null && customerDialogCtrl.isFeeDataModified()) {
			return true;
		}

		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	protected void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		this.latePayWaiverAmount.setErrorMessage("");
		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		boolean isOverdraft = false;

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		//FinanceMain Details Tab ---> 1. Basic Details

		if (!this.finReference.isReadonly() && !financeType.isFinIsGenRef()) {
			this.finReference
					.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_FinReference.value"),
							PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_UNDERSCORE, true));
		}
		if (this.finAmount.isVisible() && !this.finAmount.isReadonly()) {
			this.finAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceMainDialog_FinAmount.value"), finFormatter, true, false));
		}

		if (this.gb_RolloverFinance.isVisible()) {
			if (!recSave && this.custPayAccId.isVisible() && this.custPayAccId.isMandatory()
					&& !this.custPayAccId.isReadonly()) {
				this.custPayAccId.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinanceMainDialog_CustPayAccId.value"), null, true));
			}
		}

		if (isOverdraft) {
			this.finAssetValue.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceMainDialog_ODFinAssetValue.value"), finFormatter, true, false));
		}

		if (this.row_FinAssetValue.isVisible()) {
			if (this.finAssetValue.isVisible() && !this.finAssetValue.isReadonly()) {
				this.finAssetValue.setConstraint(new PTDecimalValidator(
						label_FinanceMainDialog_FinAssetValue.getValue(), finFormatter, true, false));
			}
			if (this.finCurrentAssetValue.isVisible() && !this.finCurrentAssetValue.isReadonly()) {
				this.finCurrentAssetValue.setConstraint(new PTDecimalValidator(
						this.label_FinanceMainDialog_FinCurrentAssetValue.getValue(), finFormatter, false, false));
			}
		}

		if (!this.planDeferCount.isReadonly()) {
			this.planDeferCount.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinanceMainDialog_PlanDeferCount.value"), false, false));
		}

		if (!this.defferments.isReadonly()) {
			int maxAllowedDeferCount = getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxDifferment();
			this.defferments.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinanceMainDialog_Defferments.value"), false, false, maxAllowedDeferCount));
		}

		Date financeDate = this.finStartDate.getValue();

		if (this.finStartDate.isVisible() && !this.finStartDate.isReadonly()) {
			this.finStartDate
					.setConstraint(new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_FinStartDate.value"),
							true, minReqFinStartDate, maxReqFinStartDate, true));
		}

		if (financeDate != null) {
			if (this.finStartDate.isVisible() && this.finContractDate.isVisible()
					&& !this.finContractDate.isReadonly()) {
				this.finContractDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_FinContractDate.value"), false,
								appStartDate, financeDate, true));
			}
		} else {
			if (this.finContractDate.isVisible() && !this.finContractDate.isReadonly()) {
				this.finContractDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_FinContractDate.value"), false,
								appStartDate, appEndDate, true));
			}
		}

		if (!this.stepPolicy.isReadonly() && this.stepFinance.isChecked() && !this.alwManualSteps.isChecked()) {
			this.stepPolicy.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_StepPolicy.value"), null, true, true));
		}

		if (!this.stepType.isDisabled() && this.stepFinance.isChecked() && this.alwManualSteps.isChecked()) {
			this.stepType.setConstraint(new StaticListValidator(PennantStaticListUtil.getStepType(),
					Labels.getLabel("label_FinanceMainDialog_StepType.value")));
		}

		if (!this.noOfSteps.isReadonly() && this.stepFinance.isChecked() && this.alwManualSteps.isChecked()) {
			this.noOfSteps.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinanceMainDialog_NumberOfSteps.value"), true, false, 2, 99));
		}

		//FinanceMain Details Tab ---> 2. Grace Period Details

		if (this.gb_gracePeriodDetails.isVisible()) {

			if (!this.gracePeriodEndDate.isReadonly() && this.manualSchedule.isChecked()) {
				Date maturityDate = appEndDate;
				if (this.maturityDate.getValue() != null) {
					maturityDate = this.maturityDate.getValue();
				}

				Date validFrom = financeDate;
				if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_CHGGRCEND)) {
					validFrom = org_grcPeriodEndDate;
				}
				this.gracePeriodEndDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_GracePeriodEndDate.value"), true,
								validFrom, maturityDate, false));
			} else {

				Date validFrom = financeDate;
				if (!this.gracePeriodEndDate.isReadonly()
						&& StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_CHGGRCEND)) {

					// Find Valid From Date by rendering 
					List<FinanceScheduleDetail> scheduelist = getFinanceDetail().getFinScheduleData()
							.getFinanceScheduleDetails();

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

						/*
						 * if (financeMain.getNextGrcCpzDate() != null && financeMain.isAllowGrcCpz() &&
						 * curSchd.getSchDate().compareTo(financeMain.getNextGrcCpzDate()) <= 0) { validFrom =
						 * curSchd.getSchDate(); continue; } if (financeMain.getNextGrcPftRvwDate() != null &&
						 * financeMain.isAllowGrcPftRvw() &&
						 * curSchd.getSchDate().compareTo(financeMain.getNextGrcPftRvwDate()) <= 0) { validFrom =
						 * curSchd.getSchDate(); continue; } if (financeMain.getNextGrcPftDate() != null &&
						 * curSchd.getSchDate().compareTo(financeMain.getNextGrcPftDate()) <= 0) { validFrom =
						 * curSchd.getSchDate(); continue; }
						 */
					}
					validFrom = DateUtility.addDays(validFrom, 1);
					this.gracePeriodEndDate.setConstraint(
							new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_GracePeriodEndDate.value"),
									true, validFrom, appEndDate, true));
				}
			}

			if (this.gracePftRate.isVisible() && !this.gracePftRate.isReadonly()) {
				this.gracePftRate.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceMainDialog_GracePftRate.value"), 9, false, false, 0, 9999));
			}
			if (!this.graceRate.isMarginReadonly()) {
				this.graceRate.setMarginConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceMainDialog_GraceMargin.value"), 9, false, true, -9999, 9999));
			}

			if (this.allowGrace.isChecked()) {
				this.graceRate.getEffRateComp().setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceMainDialog_GracePftRate.value"), 9, false));
			}

			if (!this.nextGrcPftDate.isReadonly() && StringUtils.isNotEmpty(this.gracePftFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {
				this.nextGrcPftDate_two.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextGrcPftDate.value"), true));
			}

			if (!this.nextGrcPftRvwDate.isReadonly() && StringUtils.isNotEmpty(this.gracePftRvwFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {

				this.nextGrcPftRvwDate_two.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextGrcPftRvwDate.value"), true));
			}

			if (this.row_GrcAdvPftRate.isVisible() && !this.grcAdvPftRate.isDisabled()) {
				this.grcAdvPftRate.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceMainDialog_GrcAdvPftRate.value"), 9, false));
			}

			if (this.row_FinGrcRates.isVisible()) {

				if (!this.finGrcMinRate.isDisabled()) {
					this.finGrcMinRate.setConstraint(new PTDecimalValidator(
							Labels.getLabel("label_FinanceMainDialog_FinGrcMinRate.value"), 9, false, false, 0, 9999));
				}
				if (!this.finGrcMaxRate.isDisabled()) {
					this.finGrcMaxRate.setConstraint(new PTDecimalValidator(
							Labels.getLabel("label_FinanceMainDialog_FinGrcMaxRate.value"), 9, false, false, 0, 9999));
				}
			}
			
			if (this.row_GrcMaxAmount.isVisible() && !this.grcMaxAmount.isReadonly()) {
				this.grcMaxAmount.setConstraint(new PTDecimalValidator(Labels
						.getLabel("label_FinanceMainDialog_GrcMaxReqAmount.value"), finFormatter, true, false));
			}
		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		if (!this.numberOfTerms.isReadonly() && this.numberOfTerms.intValue() == 0
				&& this.numberOfTerms_two.intValue() == 0 && this.maturityDate.getValue() == null
				&& !this.manualSchedule.isChecked()) {
			this.numberOfTerms.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"), true, false));
		}

		if (financeType.isRollOverFinance() && !this.finRepaymentAmount.isReadonly()) {
			this.finRepaymentAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_FinanceMainDialog_ReqRepayAmount.value"),
							finFormatter, true, false, this.finAmount.getActualValue().doubleValue()));
		}

		if (!this.nextRepayDate.isReadonly() && StringUtils.isNotEmpty(this.repayFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

			this.nextRepayDate
					.setConstraint(new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayDate.value"),
							false, gracePeriodEndDate.getValue(), null, false));
			this.nextRepayDate_two.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayDate.value"), true));
		}

		if (this.rolloverFrqRow.isVisible() && !this.nextRollOverDate.isReadonly()
				&& StringUtils.isNotEmpty(this.rolloverFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.rolloverFrq.getValue()) == null) {

			this.nextRollOverDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRolloverDate.value"), false,
							gracePeriodEndDate.getValue(), null, false));
			this.nextRollOverDate_two.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRolloverDate.value"), false));
		}

		if (this.rpyPftFrqRow.isVisible() && !this.nextRepayPftDate.isReadonly()
				&& StringUtils.isNotEmpty(this.repayPftFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {

			this.nextRepayPftDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayPftDate.value"), false,
							gracePeriodEndDate.getValue(), null, false));
			this.nextRepayPftDate_two.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayPftDate.value"), true));
		}

		if (!this.nextRepayRvwDate.isReadonly() && StringUtils.isNotEmpty(this.repayRvwFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue()) == null) {

			this.nextRepayRvwDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayRvwDate.value"), false,
							gracePeriodEndDate.getValue(), null, false));
			this.nextRepayRvwDate_two.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayRvwDate.value"), true));
		}

		if (!this.nextRepayCpzDate.isReadonly() && StringUtils.isNotEmpty(this.repayCpzFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue()) == null) {

			this.nextRepayCpzDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayCpzDate.value"), false,
							gracePeriodEndDate.getValue(), null, false));
			this.nextRepayCpzDate_two.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayCpzDate.value"), true));
		}

		if (this.repayProfitRate.isVisible() && !this.repayProfitRate.isReadonly()) {
			this.repayProfitRate.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceMainDialog_ProfitRate.value"), 9, false, false, 0, 9999));
		}

		if (!this.repayRate.isMarginReadonly()) {
			this.repayRate.setMarginConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceMainDialog_RepayMargin.value"), 9, false, true, -9999, 9999));
		}

		if (this.row_RpyAdvPftRate.isVisible() && !this.rpyAdvPftRate.isDisabled()) {
			this.rpyAdvPftRate.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_FinanceMainDialog_RpyAdvPftRate.value"), 9, false));
		}

		if (this.row_RpyAdvBaseRate.isVisible() && !this.rpyAdvRate.isMarginReadonly()) {
			this.rpyAdvRate.setMarginConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceMainDialog_RpyAdvMargin.value"), 9, false, true, -9999, 9999));
		}

		if (this.row_FinRepRates.isVisible()) {

			if (!this.finMinRate.isDisabled()) {
				this.finMinRate.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceMainDialog_FinMinRate.value"), 9, false, false, 0, 9999));
			}
			if (!this.finMaxRate.isDisabled()) {
				this.finMaxRate.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceMainDialog_FinMaxRate.value"), 9, false, false, 0, 9999));
			}
		}

		if (this.alwPlannedEmiHoliday.isChecked() && this.numberOfTerms_two.intValue() > 0) {
			if (this.row_PlanEmiHLockPeriod.isVisible() && !this.planEmiHLockPeriod.isReadonly()) {
				this.planEmiHLockPeriod.setConstraint(
						new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_PlanEmiHolidayLockPeriod.value"),
								false, false, 0, this.numberOfTerms_two.intValue() - 1));
			}
			if (this.row_MaxPlanEmi.isVisible()) {
				if (!this.maxPlanEmiPerAnnum.isReadonly()) {
					int maxEmiPerYear = 11;
					if (this.numberOfTerms_two.intValue() < 11) {
						maxEmiPerYear = this.numberOfTerms_two.intValue() - 1;
					}
					this.maxPlanEmiPerAnnum.setConstraint(
							new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_MaxPlanEmiPerAnnum.value"),
									true, false, 1, maxEmiPerYear));
				}
				if (!this.maxPlanEmi.isReadonly()) {
					this.maxPlanEmi.setConstraint(
							new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_MaxPlanEmi.value"), true,
									false, 1, this.numberOfTerms_two.intValue() - 1));
				}
			}
		}

		if (!this.unPlannedEmiHLockPeriod.isReadonly()) {
			this.unPlannedEmiHLockPeriod.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinanceMainDialog_UnPlannedEmiHolidayLockPeriod.value"), false, false, 0,
					this.numberOfTerms_two.intValue()));
		}
		if (!this.maxUnplannedEmi.isReadonly()) {
			this.maxUnplannedEmi.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_MaxUnPlannedEmiHoliday.value"),
							false, false, 0, this.numberOfTerms_two.intValue()));
		}
		if (!this.maxReAgeHolidays.isReadonly()) {
			this.maxReAgeHolidays.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_MaxReAgeHoliday.value"), false,
							false, 0, this.numberOfTerms_two.intValue()));
		}

		/*
		 * if (this.row_supplementRent.isVisible() && !this.supplementRent.isDisabled()) {
		 * this.supplementRent.setConstraint(new
		 * PTDecimalValidator(Labels.getLabel("label_FinanceMainDialog_SupplementRent.value"), 3, false)); } if
		 * (this.row_supplementRent.isVisible() && !this.increasedCost.isDisabled()) {
		 * this.increasedCost.setConstraint(new
		 * PTDecimalValidator(Labels.getLabel("label_FinanceMainDialog_IncreasedCost.value"), 3, false)); }
		 */

		if (financeType.getFinMinTerm() == 1 && financeType.getFinMaxTerm() == 1) {
			this.maturityDate_two.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_MaturityDate.value"), true));
		}

		//Mandatory Maturity Date Validation if Manual Schedule is Checked
		if (this.manualSchedule.isChecked() && !this.maturityDate.isReadonly()) {
			this.maturityDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_MaturityDate.value"), true));
		}

		//OD Finance Validation Check
		if (isOverdraft) {

			if (this.finStartDate.isVisible() && !this.finStartDate.isReadonly()) {
				this.finStartDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_FinStartDate.value"), false,
								minReqFinStartDate, maxReqFinStartDate, false));
			}
			if (this.row_DroplineFrq.isVisible() && !this.firstDroplineDate.isDisabled()) {
				this.firstDroplineDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_DroplineDate.value"), true));
			}

			if (!this.odYearlyTerms.isReadonly()) {
				this.odYearlyTerms.setConstraint(
						new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_ODTenor.value"), false, false));
			}
			if (!this.odMnthlyTerms.isReadonly()) {
				this.odMnthlyTerms.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_FinanceMainDialog_ODTenor.value"), false, false, 0, 11));
			}

		}
		//OverduePenalty GroupBox Validations
		if (this.gb_OverDuePenalty.isVisible()) {

			if (!this.oDChargeAmtOrPerc.isDisabled()
					&& StringUtils.isNotEmpty(this.space_oDChargeAmtOrPerc.getSclass())) {

				if (FinanceConstants.PENALTYTYPE_FLAT.equals(getComboboxValue(this.oDChargeType))
						|| FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					this.oDChargeAmtOrPerc.setConstraint(
							new PTDecimalValidator(Labels.getLabel("label_FinanceMainDialog_ODChargeAmtOrPerc.value"),
									finFormatter, true, false, 9999999));
				} else if (FinanceConstants.PENALTYTYPE_PERC_ONETIME.equals(getComboboxValue(this.oDChargeType))
						|| FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(getComboboxValue(this.oDChargeType))
						|| FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					this.oDChargeAmtOrPerc.setConstraint(new PTDecimalValidator(
							Labels.getLabel("label_FinanceMainDialog_ODChargeAmtOrPerc.value"), 2, true, false, 100));
				}
			}

			if (!this.oDMaxWaiverPerc.isDisabled()) {
				this.oDMaxWaiverPerc.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceMainDialog_ODMaxWaiver.value"), 2, true, false, 100));
			}

			if (!this.oDGraceDays.isReadonly()) {
				this.oDGraceDays.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_FinanceMainDialog_ODGraceDays.value"), false, false));
			}
		}

		if (this.gb_ddaRequest.isVisible()) {
			if (!recSave && !buildEvent) {
				if (!this.bankName.isReadonly()) {
					this.bankName.setConstraint(new PTStringValidator(
							Labels.getLabel("label_FinanceMainDialog_BankName.value"), null, true, true));
				}
				if (!this.iban.isReadonly()) {
					this.iban.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_IBAN.value"),
							PennantRegularExpressions.REGEX_ALPHANUM_FL23, true));
				}
				if (this.hbox_Financemain_IfscCode.isVisible() && !this.ifscCode.isReadonly()) {
					this.ifscCode.setConstraint(
							new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_IfscCode.value"),
									PennantRegularExpressions.REGEX_ALPHANUM_CODE, true));
				}
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	protected void doRemoveValidation() {
		setValidationOn(false);
		for (Component component : this.window.getFellows()) {
			if (component instanceof InputElement) {
				((InputElement) component).setConstraint("");
			} else if (component instanceof ExtendedCombobox) {
				((ExtendedCombobox) component).setConstraint("");
			} else if (component instanceof CurrencyBox) {
				((CurrencyBox) component).setConstraint("");
			} else if (component instanceof AccountSelectionBox) {
				((AccountSelectionBox) component).setConstraint("");
			}
		}
	}

	/**
	 * Method to set validation on LOV fields
	 */
	protected void doSetLOVValidation() {
		logger.debug("Entering");

		//FinanceMain Details Tab ---> 1. Basic Details

		this.lovDescFinTypeName.setConstraint(
				new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_FinType.value"), null, true));

		this.finCcy.setConstraint(
				new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_FinCcy.value"), null, true, true));

		if (!this.finBranch.isReadonly()) {
			this.finBranch.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_FinBranch.value"), null, false, true));
		}

		if (!this.custCIF.isReadonly()) {
			this.custCIF.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_CustID.value"), null, true, true));
		}

		if (!recSave && this.disbAcctId.isVisible() && this.disbAcctId.isMandatory() && !this.disbAcctId.isReadonly()) {
			this.disbAcctId.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_DisbAcctId.value"), null, true));
		}

		if (!recSave && this.repayAcctId.isVisible() && this.repayAcctId.isMandatory()
				&& !this.repayAcctId.isReadonly()) {
			this.repayAcctId.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_RepayAcctId.value"), null, true));
		}

		if (!recSave && this.downPayAccount.isVisible() && this.downPayAccount.isMandatory()) {
			this.downPayAccount.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_DownPayAccount.value"), null, true));
		}

		if (this.commitmentRef.isButtonVisible() && !recSave) {
			this.commitmentRef
					.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_CommitRef.value"),
							null, getFinanceDetail().getFinScheduleData().getFinanceType().isFinCommitmentReq()));
		}

		if (this.rowLimitRef.isVisible() && this.finLimitRef.isButtonVisible() && !recSave) {
			this.finLimitRef.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_FinLimitRef.value"), null, true));
		}

		//MMAReference
		if (this.rowLimitRef.isVisible() && this.mMAReference.isButtonVisible() && !recSave) {
			this.mMAReference.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_mMAReference.value"), null, true, true));
		}

		if (!this.finPurpose.isReadonly()) {
			this.finPurpose.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_FinPurpose.value"), null, false, true));
		}

		if (!this.finDivision.equals(FinanceConstants.FIN_DIVISION_CORPORATE) && !recSave && !buildEvent) {
			if (!this.accountsOfficer.isReadonly() && this.accountsOfficer.isMandatory()) {
				this.accountsOfficer.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinanceMainDialog_AccountsOfficer.value"), null, true, true));
			}
			if (!this.dsaCode.isReadonly() && this.dsaCode.isMandatory()) {
				this.dsaCode.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinanceMainDialog_DSACode.value"), null, true, true));
			}
		}

		//FinanceMain Details Tab ---> 2. Grace Period Details

		if (!this.graceRate.isBaseReadonly()) {
			this.graceRate.setBaseConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_GraceBaseRate.value"), null, true, true));
		}

		if (this.row_GrcAdvBaseRate.isVisible() && !this.grcAdvRate.isBaseReadonly()) {
			this.grcAdvRate.setBaseConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_GrcAdvBaseRate.value"), null, true, true));
		}

		//FinanceMain Details Tab ---> 3. Repayments Period Details

		if (!this.repayRate.isBaseReadonly()) {
			this.repayRate.setBaseConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_RepayBaseRate.value"), null, true, true));
		}

		if (this.row_RpyAdvBaseRate.isVisible() && !this.rpyAdvRate.isBaseReadonly()) {
			this.rpyAdvRate.setBaseConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_RpyAdvBaseRate.value"), null, true, true));
		}

		if (!recSave && this.custPayAccId.isVisible() && this.custPayAccId.isMandatory()) {
			this.custPayAccId.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_CustPayAccId.value"), null, true));
		}

		if (this.repayRate.isSpecialVisible() && !this.repayRate.isSpecialReadonly()) {
			this.repayRate.setSpecialConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_RepaySpecialRate.value"), null, true, true));
		}

		logger.debug("Leaving ");
	}

	/**
	 * Method to remove validation on LOV fields.
	 * 
	 **/
	protected void doRemoveLOVValidation() {
		logger.debug("Entering ");

		//FinanceMain Details Tab ---> 1. Basic Details

		this.lovDescFinTypeName.setConstraint("");
		this.finCcy.setConstraint("");
		this.finBranch.setConstraint("");
		this.custCIF.setConstraint("");
		this.commitmentRef.setConstraint("");
		this.finLimitRef.setConstraint("");
		this.finPurpose.setConstraint("");
		this.accountsOfficer.setConstraint("");
		this.dsaCode.setConstraint("");
		this.referralId.setConstraint("");
		this.dmaCode.setConstraint("");
		this.connector.setConstraint("");
		this.salesDepartment.setConstraint("");

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.graceRate.setBaseConstraint("");
		this.graceRate.setSpecialConstraint("");

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		this.repayRate.setBaseConstraint("");
		this.repayRate.setSpecialConstraint("");

		logger.debug("Leaving ");
	}

	public void setReadOnlyForCombobox() {
		logger.debug("Entering");
		this.cbProfitDaysBasis.setReadonly(true);
		this.finRepayMethod.setReadonly(true);
		this.grcRateBasis.setReadonly(true);
		this.cbGrcSchdMthd.setReadonly(true);
		this.repayRateBasis.setReadonly(true);
		this.cbScheduleMethod.setReadonly(true);
		this.oDChargeCalOn.setReadonly(true);
		this.oDChargeType.setReadonly(true);
		logger.debug("Leaving");
	}

	/**
	 * Method for Check AllowGracePeriod component for Allow Grace Or not
	 */
	protected void doAllowGraceperiod(boolean onCheckProc) {
		logger.debug("Entering");

		boolean checked = false;
		FinanceType finType = getFinanceDetail().getFinScheduleData().getFinanceType();

		if (this.allowGrace.isChecked()) {

			if (isReadOnly("FinanceMainDialog_NoScheduleGeneration")) {
				this.gb_gracePeriodDetails.setVisible(true);
			}

			checked = true;
			readOnlyComponent(isReadOnly("FinanceMainDialog_gracePeriodEndDate"), this.gracePeriodEndDate);
			readOnlyComponent(isReadOnly("FinanceMainDialog_graceRateBasis"), this.grcRateBasis);
			readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
			this.graceRate.setBaseReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
			this.graceRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
			readOnlyComponent(isReadOnly("FinanceMainDialog_graceBaseRate"), this.graceRate.getMarginComp());
			readOnlyComponent(isReadOnly("FinanceMainDialog_grcPftDaysBasis"), this.grcPftDaysBasis);

			if (finType.isFInIsAlwGrace()) {
				this.gracePftFrq.setDisabled(isReadOnly("FinanceMainDialog_gracePftFrq"));
				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftDate"), this.nextGrcPftDate);
			}

			if (finType.isFinGrcIsRvwAlw()) {
				this.gracePftRvwFrq.setDisabled(isReadOnly("FinanceMainDialog_gracePftRvwFrq"));
				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"), this.nextGrcPftRvwDate);
			}

			if (finType.isFinGrcIsIntCpz()) {
				this.graceCpzFrq.setDisabled(isReadOnly("FinanceMainDialog_graceCpzFrq"));
				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcCpzDate"), this.nextGrcCpzDate);
			}

			readOnlyComponent(isReadOnly("FinanceMainDialog_allowGrcRepay"), this.allowGrcRepay);
			readOnlyComponent(isReadOnly("FinanceMainDialog_grcSchdMthd"), this.cbGrcSchdMthd);

		} else {

			this.gb_gracePeriodDetails.setVisible(false);
			readOnlyComponent(true, this.gracePeriodEndDate);
			readOnlyComponent(true, this.grcRateBasis);
			readOnlyComponent(true, this.gracePftRate);
			this.graceRate.setReadonly(true);
			readOnlyComponent(true, this.grcPftDaysBasis);

			this.gracePftFrq.setDisabled(true);
			readOnlyComponent(true, this.nextGrcPftDate);

			this.gracePftRvwFrq.setDisabled(true);
			readOnlyComponent(true, this.nextGrcPftRvwDate);

			this.graceCpzFrq.setDisabled(true);
			readOnlyComponent(true, this.nextGrcCpzDate);

			readOnlyComponent(true, this.allowGrcRepay);
			readOnlyComponent(true, this.cbGrcSchdMthd);
		}

		if (onCheckProc) {

			fillComboBox(grcRateBasis, finType.getFinGrcRateType(), PennantStaticListUtil.getInterestRateType(
					!getFinanceDetail().getFinScheduleData().getFinanceMain().isMigratedFinance()), ",C,D,");
			fillComboBox(this.grcPftDaysBasis, finType.getFinDaysCalType(), PennantStaticListUtil.getProfitDaysBasis(),
					"");
			this.graceRate.setMarginValue(finType.getFinGrcMargin());

			this.finGrcMinRate.setValue(finType.getFInGrcMinRate());
			this.finGrcMaxRate.setValue(finType.getFinGrcMaxRate());

			if (CalculationConstants.RATE_BASIS_R.equals(getComboboxValue(this.grcRateBasis))) {

				this.graceRate.setBaseValue(finType.getFinGrcBaseRate());
				this.graceRate.setSpecialValue(finType.getFinGrcSplRate());

				if (StringUtils.isNotBlank(finType.getFinGrcBaseRate())) {
					RateDetail rateDetail = RateUtil.rates(this.graceRate.getBaseValue(), this.finCcy.getValue(),
							this.graceRate.getSpecialValue(),
							this.graceRate.getMarginValue() == null ? BigDecimal.ZERO : this.graceRate.getMarginValue(),
							this.finGrcMinRate.getValue(), this.finGrcMaxRate.getValue());
					this.graceRate.setEffRateText(
							PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
				} else {
					this.gracePftRate.setValue(finType.getFinGrcIntRate());
				}
				this.grcBaseRateRow.setVisible(true);
				this.graceRate.setVisible(true);
			}

			if (CalculationConstants.RATE_BASIS_F.equals(getComboboxValue(this.grcRateBasis))
					|| CalculationConstants.RATE_BASIS_C.equals(getComboboxValue(this.grcRateBasis))
					|| CalculationConstants.RATE_BASIS_D.equals(getComboboxValue(this.grcRateBasis))) {
				this.graceRate.setEffRateValue(finType.getFinGrcIntRate());
				this.gracePftRate.setValue(finType.getFinGrcIntRate());
			}

			if (finType.isFInIsAlwGrace()) {
				this.gracePftFrq.setValue(finType.getFinGrcDftIntFrq());
				this.gracePftFrq.setDisabled(checked ? isReadOnly("FinanceMainDialog_gracePftFrq") : true);

				if (this.finStartDate.getValue() == null) {
					this.finStartDate.setValue(appDate);
				}

				if (this.allowGrace.isChecked()) {

					this.nextGrcPftDate_two.setValue(FrequencyUtil
							.getNextDate(this.gracePftFrq.getValue(), 1, this.finStartDate.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false, finType.getFddLockPeriod())
							.getNextFrequencyDate());

					if (this.nextGrcPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						this.nextGrcPftDate_two.setValue(this.gracePeriodEndDate_two.getValue());
					}
					this.nextGrcPftDate.setValue(null);
					this.gracePeriodEndDate.setValue(null);

				} else {

					this.gracePeriodEndDate.setValue(null);
					this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
					this.nextGrcPftDate.setValue(null);
					this.nextGrcPftDate_two.setValue(this.finStartDate.getValue());
				}

				if (StringUtils.isNotBlank(this.gracePftFrq.getValue())) {
					processFrqChange(this.gracePftFrq);
				}
			}

			if (finType.isFinGrcIsRvwAlw()) {
				this.grcPftRvwFrqRow.setVisible(true);
				this.gracePftRvwFrq.setValue(finType.getFinGrcRvwFrq());
				this.gracePftRvwFrq.setDisabled(checked ? isReadOnly("FinanceMainDialog_gracePftRvwFrq") : true);
				if (this.allowGrace.isChecked()) {
					this.nextGrcPftRvwDate_two.setValue(FrequencyUtil
							.getNextDate(this.gracePftRvwFrq.getValue(), 1, this.finStartDate.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false, finType.getFddLockPeriod())
							.getNextFrequencyDate());
					if (this.nextGrcPftRvwDate_two.getValue() != null
							&& this.nextGrcPftRvwDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						this.nextGrcPftRvwDate_two.setValue(this.gracePeriodEndDate_two.getValue());
					}
					this.nextGrcPftRvwDate.setValue(null);
				} else {
					this.nextGrcPftRvwDate.setValue(null);
					this.nextGrcPftRvwDate_two.setValue(this.finStartDate.getValue());
				}

				if (StringUtils.isNotBlank(this.gracePftRvwFrq.getValue())) {
					processFrqChange(this.gracePftRvwFrq);
				}
			} else {
				this.grcPftRvwFrqRow.setVisible(false);
			}

			if (finType.isFinGrcIsIntCpz()) {
				this.grcCpzFrqRow.setVisible(true);
				this.graceCpzFrq.setValue(finType.getFinGrcCpzFrq());
				this.graceCpzFrq.setDisabled(checked ? isReadOnly("FinanceMainDialog_graceCpzFrq") : true);

				if (this.allowGrace.isChecked()) {

					this.nextGrcCpzDate_two.setValue(FrequencyUtil
							.getNextDate(this.graceCpzFrq.getValue(), 1, this.finStartDate.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false, finType.getFddLockPeriod())
							.getNextFrequencyDate());

					if (this.nextGrcCpzDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						this.nextGrcCpzDate_two.setValue(this.gracePeriodEndDate_two.getValue());
					}
					this.nextGrcCpzDate.setValue(null);

				} else {

					this.nextGrcCpzDate.setValue(null);
					this.nextGrcCpzDate_two.setValue(this.finStartDate.getValue());
				}

				if (StringUtils.isNotBlank(this.graceCpzFrq.getValue())) {
					processFrqChange(this.graceCpzFrq);
				}
			} else {
				this.grcCpzFrqRow.setVisible(false);
			}

			this.allowGrcRepay.setChecked(finType.isFinIsAlwGrcRepay());
			fillComboBox(cbGrcSchdMthd, finType.getFinGrcSchdMthd(), PennantStaticListUtil.getScheduleMethods(),
					",EQUAL,PRI_PFT,PRI,");

			if (finType.isFinIsAlwGrcRepay()) {
				this.grcRepayRow.setVisible(true);
			} else {
				this.grcRepayRow.setVisible(false);
			}

			if ((StringUtils.equalsIgnoreCase(getProductCode(), FinanceConstants.PRODUCT_IJARAH)
					|| StringUtils.equalsIgnoreCase(getProductCode(), FinanceConstants.PRODUCT_FWIJARAH))
					&& finType.isFinIsAlwMD()) {
				this.finAssetValue.setReadonly(isReadOnly("FinanceMainDialog_finAssetValue"));
				this.row_FinAssetValue.setVisible(true);
			}

			setGrcPolicyRate(false, getFinanceDetail().isNewRecord());

		}
		if (!this.grcRateBasis.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {

			this.graceRate.setBaseReadonly(true);
			this.graceRate.setSpecialReadonly(true);
			this.graceRate.setMarginReadonly(true);

			if (CalculationConstants.RATE_BASIS_F.equals(this.grcRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_C
							.equals(this.grcRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_D
							.equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {

				if (!this.allowGrace.isChecked()) {
					readOnlyComponent(true, this.gracePftRate);
				} else {
					readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
				}

			} else if (CalculationConstants.RATE_BASIS_R
					.equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {

				if (StringUtils
						.isNotBlank(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate())) {

					if (!this.allowGrace.isChecked()) {
						this.graceRate.setBaseReadonly(true);
						this.graceRate.setSpecialReadonly(true);
					} else {
						this.graceRate.setBaseReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
						this.graceRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
						this.graceRate.setMarginReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
					}
					readOnlyComponent(true, this.gracePftRate);
					this.gracePftRate.setText("");

				} else {

					if (!this.allowGrace.isChecked()) {
						readOnlyComponent(true, this.gracePftRate);
					} else {
						readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
					}
					this.gracePftRate
							.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcIntRate());
				}
			}
		}

		// Min & Max Rates Setting
		if (StringUtils.equals(CalculationConstants.RATE_BASIS_R,
				this.grcRateBasis.getSelectedItem().getValue().toString())
				&& StringUtils.isNotEmpty(finType.getFinGrcBaseRate())) {
			if ((finType.getFInGrcMinRate() == null || finType.getFInGrcMinRate().compareTo(BigDecimal.ZERO) == 0)
					&& (finType.getFinGrcMaxRate() == null
							|| finType.getFinGrcMaxRate().compareTo(BigDecimal.ZERO) == 0)) {
				this.row_FinGrcRates.setVisible(false);
			} else {
				this.row_FinGrcRates.setVisible(true);
			}
		} else {
			this.row_FinGrcRates.setVisible(false);
		}
		
		onChangeGrcSchdMthd();

		logger.debug("Leaving");
	}

	// CRUD operations

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws Exception
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		Cloner cloner = new Cloner();
		aFinanceDetail = cloner.deepClone(getFinanceDetail());

		boolean isNew = false;
		FinScheduleData aFinScheduleData = aFinanceDetail.getFinScheduleData();
		FinanceMain aFinanceMain = aFinScheduleData.getFinanceMain();
		recSave = false;
		buildEvent = false;

		boolean isOverdraft = false;

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())) {
			isOverdraft = true;
		}
		if (this.userAction.getSelectedItem() != null) {
			if ("Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| "Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")
					|| this.userAction.getSelectedItem().getLabel().contains("Hold")) {
				recSave = true;
				aFinanceDetail.setActionSave(true);
			}
			aFinanceDetail.setUserAction(this.userAction.getSelectedItem().getLabel());
		}
		
		aFinanceDetail.setModuleDefiner(
				StringUtils.isEmpty(moduleDefiner) ? FinanceConstants.FINSER_EVENT_ORG : moduleDefiner);

		//Resetting Service Task ID's from Original State
		aFinanceMain.setRoleCode(this.curRoleCode);
		aFinanceMain.setNextRoleCode(this.curNextRoleCode);
		aFinanceMain.setTaskId(this.curTaskId);
		aFinanceMain.setNextTaskId(this.curNextTaskId);
		aFinanceMain.setNextUserId(this.curNextUserId);
		isNew = aFinanceDetail.isNew();

		if (!primaryValidations()) {
			return;
		}

		// force validation, if on, than execute by component.getValue()
		// fill the financeMain object with the components data
		this.doWriteComponentsToBean(aFinScheduleData);

		// Setting Accounting Event Code for Postings execution
		if (StringUtils.isBlank(eventCode)) {
			eventCode = PennantApplicationUtil.getEventCode(aFinanceMain.getFinStartDate());
		}
		aFinanceDetail.setAccountingEventCode(eventCode);
		
		// Extended Field validations
		if (aFinanceDetail.getExtendedFieldHeader() != null) {
			aFinanceDetail.setExtendedFieldRender(extendedFieldCtrl.save(!recSave));
		}

		//Save Contributor List Details
		if (isRIAExist) {
			aFinanceDetail = contributorDetailsDialogCtrl.doSaveContributorsDetail(aFinanceDetail,
					getTab(AssetConstants.UNIQUE_ID_CONTRIBUTOR));
		} else {
			aFinanceDetail.setFinContributorHeader(null);
		}

		//For istisna Product Configurations
		if (disbursementDetailDialogCtrl != null) {
			boolean isValid = disbursementDetailDialogCtrl.validateContractorAssetDetails(aFinanceDetail,
					getTab(AssetConstants.UNIQUE_ID_DISBURSMENT));
			if (!isValid) {
				return;
			}
		}

		FinanceType finType = aFinanceDetail.getFinScheduleData().getFinanceType();

		//Schedule details Tab Validation
		if (!manualSchedule.isChecked()) {

			if (isReadOnly("FinanceMainDialog_NoScheduleGeneration")) {

				if (isOverdraft) {
					if ((finType.isDroplineOD()
							|| StringUtils.equals(FinanceConstants.FINSER_EVENT_OVERDRAFTSCHD, this.moduleDefiner))
							&& isSchdlRegenerate()) {
						MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
						return;
					} else {
						if (!recSave && StringUtils.isEmpty(this.moduleDefiner)
								&& StringUtils.isEmpty(aFinanceMain.getDroplineFrq())) {
							//To Rebuild the overdraft if any fields are changed
							aFinanceDetail.getFinScheduleData().getFinanceMain()
									.setEventFromDate(aFinanceMain.getFinStartDate());

							aFinanceDetail.setFinScheduleData(
									ScheduleCalculator.buildODSchedule(aFinanceDetail.getFinScheduleData()));
						}
					}
				} else {
					if (isSchdlRegenerate()) {
						MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
						return;
					}
				}

				if (!recSave && !(isOverdraft)
						&& aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() <= 0) {
					MessageUtil.showError(Labels.getLabel("label_Finance_GenSchedule"));
					return;
				}

				//Commitment Available Amount Checking During Finance Approval
				if (!recSave && !doValidateCommitment(aFinanceDetail)) {
					return;
				}
			}

		} else {

			if (getManualScheduleDetailDialogCtrl() != null) {
				boolean isEndingBal = getManualScheduleDetailDialogCtrl().doCheckEndingBal();
				if (!isEndingBal) {
					Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_SCHEDULE));
					tab.setSelected(true);
					MessageUtil.showError(Labels.getLabel("label_Finance_ManualSchd_Changed"));
					return;
				} else {
					if (this.btnBuildSchedule.isVisible() && !buildEvent
							&& getManualScheduleDetailDialogCtrl().isDataChanged()) {
						MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
						return;
					}
				}

				if (isReadOnly("FinanceMainDialog_NoScheduleGeneration") && isSchdlRegenerate()) {
					MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
					return;
				}

				if (getManualScheduleDetailDialogCtrl().isSchRebuildReq()) {
					MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
					return;
				}

			} else {
				if (isReadOnly("FinanceMainDialog_NoScheduleGeneration") && isSchdlRegenerate()) {
					MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
					return;
				}
			}
		}

		// Finance Flags
		if (StringUtils.isEmpty(moduleDefiner)) {
			fetchFlagDetals();
			if (getFinFlagsDetailList() != null && !getFinFlagsDetailList().isEmpty()) {
				aFinanceDetail.setFinFlagsDetails(getFinFlagsDetailList());
			} else {
				aFinanceDetail.setFinFlagsDetails(null);
			}
		}

		//Deviation calculations
		if (StringUtils.isEmpty(moduleDefiner)) {
			deviationExecutionCtrl.checkProductDeviations(getFinanceDetail());
			deviationExecutionCtrl.checkFeeDeviations(getFinanceDetail());
			aFinanceDetail.setApprovedFinanceDeviations(getFinanceDetail().getApprovedFinanceDeviations());

		}

		// Customer Details Tab ---> Customer Details 
		if (customerDialogCtrl != null) {
			boolean validatePhone = !recSave || "Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel());
			if (!processCustomerDetails(aFinanceDetail, validatePhone)) {
				return;
			}
		}

		// Planned EMI Holiday Details Validation
		if (getFinanceDetail().getFinScheduleData().getFinanceMain().isLovDescIsSchdGenerated()
				&& aFinanceMain.isPlanEMIHAlw()) {
			if (StringUtils.equals(aFinanceMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				if (aFinScheduleData.getPlanEMIHmonths() == null || aFinScheduleData.getPlanEMIHmonths().isEmpty()) {
					Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_SCHEDULE));
					tab.setSelected(true);
					MessageUtil.showError(Labels.getLabel("label_Finance_PlanEMIHoliday"));
					return;
				}
			} else if (StringUtils.equals(aFinanceMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
				if (aFinScheduleData.getPlanEMIHDates() == null || aFinScheduleData.getPlanEMIHDates().isEmpty()) {
					MessageUtil.showError(Labels.getLabel("label_Finance_PlanEMIHoliday"));
					return;
				}
			}
		}

		// After Changing Planned EMI Dates / Months Validation for Recalculated or not
		if (StringUtils.isEmpty(moduleDefiner)
				|| StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_PLANNEDEMI)) {

			// Planned EMI Holiday Months
			if (getScheduleDetailDialogCtrl() != null && aFinanceMain.isPlanEMIHAlw()) {
				if (StringUtils.equals(aFinanceMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
					if (!getScheduleDetailDialogCtrl().getPlanEMIHMonths().containsAll(this.oldVar_planEMIMonths)
							|| !this.oldVar_planEMIMonths
									.containsAll(getScheduleDetailDialogCtrl().getPlanEMIHMonths())) {
						MessageUtil.showError(Labels.getLabel("label_Finance_PlanEMIHoliday"));
						return;
					}
				} else if (StringUtils.equals(aFinanceMain.getPlanEMIHMethod(),
						FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
					if (!getScheduleDetailDialogCtrl().getPlanEMIHDateList().containsAll(this.oldVar_planEMIDates)
							|| !this.oldVar_planEMIDates
									.containsAll(getScheduleDetailDialogCtrl().getPlanEMIHDateList())) {
						MessageUtil.showError(Labels.getLabel("label_Finance_PlanEMIHoliday"));
						return;
					}
				}
			}
		}

		// Guaranteer Details Tab ---> Guaranteer Details 
		if (jointAccountDetailDialogCtrl != null) {
			if (jointAccountDetailDialogCtrl.getGuarantorDetailList() != null
					&& jointAccountDetailDialogCtrl.getGuarantorDetailList().size() > 0) {
				jointAccountDetailDialogCtrl.doSave_GuarantorDetail(aFinanceDetail);
			}
			if (jointAccountDetailDialogCtrl.getJountAccountDetailList() != null
					&& jointAccountDetailDialogCtrl.getJountAccountDetailList().size() > 0) {
				jointAccountDetailDialogCtrl.doSave_JointAccountDetail(aFinanceDetail);
			}
		} else {
			aFinanceDetail.setJountAccountDetailList(null);
			aFinanceDetail.setGurantorsDetailList(null);
		}

		//Finance Eligibility Details Tab
		setFinanceDetail(aFinanceDetail);
		if (eligibilityDetailDialogCtrl != null) {
			aFinanceDetail = eligibilityDetailDialogCtrl.doSave_EligibilityList(aFinanceDetail);
		}

		//Finance Scoring Details Tab
		if (scoringDetailDialogCtrl != null) {
			boolean scoreexcuted = true;
			try {
				aFinanceDetail.setCustomerEligibilityCheck(prepareCustElgDetail(false).getCustomerEligibilityCheck());
				scoringDetailDialogCtrl.doSave_ScoreDetail(aFinanceDetail);

			} catch (InterruptedException e) {
				//Show validation if the user action is  not save.
				if (!recSave) {
					scoreexcuted = false;
					MessageUtil.showError(Labels.getLabel("label_Finance_Verify_Score"));
				}
			} catch (EmptyResultDataAccessException e) {
				//Show validation if the user action is not save.
				if (!recSave) {
					MessageUtil.showError(Labels.getLabel("label_Finance_ScoreInsufficient_Error"));
					return;
				}
			}

			if (!scoreexcuted) {
				return;
			}

		} else {
			aFinanceDetail.setFinScoreHeaderList(null);
			aFinanceDetail.setScore(BigDecimal.ZERO);
		}

		//FIXME Satish this will be used for disbursements instructions, need to be renamed when time permits.
		if (!(isOverdraft && StringUtils.isEmpty(moduleDefiner))) {
			if (advancePaymentWindow != null && finAdvancePaymentsListCtrl != null) {
				finAdvancePaymentsListCtrl.doSetLabels(getFinBasicDetails());
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("financeMainDialogCtrl", this);
				map.put("financeDetail", aFinanceDetail);
				map.put("userAction", this.userAction.getSelectedItem().getLabel());
				map.put("isFinalStage", "Accounting".equals(getTaskTabs(getTaskId(getRole()))));
				map.put("moduleDefiner", moduleDefiner);
				boolean proceed = finAdvancePaymentsListCtrl.onAdvancePaymentValidation(map);
				if (proceed) {
					if (aFinanceDetail.getAdvancePaymentsList() != null
							&& !aFinanceDetail.getAdvancePaymentsList().isEmpty()) {
						for (FinAdvancePayments finPayDetail : aFinanceDetail.getAdvancePaymentsList()) {
							finPayDetail.setFinReference(this.finReference.getValue());
							finPayDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
							finPayDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
							finPayDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
						}
					}
				} else {
					return;
				}
			}
		}
		//Finance Fee Details
		if (finFeeDetailListCtrl != null) {
			finFeeDetailListCtrl.processFeeDetails(aFinanceDetail.getFinScheduleData());
		}

		//Document Details Saving
		if (documentDetailDialogCtrl != null) {

			if (!recSave && getUserWorkspace().isAllowed("IsCasdoc_Mandatory")) {
				//Add Credit scoring Document to the document list
				addCasDocument(aFinanceDetail);
			}
			aFinanceDetail.setDocumentDetailsList(documentDetailDialogCtrl.getDocumentDetailsList());

		} else {
			aFinanceDetail.setDocumentDetailsList(null);
		}

		if (financeCheckListReferenceDialogCtrl != null) {
			financeCheckListReferenceDialogCtrl.doSetLabels(getFinBasicDetails());
			financeCheckListReferenceDialogCtrl.doWriteBeanToComponents(aFinanceDetail.getCheckList(),
					aFinanceDetail.getFinanceCheckList(), false);
		}

		//Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aFinanceDetail, false);
			if (!validationSuccess) {
				return;
			}
		} else {
			aFinanceDetail.setFinanceCheckList(null);
		}

		if (covenantTypeWindow != null) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", aFinanceDetail);
			map.put("userAction", this.userAction.getSelectedItem().getLabel());
			Events.sendEvent("onCovenantTypeValidation", covenantTypeWindow, map);
			if (aFinanceDetail.getCovenantTypeList() != null && !aFinanceDetail.getCovenantTypeList().isEmpty()) {
				for (FinCovenantType finCovenantType : aFinanceDetail.getCovenantTypeList()) {
					if (!recSave && !finCovenantType.isAlwWaiver() && !finCovenantType.isAlwPostpone()
							&& StringUtils.equals(getRole(), finCovenantType.getMandRole())
							&& !isCovenantDocumentExist(aFinanceDetail, finCovenantType.getCovenantType())) {
						if (DocumentCategories.CUSTOMER.getKey().equals(finCovenantType.getCategoryCode())) {
							this.custDetailTab.setSelected(true);
							;
						} else {
							Tab tab = (Tab) tabsIndexCenter
									.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_DOCUMENTDETAIL));
							tab.setSelected(true);
						}
						MessageUtil.showError(Labels.getLabel("label_CovenantType_Doc_Mandatory",
								new String[] { finCovenantType.getCovenantTypeDesc() }));
						return;
					}
					finCovenantType.setFinReference(this.finReference.getValue());
					finCovenantType.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					finCovenantType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					finCovenantType.setUserDetails(getUserWorkspace().getLoggedInUser());
				}
			}
		}

		// Internal Collateral Assignment Details
		if (collateralHeaderDialogCtrl != null) {

			// Validate Assigned Collateral Value
			if (!recSave && finType.isFinCollateralReq()) {

				BigDecimal utilizedAmt = BigDecimal.ZERO;
				if (!aFinanceMain.isLovDescIsSchdGenerated()) {
					if(PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(finType.getFinLTVCheck())){
						utilizedAmt = aFinanceMain.getFinAssetValue().subtract(aFinanceMain.getDownPayment())
								.add(aFinanceMain.getFeeChargeAmt()).add(aFinanceMain.getInsuranceAmt());
					} else {
						utilizedAmt = aFinanceMain.getFinAmount().subtract(aFinanceMain.getDownPayment())
								.add(aFinanceMain.getFeeChargeAmt()).add(aFinanceMain.getInsuranceAmt());
					}
				} else {
					if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(finType.getFinLTVCheck())) {
						utilizedAmt = utilizedAmt.add(aFinanceMain.getFinAssetValue())
								.add(aFinanceMain.getFeeChargeAmt().add(aFinanceMain.getInsuranceAmt()));
					} else {
						for (FinanceDisbursement curDisb : aFinScheduleData.getDisbursementDetails()) {
							if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus())) {
								continue;
							}
							utilizedAmt = utilizedAmt.add(curDisb.getDisbAmount())
									.add(aFinanceMain.getFeeChargeAmt().add(aFinanceMain.getInsuranceAmt()));
						}

						utilizedAmt = utilizedAmt.subtract(aFinanceMain.getDownPayment())
								.subtract(aFinanceMain.getFinRepaymentAmount());
					}
				}
				boolean isValid = collateralHeaderDialogCtrl.validCollateralValue(utilizedAmt);
				if (!isValid) {
					if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(finType.getFinLTVCheck())) {
						String msg = Labels.getLabel("label_CollateralAssignment_InSufficient_FinAmt");
						if (finType.isPartiallySecured()) {
							if (MessageUtil.confirm(msg, MessageUtil.CANCEL | MessageUtil.OVERIDE) == MessageUtil.CANCEL) {
								return;
							}
						} else {
							MessageUtil.showError(msg);
							return;
						}
					} else {
						String msg = Labels.getLabel("label_CollateralAssignment_InSufficient");
						if (finType.isPartiallySecured()) {
							if (MessageUtil.confirm(msg,MessageUtil.CANCEL | MessageUtil.OVERIDE) == MessageUtil.CANCEL) {
								return;
							}
						} else {
							MessageUtil.showError(msg);
							return;
						}
					}
				}
			}

			aFinanceDetail.setCollateralAssignmentList(collateralHeaderDialogCtrl.getCollateralAssignments());
			aFinanceDetail.setFinAssetTypesList(collateralHeaderDialogCtrl.getFinAssetTypes());
			aFinanceDetail.setExtendedFieldRenderList(collateralHeaderDialogCtrl.getExtendedFieldRenderList());
		} else {
			aFinanceDetail.setCollateralAssignmentList(null);
			aFinanceDetail.setFinAssetTypesList(null);
			aFinanceDetail.setExtendedFieldRenderList(null);
		}

		// Vas Recording Details capturing
		if (finVasRecordingDialogCtrl != null) {

			List<VASRecording> vasRecordings = finVasRecordingDialogCtrl.getVasRecordings();
			for (VASRecording recording : vasRecordings) {
				if (StringUtils.isEmpty(recording.getVasReference())) {
					MessageUtil.showError(Labels.getLabel("label_Finance_MandatoryVAS_Update"));
					return;
				}

				if (aFinanceDetail.getFinScheduleData().getFinFeeDetailActualList() != null
						&& !aFinanceDetail.getFinScheduleData().getFinFeeDetailActualList().isEmpty()) {

					for (FinFeeDetail feeDetail : aFinanceDetail.getFinScheduleData().getFinFeeDetailActualList()) {

						if (StringUtils.equals(feeDetail.getVasReference(), recording.getVasReference())) {

							recording.setFee(feeDetail.getActualAmount());
							recording.setPaidAmt(feeDetail.getPaidAmount());
							recording.setWaivedAmt(feeDetail.getWaivedAmount());
						}
					}
				}
			}

			aFinanceDetail.getFinScheduleData().setVasRecordingList(vasRecordings);
		}

		if (StringUtils.isBlank(this.custCIF.getValue())) {
			aFinanceDetail.setStageAccountingList(null);
		} else {

			boolean accVerificationReq = true;
			if (isOverdraft && StringUtils.isEmpty(moduleDefiner)) {
				accVerificationReq = false;
			}

			if (StringUtils.equals(FinanceConstants.FINSER_EVENT_HOLDEMI, moduleDefiner)) {
				accVerificationReq = false;
			}

			//Finance Accounting Details Tab
			if (!recSave && "Accounting".equals(getTaskTabs(getTaskId(getRole()))) && accVerificationReq) {

				// check if accounting rules executed or not
				if (accountingDetailDialogCtrl == null || (!accountingDetailDialogCtrl.isAccountingsExecuted())) {
					// ### 10-05-2018---- PSD TCT No :124885 
					if (!ImplementationConstants.CLIENT_NFL) {
						MessageUtil.showError(Labels.getLabel("label_Finance_Calc_Accountings"));
						return;
					}
				} else {

					if (accountingDetailDialogCtrl.getDisbCrSum()
							.compareTo(accountingDetailDialogCtrl.getDisbDrSum()) != 0) {
						MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
						return;
					}
				}
			}

			//Finance Stage Accounting Details Tab
			if (!recSave && stageAccountingDetailDialogCtrl != null) {
				// check if accounting rules executed or not
				if (!stageAccountingDetailDialogCtrl.stageAccountingsExecuted) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Calc_StageAccountings"));
					return;
				}
				if (stageAccountingDetailDialogCtrl.stageDisbCrSum
						.compareTo(stageAccountingDetailDialogCtrl.stageDisbDrSum) != 0) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
					return;
				}
			} else {
				aFinanceDetail.setStageAccountingList(null);
			}
		}

		//Finance Collaterals Details validating & Saving
		if (finCollateralHeaderDialogCtrl != null) {
			BigDecimal totCost = BigDecimal.ZERO;
			boolean isFDAmount = false;
			for (FinCollaterals finCollateral : finCollateralHeaderDialogCtrl.getFinCollateralDetailsList()) {
				totCost = totCost.add(finCollateral.getValue() == null ? BigDecimal.ZERO : finCollateral.getValue());
				if (StringUtils.equals(finCollateral.getCollateralType(), PennantConstants.FIXED_DEPOSIT)) {
					isFDAmount = true;
				}
			}

			if (!finCollateralHeaderDialogCtrl.getFinCollateralDetailsList().isEmpty()
					&& totCost.compareTo(aFinanceMain.getFinAmount()) < 0 && isFDAmount) {
				Tab tab = getTab(AssetConstants.UNIQUE_ID_COLLATERAL);
				if (tab != null) {
					tab.setSelected(true);
				}
				MessageUtil.showError(Labels.getLabel("label_Collateral_FDAmount"));
				return;
			}
			aFinanceDetail.setFinanceCollaterals(finCollateralHeaderDialogCtrl.getFinCollateralDetailsList());
		} else {
			aFinanceDetail.setFinanceCollaterals(null);
		}

		//Etihad Credit Bureau Details 
		if (etihadCreditBureauDetailDialogCtrl != null) {
			etihadCreditBureauDetailDialogCtrl.doSave_EtihadCreditBureauDetail(aFinanceDetail,
					getTab(AssetConstants.UNIQUE_ID_ETIHADCB), recSave);
		}

		//Bundled Products Details 
		if (bundledProductsDetailDialogCtrl != null) {
			bundledProductsDetailDialogCtrl.doSave_BundledProductsDetail(aFinanceDetail,
					getTab(AssetConstants.UNIQUE_ID_BUNDLEDPRODUCTS), recSave);
		}

		//Agreement Fields Details 
		if (agreementFieldsDetailDialogCtrl != null) {
			agreementFieldsDetailDialogCtrl.doSave_AgreementFieldsDetail(aFinanceDetail, recSave);
		}

		//Asset Evaluation Details 
		if (finAssetEvaluationDialogCtrl != null) {
			finAssetEvaluationDialogCtrl.doSave_FinAssetEvaluation(aFinanceDetail,
					getTab(AssetConstants.UNIQUE_ID_ASSETEVALUATION), recSave);
		}

		//Mandate tab
		Tab mandateTab = getTab(AssetConstants.UNIQUE_ID_MANDATE);
		if (mandateDialogCtrl != null && mandateTab.isVisible()) {
			mandateDialogCtrl.doSave_Mandate(aFinanceDetail, mandateTab, recSave);
		}

		//PDC
		Tab pdcTab = getTab(AssetConstants.UNIQUE_ID_CHEQUE);
		if (chequeDetailDialogCtrl != null && pdcTab.isVisible()) {
			chequeDetailDialogCtrl.doSave_PDC(aFinanceDetail, getFinanceMain().getFinReference());
		}

		// Tax Detail
		Tab taxTab = getTab(AssetConstants.UNIQUE_ID_TAX);
		if (financeTaxDetailDialogCtrl != null && taxTab.isVisible()) {
			financeTaxDetailDialogCtrl.doSave_Tax(aFinanceDetail, taxTab, recSave);
		} else {
			aFinanceDetail.setFinanceTaxDetails(null);
		}

		// FI Init Verification Detail
		Tab fiInitTab = getTab(AssetConstants.UNIQUE_ID_FIINITIATION);
		if ((fiInitTab != null && fiInitTab.isVisible()) && fieldVerificationDialogCtrl != null) {
			fieldVerificationDialogCtrl.doSave(aFinanceDetail, fiInitTab, recSave, userAction);
		}

		// FI Approval Verification Detail
		Tab fiApprovalTab = getTab(AssetConstants.UNIQUE_ID_FIAPPROVAL);
		if ((fiApprovalTab != null && fiApprovalTab.isVisible()) && fieldVerificationDialogCtrl != null) {
			if (!fieldVerificationDialogCtrl.doSave(aFinanceDetail, fiApprovalTab, recSave, userAction)) {
				return;
			}

		}

		// TV Init Verification Detail
		Tab tvInitTab = getTab(AssetConstants.UNIQUE_ID_TVINITIATION);
		if ((tvInitTab != null && tvInitTab.isVisible()) && tVerificationDialogCtrl != null) {
			tVerificationDialogCtrl.doSave(aFinanceDetail, tvInitTab, recSave, userAction);
		}

		// TV Approval Verification Detail
		Tab tvApprovalTab = getTab(AssetConstants.UNIQUE_ID_TVAPPROVAL);
		if ((tvApprovalTab != null && tvApprovalTab.isVisible()) && tVerificationDialogCtrl != null) {
			if (!tVerificationDialogCtrl.doSave(aFinanceDetail, tvApprovalTab, recSave, userAction)) {
				return;
			}

		}

		// LV Init Verification Detail
		Tab lvInitTab = getTab(AssetConstants.UNIQUE_ID_LVINITIATION);
		if (lvInitTab != null && lvInitTab.isVisible() && lVerificationCtrl != null) {
			if (!lVerificationCtrl.doSave(aFinanceDetail, lvInitTab, recSave, userAction)) {
				return;
			}
		}

		// LV Approval Verification Detail
		Tab lvApprovalTab = getTab(AssetConstants.UNIQUE_ID_LVAPPROVAL);
		if (lvApprovalTab != null && lvApprovalTab.isVisible() && lVerificationCtrl != null) {
			if (!lVerificationCtrl.doSave(aFinanceDetail, lvApprovalTab, recSave, userAction)) {
				return;
			}

		}

		// RCU Init Verification Detail
		Tab rcuInitTab = getTab(AssetConstants.UNIQUE_ID_RCUINITIATION);
		if ((rcuInitTab != null && rcuInitTab.isVisible()) && rcuVerificationDialogCtrl != null) {
			rcuVerificationDialogCtrl.doSave(aFinanceDetail, rcuInitTab, recSave, userAction);
		}

		// RCU Approval Verification Detail
		Tab rcuApprovalTab = getTab(AssetConstants.UNIQUE_ID_RCUAPPROVAL);
		if ((rcuApprovalTab != null && rcuApprovalTab.isVisible()) && rcuVerificationDialogCtrl != null) {
			if (!rcuVerificationDialogCtrl.doSave(aFinanceDetail, rcuApprovalTab, recSave, userAction)) {
				return;
			}

		}
		
		
		Tab pslDetailsTab = getTab(AssetConstants.UNIQUE_ID_PSL_DETAILS);
		if ((pslDetailsTab != null && pslDetailsTab.isVisible()) && pSLDetailDialogCtrl != null) {
			pSLDetailDialogCtrl.doSave(aFinanceDetail, pslDetailsTab, recSave);
		}

		//Validation For Mandatory Recommendation
		if (!doValidateRecommendation()) {
			return;
		}

		// Sampling initiation Details
		if (samplingRequired.isChecked() && !samplingRequired.isDisabled()) {
			/*
			 * if
			 * (extendedFieldDetailsService.getLoanOrgExtendedValue(financeDetail.getFinScheduleData().getFinReference()
			 * , "CUSTREQLOANAMOUNT") == null) {
			 * MessageUtil.showError("Requested loan amount should be available in the extended fields for sampling");
			 * this.financeTypeDetailsTab.setSelected(true); return;
			 * 
			 * }
			 */

		}

		// Sampling Approval Details
		Tab samplingTab = getTab(AssetConstants.UNIQUE_ID_SAMPLINGAPPROVAL);
		if ((samplingTab != null && samplingTab.isVisible()) && finSamplingDialogCtrl != null) {
			finSamplingDialogCtrl.doSave(aFinanceDetail, samplingTab, recSave, userAction);
		}

		// save the FinanceMain Extension details
		if (StringUtils.equals(getComboboxValue(this.finRepayMethod), FinanceConstants.REPAYMTH_AUTODDA)
				&& !StringUtils.isBlank(this.repayAcctId.getValue()) && !this.repayAcctId.isReadonly()) {

			FinanceMainExt financeMainExt = new FinanceMainExt();
			financeMainExt.setFinReference(aFinanceMain.getFinReference());
			if (this.repayAcctId.getSelectedAccount() != null) {
				financeMainExt.setRepayIBAN(this.repayAcctId.getSelectedAccount().getIban());
			}
			financeMainExt.setIfscCode(this.ifscCode.getValue());
			getFinanceMainExtService().saveFinanceMainExtDetails(financeMainExt);
		}

		// validating DDA mobile number when repay method is Auto from DDA
		if (!recSave && StringUtils.equals(getComboboxValue(this.finRepayMethod), FinanceConstants.REPAYMTH_AUTODDA)) {
			Customer customer = aFinanceDetail.getCustomerDetails().getCustomer();
			if (!validateDDAMobileNumber(customer.getPhoneNumber())) {
				custDetailTab.focus();
				MessageUtil.showError(Labels.getLabel("DDA_MOB_VALIDATION"));
				return;
			}
		}

		if (!recSave && StringUtils.isEmpty(moduleDefiner)) {
			try {

				String curLoginUser = getUserWorkspace().getUserDetails().getSecurityUser().getUsrLogin();
				// Customer Dedup Process Check
				boolean processCompleted = dedupValidation.doCheckDedup(aFinanceDetail,
						aFinanceDetail.getFinScheduleData().getFinanceMain().getFinReference(), getRole(),
						getMainWindow(), curLoginUser);
				if (!processCompleted) {
					return;
				} else {
					if (aFinanceDetail.getCustomerDedupList() != null
							&& !aFinanceDetail.getCustomerDedupList().isEmpty()) {
						CustomerDedup dedup = aFinanceDetail.getCustomerDedupList().get(0);
						if (dedup != null) {
							aFinanceDetail.getCustomerDetails().getCustomer().setCustCoreBank(dedup.getCustCoreBank());
						}
						logger.debug("Posidex Id:" + dedup.getCustCoreBank());
					}
				}

				//in case of no match found from posidex the same message has to be shown for the user
				if (aFinanceDetail.getCustomerDetails().getReturnStatus() != null
						&& aFinanceDetail.getCustomerDetails().getReturnStatus().getReturnText() != null
						&& StringUtils.equalsIgnoreCase(
								aFinanceDetail.getCustomerDetails().getReturnStatus().getReturnText(), "No Match")) {
					MessageUtil.showMessage(Labels.getLabel("Label_Dedupe_NoMatch"));
				}

				// Limits Checking with ACP Interface
				processCompleted = doLimitCheckProcess(getRole(), aFinanceDetail);
				if (!processCompleted) {
					return;
				}

			} catch (Exception ex) {
				MessageUtil.showError(ex);
				return;
			}
		}
		
		if (StringUtils.isEmpty(moduleDefiner)) {
			deviationExecutionCtrl.checkCustomDeviations(getFinanceDetail());
		}

		if (ImplementationConstants.ALLOW_DEVIATIONS) {
			if (StringUtils.isEmpty(moduleDefiner) && deviationDetailDialogCtrl != null) {
				//### 01-05-2018 - Start - story #361(tuleap server) Manual Deviations
				// Check whether user has taken decision on the manual deviations for which he has the authority.
				if (this.userAction.getSelectedItem() != null
						&& !"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {
					List<FinanceDeviations> manualDeviations = getDeviationDetailDialogCtrl().getManualDeviationList();
					List<FinanceDeviations> Autodeviations = getDeviationDetailDialogCtrl().getFinanceDetail()
							.getFinanceDeviations();
					List<FinanceDeviations> deviations = new ArrayList<>();
					deviations.addAll(Autodeviations);
					deviations.addAll(manualDeviations);
					List<String> pendingDecisions = new ArrayList<>();

					for (FinanceDeviations deviation : deviations) {
						if (getUserWorkspace().getUserRoles().contains(deviation.getDelegationRole())
								&& (StringUtils.equals(PennantConstants.List_Select, deviation.getApprovalStatus())
										|| StringUtils.isBlank(deviation.getApprovalStatus()))) {
							pendingDecisions.add(StringUtils.equals(DeviationConstants.CAT_MANUAL, deviation.getDeviationCategory()) ? deviation.getDeviationCodeDesc()
									: deviation.getModule() + " - " + deviation.getDeviationCode());
						}
					}

					if (pendingDecisions.size() > 0) {
						String errorMessage = "Please mark your decision for the below deviations.";
						for (String deviation : pendingDecisions) {
							errorMessage = errorMessage.concat("\n - " + deviation);
						}

						MessageUtil.showError(errorMessage);
						return;
					}
				}
				// ### 01-05-2018 - End

				if (!processDeviations(aFinanceDetail, recSave)) {
					return;
				}
			} else {
				aFinanceDetail.setFinanceDeviations(null);
			}
		} else {
			aFinanceDetail.setFinanceDeviations(null);
		}

		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

		String tranType = "";
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isEmpty(aFinanceMain.getRecordType())) {
				aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
				if (isNew) {
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceMain.setNewRecord(true);
				}
			}

		} else {
			aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
			if (doProcess(aFinanceDetail, tranType)) {

				if (autoDownloadMap != null && !autoDownloadMap.isEmpty()) {
					List<DocumentDetails> downLoaddocLst = autoDownloadMap.get("autoDownLoadDocs");
					if (downLoaddocLst != null && downLoaddocLst.size() > 0) {
						for (DocumentDetails ldocDetails : downLoaddocLst) {
							if (PennantConstants.DOC_TYPE_PDF.equals(ldocDetails.getDocName())) {
								Filedownload.save(new AMedia(ldocDetails.getDocName(), "pdf", "application/pdf",
										ldocDetails.getDocImage()));

							} else {
								Filedownload.save(new AMedia(ldocDetails.getDocName(), "msword", "application/msword",
										ldocDetails.getDocImage()));
							}

						}
					}
					downLoaddocLst = null;
				}

				autoDownloadMap = null;

				// Mail Alert Notification for Customer/Dealer/Provider...etc
				if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {
					// notification should not stop the process. why because
					// tranaction already commited.
					try {
						processNotifications(aFinanceDetail, aFinanceMain);
					} catch (Exception e) {
						logger.debug(e);
					}
				}

				// User Notifications Message/Alert
				try {
					if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !"Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							&& !this.userAction.getSelectedItem().getLabel().contains("Reject")) {
						String reference = aFinanceMain.getFinReference();

						// Send message Notification to Users
						if (aFinanceMain.getNextUserId() != null) {
							String usrLogins = aFinanceMain.getNextUserId();
							List<String> usrLoginList = new ArrayList<String>();

							if (usrLogins.contains(",")) {
								String[] to = usrLogins.split(",");
								for (String roleCode : to) {
									usrLoginList.add(roleCode);
								}
							} else {
								usrLoginList.add(usrLogins);
							}

							List<String> userLogins = getFinanceDetailService().getUsersLoginList(usrLoginList);

							String[] to = new String[userLogins.size()];
							for (int i = 0; i < userLogins.size(); i++) {
								to[i] = String.valueOf(userLogins.get(i));
							}

							if (StringUtils.isNotEmpty(reference)) {
								if (!PennantConstants.RCD_STATUS_CANCELLED
										.equalsIgnoreCase(aFinanceMain.getRecordStatus())) {
									getEventManager().publish(Labels.getLabel("REC_PENDING_MESSAGE") + " with Reference"
											+ ":" + reference, Notify.USER, to);
								}
							} else {
								getEventManager().publish(Labels.getLabel("REC_PENDING_MESSAGE"), Notify.USER, to);
							}
						} else {
							if (StringUtils.isNotEmpty(aFinanceMain.getNextRoleCode())) {
								if (!PennantConstants.RCD_STATUS_CANCELLED.equals(aFinanceMain.getRecordStatus())) {
									String[] to = aFinanceMain.getNextRoleCode().split(",");
									String message;

									if (StringUtils.isBlank(aFinanceMain.getNextTaskId())) {
										message = Labels.getLabel("REC_FINALIZED_MESSAGE");
									} else {
										message = Labels.getLabel("REC_PENDING_MESSAGE");
									}
									message += " with Reference" + ":" + reference;

									getEventManager().publish(message, to, finDivision, aFinanceMain.getFinBranch());
								}
							}
						}
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}

				// For Finance Origination
				if (getFinanceMainListCtrl() != null) {
					refreshList();
				}

				// For Finance Maintenance
				if (getFinanceSelectCtrl() != null) {
					refreshMaintainList();
				}

				// User Notification for Role Identification
				if (StringUtils.isBlank(aFinanceMain.getNextTaskId())) {
					aFinanceMain.setNextRoleCode("");
				}

				if (aFinanceMain.isDeviationApproval()) {
					String msg = Labels.getLabel("SENT_DELEGATION_APPROVALS",
							new String[] { aFinanceMain.getFinReference() });
					Clients.showNotification(msg, "info", null, null, -1);
				} else {
					String msg = PennantApplicationUtil.getSavingStatus(aFinanceMain.getRoleCode(),
							aFinanceMain.getNextRoleCode(), aFinanceMain.getFinReference(), " Loan ",
							aFinanceMain.getRecordStatus(), getNextUserId());
					Clients.showNotification(msg, "info", null, null, -1);
				}
				if (extendedFieldRenderList != null && financeDetail.getExtendedFieldHeader() != null) {
					extendedFieldCtrl.deAllocateAuthorities();
				}
				closeDialog();
				
				//Download Legal MODT Document
				downLoadLegalDocument(aFinanceMain);
				
				if (listWindowTab != null) {
					listWindowTab.setSelected(true);
				}
			}

		} catch (DataAccessException | InterfaceException e) {
			MessageUtil.showError(e);
		}

		if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {

			FinanceReferenceDetail financeRefDetail = new FinanceReferenceDetail();
			financeRefDetail.setMandInputInStage(getRole() + ",");
			financeRefDetail.setFinType(aFinanceMain.getFinType());
			List<FinanceReferenceDetail> queryCodeList = getDedupParmService().getQueryCodeList(financeRefDetail,
					"_TATView");

			if (queryCodeList != null && !queryCodeList.isEmpty()) {
				TATDetail tatDetail = new TATDetail();

				tatDetail.setNewrecord(false);
				tatDetail.settATEndTime(new Timestamp(System.currentTimeMillis()));
				tatDetail.setReference(aFinanceMain.getFinReference());
				if (aFinanceMain.getRoleCode().equals(null)) {
					tatDetail.setRoleCode(aFinanceMain.getNextRoleCode());
				} else {
					tatDetail.setRoleCode(aFinanceMain.getRoleCode());
				}
				TATDetail dataExist = getFinanceDetailService().getTATDetail(aFinanceMain.getFinReference(),
						tatDetail.getRoleCode());
				if (dataExist != null) {
					getFinanceDetailService().updateTATDetail(tatDetail);
				}
			}
		}

		logger.debug("Leaving");
	}

	private void processNotifications(FinanceDetail aFinanceDetail, FinanceMain aFinanceMain)
			throws IOException, TemplateException {
		List<String> templateTyeList = new ArrayList<String>();
		templateTyeList.add(NotificationConstants.TEMPLATE_FOR_AE);
		templateTyeList.add(NotificationConstants.TEMPLATE_FOR_CN);
		templateTyeList.add(NotificationConstants.TEMPLATE_FOR_SP);

		String finType = aFinanceMain.getFinType();
		String finEvent = StringUtils.isEmpty(moduleDefiner) ? FinanceConstants.FINSER_EVENT_ORG : moduleDefiner;
		List<ValueLabel> referenceIdList = getFinanceReferenceDetailService().getTemplateIdList(finType, finEvent,
				getRole(), templateTyeList);

		templateTyeList = null;
		if (!referenceIdList.isEmpty()) {

			FinanceMain financeMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
			CustomerDetails customerDetails = aFinanceDetail.getCustomerDetails();
			String finReference = financeMain.getFinReference();
			List<CustomerEMail> customerEMailList = customerDetails.getCustomerEMailList();
			List<CustomerPhoneNumber> customerPhoneNumList = customerDetails.getCustomerPhoneNumList();
			List<DocumentDetails> docDetailsList = aFinanceDetail.getDocumentDetailsList();

			VehicleDealer vehicleDealer = null;
			boolean isCustomerNotificationExists = false;
			boolean isSourcingPartnerNotificationExists = false;
			List<Long> notifyIdlist = new ArrayList<Long>();

			for (ValueLabel valueLabel : referenceIdList) {
				notifyIdlist.add(Long.valueOf(valueLabel.getValue()));
				if (NotificationConstants.TEMPLATE_FOR_CN.equals(valueLabel.getLabel())) {
					isCustomerNotificationExists = true;
				} else if (NotificationConstants.TEMPLATE_FOR_SP.equals(valueLabel.getLabel())) {
					isSourcingPartnerNotificationExists = true;
				}
			}

			// Mail ID details preparation
			Map<String, List<String>> mailIDMap = new HashMap<String, List<String>>();
			//Customer mobile numbers logic start
			Map<String, List<String>> mobileNoMap = new HashMap<String, List<String>>();

			// Customer Email Preparation
			if (isCustomerNotificationExists) {

				if (customerEMailList != null && !customerEMailList.isEmpty()) {
					List<String> mailIdList = mailIDMap.get(NotificationConstants.TEMPLATE_FOR_CN);
					if (mailIdList == null) {
						mailIdList = new ArrayList<String>();
						mailIDMap.put(NotificationConstants.TEMPLATE_FOR_CN, mailIdList);
					}

					for (CustomerEMail customerEMail : customerEMailList) {
						mailIDMap.get(NotificationConstants.TEMPLATE_FOR_CN).add(customerEMail.getCustEMail());
					}
				}
				if (customerPhoneNumList != null && !customerPhoneNumList.isEmpty()) {
					List<String> custPhoneNoList = new ArrayList<String>();
					for (CustomerPhoneNumber customerPhoneNumber : customerPhoneNumList) {
						custPhoneNoList.add(customerPhoneNumber.getPhoneNumber());
					}
					if (!custPhoneNoList.isEmpty()) {
						mobileNoMap.put(NotificationConstants.TEMPLATE_FOR_CN, custPhoneNoList);
					}
				}
			}

			// vehicleDealer Email Preparation
			if (isSourcingPartnerNotificationExists) {
				List<String> mailIdList = new ArrayList<String>();
				List<String> dealerPhoneNoList = new ArrayList<String>();
				long vehicleDealerid = getFinanceDetail().getCustomerDetails().getCustomer().getCustRO1();
				vehicleDealer = getVehicleDealerService().getApprovedVehicleDealerById(vehicleDealerid);
				if (vehicleDealer != null) {
					mailIdList.add(StringUtils.trimToEmpty(vehicleDealer.getEmail()));
					dealerPhoneNoList.add(StringUtils.trimToEmpty(vehicleDealer.getDealerTelephone()));
				}
				if (!mailIdList.isEmpty()) {
					mailIDMap.put(NotificationConstants.TEMPLATE_FOR_SP, mailIdList);
				}

				if (!dealerPhoneNoList.isEmpty()) {
					mobileNoMap.put(NotificationConstants.TEMPLATE_FOR_SP, dealerPhoneNoList);
				}

			}

			HashMap<String, Object> fieldsAndValues = getPreparedMailData(aFinanceDetail, vehicleDealer);

			if (isExtMailService()) {
				try {
					List<MailTemplate> templates = getMailUtil().getMailDetails(notifyIdlist, fieldsAndValues,
							docDetailsList, mailIDMap);
					// send mail to external service
					getMailTemplateService().sendMail(templates, finReference);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}

			} else {
				try {
					getMailUtil().sendMail(notifyIdlist, fieldsAndValues, docDetailsList, mailIDMap, null);
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}

			}

			// Customer mobile numbers logic end						
			if (isExtSMSService()) {
				List<MailTemplate> smsList = getSmsUtil().getSMSContent(notifyIdlist, fieldsAndValues, mobileNoMap);
				// send SMS to external service
				getShortMessageService().sendMessage(smsList, finReference);
			}
		}
	}

	public HashMap<String, Object> getPreparedMailData(FinanceDetail aFinanceDetail, VehicleDealer vehicleDealer) {
		logger.debug("Entering");

		FinanceMain main = aFinanceDetail.getFinScheduleData().getFinanceMain();
		Customer customer = aFinanceDetail.getCustomerDetails().getCustomer();
		// Role Code For Alert Notification
		main.setNextRoleCodeDesc(PennantApplicationUtil.getSecRoleCodeDesc(main.getRoleCode()));

		// user Details
		main.setSecUsrFullName(PennantApplicationUtil.getUserDesc(main.getLastMntBy()));
		main.setWorkFlowType(PennantApplicationUtil.getWorkFlowType(main.getWorkflowId()));
		main.setFinPurpose(main.getLovDescFinPurposeName());
		main.setFinBranch(main.getLovDescFinBranchName());

		logger.debug("Leaving");

		HashMap<String, Object> declaredFieldValues = main.getDeclaredFieldValues();
		declaredFieldValues.put("fm_recordStatus", main.getRecordStatus());
		declaredFieldValues.putAll(customer.getDeclaredFieldValues());
		if (vehicleDealer != null) {
			declaredFieldValues.putAll(vehicleDealer.getDeclaredFieldValues());
		}

		return declaredFieldValues;
	}

	/*
	 * Downloading the legal MODT document
	 */
	private void downLoadLegalDocument(FinanceMain aFinanceMain) {
		logger.debug(Literal.ENTERING);
		try {
			if (PennantConstants.NO.equals(SysParamUtil.getValueAsString("ESFB_LEGAL_DETAIL_DOCUMENT_DOWNLOAD"))) {
				return;
			}
			if (!aFinanceMain.isLegalRequired()) {
				return;
			}
			String rolesList = SysParamUtil.getValueAsString("ESFB_LEGAL_DETAIL_MODT_RMDT_DOC_ROLES");
			if (StringUtils.isEmpty(rolesList)) {
				return;
			}
			boolean genarateDoc = false;
			String[] roles = rolesList.split(",");
			for (String role : roles) {
				if (StringUtils.equalsIgnoreCase(role, aFinanceMain.getNextRoleCode())) {
					genarateDoc = true;
					break;
				}
			}
			if (!genarateDoc) {
				return;
			}
			
			aFinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());
			List<LegalDetail> legalDEtailsLIst = getLegalDetailService().getApprovedLegalDetail(aFinanceMain);
			if (CollectionUtils.isNotEmpty(legalDEtailsLIst)) {
				List<DocumentDetails> documentsList =  new ArrayList<>();
				for (LegalDetail legalDetail : legalDEtailsLIst) {
					String template = "";
					if (legalDetail.isModtDoc()) {
						template = "Legal/MODT Draft";
					} else {
						template = "Legal/RMDT Draft";
					}
					String templateName = template.concat(PennantConstants.DOC_TYPE_WORD_EXT);
					String fileName = template.concat(PennantConstants.DOC_TYPE_PDF_EXT);
					AgreementEngine engine = new AgreementEngine("");
					engine.setTemplate(templateName);
					engine.loadTemplate();
					engine.mergeFields(legalDetail);
					Window window = new Window();
					if (getFinanceMainListCtrl() != null) {
						window = getFinanceMainListCtrl().window_FinanceMainList;
					}
					engine.showDocument(window, fileName, SaveFormat.PDF);
					// Will save the data in one table for another menu option
					// download
					legalDetail.setDocImage(engine.getDocumentInByteArray(
							template.concat(PennantConstants.DOC_TYPE_PDF_EXT), SaveFormat.PDF));

					DocumentDetails details = new DocumentDetails();
					details.setDocModule(FinanceConstants.MODULE_NAME);
					details.setDocCategory("LEG003");
					details.setDoctype(PennantConstants.DOC_TYPE_PDF);
					details.setDocName(fileName);
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					details.setFinEvent(FinanceConstants.FINSER_EVENT_ORG);
					details.setDocImage(legalDetail.getDocImage());
					details.setReferenceId(legalDetail.getLoanReference());
					documentsList.add(details);
					engine.close();
				}
				getLegalDetailService().saveDocumentDetails(documentsList);
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private boolean primaryValidations() {
		logger.debug("Entering");
		if (!recSave && !this.finStartDate.isReadonly() && !isFirstTask()
				&& StringUtils.equals(finDivision, FinanceConstants.FIN_DIVISION_RETAIL)
				&& !StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole())) {
			// validate finance start date and application date
			int maxAwdFinDays = SysParamUtil.getValueAsInt("DAYS_BET_APP_START");
			if (DateUtility.getDaysBetween(this.finStartDate.getValue(), appDate) > maxAwdFinDays) {
				String msg = Labels.getLabel("label_StartDate_Validation",
						new String[] { Labels.getLabel("label_FinanceMainDialog_FinStartDate.value"),
								DateUtility.formatToShortDate(DateUtility.addDays(appDate, -maxAwdFinDays)),
								DateUtility.formatToShortDate(DateUtility.addDays(appDate, maxAwdFinDays)) });
				if (MessageUtil.confirm(msg) == MessageUtil.YES) {
					return false;
				}
			}

			if (!isReadOnly("FinanceMainDialog_validateFinProcessDays")) {
				int maxDaystoProcessFin = SysParamUtil.getValueAsInt("MAX_ALLOWEDDAYS_TO_PROCESS_FINANCE");
				if (DateUtility.getDaysBetween(
						getFinanceDetail().getFinScheduleData().getFinanceMain().getInitiateDate(),
						DateUtility.getSysDate()) > maxDaystoProcessFin) {
					String msg = Labels.getLabel("label_MaxFinanceProcessDays_Validation",
							new String[] { String.valueOf(maxDaystoProcessFin),
									Labels.getLabel("label_FinanceMainDialog_FinStartDate.value") });
					if (MessageUtil.confirm(msg) == MessageUtil.YES) {
						return false;
					}
				}
			}
		}
		logger.debug("Leaving");
		return true;
	}

	private void addCasDocument(FinanceDetail aFinanceDetail) throws Exception {
		logger.debug("Entering");
		HashMap<String, Long> docNames = new HashMap<>();
		DocumentDetails details = new DocumentDetails();

		for (FinanceReferenceDetail financeReferenceDetail : getFinanceDetail().getAggrementList()) {
			if (StringUtils.equals(financeReferenceDetail.getLovDescCodelov(), PennantConstants.CASDOC)) {

				AgreementEngine engine = new AgreementEngine(
						getFinanceDetail().getFinScheduleData().getFinanceMain().getFinPurpose());
				engine.setTemplate("CreditAssessmentSheet" + PennantConstants.DOC_TYPE_WORD_EXT);
				engine.loadTemplate();
				engine.mergeFields(agreementGeneration.getAggrementData(getFinanceDetail(),
						financeReferenceDetail.getLovDescAggImage(), getUserWorkspace().getUserDetails()));

				details.setDocModule(FinanceConstants.MODULE_NAME);
				details.setDocCategory("CRASSMNT");
				details.setReferenceId(this.finReference.getValue());
				details.setDoctype(PennantConstants.DOC_TYPE_PDF);
				details.setDocName(PennantConstants.CASDOC + PennantConstants.DOC_TYPE_PDF_EXT);
				details.setDocImage(engine.getDocumentInByteArray(
						details.getDocName() + PennantConstants.DOC_TYPE_PDF_EXT, SaveFormat.PDF));
				details.setVersion(1);
				details.setFinEvent(FinanceConstants.FINSER_EVENT_ORG);
				details.setNewRecord(true);
				details.setLastMntOn(DateUtility.getTimestamp(DateUtility.getAppDate()));
				details.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				details.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

				for (DocumentDetails docDetails : documentDetailDialogCtrl.getDocumentDetailsList()) {
					docNames.put(docDetails.getDocCategory(), docDetails.getDocId());
				}

				if (!docNames.keySet().contains("CRASSMNT")) {
					documentDetailDialogCtrl.getDocumentDetailsList().add(details);
				}

			}

		}

		logger.debug("Leaving");
	}

	private boolean isCovenantDocumentExist(FinanceDetail aFinanceDetail, String docType) {
		logger.debug("Entering");

		if (aFinanceDetail.getDocumentDetailsList() != null && !aFinanceDetail.getDocumentDetailsList().isEmpty()) {
			for (DocumentDetails docDetails : aFinanceDetail.getDocumentDetailsList()) {
				if (StringUtils.equals(docType, docDetails.getDocCategory())) {
					if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, docDetails.getRecordType())) {
						continue;
					}
					return true;
				}
			}
		}
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Area code of a mobile number should be "+971" when repay method is DDA
	 * 
	 * @param phoneNumber
	 */
	private boolean validateDDAMobileNumber(String phoneNumber) {
		String[] mobileNum = PennantApplicationUtil.unFormatPhoneNumber(phoneNumber);
		if (mobileNum != null && mobileNum.length > 0) {
			mobileNum[0] = "+" + mobileNum[0];
			if (!StringUtils.equals(mobileNum[0], "+971")) {
				return false;
			}
		}
		return true;
	}

	// WorkFlow Creations

	private boolean processDeviations(FinanceDetail aFinanceDetail, boolean recordSave) {
		if (deviationExecutionCtrl != null) {
			aFinanceDetail.setFinanceDeviations(deviationExecutionCtrl.getFinanceDeviations());
		}
		if (getDeviationDetailDialogCtrl() != null) {
			List<FinanceDeviations> list = getDeviationDetailDialogCtrl().getManualDeviationList();
			aFinanceDetail.setManualDeviations(list);
		}
		if ((aFinanceDetail.getFinanceDeviations() != null && !aFinanceDetail.getFinanceDeviations().isEmpty())
				|| aFinanceDetail.getManualDeviations() != null && !aFinanceDetail.getManualDeviations().isEmpty()) {
			try {
				//show pop up to take confirmation and stop if any un allowed deviation
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("roleCode", getRole());
				map.put("do", this);
				map.put("financeDetail", aFinanceDetail);
				map.put("enquiry", "");
				map.put("finHeaderList", getFinBasicDetails());
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DeviationDetailDialog.zul",
						getMainWindow(), map);
				Executions.getCurrent().setAttribute("devationConfirm", false);
				Executions.wait(aFinanceDetail.getFinScheduleData().getFinanceMain().getFinReference());
				Boolean response = (Boolean) Executions.getCurrent().getAttribute("devationConfirm");
				return response;

			} catch (Exception e) {
				logger.debug(e);
				return false;
			}
		}
		return true;
	}

	/**
	 * Method for checking Customer limits
	 * 
	 * @param role
	 * @param aFinanceDetail
	 * @return
	 * @throws InterfaceException
	 * @throws InterruptedException
	 */
	private boolean doLimitCheckProcess(String role, FinanceDetail aFinanceDetail)
			throws InterfaceException, InterruptedException {
		logger.debug("Entering");

		if (!ImplementationConstants.LIMIT_INTERNAL) {
			// Checking for Limit check Authority i.e Is current Role contains limit check authority (or) Not
			List<FinanceReferenceDetail> limitCheckList = getLimitCheckDetails().doLimitChek(role,
					aFinanceDetail.getFinScheduleData().getFinanceType().getFinType());
			if (limitCheckList == null || limitCheckList.isEmpty()) {
				return true;
			}

			boolean ispreDealCheck = false;

			for (FinanceReferenceDetail finRefDetail : limitCheckList) {
				if (StringUtils.equals(finRefDetail.getLovDescNamelov(), FinanceConstants.PRECHECK)) {
					ispreDealCheck = true;
				}
			}

			if (ispreDealCheck) {
				return getLimitCheckDetails().limitServiceProcess(aFinanceDetail);
			}
		}

		logger.debug("Leaving");
		return true;
	}

	protected String getServiceTasks(String taskId, FinanceMain financeMain, String finishedTasks) {
		logger.debug("Entering");
		// changes regarding parallel work flow 
		String nextRoleCode = StringUtils.trimToEmpty(financeMain.getNextRoleCode());
		String nextRoleCodes[] = nextRoleCode.split(",");

		if (nextRoleCodes.length > 1) {
			return "";
		}

		String serviceTasks = getServiceOperations(taskId, financeMain);
		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	protected void setNextTaskDetails(String taskId, FinanceMain financeMain) {
		logger.debug("Entering");

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(financeMain.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if ("Resubmit".equals(action)) {
				nextTaskId = "";
			} else if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, financeMain);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";
		String nextRole = "";
		Map<String, String> baseRoleMap = null;

		if ("".equals(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				baseRoleMap = new HashMap<String, String>(nextTasks.length);
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRole = getTaskOwner(nextTasks[i]);
					nextRoleCode += nextRole;
					String baseRole = "";
					if (!"Resubmit".equals(action)) {
						baseRole = StringUtils.trimToEmpty(getTaskBaseRole(nextTasks[i]));
					}
					baseRoleMap.put(nextRole, baseRole);
				}
			}
		}

		financeMain.setTaskId(taskId);
		financeMain.setNextTaskId(nextTaskId);
		financeMain.setRoleCode(getRole());
		financeMain.setNextRoleCode(nextRoleCode);

		financeMain.setLovDescAssignMthd(StringUtils.trimToEmpty(getTaskAssignmentMethod(taskId)));
		financeMain.setLovDescBaseRoleCodeMap(baseRoleMap);
		baseRoleMap = null;

		if (!nextRoleCode.contains(getRole())) {
			financeMain.setPriority(0);
			if (StringUtils.isBlank(financeMain.getLovDescAssignMthd())) {
				financeMain.setNextUserId(null);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 * @throws Exception
	 */
	private boolean doProcess(FinanceDetail aFinanceDetail, String tranType) throws Exception, InterfaceException {
		logger.debug("Entering");

		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		afinanceMain.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());
		aFinanceDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		// Setting workflow details to Finance Flags
		List<FinFlagsDetail> flagList = aFinanceDetail.getFinFlagsDetails();
		if (flagList != null && !flagList.isEmpty()) {
			for (int i = 0; i < flagList.size(); i++) {
				FinFlagsDetail finFlagsDetail = flagList.get(i);
				finFlagsDetail.setReference(afinanceMain.getFinReference());
				finFlagsDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				finFlagsDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				finFlagsDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
				finFlagsDetail.setRecordStatus(afinanceMain.getRecordStatus());
				finFlagsDetail.setWorkflowId(afinanceMain.getWorkflowId());

			}
		}
		//Finance Asset Type
		if (aFinanceDetail.getFinAssetTypesList() != null && !aFinanceDetail.getFinAssetTypesList().isEmpty()) {
			for (FinAssetTypes finAssetTypes : aFinanceDetail.getFinAssetTypesList()) {
				finAssetTypes.setReference(afinanceMain.getFinReference());
				finAssetTypes.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				finAssetTypes.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				finAssetTypes.setUserDetails(getUserWorkspace().getLoggedInUser());
				finAssetTypes.setRecordStatus(afinanceMain.getRecordStatus());
				finAssetTypes.setWorkflowId(afinanceMain.getWorkflowId());
				finAssetTypes.setTaskId(taskId);
				finAssetTypes.setNextTaskId(nextTaskId);
				finAssetTypes.setRoleCode(getRole());
				finAssetTypes.setNextRoleCode(nextRoleCode);
				if (PennantConstants.RECORD_TYPE_DEL.equals(afinanceMain.getRecordType())) {
					if (StringUtils.trimToNull(finAssetTypes.getRecordType()) == null) {
						finAssetTypes.setRecordType(afinanceMain.getRecordType());
						finAssetTypes.setNewRecord(true);
					}
				}
			}
		}

		if (aFinanceDetail.getExtendedFieldRenderList() != null
				&& !aFinanceDetail.getExtendedFieldRenderList().isEmpty()) {
			for (ExtendedFieldRender extendedFieldDetail : aFinanceDetail.getExtendedFieldRenderList()) {
				extendedFieldDetail.setReference(afinanceMain.getFinReference());
				extendedFieldDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				extendedFieldDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				extendedFieldDetail.setRecordStatus(afinanceMain.getRecordStatus());
				extendedFieldDetail.setWorkflowId(afinanceMain.getWorkflowId());
				extendedFieldDetail.setTaskId(taskId);
				extendedFieldDetail.setNextTaskId(nextTaskId);
				extendedFieldDetail.setRoleCode(getRole());
				extendedFieldDetail.setNextRoleCode(nextRoleCode);
				if (PennantConstants.RECORD_TYPE_DEL.equals(afinanceMain.getRecordType())) {
					if (StringUtils.trimToNull(extendedFieldDetail.getRecordType()) == null) {
						extendedFieldDetail.setRecordType(afinanceMain.getRecordType());
						extendedFieldDetail.setNewRecord(true);
					}
				}
			}
		}
		// Cheque Details
		if (aFinanceDetail.getChequeHeader() != null) {
			ChequeHeader chequeHeader = aFinanceDetail.getChequeHeader();
			chequeHeader.setFinReference(afinanceMain.getFinReference());
			chequeHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			chequeHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			chequeHeader.setRecordStatus(afinanceMain.getRecordStatus());
			chequeHeader.setWorkflowId(afinanceMain.getWorkflowId());
			chequeHeader.setTaskId(taskId);
			chequeHeader.setNextTaskId(nextTaskId);
			chequeHeader.setRoleCode(getRole());
			chequeHeader.setNextRoleCode(nextRoleCode);
			if (PennantConstants.RECORD_TYPE_DEL.equals(afinanceMain.getRecordType())) {
				if (StringUtils.trimToNull(chequeHeader.getRecordType()) == null) {
					chequeHeader.setRecordType(afinanceMain.getRecordType());
					chequeHeader.setNewRecord(true);
				}
			}

			List<ChequeDetail> chequeDetails = chequeHeader.getChequeDetailList();
			if (chequeDetails != null && !chequeDetails.isEmpty()) {
				for (ChequeDetail chequeDetail : chequeDetails) {
					chequeDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					chequeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					chequeDetail.setWorkflowId(afinanceMain.getWorkflowId());
				}
			}
		}

		// Extended Field details
		if (aFinanceDetail.getExtendedFieldRender() != null) {
			int seqNo = 0;
			ExtendedFieldRender details = aFinanceDetail.getExtendedFieldRender();
			details.setReference(afinanceMain.getFinReference());
			details.setSeqNo(++seqNo);
			details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			details.setRecordStatus(afinanceMain.getRecordStatus());
			details.setRecordType(afinanceMain.getRecordType());
			details.setVersion(afinanceMain.getVersion());
			details.setWorkflowId(afinanceMain.getWorkflowId());
			details.setTaskId(taskId);
			details.setNextTaskId(nextTaskId);
			details.setRoleCode(getRole());
			details.setNextRoleCode(nextRoleCode);
			details.setNewRecord(aFinanceDetail.isNewRecord());
			if (PennantConstants.RECORD_TYPE_DEL.equals(afinanceMain.getRecordType())) {
				if (StringUtils.trimToNull(details.getRecordType()) == null) {
					details.setRecordType(afinanceMain.getRecordType());
					details.setNewRecord(true);
				}
			}
		}
		//story #491  
		//Auto Generation of Loan Agreements while submitting
		//before submitting loan generate AGREEMENTS...
	
		if ("Submit".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {
			List<DocumentDetails> agenDocList = new ArrayList<DocumentDetails>();
			DocumentDetails documentDetails = null;
			autoDownloadMap = new HashMap();
			AgreementDefinition agreementDefinition = null;
			List<DocumentDetails> autoDownloadLst = new ArrayList<DocumentDetails>();
			for (FinanceReferenceDetail financeReferenceDetail : financeDetail.getAggrementList()) {
				long id = financeReferenceDetail.getFinRefId();
				agreementDefinition = getAgreementDefinitionService().getAgreementDefinitionById(id);
				// For Agreement Rules
				boolean isAgrRender = true;
				// Check Each Agreement is attached with Rule or Not, If Rule
				// Exists based on Rule Result Agreement will display
				if (StringUtils.isNotBlank(financeReferenceDetail.getLovDescAggRuleName())) {
					Rule rule = getRuleService().getApprovedRuleById(financeReferenceDetail.getLovDescAggRuleName(),
							RuleConstants.MODULE_AGRRULE, RuleConstants.EVENT_AGRRULE);
					if (rule != null) {
						HashMap<String, Object> fieldsAndValues = getFinanceDetail().getCustomerEligibilityCheck()
								.getDeclaredFieldValues();
						isAgrRender = (boolean) getRuleExecutionUtil().executeRule(rule.getSQLRule(), fieldsAndValues,
								getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy(),
								RuleReturnType.BOOLEAN);
					}
				}
				if (isAgrRender) {
					if (agreementDefinition.isAutoGeneration()) {
						try {
							documentDetails = autoGenerateAgreement(financeReferenceDetail, aFinanceDetail,
									agreementDefinition);
							agenDocList.add(documentDetails);
							if (agreementDefinition.isAutoDownload()) {
								autoDownloadLst.add(documentDetails);
							}
						} catch (Exception e) {
							MessageUtil.showError(e.getMessage());
						}
					}
				}
			}
			agenDocList.addAll(aFinanceDetail.getDocumentDetailsList());
			aFinanceDetail.setDocumentDetailsList(agenDocList);
			autoDownloadMap.put("autoDownLoadDocs", autoDownloadLst);
		}
		
		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			afinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			if (isNotesMandatory(taskId, afinanceMain)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}
			auditHeader = getAuditHeader(aFinanceDetail, PennantConstants.TRAN_WF);
			doProcess_Assets(aFinanceDetail);
			auditHeader.getAuditDetail().setModelData(aFinanceDetail);
			processCompleted = doSaveProcess(auditHeader, null);
		} else {
			doProcess_Assets(aFinanceDetail);
			auditHeader = getAuditHeader(aFinanceDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Method for Saving Details Record
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 * @throws InterruptedException
	 */
	
	private boolean doSaveProcess(AuditHeader auditHeader, String method)
			throws InterfaceException, InterruptedException {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinanceDetail afinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();

		try {

			if (afinanceMain.getMaturityDate() != null && afinanceMain.getMaturityDate().compareTo(appEndDate) > 0) {
				auditHeader.setErrorDetails(
						new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("Label_Exceed"), null));
				ErrorControl.showErrorControl(getMainWindow(), auditHeader);
				return processCompleted;
			}

			while (retValue == PennantConstants.porcessOVERIDE) {
				String usrAction = this.userAction.getSelectedItem().getLabel();
				WorkflowEngine engine = getWorkFlow();

				// Execute service tasks
				auditHeader = getFinanceDetailService().executeWorkflowServiceTasks(auditHeader, getRole(), usrAction,
						engine);

				auditHeader = ErrorControl.showErrorDetails(getMainWindow(), auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.financeDetail.getFinScheduleData().getFinanceMain()), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
			setOverideMap(auditHeader.getOverideMap());
			setNextUserId(((FinanceDetail) auditHeader.getAuditDetail().getModelData()).getFinScheduleData()
					.getFinanceMain().getNextUserId());
		} catch (AppException e) {
			MessageUtil.showError(e);
		} catch (DataAccessException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		logger.debug("Leaving");
		return processCompleted;
	}
	// ******************************************************//
	// ****************OnSelect ComboBox Events**************//
	// ******************************************************//

	//FinanceMain Details Tab ---> 1. Basic Details

	//On Change Event for Finance Start Date
	public void onChange$finStartDate(Event event) {
		logger.debug("Entering" + event.toString());

		if (this.finStartDate.getValue() != null) {
			//####_0.2
			//changeFrequencies();
			onChangefinStartDate();

			// To set the Maturitydate when fincategory is Overdraft 
			if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
					getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
				calMaturityDate();
			}
			// if finStart date is changed to reset the date and recalculate the manual schedule
			if (this.manualSchedule.isChecked() && getManualScheduleDetailDialogCtrl() != null) {
				getManualScheduleDetailDialogCtrl().curDateChange(this.finStartDate.getValue(), false);
			}

		} else {
			this.finStartDate.setValue(DateUtility.getAppDate());
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onChangefinStartDate() {
		logger.debug("Entering");
		try {

			//Reset LatePayAmount based on Date difference of finance start date and Rolleover Date
			BigDecimal totalRolloverAmt = BigDecimal.ZERO;
			BigDecimal profitRate = BigDecimal.ZERO;
			Date rolledOverDate = null;
			if (getFinanceDetail().getRolledoverFinanceHeader() != null
					&& getFinanceDetail().getRolledoverFinanceHeader().getRolledoverFinanceDetails() != null) {
				for (RolledoverFinanceDetail rolledoverFinDetail : getFinanceDetail().getRolledoverFinanceHeader()
						.getRolledoverFinanceDetails()) {
					totalRolloverAmt = totalRolloverAmt.add(rolledoverFinDetail.getRolloverAmount());
					profitRate = rolledoverFinDetail.getProfitRate();
					rolledOverDate = rolledoverFinDetail.getRolloverDate();
				}
				BigDecimal latePayAmt = CalculationUtil.calInterest(rolledOverDate, this.finStartDate.getValue(),
						totalRolloverAmt, financeDetail.getFinScheduleData().getFinanceMain().getProfitDaysBasis(),
						profitRate);
				this.latePayAmount.setValue(
						PennantAppUtil.formateAmount(latePayAmt, CurrencyUtil.getFormat(getFinanceMain().getFinCcy())));
			}

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
	}

	public Date getfinstartDate() {
		return this.finStartDate.getValue();
	}

	public void onChange$maturityDate(Event event) {
		logger.debug("Entering");

		if (!isReadOnly("FinanceMainDialog_NoScheduleGeneration")) {
			doChangeTerms();
		}

		// if finStart date is changed to reset the date and recalculate the manual schedule
		if (this.manualSchedule.isChecked() && getManualScheduleDetailDialogCtrl() != null) {
			getManualScheduleDetailDialogCtrl().curDateChange(this.maturityDate.getValue(), true);
		}
		logger.debug("Leaving");
	}

	public void onChange$planDeferCount(Event event) {
		logger.debug("Entering" + event.toString());

		if (this.planDeferCount.intValue() == 0) {
			if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwDifferment()) {
				this.defferments.setReadonly(isReadOnly("FinanceMainDialog_defferments"));
			} else {
				this.defferments.setReadonly(true);
				this.defferments.setValue(0);
			}
		} else {
			this.defferments.setReadonly(true);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$graceTerms(Event event) {
		logger.debug("Entering" + event.toString());

		if (this.graceTerms.getValue() != null) {
			this.graceTerms_Two.setValue(this.graceTerms.intValue());

			if (this.graceTerms_Two.intValue() > 0 && this.gracePeriodEndDate.getValue() == null) {

				int checkDays = 0;
				if (this.graceTerms_Two.intValue() == 1) {
					checkDays = getFinanceDetail().getFinScheduleData().getFinanceType().getFddLockPeriod();
				}

				List<Calendar> scheduleDateList = FrequencyUtil
						.getNextDate(this.gracePftFrq.getValue(), this.graceTerms_Two.intValue(),
								this.finStartDate.getValue(), HolidayHandlerTypes.MOVE_NONE, false, checkDays)
						.getScheduleList();

				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					this.gracePeriodEndDate_two.setValue(calendar.getTime());
				}
				scheduleDateList = null;
			}

		} else {
			this.graceTerms_Two.setValue(0);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$gracePeriodEndDate(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering" + event.toString());
		if (!StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_CHGGRCEND)) {
			return;
		} else {
			if (this.graceTerms.isReadonly() && !this.gracePeriodEndDate.isReadonly()
					&& !this.manualSchedule.isChecked()) {
				if (this.gracePeriodEndDate.getValue() != null || this.gracePeriodEndDate_two.getValue() != null) {
					if (this.finStartDate.getValue().compareTo(this.nextGrcPftDate_two.getValue()) == 0) {
						this.graceTerms_Two.setValue(FrequencyUtil.getTerms(this.gracePftFrq.getValue(),
								this.nextGrcPftDate_two.getValue(),
								this.gracePeriodEndDate.getValue() == null ? this.gracePeriodEndDate_two.getValue()
										: this.gracePeriodEndDate.getValue(),
								false, false).getTerms());
					} else if (this.finStartDate.getValue().compareTo(this.nextGrcPftDate_two.getValue()) < 0) {
						this.graceTerms_Two.setValue(FrequencyUtil.getTerms(this.gracePftFrq.getValue(),
								this.nextGrcPftDate_two.getValue(),
								this.gracePeriodEndDate.getValue() == null ? this.gracePeriodEndDate_two.getValue()
										: this.gracePeriodEndDate.getValue(),
								true, false).getTerms());
					}

					this.graceTerms.setText("");
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	//Change all the Frequencies 
	public void changeFrequencies() {
		logger.debug("Entering");
		if (StringUtils.isNotBlank(this.gracePftFrq.getValue())) {
			processFrqChange(this.gracePftFrq);
		}
		if (StringUtils.isNotBlank(this.gracePftRvwFrq.getValue())) {
			processFrqChange(this.gracePftRvwFrq);
		}
		if (StringUtils.isNotBlank(this.graceCpzFrq.getValue())) {
			processFrqChange(this.graceCpzFrq);
		}
		if (StringUtils.isNotBlank(this.repayPftFrq.getValue())) {
			processFrqChange(this.repayPftFrq);
		}
		if (StringUtils.isNotBlank(this.repayRvwFrq.getValue())) {
			processFrqChange(this.repayRvwFrq);
		}
		if (StringUtils.isNotBlank(this.repayCpzFrq.getValue())) {
			processFrqChange(this.repayCpzFrq);
		}
		if (StringUtils.isNotBlank(this.repayFrq.getValue())) {
			processFrqChange(this.repayFrq);
		}
		if (StringUtils.isNotBlank(this.rolloverFrq.getValue())) {
			processFrqChange(this.rolloverFrq);
		}
		if (StringUtils.isNotBlank(this.droplineFrq.getValue())) {
			processFrqChange(this.droplineFrq);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method is for updating frequency with latest data based on finance start date
	 */
	public void processFrqChange(FrequencyBox frequencyBox) {
		logger.debug("Entering");
		String mnth = "";
		String frqCode = frequencyBox.getFrqCodeValue();
		frequencyBox.setFrqCodeDetails();
		if (!PennantConstants.List_Select.equals(frqCode)) {
			if (null != this.finStartDate.getValue()) {
				if (FrequencyCodeTypes.FRQ_QUARTERLY.equals(frqCode)
						|| FrequencyCodeTypes.FRQ_HALF_YEARLY.equals(frqCode)
						|| FrequencyCodeTypes.FRQ_BIMONTHLY.equals(frqCode)) {
					mnth = FrequencyUtil.getMonthFrqValue(DateUtility
							.formatUtilDate(this.finStartDate.getValue(), PennantConstants.DBDateFormat).split("-")[1],
							frqCode);
				} else if (FrequencyCodeTypes.FRQ_YEARLY.equals(frqCode)) {
					mnth = DateUtility.formatUtilDate(this.finStartDate.getValue(), PennantConstants.DBDateFormat)
							.split("-")[1];
				}
			}
			mnth = frqCode.concat(mnth).concat("00");
			String day = DateUtility.formatUtilDate(this.finStartDate.getValue(), PennantConstants.DBDateFormat)
					.split("-")[2];
			if (FrequencyCodeTypes.FRQ_DAILY.equals(frqCode)) {
				day = "00";
			} else if (FrequencyCodeTypes.FRQ_WEEKLY.equals(frqCode)) {
				day = StringUtils.leftPad(String.valueOf(Integer.parseInt(day) % 7), 2, "0");
			} else if (FrequencyCodeTypes.FRQ_FORTNIGHTLY.equals(frqCode)) {
				day = StringUtils.leftPad(String.valueOf(Integer.parseInt(day) % 14), 2, "0");
			}
			frequencyBox.updateFrequency(mnth, day);
		}
		logger.debug("Leaving");
	}

	public void onCheckmanualSchedule() {
		logger.debug("Entering");

		if (manualSchedule.isChecked()) {

			if (getFinanceDetail().getFinScheduleData().isSchduleGenerated()) {
				if (MessageUtil.confirm(Labels.getLabel("label_ScheduleMsg")) == MessageUtil.YES) {
					//set the totals and profits to zero in finance main bean 
					appendScheduleDetailTab(false, false);
				} else {
					this.manualSchedule.setChecked(false);
					return;
				}
			}

			if (this.allowGrace.isChecked()) {
				onCheckgrace(false);
			}

			fillComboBox(this.repayRateBasis, CalculationConstants.RATE_BASIS_R,
					PennantStaticListUtil.getInterestRateType(true), ",C,");
			this.row_stepFinance.setVisible(false);
			this.row_manualSteps.setVisible(false);
			this.stepFinance.setChecked(false);
			doStepPolicyCheck(false);
			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_STEPDETAILS)) != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_STEPDETAILS));
				tab.setVisible(false);
			}
			this.defermentsRow.setVisible(false);
			this.defferments.setValue(0);
			this.planDeferCount.setValue(0);
			if (StringUtils.isNotBlank(this.cbScheduleMethod.getValue())) {
				this.cbSchddemethod = getComboboxValue(this.cbScheduleMethod);
			}
			this.finRepaymentAmount.setVisible(false);
			this.numberOfTerms.setReadonly(true);
			this.numberOfTerms_two.setValue(0);
			this.repayBaseRateRow.setVisible(true);
			this.row_RpyAdvBaseRate.setVisible(false);
			this.row_RpyAdvPftRate.setVisible(false);
			this.row_supplementRent.setVisible(false);
			this.row_FinRepRates.setVisible(false);
			this.rpyPftFrqRow.setVisible(false);
			this.rpyRvwFrqRow.setVisible(false);
			this.rpyCpzFrqRow.setVisible(false);
			this.rpyFrqRow.setVisible(false);
			this.rolloverFrqRow.setVisible(false);
			this.finRepayPftOnFrq.setVisible(false);

			if (this.maturityDate.getValue() == null) {
				this.maturityDate.setValue(this.maturityDate_two.getValue());
			}
			if (this.gracePeriodEndDate.getValue() == null) {
				this.gracePeriodEndDate.setValue(this.gracePeriodEndDate_two.getValue());
			}
		} else {
			if (getManualScheduleDetailDialogCtrl() != null) {
				if (MessageUtil.confirm(Labels.getLabel("label_ScheduleMsg")) == MessageUtil.YES) {
					Tab tab = null;
					if (tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_SCHEDULE)) != null) {
						tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_SCHEDULE));
						tab.setVisible(false);
					}
					setManualScheduleDetailDialogCtrl(null);
					if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 2) {
						appendScheduleDetailTab(false, false);
					}

				} else {
					this.manualSchedule.setChecked(true);
					return;
				}
			}
			// Setting Default Values from Finance Type
			FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

			if (this.allowGrace.isChecked()) {
				onCheckgrace(true);
			}

			if (financeType.isStepFinance()) {
				this.row_stepFinance.setVisible(true);
			}
			if (financeType.isAlwPlanDeferment()) {
				this.defermentsRow.setVisible(true);
				this.planDeferCount.setValue(financeType.getPlanDeferCount());
				this.defferments.setValue(financeType.getFinMaxDifferment());
			}
			fillComboBox(this.repayRateBasis, financeType.getFinRateType(),
					PennantStaticListUtil.getInterestRateType(true), "");
			finRepaymentAmount.setVisible(true);
			this.maturityDate.setText("");
			this.numberOfTerms.setReadonly(isReadOnly("FinanceMainDialog_numberOfTerms"));
			if (StringUtils.equals(CalculationConstants.RATE_BASIS_R,
					this.repayRateBasis.getSelectedItem().getValue().toString())
					&& StringUtils.isNotEmpty(financeType.getFinBaseRate())) {
				this.repayBaseRateRow.setVisible(true);
			}
			if (StringUtils.isNotBlank(financeType.getRpyAdvBaseRate())) {
				this.row_RpyAdvBaseRate.setVisible(true);
			}
			if (financeType.getRpyAdvPftRate().compareTo(BigDecimal.ZERO) > 0) {
				row_RpyAdvPftRate.setVisible(true);
			}
			if (financeType.getFInMinRate().compareTo(BigDecimal.ZERO) > 0
					&& financeType.getFinMaxRate().compareTo(BigDecimal.ZERO) > 0) {
				this.row_FinRepRates.setVisible(true);
			}
			if (StringUtils.isNotBlank(financeType.getFinRvwFrq())) {
				this.rpyRvwFrqRow.setVisible(true);
				this.repayRvwFrq.setVisible(true);
			}
			if (financeType.isFinIsIntCpz()) {
				this.rpyCpzFrqRow.setVisible(true);
			}
			if (financeType.isRollOverFinance()) {
				this.rolloverFrqRow.setVisible(true);
			}
			if (financeType.isFinRepayPftOnFrq()) {
				this.finRepayPftOnFrq.setVisible(true);
			}
			if (!financeType.isAllowDownpayPgm() && !this.stepFinance.isChecked()) {
				this.repayRateBasis.setDisabled(isReadOnly("FinanceMainDialog_repayRateBasis"));
			}

			this.rpyPftFrqRow.setVisible(true);
			this.rpyFrqRow.setVisible(true);

		}

		logger.debug("Leaving");
	}

	/*
	 * on check manual Schedule ,if Grace is Checked the visibility of the Grace fields
	 */

	public void onCheckgrace(boolean isGraceCheck) {
		logger.debug("Entering");

		FinanceType finType = getFinanceDetail().getFinScheduleData().getFinanceType();

		if (isGraceCheck) {
			this.space_grcPeriodEndDate.setSclass("");
			this.graceTerms.setReadonly(isReadOnly("FinanceMainDialog_graceTerms"));
		} else {
			this.graceTerms.setReadonly(true);
			this.space_grcPeriodEndDate.setSclass(PennantConstants.mandateSclass);
		}

		if (StringUtils.isNotBlank(this.cbGrcSchdMthd.getValue())) {
			this.cbGrcSchddemethod = getComboboxValue(this.cbGrcSchdMthd);
		}
		if (isGraceCheck) {

			doAllowGraceperiod(true);

			if (isGraceCheck) {
				if (StringUtils.equals(FinanceConstants.PRODUCT_STRUCTMUR, finType.getProductCategory())) {
					if (StringUtils.isNotBlank(finType.getGrcAdvBaseRate())) {
						this.row_GrcAdvBaseRate.setVisible(true);
						this.row_GrcAdvPftRate.setVisible(false);
					} else {
						this.row_GrcAdvPftRate.setVisible(true);
						this.row_GrcAdvBaseRate.setVisible(false);
					}
				}

				this.grcPftFrqRow.setVisible(isGraceCheck);
				fillComboBox(cbGrcSchdMthd, this.cbGrcSchddemethod, PennantStaticListUtil.getScheduleMethods(),
						",EQUAL,PRI_PFT,PRI,");

				if (finType.isFinGrcIsRvwAlw()) {
					if (StringUtils.isNotBlank(finType.getFinGrcRvwFrq())
							&& !StringUtils.equals(finType.getFinGrcRvwFrq(), PennantConstants.List_Select)) {
						this.grcPftRvwFrqRow.setVisible(true);
						this.gracePftRvwFrq.setValue(finType.getFinGrcRvwFrq());
					}
					readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"), this.nextGrcPftRvwDate);
				} else {
					this.gracePftRvwFrq.setDisabled(true);
					this.nextGrcPftRvwDate.setValue(SysParamUtil.getValueAsDate("APP_DFT_ENDDATE"));
					readOnlyComponent(true, this.nextGrcPftRvwDate);

				}

				if (finType.isFinGrcIsIntCpz()) {
					this.graceCpzFrq.setDisabled(isReadOnly("FinanceMainDialog_graceCpzFrq"));
					if (StringUtils.isNotBlank(finType.getFinGrcCpzFrq()) || !StringUtils
							.trimToEmpty(finType.getFinGrcCpzFrq()).equals(PennantConstants.List_Select)) {
						this.grcCpzFrqRow.setVisible(true);
						this.graceCpzFrq.setValue(finType.getFinGrcCpzFrq());
					}
					readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcCpzDate"), this.nextGrcCpzDate);
				} else {
					this.graceCpzFrq.setDisabled(true);
					this.nextGrcCpzDate.setValue(SysParamUtil.getValueAsDate("APP_DFT_ENDDATE"));
					readOnlyComponent(true, this.nextGrcCpzDate);
				}
			}
		} else {
			this.row_GrcAdvBaseRate.setVisible(isGraceCheck);
			this.graceRate.setVisible(isGraceCheck);
			this.grcBaseRateRow.setVisible(isGraceCheck);
			this.grcAdvRate.setVisible(isGraceCheck);
			this.row_GrcAdvPftRate.setVisible(isGraceCheck);
			this.grcPftFrqRow.setVisible(isGraceCheck);
			this.grcPftRvwFrqRow.setVisible(isGraceCheck);
			this.grcCpzFrqRow.setVisible(isGraceCheck);
			this.row_GrcPftDayBasis.setVisible(isGraceCheck);
		}
		onChangeGrcSchdMthd();
		logger.debug("Leaving");
	}

	public void onCheck$alwBpiTreatment(Event event) {
		logger.debug("Entering");
		oncheckalwBpiTreatment(true);
		logger.debug("Leaving");
	}

	private void oncheckalwBpiTreatment(boolean isAction) {
		logger.debug("Entering");
		if (this.alwBpiTreatment.isChecked()) {
			this.space_DftBpiTreatment.setSclass(PennantConstants.mandateSclass);
			this.dftBpiTreatment.setDisabled(isReadOnly("FinanceMainDialog_DftBpiTreatment"));
			if (isAction) {
				fillComboBox(this.dftBpiTreatment, FinanceConstants.BPI_NO, PennantStaticListUtil.getDftBpiTreatment(),
						"");
			}
			this.row_BpiTreatment.setVisible(true);
		} else {
			this.alwBpiTreatment.setDisabled(isReadOnly("FinanceMainDialog_DftBpiTreatment"));
			this.dftBpiTreatment.setDisabled(true);
			this.space_DftBpiTreatment.setSclass("");
			this.dftBpiTreatment.setConstraint("");
			this.dftBpiTreatment.setErrorMessage("");
			fillComboBox(this.dftBpiTreatment, FinanceConstants.BPI_NO, PennantStaticListUtil.getDftBpiTreatment(), "");
			if (!isAction) {
				if (!getFinanceDetail().getFinScheduleData().getFinanceType().isAlwBPI()) {
					this.row_BpiTreatment.setVisible(false);
				} else {
					if (!isReadOnly("FinanceMainDialog_DftBpiTreatment")) {
						this.row_BpiTreatment.setVisible(true);
					}
				}
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Setting Default Values of visibility on Check Planned Emi Holidays
	 */
	public void onCheck$alwPlannedEmiHoliday(Event event) {
		logger.debug("Entering");
		onCheckPlannedEmiholiday(null, true);
		logger.debug("Leaving");
	}

	private void onCheckPlannedEmiholiday(String planEmiHMType, boolean isAction) {
		logger.debug("Entering");
		FinanceType financeType = new FinanceType();
		financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		if (this.alwPlannedEmiHoliday.isChecked()) {
			this.row_PlannedEMIH.setVisible(true);
			this.label_FinanceMainDialog_PlanEmiHolidayMethod.setVisible(true);
			this.hbox_planEmiMethod.setVisible(true);
			this.row_MaxPlanEmi.setVisible(true);
			this.row_PlanEmiHLockPeriod.setVisible(true);
			if (isAction) {
				this.planEmiHLockPeriod.setValue(financeType.getPlanEMIHLockPeriod());
				this.cpzAtPlanEmi.setChecked(financeType.isPlanEMICpz());
				this.maxPlanEmiPerAnnum.setValue(financeType.getPlanEMIHMaxPerYear());
				this.maxPlanEmi.setValue(financeType.getPlanEMIHMax());
			}

			if (planEmiHMType == null) {
				setComboboxSelectedItem(this.planEmiMethod, FinanceConstants.PLANEMIHMETHOD_FRQ);
			} else {
				setComboboxSelectedItem(this.planEmiMethod, planEmiHMType);
			}

		} else {
			this.planEmiMethod.setSelectedIndex(0);
			this.planEmiHLockPeriod.setErrorMessage("");
			this.planEmiMethod.setErrorMessage("");
			this.maxPlanEmiPerAnnum.setErrorMessage("");
			this.maxPlanEmi.setErrorMessage("");
			this.hbox_planEmiMethod.setVisible(false);
			this.row_MaxPlanEmi.setVisible(false);
			this.row_PlanEmiHLockPeriod.setVisible(false);
			this.cpzAtPlanEmi.setChecked(false);
			this.label_FinanceMainDialog_PlanEmiHolidayMethod.setVisible(false);

			if (getFinanceDetail().getFinScheduleData().getFinanceType().isPlanEMIHAlw()
					&& !isReadOnly("FinanceMainDialog_AlwPlannedEmiHoliday")) {
				this.row_PlannedEMIH.setVisible(true);
			}
		}

		setPlanEMIHMethods(isAction);
		logger.debug("Leaving");

	}

	/**
	 * Method for Setting Planned EMI Holiday Methods
	 */
	public void onChange$planEmiMethod(Event event) {
		logger.debug("Entering" + event.toString());
		setPlanEMIHMethods(true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Setting Variables on Schedule Tab based on selected Planned EMI Holiday Method
	 * 
	 * @param isAction
	 */
	private void setPlanEMIHMethods(boolean isAction) {
		// Setting Planned EMI Holiday Methods
		if (getScheduleDetailDialogCtrl() != null) {

			boolean alwPlanEMIHMethods = false;
			boolean alwPlanEMIHDates = false;
			if (StringUtils.equals(getComboboxValue(this.planEmiMethod), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				alwPlanEMIHMethods = true;
				// Data Setting on Rendering
				if (!isAction) {
					getScheduleDetailDialogCtrl()
							.setPlanEMIHMonths(getFinanceDetail().getFinScheduleData().getPlanEMIHmonths());
				}
			} else if (StringUtils.equals(getComboboxValue(this.planEmiMethod),
					FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
				alwPlanEMIHDates = true;
			}
			getScheduleDetailDialogCtrl().visiblePlanEMIHolidays(alwPlanEMIHMethods, alwPlanEMIHDates);
		}
	}

	/*** Frequency Methods ***/
	/**
	 * On Selecting GracePeriod Profit Frequency Day
	 * 
	 * @param event
	 */
	public void onSelectDay$gracePftFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.gracePftFrq.getDaySelectedIndex(), true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting GracePeriod Profit Review Frequency Day
	 * 
	 * @param event
	 */
	public void onSelectDay$gracePftRvwFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.gracePftRvwFrq.getDaySelectedIndex(), true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting GracePeriod capitalizing Frequency Day
	 * 
	 * @param event
	 */
	public void onSelectDay$graceCpzFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.graceCpzFrq.getDaySelectedIndex(), true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Repay Frequency code
	 * 
	 * @param event
	 */
	public void onSelectCode$repayFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.repayFrq);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Repay Profit Frequency code
	 * 
	 * @param event
	 */
	public void onSelectCode$repayPftFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.repayPftFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectCode$depreciationFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.depreciationFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectCode$gracePftFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.gracePftFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectCode$gracePftRvwFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.gracePftRvwFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectCode$graceCpzFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.graceCpzFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectCode$repayRvwFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.repayRvwFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectCode$repayCpzFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.repayCpzFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectCode$rolloverFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.rolloverFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectCode$droplineFrq(Event event) {
		logger.debug("Entering" + event.toString());
		this.droplineFrq.setFrqCodeDetails();
		processFrqChange(this.droplineFrq);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Repay Frequency Day
	 * 
	 * @param event
	 */
	public void onSelectDay$repayFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.repayFrq.getDaySelectedIndex(), false);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Rollover Frequency Day
	 * 
	 * @param event
	 */
	public void onSelect$rolloverFrqDay(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.rolloverFrq.getDaySelectedIndex(), false);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Repay Profit Frequency Day
	 * 
	 * @param event
	 */
	public void onSelectDay$repayPftFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.repayPftFrq.getDaySelectedIndex(), false);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Repay profit Frequency day
	 * 
	 * @param event
	 */
	public void onSelectDay$repayRvwFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.repayRvwFrq.getDaySelectedIndex(), false);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Repay Capitalizing Frequency day
	 * 
	 * @param event
	 */
	public void onSelectDay$repayCpzFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.repayCpzFrq.getDaySelectedIndex(), false);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Dropline Frequency Day
	 * 
	 * @param event
	 */
	public void onSelectDay$droplineFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.droplineFrq.getDaySelectedIndex(), false);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting
	 * 
	 * @param event
	 */
	public void onSelect$finRepayMethod(Event event) {
		logger.debug("Entering" + event.toString());
		doCheckDDA();
		logger.debug("Leaving" + event.toString());
	}

	public void doCheckDDA() {
		if (StringUtils.equals(getComboboxValue(this.finRepayMethod), FinanceConstants.REPAYMTH_AUTODDA)) {
			this.gb_ddaRequest.setVisible(true);
		} else {
			this.gb_ddaRequest.setVisible(false);
			this.bankName.setValue("", "");
			this.iban.setValue("");
			this.ifscCode.setValue("");
		}
	}

	private void resetFrqDay(int selectedIndex, boolean inclGrc) {
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		// clear error messages on components
		this.repayPftFrq.getFrqDayCombobox().setErrorMessage("");
		this.repayRvwFrq.getFrqDayCombobox().setErrorMessage("");
		this.repayFrq.getFrqDayCombobox().setErrorMessage("");

		if (inclGrc) {
			if (this.gracePftFrq.getDaySelectedIndex() != selectedIndex) {
				this.gracePftFrq.resetFrqDay(selectedIndex);
			}
			this.nextGrcPftDate.setText("");
			if (financeMain.isAllowGrcPftRvw()) {
				if (this.gracePftRvwFrq.getDaySelectedIndex() != selectedIndex) {
					this.gracePftRvwFrq.resetFrqDay(selectedIndex);
				}
				this.nextGrcPftRvwDate.setText("");
			}
			if (financeMain.isAllowGrcCpz()) {
				if (this.graceCpzFrq.getDaySelectedIndex() != selectedIndex) {
					this.graceCpzFrq.resetFrqDay(selectedIndex);
				}
				this.nextGrcCpzDate.setText("");
			}
		}
		if (this.repayPftFrq.getDaySelectedIndex() != selectedIndex) {
			this.repayPftFrq.resetFrqDay(selectedIndex);
		}
		this.nextRepayPftDate.setText("");

		if (this.repayFrq.getDaySelectedIndex() != selectedIndex) {
			this.repayFrq.resetFrqDay(selectedIndex);
		}
		this.nextRepayDate.setText("");

		if (StringUtils.isNotBlank(this.rolloverFrq.getValue())) {
			if (this.rolloverFrq.getDaySelectedIndex() != selectedIndex) {
				this.rolloverFrq.resetFrqDay(selectedIndex);
			}
			this.nextRollOverDate.setText("");
		}
		if (financeMain.isAllowRepayRvw()) {
			if (this.repayRvwFrq.getDaySelectedIndex() != selectedIndex) {
				this.repayRvwFrq.resetFrqDay(selectedIndex);
			}
			this.nextRepayRvwDate.setText("");
		}
		if (financeMain.isAllowRepayCpz()) {
			if (this.repayCpzFrq.getDaySelectedIndex() != selectedIndex) {
				this.repayCpzFrq.resetFrqDay(selectedIndex);
			}
			this.nextRepayCpzDate.setText("");
		}
	}

	/**
	 * Change the branch for the Account on changing the finance Branch
	 * 
	 * @param event
	 */
	public void onFulfill$finBranch(Event event) {
		logger.debug("Entering");

		if (StringUtils.isBlank(this.finBranch.getValue())) {
			this.finBranch.setValue(getFinanceDetail().getCustomerDetails().getCustomer().getCustDftBranch());
			this.finBranch.setDescription(
					getFinanceDetail().getCustomerDetails().getCustomer().getLovDescCustDftBranchName());
		} else {
			SecurityUserDivBranch branch = (SecurityUserDivBranch) this.finBranch.getObject();
			if (branch != null) {
				this.finBranch.setValue(branch.getUserBranch(), branch.getUserBranchDesc());
				branchSwiftCode = branch.getBranchSwiftBrnCde();
			}
		}
		isBranchanged = true;

		this.disbAcctId.setBranchCode(this.finBranch.getValue());
		this.repayAcctId.setBranchCode(this.finBranch.getValue());
		this.downPayAccount.setBranchCode(this.finBranch.getValue());
		logger.debug("Leaving");
	}

	/**
	 * Payment Account field Validation compare to Secondary Account To Avaid dublication compare to Secondary Account
	 * 
	 * @param event
	 */
	public void onFulfill$repayAcctId(Event event) {
		logger.debug("Entering");
		Clients.clearWrongValue(this.repayAcctId);
		List<SecondaryAccount> secAccList = financeDetail.getFinScheduleData().getFinanceMain().getSecondaryAccount();
		if (!secAccList.isEmpty()) {
			for (SecondaryAccount secondaryAccount : secAccList) {
				if (StringUtils.equals(this.repayAcctId.getValue(), secondaryAccount.getAccountNumber())) {
					throw new WrongValueException(this.repayAcctId, Labels.getLabel("PAYMENTACC_EXISTS",
							new String[] { Labels.getLabel("label_FinanceMainDialog_RepayAcctId.value") }));
				}
			}
		}

		logger.debug("Leaving");
	}

	public void onFulfill$accountsOfficer(Event event) {
		logger.debug("Entering");

		Object dataObject = accountsOfficer.getObject();
		if (dataObject instanceof String) {
			this.accountsOfficer.setValue(dataObject.toString());
			this.accountsOfficer.setDescription("");
		} else {
			VehicleDealer details = (VehicleDealer) dataObject;
			if (details != null) {
				this.accountsOfficer.setAttribute("DealerId", details.getDealerId());
			}
		}
		logger.debug("Leaving");
	}
	
	public void onFulfill$eligibilityMethod(Event event) {
		logger.debug("Entering");
		Object dataObject = eligibilityMethod.getObject();
		if (dataObject instanceof String) {
			this.eligibilityMethod.setValue(dataObject.toString());
			this.eligibilityMethod.setDescription("");
		} else {
			LovFieldDetail details = (LovFieldDetail) dataObject;
			if (details != null) {
				this.eligibilityMethod.setAttribute("FieldCodeId", details.getFieldCodeId());
			}
		}
		logger.debug("Leaving");
	}

	public void onFulfill$dsaCode(Event event) {
		logger.debug("Entering");
		Object dataObject = dsaCode.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.dsaCode.setValue("");
			this.dsaCode.setDescription("");
			this.dsaCode.setAttribute("DSAdealerID", null);
		} else {
			VehicleDealer details = (VehicleDealer) dataObject;
			if (details != null) {
				this.dsaCode.setAttribute("DSAdealerID", details.getId());
			}
		}
		logger.debug("Leaving");
	}

	public void onFulfill$dmaCode(Event event) {
		logger.debug("Entering");
		Object dataObject = dmaCode.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.dmaCode.setValue("");
			this.dmaCode.setDescription("");
			this.dmaCode.setAttribute("DMAdealerID", null);
		} else {
			VehicleDealer details = (VehicleDealer) dataObject;
			if (details != null) {
				this.dmaCode.setAttribute("DMAdealerID", details.getId());
			}
		}
		logger.debug("Leaving");
	}
	
	public void onFulfill$connector(Event event) {
		logger.debug("Entering");
		Object dataObject = connector.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.connector.setValue("");
			this.connector.setDescription("");
			this.connector.setAttribute("DealerId", null);
		} else {
			VehicleDealer details = (VehicleDealer) dataObject;
			if (details != null) {
				this.connector.setAttribute("DealerId", details.getId());
			}
		}
		logger.debug("Leaving");
	}
	//FinanceMain Details Tab ---> 2. Grace Period Details

	/**
	 * Method for Adv Effective Rate Calculation
	 * 
	 * @param baseRate
	 * @param currency
	 * @param margin
	 * @param minRate
	 * @param maxRate
	 * @param effRate
	 * @throws InterruptedException
	 */
	protected void calAdvPftRate(String baseRate, String currency, BigDecimal margin, BigDecimal minRate,
			BigDecimal maxRate, Decimalbox effRate) {

		if (StringUtils.isBlank(baseRate)) {
			return;
		}
		RateDetail rateDetail = RateUtil.rates(baseRate, currency, "", margin, minRate, maxRate);
		if (rateDetail.getErrorDetails() == null) {
			effRate.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
		} else {
			MessageUtil.showError(ErrorUtil
					.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			effRate.setValue(BigDecimal.ZERO);
		}
	}

	/**
	 * when clicks on button "GraceSpecialRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$graceRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		ForwardEvent forwardEvent = (ForwardEvent) event;
		String rateType = (String) forwardEvent.getOrigin().getData();
		if (StringUtils.equals(rateType, PennantConstants.RATE_BASE)) {
			this.graceRate.getEffRateComp().setConstraint("");
			Object dataObject = graceRate.getBaseObject();

			if (dataObject instanceof String) {
				this.graceRate.setBaseValue(dataObject.toString());
				this.graceRate.setBaseDescription("");
				this.graceRate.setEffRateValue(BigDecimal.ZERO);
			} else {
				BaseRateCode details = (BaseRateCode) dataObject;
				if (details != null) {
					this.graceRate.setBaseValue(details.getBRType());
					this.graceRate.setBaseDescription(details.getBRTypeDesc());
				}
			}
			if (StringUtils.isNotBlank(this.graceRate.getBaseValue())) {
				calculateRate(this.graceRate.getBaseValue(), this.finCcy.getValue(), this.graceRate.getSpecialComp(),
						this.graceRate.getBaseComp(), this.graceRate.getMarginValue(), this.graceRate.getEffRateComp(),
						this.finGrcMinRate, this.finGrcMaxRate);
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			this.graceRate.getEffRateComp().setConstraint("");
			Object dataObject = graceRate.getSpecialObject();

			if (dataObject instanceof String) {
				this.graceRate.setSpecialValue(dataObject.toString());
				this.graceRate.setSpecialDescription("");
				this.graceRate.setEffRateValue(BigDecimal.ZERO);
			} else {
				SplRateCode details = (SplRateCode) dataObject;
				if (details != null) {
					this.graceRate.setSpecialValue(details.getSRType());
					this.graceRate.setSpecialDescription(details.getSRTypeDesc());
				}
			}

			if (StringUtils.isNotBlank(this.graceRate.getSpecialValue())) {
				calculateRate(this.graceRate.getBaseValue(), this.finCcy.getValue(), this.graceRate.getSpecialComp(),
						this.graceRate.getBaseComp(), this.graceRate.getMarginValue(), this.graceRate.getEffRateComp(),
						this.finGrcMinRate, this.finGrcMaxRate);
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {
			if (this.graceRate.getMarginValue() != null) {
				RateDetail rateDetail = RateUtil.rates(this.graceRate.getBaseValue(), this.finCcy.getValue(),
						this.graceRate.getSpecialValue(),
						this.graceRate.getMarginValue() == null ? BigDecimal.ZERO : this.graceRate.getMarginValue(),
						this.finGrcMinRate.getValue() != null
								&& this.finGrcMinRate.getValue().compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO
										: this.finGrcMinRate.getValue(),
						this.finGrcMaxRate.getValue() != null
								&& this.finGrcMaxRate.getValue().compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO
										: this.finGrcMaxRate.getValue());
				this.graceRate.setEffRateText(
						PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method for Changing Grace rate Basis
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onChange$grcRateBasis(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();

		this.graceRate.setBaseConstraint("");
		this.graceRate.setSpecialConstraint("");
		this.graceRate.getEffRateComp().setConstraint("");

		this.graceRate.setBaseReadonly(true);
		this.graceRate.setSpecialReadonly(true);
		this.graceRate.setMarginReadonly(true);

		this.graceRate.setBaseValue("");
		this.graceRate.setSpecialValue("");
		this.graceRate.setBaseDescription("");
		this.graceRate.setSpecialDescription("");
		readOnlyComponent(true, this.gracePftRate);
		this.graceRate.setEffRateText("0.00");
		this.gracePftRate.setText("0.00");

		if (!this.grcRateBasis.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {

			if (CalculationConstants.RATE_BASIS_F.equals(this.grcRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_C
							.equals(this.grcRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_D
							.equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {

				this.graceRate.setBaseReadonly(true);
				this.graceRate.setSpecialReadonly(true);
				this.graceRate.setMarginReadonly(true);

				this.graceRate.setBaseDescription("");
				this.graceRate.setSpecialDescription("");

				this.graceRate.setEffRateText("0.00");
				readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);

				this.row_FinGrcRates.setVisible(false);
				this.grcBaseRateRow.setVisible(false);

			} else if (CalculationConstants.RATE_BASIS_R
					.equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {

				FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

				if (StringUtils.isNotBlank(financeType.getFinGrcBaseRate())) {

					this.graceRate.setBaseReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
					this.graceRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
					this.graceRate.setMarginReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
					if ((financeType.getFInGrcMinRate() == null
							|| financeType.getFInGrcMinRate().compareTo(BigDecimal.ZERO) == 0)
							&& (financeType.getFinGrcMaxRate() == null
									|| financeType.getFinGrcMaxRate().compareTo(BigDecimal.ZERO) == 0)) {
						this.row_FinGrcRates.setVisible(false);
					} else {
						this.row_FinGrcRates.setVisible(true);
					}
					this.grcBaseRateRow.setVisible(true);
					this.graceRate.setVisible(true);
					this.graceRate.setBaseValue(financeType.getFinGrcBaseRate());
					this.graceRate.setSpecialValue(financeType.getFinGrcSplRate());
				} else {
					readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
				}

				this.graceRate.setEffRateText("0.00");
				this.gracePftRate.setText("0.00");
			}
		} else {
			this.row_FinGrcRates.setVisible(false);
			this.grcBaseRateRow.setVisible(false);
		}

		setGrcPolicyRate(false, getFinanceDetail().isNewRecord());

		// Re calculate Total manual Schedule based on Rate basis selection
		if (getManualScheduleDetailDialogCtrl() != null) {
			getManualScheduleDetailDialogCtrl().validateAndRecalSchd();
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when user checks the allowGrcRepay checkbox
	 * 
	 * @param event
	 */
	public void onCheck$allowGrcRepay(Event event) {
		logger.debug("Entering" + event.toString());

		if (this.allowGrcRepay.isChecked()) {
			readOnlyComponent(false, this.cbGrcSchdMthd);
			this.space_GrcSchdMthd.setStyle("background-color:red");
			fillComboBox(this.cbGrcSchdMthd, getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSchdMthd(),
					PennantStaticListUtil.getScheduleMethods(), ",EQUAL,PRI_PFT,PRI,");
		} else {
			readOnlyComponent(true, this.cbGrcSchdMthd);
			this.cbGrcSchdMthd.setSelectedIndex(0);
			this.space_GrcSchdMthd.setStyle("background-color:white");
		}
		onChangeGrcSchdMthd();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Allow/ Not Grace In Finance
	 * 
	 * @param event
	 */
	public void onCheck$allowGrace(Event event) {
		logger.debug("Entering" + event.toString());
		doAllowGraceperiod(true);

		if (this.manualSchedule.isChecked()) {
			onCheckgrace(false);
		}
		logger.debug("Leaving" + event.toString());
	}

	//FinanceMain Details Tab ---> 3. Repayment Period Details

	/**
	 * Method for Changing Repay Period rate Basis
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onChange$repayRateBasis(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();

		this.repayRate.setBaseConstraint("");
		this.repayRate.setSpecialConstraint("");
		this.repayRate.getEffRateComp().setConstraint("");

		this.repayRate.setBaseReadonly(true);
		this.repayRate.setSpecialReadonly(true);

		this.repayRate.setBaseValue("");
		this.repayRate.setBaseDescription("");
		this.repayRate.setSpecialValue("");
		this.repayRate.setSpecialDescription("");
		readOnlyComponent(true, this.repayProfitRate);
		this.repayRate.setEffRateText("0.00");
		this.repayProfitRate.setText("0.00");

		if (!this.repayRateBasis.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
			if (CalculationConstants.RATE_BASIS_F.equals(this.repayRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_C
							.equals(this.repayRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_D
							.equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {
				this.repayRate.setBaseReadonly(true);
				this.repayRate.setSpecialReadonly(true);

				this.repayRate.setBaseDescription("");
				this.repayRate.setSpecialDescription("");

				this.repayRate.setEffRateText("0.00");
				readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);

				this.row_FinRepRates.setVisible(false);
				this.repayBaseRateRow.setVisible(true);

			} else if (CalculationConstants.RATE_BASIS_R
					.equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {

				FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
				if (StringUtils.isNotBlank(financeType.getFinBaseRate())) {
					this.repayRate.setBaseReadonly(isReadOnly("FinanceMainDialog_repayBaseRate"));
					this.repayRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_repaySpecialRate"));

					if (financeType.getFInMinRate().compareTo(BigDecimal.ZERO) == 0
							&& financeType.getFinMaxRate().compareTo(BigDecimal.ZERO) == 0) {
						this.row_FinRepRates.setVisible(false);
					} else {
						this.row_FinRepRates.setVisible(true);
					}
					this.repayBaseRateRow.setVisible(true);
					this.repayRate.setBaseValue(financeType.getFinBaseRate());
					this.repayRate.setSpecialValue(financeType.getFinSplRate());
				} else {
					readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);
				}
				this.repayRate.setEffRateText("0.00");
				this.repayProfitRate.setText("0.00");
			}
		} else {
			this.row_FinRepRates.setVisible(false);
			this.repayBaseRateRow.setVisible(true);
		}

		setPolicyRate(false, getFinanceDetail().isNewRecord());

		// Re calculate Total manual Schedule based on Rate basis selection
		if (getManualScheduleDetailDialogCtrl() != null) {
			getManualScheduleDetailDialogCtrl().validateAndRecalSchd();
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onChange$cbGrcSchdMthd(Event event) {
		logger.debug("Entering");

		if (manualSchedule.isChecked() && getManualScheduleDetailDialogCtrl() != null) {
			final String msg = "Schedule will be recreated would you like to proceed";

			if (MessageUtil.confirm(msg) == MessageUtil.YES) {
				if (getManualScheduleDetailDialogCtrl() != null) {
					getManualScheduleDetailDialogCtrl().doPrepareSchdData(getFinanceDetail().getFinScheduleData(),
							false);
					appendScheduleDetailTab(false, false);
				}

			} else {
				fillComboBox(this.cbGrcSchdMthd, cbGrcSchddemethod, PennantStaticListUtil.getScheduleMethods(),
						",EQUAL,PRI_PFT,PRI,");
				return;
			}
		}

		cbGrcSchddemethod = getComboboxValue(this.cbGrcSchdMthd);
		onChangeGrcSchdMthd();
		logger.debug("Leaving");
	}
	
	private void onChangeGrcSchdMthd(){
		if(this.cbGrcSchdMthd.getSelectedIndex() > 0 && StringUtils.equals(this.cbGrcSchdMthd.getSelectedItem().getValue().toString(), 
				CalculationConstants.SCHMTHD_PFTCAP)){
			this.row_GrcMaxAmount.setVisible(true);
			this.grcMaxAmount.setMandatory(true);
		}else{
			this.row_GrcMaxAmount.setVisible(false);
			this.grcMaxAmount.setValue(BigDecimal.ZERO);
			this.grcMaxAmount.setMandatory(false);
		}
	}

	/**
	 * Method for Changing Repay Period Schedule Method
	 * 
	 * @param event
	 */
	public void onChange$cbScheduleMethod(Event event) {
		logger.debug("Entering" + event.toString());

		if (manualSchedule.isChecked() && getManualScheduleDetailDialogCtrl() != null) {
			final String msg = "Schedule will be recreated would you like to proceed";

			if (MessageUtil.confirm(msg) == MessageUtil.YES) {
				if (!getFinanceDetail().getFinScheduleData().getFinanceType().isAllowDownpayPgm()
						&& !this.stepFinance.isChecked()) {
					this.repayRateBasis.setDisabled(isReadOnly("FinanceMainDialog_repayRateBasis"));
				}

				if (getManualScheduleDetailDialogCtrl() != null) {
					getManualScheduleDetailDialogCtrl().doPrepareSchdData(getFinanceDetail().getFinScheduleData(),
							false);
					appendScheduleDetailTab(false, false);
				}

			} else {
				if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
						getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
					fillComboBox(this.cbScheduleMethod, cbSchddemethod, PennantStaticListUtil.getScheduleMethods(),
							",NO_PAY,GRCNDPAY,PFTCAP,");
					return;
				}
			}
		}
		cbSchddemethod = getComboboxValue(this.cbScheduleMethod);
		logger.debug("Leaving" + event.toString());
	}

	public void onFulfill$repayRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		ForwardEvent forwardEvent = (ForwardEvent) event;
		String rateType = (String) forwardEvent.getOrigin().getData();
		if (StringUtils.equals(rateType, PennantConstants.RATE_BASE)) {
			this.repayRate.getEffRateComp().setConstraint("");
			Object dataObject = repayRate.getBaseObject();
			if (dataObject instanceof String) {
				this.repayRate.setBaseValue(dataObject.toString());
				this.repayRate.setBaseDescription("");
				this.repayRate.setEffRateValue(BigDecimal.ZERO);
			} else {
				BaseRateCode details = (BaseRateCode) dataObject;
				if (details != null) {
					this.repayRate.setBaseValue(details.getBRType());
					this.repayRate.setBaseDescription(details.getBRTypeDesc());
				}
			}
			if (StringUtils.isNotBlank(this.repayRate.getBaseValue())) {
				calculateRate(this.repayRate.getBaseValue(), this.finCcy.getValue(), this.repayRate.getSpecialComp(),
						this.repayRate.getBaseComp(), this.repayRate.getMarginValue(), this.repayRate.getEffRateComp(),
						this.finMinRate, this.finMaxRate);
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			this.repayRate.getEffRateComp().setConstraint("");
			Object dataObject = repayRate.getSpecialObject();

			if (dataObject instanceof String) {
				this.repayRate.setSpecialValue(dataObject.toString());
				this.repayRate.setSpecialDescription("");
				this.repayRate.setEffRateValue(BigDecimal.ZERO);
			} else {
				SplRateCode details = (SplRateCode) dataObject;
				if (details != null) {
					this.repayRate.setSpecialValue(details.getSRType());
					this.repayRate.setSpecialDescription(details.getSRTypeDesc());
				}
			}

			if (StringUtils.isNotBlank(this.repayRate.getSpecialValue())) {
				calculateRate(this.repayRate.getBaseValue(), this.finCcy.getValue(), this.repayRate.getSpecialComp(),
						this.repayRate.getBaseComp(), this.repayRate.getMarginValue(), this.repayRate.getEffRateComp(),
						this.finMinRate, this.finMaxRate);
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {
			if (this.repayRate.getMarginValue() != null) {
				RateDetail rateDetail = RateUtil.rates(this.repayRate.getBaseValue(), this.finCcy.getValue(),
						this.repayRate.getSpecialValue(),
						this.repayRate.getMarginValue() == null ? BigDecimal.ZERO : this.repayRate.getMarginValue(),
						this.finMinRate.getValue() != null && this.finMinRate.getValue().compareTo(BigDecimal.ZERO) <= 0
								? BigDecimal.ZERO : this.finMinRate.getValue(),
						this.finMaxRate.getValue() != null && this.finMaxRate.getValue().compareTo(BigDecimal.ZERO) <= 0
								? BigDecimal.ZERO : this.finMaxRate.getValue());
				this.repayRate.setEffRateText(
						PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
			}

		}
		logger.debug("Leaving " + event.toString());
	}

	/**********************************/
	/*** Step Policy Details ***/
	/**********************************/

	/**
	 * when clicks on button "Step Policy Detail"
	 * 
	 * @param event
	 */
	public void onFulfill$stepPolicy(Event event) {
		logger.debug("Entering " + event.toString());

		this.stepPolicy.setConstraint("");
		this.noOfSteps.setConstraint("");
		this.stepType.setConstraint("");
		this.stepPolicy.clearErrorMessage();
		this.noOfSteps.setErrorMessage("");
		this.stepType.setErrorMessage("");

		Object dataObject = stepPolicy.getObject();
		if (dataObject == null || dataObject instanceof String) {
			if (dataObject != null) {
				this.stepPolicy.setValue(dataObject.toString());
				this.stepPolicy.setDescription("");
			}
			fillComboBox(this.stepType, "", PennantStaticListUtil.getStepType(), "");
			getFinanceDetail().getFinScheduleData().getStepPolicyDetails().clear();

		} else {
			StepPolicyHeader detail = (StepPolicyHeader) dataObject;
			if (detail != null) {
				this.stepPolicy.setValue(detail.getPolicyCode(), detail.getPolicyDesc());
				fillComboBox(this.stepType, detail.getStepType(), PennantStaticListUtil.getStepType(), "");
				// Fetch Step Policy Details List
				List<StepPolicyDetail> policyList = getStepPolicyService()
						.getStepPolicyDetailsById(this.stepPolicy.getValue());
				this.noOfSteps.setValue(policyList.size());
				getFinanceDetail().getFinScheduleData().resetStepPolicyDetails(policyList);
			}
		}

		if (stepDetailDialogCtrl != null) {
			stepDetailDialogCtrl.doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
		}
		logger.debug("Leaving " + event.toString());
	}

	/*
	 * onCheck Event For Step Finance Check Box
	 */
	public void onCheck$stepFinance(Event event) {
		logger.debug("Entering : " + event.toString());
		doStepPolicyCheck(true);
		logger.debug("Leaving : " + event.toString());
	}

	private void doStepPolicyCheck(boolean isAction) {

		this.stepPolicy.setMandatoryStyle(false);
		this.stepPolicy.setConstraint("");
		this.stepPolicy.setErrorMessage("");
		this.stepPolicy.setValue("", "");

		this.alwManualSteps.setChecked(false);
		this.noOfSteps.setConstraint("");
		this.noOfSteps.setErrorMessage("");
		this.noOfSteps.setValue(0);
		this.row_manualSteps.setVisible(false);

		this.stepPolicy.setVisible(false);
		this.label_FinanceMainDialog_StepPolicy.setVisible(false);
		this.label_FinanceMainDialog_numberOfSteps.setVisible(false);
		this.hbox_numberOfSteps.setVisible(false);
		this.row_stepType.setVisible(false);

		Tab tab = getTab(AssetConstants.UNIQUE_ID_STEPDETAILS);
		if (tab != null) {
			tab.setVisible(this.stepFinance.isChecked());
		}

		//Clear Step Details Tab Data on User Action
		if (isAction) {
			getFinanceDetail().getFinScheduleData().getStepPolicyDetails().clear();
			if (stepDetailDialogCtrl != null) {
				stepDetailDialogCtrl.doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
			}
		}

		if (this.stepFinance.isChecked()) {
			FinanceType type = getFinanceDetail().getFinScheduleData().getFinanceType();
			if (type.isAlwManualSteps()
					|| getFinanceDetail().getFinScheduleData().getFinanceMain().isAlwManualSteps()) {
				this.row_manualSteps.setVisible(true);
			}
			if (type.isSteppingMandatory()) {
				this.stepFinance.setDisabled(true);
			}
			this.label_FinanceMainDialog_StepPolicy.setVisible(true);
			this.stepPolicy.setVisible(true);
			if (!StringUtils.trimToEmpty(type.getDftStepPolicy()).equals(PennantConstants.List_Select)) {
				this.stepPolicy.setValue(type.getDftStepPolicy(), type.getLovDescDftStepPolicyName());
			}
			this.stepPolicy.setMandatoryStyle(true);
			this.stepPolicy.setReadonly(isReadOnly("FinanceMainDialog_stepPolicy"));

			fillComboBox(this.stepType, type.getDftStepPolicyType(), PennantStaticListUtil.getStepType(), "");
			this.row_stepType.setVisible(true);
			this.space_stepType.setSclass("");
			this.stepType.setDisabled(true);

			//Filling Step Policy Details List
			if (isAction) {
				List<StepPolicyDetail> policyList = getStepPolicyService()
						.getStepPolicyDetailsById(this.stepPolicy.getValue());
				getFinanceDetail().getFinScheduleData().resetStepPolicyDetails(policyList);
				if (stepDetailDialogCtrl != null) {
					stepDetailDialogCtrl
							.doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
				} else {
					appendStepDetailTab(false, false);
				}
			}
		} else {
			if (isReadOnly("FinanceMainDialog_stepFinance")) {
				this.row_stepFinance.setVisible(false);
			}
		}
	}

	/*
	 * onCheck Event For Manual Steps Check Box
	 */
	public void onCheck$alwManualSteps(Event event) {
		logger.debug("Entering : " + event.toString());
		doAlwManualStepsCheck(true);
		logger.debug("Leaving : " + event.toString());
	}

	private void doAlwManualStepsCheck(boolean isAction) {

		this.stepPolicy.setConstraint("");
		this.stepPolicy.setErrorMessage("");

		this.noOfSteps.setConstraint("");
		this.noOfSteps.setErrorMessage("");
		this.noOfSteps.setValue(0);

		if (this.alwManualSteps.isChecked()) {
			this.stepPolicy.setMandatoryStyle(false);
			this.label_FinanceMainDialog_numberOfSteps.setVisible(true);
			this.hbox_numberOfSteps.setVisible(true);
			this.stepPolicy.setValue("", "");
			this.stepPolicy.setReadonly(true);
			this.space_stepType.setSclass(PennantConstants.mandateSclass);
			this.stepType.setDisabled(isReadOnly("FinanceMainDialog_stepType"));

		} else {

			if (this.financeDetail.isNewRecord()) {
				this.stepPolicy.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getDftStepPolicy());
				this.stepPolicy.setDescription(
						getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescDftStepPolicyName());
				this.stepType.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getDftStepPolicyType());
				fillComboBox(this.stepType,
						getFinanceDetail().getFinScheduleData().getFinanceType().getDftStepPolicyType(),
						PennantStaticListUtil.getStepType(), "");
			} else {
				this.stepPolicy.setValue(getFinanceDetail().getFinScheduleData().getFinanceMain().getStepPolicy());
				this.stepPolicy.setDescription(
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescStepPolicyName());
				fillComboBox(this.stepType, getFinanceDetail().getFinScheduleData().getFinanceMain().getStepType(),
						PennantStaticListUtil.getStepType(), "");
			}
			this.stepPolicy.setMandatoryStyle(true);
			this.label_FinanceMainDialog_numberOfSteps.setVisible(false);
			this.hbox_numberOfSteps.setVisible(false);
			this.stepPolicy.setReadonly(isReadOnly("FinanceMainDialog_stepPolicy"));
			this.stepType.setReadonly(isReadOnly("FinanceMainDialog_stepType"));
			this.space_stepType.setSclass("");
			this.stepType.setDisabled(true);
			if (isReadOnly("FinanceMainDialog_alwManualSteps")) {
				this.row_manualSteps.setVisible(false);
			}

		}

		if (stepDetailDialogCtrl != null) {
			stepDetailDialogCtrl.setAllowedManualSteps(this.alwManualSteps.isChecked());
		}

		//Filling Step Policy Details List
		if (isAction) {

			List<StepPolicyDetail> policyList = new ArrayList<StepPolicyDetail>();
			if (StringUtils.isNotEmpty(this.stepPolicy.getValue())) {
				policyList = getStepPolicyService().getStepPolicyDetailsById(this.stepPolicy.getValue());
			}
			getFinanceDetail().getFinScheduleData().resetStepPolicyDetails(policyList);
			if (stepDetailDialogCtrl != null) {
				stepDetailDialogCtrl.doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
			} else {
				appendStepDetailTab(false, false);
			}
		}
	}

	/**
	 * Method for getting selected Step Type Using In Step Detail Dialog Controller for EMI Validation
	 * 
	 * @return
	 */
	public String getStepType() {
		this.stepType.setConstraint("");
		this.stepType.setErrorMessage("");
		return getComboboxValue(this.stepType);
	}

	/**
	 * Method to calculate rates based on given base and special rate codes
	 * 
	 * @throws InterruptedException
	 **/
	private void calculateRate(String rate, String currency, ExtendedCombobox splRate, ExtendedCombobox lovFieldTextBox,
			BigDecimal margin, Decimalbox effectiveRate, Decimalbox minAllowedRate, Decimalbox maxAllowedRate)
			throws InterruptedException {
		logger.debug("Entering");

		RateDetail rateDetail = RateUtil.rates(rate, currency, splRate.getValue(), margin, minAllowedRate.getValue(),
				maxAllowedRate.getValue());
		if (rateDetail.getErrorDetails() == null) {
			effectiveRate.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
		} else {
			MessageUtil.showError(ErrorUtil
					.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			splRate.setValue("");
			lovFieldTextBox.setDescription("");
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to validate the data before generating the schedule
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 */
	private boolean doValidation(AuditHeader auditHeader) throws InterruptedException {
		logger.debug("Entering");

		int retValue = PennantConstants.porcessOVERIDE;
		while (retValue == PennantConstants.porcessOVERIDE) {

			FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

			ArrayList<ErrorDetail> errorList = new ArrayList<ErrorDetail>();

			//FinanceMain Details Tab ---> 1. Basic Details

			// validate finance currency
			if (!this.finCcy.isReadonly()) {

				if (StringUtils.isEmpty(this.finCcy.getValue())) {
					errorList.add(new ErrorDetail("finCcy", "30504", new String[] {}, new String[] {}));
				} else if (!this.finCcy.getValue().equals(financeType.getFinCcy())) {

					errorList.add(new ErrorDetail("finCcy", "65001",
							new String[] { this.finCcy.getValue(), financeType.getFinCcy() },
							new String[] { this.finCcy.getValue() }));
				}
			}

			// validate finance schedule method
			if (!this.cbScheduleMethod.isReadonly()) {

				if (getComboboxValue(this.cbScheduleMethod).equals(PennantConstants.List_Select)) {
					errorList.add(new ErrorDetail("scheduleMethod", "90189", new String[] {}, new String[] {}));
				} else if (!getComboboxValue(this.cbScheduleMethod).equals(financeType.getFinSchdMthd())) {

					errorList.add(new ErrorDetail("scheduleMethod", "65002",
							new String[] { getComboboxValue(this.cbScheduleMethod),
									getFinanceDetail().getFinScheduleData().getFinanceMain().getScheduleMethod() },
							new String[] { getComboboxValue(this.cbScheduleMethod) }));
				}
			}

			// validate finance profit days basis
			if (!this.cbProfitDaysBasis.isReadonly()) {
				if (getComboboxValue(this.cbProfitDaysBasis).equals(PennantConstants.List_Select)) {
					errorList.add(new ErrorDetail("profitDaysBasis", "30505", new String[] {}, new String[] {}));
				} else if (!getComboboxValue(this.cbProfitDaysBasis).equals(financeType.getFinDaysCalType())) {

					errorList.add(new ErrorDetail("profitDaysBasis", "65003",
							new String[] { getComboboxValue(this.cbProfitDaysBasis),
									getFinanceDetail().getFinScheduleData().getFinanceMain().getProfitDaysBasis() },
							new String[] { getComboboxValue(this.cbProfitDaysBasis) }));
				}
			}

			// validate finance reference number
			if (!this.finReference.isReadonly() && StringUtils.isNotBlank(this.finReference.getValue())) {
				if (getFinanceDetailService().isFinReferenceExits(this.finReference.getValue(), "_View", false)) {

					errorList.add(new ErrorDetail("finReference", "30506",
							new String[] { Labels.getLabel("label_FinanceMainDialog_FinReference.value"),
									this.finReference.getValue() },
							new String[] {}));
				}
			}
			//Step Policy Conditions Verification
			if (this.stepFinance.isChecked()) {

				if (StringUtils.equals(this.cbScheduleMethod.getSelectedItem().getValue().toString(),
						CalculationConstants.SCHMTHD_PFT)) {
					errorList.add(new ErrorDetail("StepFinance", "30552",
							new String[] { Labels.getLabel("label_ScheduleMethod_CalculatedProfit") },
							new String[] {}));
				}

				if (StringUtils.equals(this.stepType.getSelectedItem().getValue().toString(),
						FinanceConstants.STEPTYPE_PRIBAL)
						&& StringUtils.equals(this.cbScheduleMethod.getSelectedItem().getValue().toString(),
								CalculationConstants.SCHMTHD_EQUAL)) {
					errorList.add(new ErrorDetail("StepFinance", "30555",
							new String[] { Labels.getLabel("label_ScheduleMethod_Equal") }, new String[] {}));
				}

				if (StringUtils.equals(this.stepType.getSelectedItem().getValue().toString(),
						FinanceConstants.STEPTYPE_EMI)
						&& !StringUtils.equals(this.cbScheduleMethod.getSelectedItem().getValue().toString(),
								CalculationConstants.SCHMTHD_EQUAL)) {
					errorList.add(new ErrorDetail("StepFinance", "30703",
							new String[] { Labels.getLabel("label_ScheduleMethod_Equal") }, new String[] {}));
				}

				if (ImplementationConstants.IMPLEMENTATION_ISLAMIC) {
					if (StringUtils.equals(
							getFinanceDetail().getFinScheduleData().getFinanceType().getProductCategory(),
							FinanceConstants.PRODUCT_MURABAHA)) {
						if (StringUtils.equals(this.repayRateBasis.getSelectedItem().getValue().toString(),
								CalculationConstants.RATE_BASIS_F)) {
							errorList.add(new ErrorDetail("StepFinance", "30553",
									new String[] { Labels.getLabel("label_Flat") }, new String[] {}));
						}

						if (StringUtils.equals(this.stepType.getSelectedItem().getValue().toString(),
								FinanceConstants.STEPTYPE_EMI)) {
							if (StringUtils.equals(this.cbScheduleMethod.getSelectedItem().getValue().toString(),
									CalculationConstants.SCHMTHD_EQUAL)
									&& StringUtils.equals(this.repayRateBasis.getSelectedItem().getValue().toString(),
											CalculationConstants.RATE_BASIS_R)) {
								errorList.add(new ErrorDetail("StepFinance", "30554",
										new String[] { Labels.getLabel("label_ScheduleMethod_Equal"),
												Labels.getLabel("label_Reduce") },
										new String[] {}));
							}
						}
					}
				}
				if (stepDetailDialogCtrl != null) {
					errorList.addAll(stepDetailDialogCtrl.doValidateStepDetails(
							getFinanceDetail().getFinScheduleData().getFinanceMain(), this.numberOfTerms_two.intValue(),
							this.alwManualSteps.isChecked(), this.noOfSteps.intValue(),
							this.stepType.getSelectedItem().getValue().toString()));
				}

				//both step and EMI holiday not allowed
				if (getFinanceDetail().getFinScheduleData().getFinanceMain().isPlanEMIHAlw()) {
					errorList.add(new ErrorDetail("30573", null));
				}
			}

			//FinanceMain Details Tab ---> 2. Grace Period Details

			if (getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowGrcPeriod()) {

				// validate finance grace period end date
				if (!this.gracePeriodEndDate.isReadonly() && this.gracePeriodEndDate_two.getValue() != null
						&& this.finStartDate.getValue() != null) {

					if (this.gracePeriodEndDate_two.getValue().before(this.finStartDate.getValue())) {
						errorList.add(new ErrorDetail("gracePeriodEndDate", "30518",
								new String[] { PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.finStartDate.getValue(), "") },
								new String[] {}));
					}
				}

				if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CHGGRCEND)) {
					Date curBussDate = DateUtility.getAppDate();
					if (this.gracePeriodEndDate_two.getValue().before(DateUtility.addDays(curBussDate, 1))) {
						errorList.add(new ErrorDetail("gracePeriodEndDate", "30569",
								new String[] {
										Labels.getLabel("label_IjarahFinanceMainDialog_GracePeriodEndDate.value"),
										PennantAppUtil.formateDate(DateUtility.addDays(curBussDate, 1), "") },
								new String[] {}));
					}
				}

				if (!this.cbGrcSchdMthd.isReadonly() && this.allowGrcRepay.isChecked()) {

					if (getComboboxValue(this.cbGrcSchdMthd).equals(PennantConstants.List_Select)) {
						errorList.add(new ErrorDetail("scheduleMethod", "90189", new String[] {}, new String[] {}));

					} else if (!getComboboxValue(this.cbGrcSchdMthd).equals(financeType.getFinGrcSchdMthd())) {

						errorList.add(new ErrorDetail("scheduleMethod", "65002",
								new String[] { getComboboxValue(this.cbGrcSchdMthd),
										getFinanceDetail().getFinScheduleData().getFinanceMain().getGrcSchdMthd() },
								new String[] { getComboboxValue(this.cbGrcSchdMthd) }));
					}
				}

				// validate finance profit rate
				if (!this.graceRate.isBaseReadonly() && StringUtils.isEmpty(this.graceRate.getBaseValue())) {
					errorList.add(new ErrorDetail("graceBaseRate", "30513", new String[] {}, new String[] {}));
				}

				// validate selected profit date is matching to profit frequency or not
				if (!this.gracePftFrq.validateFrquency(this.nextGrcPftDate_two.getValue(),
						this.gracePeriodEndDate.getValue())) {
					errorList.add(new ErrorDetail("nextGrcPftDate_two", "65004",
							new String[] { Labels.getLabel("label_FinanceMainDialog_NextGrcPftDate.value"),
									Labels.getLabel("label_FinanceMainDialog_GracePftFrq.value"),
									Labels.getLabel("finGracePeriodDetails") },
							new String[] { this.nextGrcPftDate_two.getValue().toString(),
									this.gracePftFrq.getValue() }));
				}

				if (!this.nextGrcPftDate.isReadonly() && this.nextGrcPftDate_two.getValue() != null) {

					if (this.nextGrcPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {

						errorList.add(new ErrorDetail("nextGrcPftDate_two", "90161",
								new String[] { PennantAppUtil.formateDate(this.nextGrcPftDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },
								new String[] {}));
					}

					if (this.nextGrcPftDate_two.getValue().before(this.finStartDate.getValue())) {
						errorList.add(new ErrorDetail("nextGrcPftDate_two", "90162",
								new String[] { PennantAppUtil.formateDate(this.nextGrcPftDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.finStartDate.getValue(), "") },
								new String[] {}));
					}
				}

				// validate selected profit review date is matching to review
				// frequency or not
				if (!this.gracePftRvwFrq.validateFrquency(this.nextGrcPftRvwDate_two.getValue(),
						this.gracePeriodEndDate.getValue())) {
					errorList.add(new ErrorDetail("nextGrcPftRvwDate_two", "65004",
							new String[] { Labels.getLabel("label_FinanceMainDialog_NextGrcPftRvwDate.value"),
									Labels.getLabel("label_FinanceMainDialog_GracePftRvwFrq.value"),
									Labels.getLabel("finGracePeriodDetails") },
							new String[] { this.nextGrcPftRvwDate_two.getValue().toString(),
									this.gracePftRvwFrq.getValue() }));
				}

				if (!this.nextGrcPftRvwDate.isReadonly() && this.nextGrcPftRvwDate_two.getValue() != null) {

					if (this.nextGrcPftRvwDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						errorList.add(new ErrorDetail("nextGrcPftRvwDate_two", "30520",
								new String[] { PennantAppUtil.formateDate(this.nextGrcPftRvwDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },
								new String[] {}));
					}

					if (this.nextGrcPftRvwDate_two.getValue().before(this.finStartDate.getValue())) {
						errorList.add(new ErrorDetail("nextGrcPftRvwDate_two", "30530",
								new String[] { PennantAppUtil.formateDate(this.nextGrcPftRvwDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.finStartDate.getValue(), "") },
								new String[] {}));
					}
				}

				// validate selected capitalization date is matching to capital
				// frequency or not
				if (!this.graceCpzFrq.validateFrquency(this.nextGrcCpzDate_two.getValue(),
						this.gracePeriodEndDate.getValue())) {
					errorList.add(new ErrorDetail("nextGrcCpzDate_two", "65004",
							new String[] { Labels.getLabel("label_FinanceMainDialog_NextGrcCpzDate.value"),
									Labels.getLabel("label_FinanceMainDialog_GraceCpzFrq.value"),
									Labels.getLabel("finGracePeriodDetails") },
							new String[] { this.nextGrcCpzDate_two.getValue().toString(),
									this.graceCpzFrq.getValue() }));
				}

				if (!this.nextGrcCpzDate.isReadonly() && this.nextGrcCpzDate_two.getValue() != null) {

					if (this.nextGrcCpzDate_two.getValue().before(this.nextGrcPftDate_two.getValue())) {
						errorList.add(new ErrorDetail("nextGrcCpzDate_two", "30526",
								new String[] { PennantAppUtil.formateDate(this.nextGrcCpzDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.nextGrcPftDate_two.getValue(), "") },
								new String[] {}));
					}

					if (this.nextGrcCpzDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						errorList.add(new ErrorDetail("nextGrcCpzDate_two", "30521",
								new String[] { PennantAppUtil.formateDate(this.nextGrcCpzDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },
								new String[] {}));
					}

					if (this.nextGrcCpzDate_two.getValue().before(this.finStartDate.getValue())) {
						errorList.add(new ErrorDetail("nextGrcCpzDate_two", "30531",
								new String[] { PennantAppUtil.formateDate(this.nextGrcCpzDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.finStartDate.getValue(), "") },
								new String[] {}));
					}
				}
			}

			//FinanceMain Details Tab ---> 3. Repayment Period Details
			if (this.odYearlyTerms.getValue() != null && this.odMnthlyTerms.getValue() != null) {
				int tenor = (this.odYearlyTerms.intValue() * 12) + this.odMnthlyTerms.intValue();
				int maxalwdyears = SysParamUtil.getValueAsInt("MAX_FIN_YEARS") * 12;
				if (tenor > maxalwdyears) {
					errorList.add(new ErrorDetail("odyearlyTerms", "30578",
							new String[] { Labels.getLabel("label_FinanceMainDialog_ODTenor.value"),
									String.valueOf(SysParamUtil.getValueAsInt("MAX_FIN_YEARS")) },
							null));
				}
			}
			if (!this.repayRate.isBaseReadonly() && StringUtils.isEmpty(this.repayRate.getBaseValue())) {
				errorList.add(new ErrorDetail("repayBaseRate", "30513", new String[] {}, null));
			}

			if (this.row_RpyAdvBaseRate.isVisible() && !this.rpyAdvRate.isBaseReadonly()
					&& this.repayRate.getEffRateValue().compareTo(this.rpyAdvRate.getEffRateValue()) < 0) {
				errorList.add(new ErrorDetail("rpyAdvBaseRate", "30551",
						new String[] { Labels.getLabel("label_FinanceMainDialog_RpyAdvPftRate.value"),
								Labels.getLabel("label_FinanceMainDialog_ProfitRate.value") },
						null));
			}

			// validate selected repayments date is matching to repayments
			// frequency or not
			if (!this.repayFrq.validateFrquency(this.nextRepayDate_two.getValue(),
					this.gracePeriodEndDate.getValue())) {
				errorList.add(new ErrorDetail("nextRepayDate_two", "65004",
						new String[] { Labels.getLabel("label_FinanceMainDialog_NextRepayDate.value"),
								Labels.getLabel("label_FinanceMainDialog_RepayFrq.value"),
								Labels.getLabel("finRepaymentDetails") },
						new String[] { this.nextRepayDate_two.getValue().toString(), this.repayFrq.getValue() }));
			}

			if (!this.nextRepayDate.isReadonly() && this.nextRepayDate_two.getValue() != null) {
				if (!this.nextRepayDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {

					String errorCode = "30544";
					if (this.allowGrace.isChecked()) {
						errorCode = "30522";
					}
					errorList.add(new ErrorDetail("nextRepayDate_two", errorCode,
							new String[] { PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(), ""),
									PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },
							new String[] {}));
				}
				if (this.rpyPftFrqRow.isVisible()
						&& this.nextRepayDate_two.getValue().before(this.nextRepayPftDate_two.getValue())) {
					errorList.add(new ErrorDetail("nextRepayDate_two", "30534",
							new String[] { PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(), ""),
									PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(), "") },
							new String[] {}));
				}
			}

			// validate selected repayments profit date is matching to repay
			// profit frequency or not
			if (!this.rpyPftFrqRow.isVisible()) {
				this.repayPftFrq.setFrqValue(this.repayFrq.getValue());
			} else {
				if (!this.repayPftFrq.validateFrquency(this.nextRepayPftDate_two.getValue(),
						this.gracePeriodEndDate.getValue())) {
					errorList.add(new ErrorDetail("nextRepayPftDate_two", "65004",
							new String[] { Labels.getLabel("label_FinanceMainDialog_NextRepayPftDate.value"),
									Labels.getLabel("label_FinanceMainDialog_RepayPftFrq.value"),
									Labels.getLabel("WIFinRepaymentDetails") },
							new String[] { this.nextRepayPftDate_two.getValue().toString(),
									this.repayPftFrq.getValue() }));
				}

				if (!this.nextRepayPftDate.isReadonly() && this.nextRepayPftDate_two.getValue() != null) {
					if (!this.nextRepayPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						errorList.add(new ErrorDetail("nextRepayPftDate_two", "30523",
								new String[] { PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },
								new String[] {}));
					}
				}
			}

			// validate selected repayments review date is matching to repay
			// review frequency or not
			if (!this.repayRvwFrq.validateFrquency(this.nextRepayRvwDate_two.getValue(),
					this.gracePeriodEndDate.getValue())) {
				errorList.add(new ErrorDetail("nextRepayRvwDate_two", "65004",
						new String[] { Labels.getLabel("label_FinanceMainDialog_NextRepayRvwDate.value"),
								Labels.getLabel("label_FinanceMainDialog_RepayRvwFrq.value"),
								Labels.getLabel("finRepaymentDetails") },
						new String[] { this.nextRepayRvwDate_two.getValue().toString(), this.repayRvwFrq.getValue() }));
			}

			if (!this.nextRepayRvwDate.isReadonly() && this.nextRepayRvwDate_two.getValue() != null) {
				if (!this.nextRepayRvwDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
					errorList.add(new ErrorDetail("nextRepayRvwDate_two", "30524",
							new String[] { PennantAppUtil.formateDate(this.nextRepayRvwDate_two.getValue(), ""),
									PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },
							new String[] {}));
				}
			}

			// validate selected repayments capital date is matching to repay
			// capital frequency or not
			if (!this.repayCpzFrq.validateFrquency(this.nextRepayCpzDate_two.getValue(),
					this.gracePeriodEndDate.getValue())) {
				errorList.add(new ErrorDetail("nextRepayCpzDate_two", "65004",
						new String[] { Labels.getLabel("label_FinanceMainDialog_NextRepayCpzDate.value"),
								Labels.getLabel("label_FinanceMainDialog_RepayCpzFrq.value"),
								Labels.getLabel("finRepaymentDetails") },
						new String[] { this.nextRepayCpzDate_two.getValue().toString(), this.repayCpzFrq.getValue() }));
			}

			if (!this.nextRepayCpzDate.isReadonly() && this.nextRepayCpzDate_two.getValue() != null) {

				if (!this.nextRepayCpzDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
					errorList.add(new ErrorDetail("nextRepayCpzDate_two", "30525",
							new String[] { PennantAppUtil.formateDate(this.nextRepayCpzDate_two.getValue(), ""),
									PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },
							new String[] {}));
				}

				if (this.nextRepayPftDate_two.getValue() != null) {
					if (this.nextRepayCpzDate_two.getValue().before(this.nextRepayPftDate_two.getValue())) {
						errorList.add(new ErrorDetail("nextRepayCpzDate_two", "30528",
								new String[] { PennantAppUtil.formateDate(this.nextRepayCpzDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(), "") },
								new String[] {}));
					}
				}
			}

			// validate selected repayments date is matching to repayments
			// frequency or not
			if (!this.rolloverFrq.validateFrquency(this.nextRollOverDate_two.getValue(),
					this.gracePeriodEndDate.getValue())) {
				errorList.add(new ErrorDetail("nextRolloverDate_two", "65004",
						new String[] { Labels.getLabel("label_FinanceMainDialog_NextRolloverDate.value"),
								Labels.getLabel("label_FinanceMainDialog_RolloverFrq.value"),
								Labels.getLabel("finRepaymentDetails") },
						new String[] { this.nextRollOverDate_two.getValue().toString(), this.rolloverFrq.getValue() }));
			}

			if (!this.nextRollOverDate.isReadonly() && this.nextRollOverDate_two.getValue() != null) {
				if (!this.nextRollOverDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {

					String errorCode = "30544";
					if (this.allowGrace.isChecked()) {
						errorCode = "30522";
					}
					errorList.add(new ErrorDetail("nextRolloverDate_two", errorCode,
							new String[] { PennantAppUtil.formateDate(this.nextRollOverDate_two.getValue(), ""),
									PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },
							new String[] {}));
				}
			}

			// validate next repay date 
			if (StringUtils.equals(financeType.getFinDivision(), FinanceConstants.FIN_DIVISION_RETAIL)) {
				int diffDays = 0;
				int maxInstAlwDays = SysParamUtil.getValueAsInt("MAXDAYS_NEXT_INSTDATE");
				if (maxInstAlwDays != -1) {
					Date firstInstlDate = this.nextRepayDate_two.getValue();
					if (this.finRepayPftOnFrq.isChecked()) {
						if (this.nextRepayPftDate_two.getValue().compareTo(this.nextRepayDate_two.getValue()) < 0) {
							firstInstlDate = this.nextRepayPftDate_two.getValue();
						}
					}
					diffDays = DateUtility.getDaysBetween(this.finStartDate.getValue(), firstInstlDate);
					if (this.allowGrace.isChecked()) {
						diffDays = DateUtility.getDaysBetween(this.gracePeriodEndDate_two.getValue(), firstInstlDate);
					}
					if (diffDays > maxInstAlwDays) {
						this.financeTypeDetailsTab.setSelected(true);
						/*
						 * Date nextRepayDate = DateUtility.addDays(this.finStartDate.getValue(), maxInstAlwDays);
						 * if(this.allowGrace.isChecked()) { nextRepayDate =
						 * DateUtility.addDays(this.gracePeriodEndDate_two.getValue(), maxInstAlwDays); }
						 * errorList.add(new ErrorDetails("nextRepayDate_two", "30547", new String[] {
						 * Labels.getLabel("label_WIFinNextRepaymentDate"), String.valueOf(maxInstAlwDays),
						 * DateUtility.formatToLongDate(nextRepayDate)}, new String[] {}));
						 */
					}
				}
			}

			boolean singleTermFinance = false;
			if (financeType.getFinMinTerm() == 1 && financeType.getFinMaxTerm() == 1) {
				singleTermFinance = true;
			}

			if (!this.numberOfTerms.isReadonly() && this.numberOfTerms.intValue() != 0 && !singleTermFinance
					&& !this.manualSchedule.isChecked()) {
				if (this.numberOfTerms.intValue() >= 1 && this.maturityDate.getValue() != null) {
					errorList
							.add(new ErrorDetail("numberOfTerms", "30511",
									new String[] { Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"),
											Labels.getLabel("label_FinanceMainDialog_MaturityDate.value") },
									new String[] {}));
				}
			}

			if (!this.maturityDate.isReadonly() && !singleTermFinance && !this.manualSchedule.isChecked()) {
				if (this.maturityDate.getValue() != null && (this.numberOfTerms.intValue() >= 1)
						&& !singleTermFinance) {
					errorList
							.add(new ErrorDetail("maturityDate", "30511",
									new String[] { Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"),
											Labels.getLabel("label_FinanceMainDialog_MaturityDate.value") },
									new String[] {}));
				}
			}

			if (this.maturityDate_two.getValue() != null) {
				if (this.maturityDate_two.getValue().compareTo(appEndDate) > 0) {
					errorList.add(new ErrorDetail("maturityDate", "30510",
							new String[] { Labels.getLabel("label_FinanceMainDialog_MaturityDate.value"),
									PennantAppUtil.formateDate(this.appEndDate, "") },
							new String[] {}));
				}
				if (!this.nextRepayDate.isReadonly()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayDate_two.getValue())) {
						errorList.add(new ErrorDetail("maturityDate", "30527",
								new String[] { PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
										Labels.getLabel("label_FinanceMainDialog_NextRepayDate.value"),
										PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(), "") },
								new String[] {}));
					}
				}

				if (!this.nextRepayPftDate.isReadonly()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayPftDate_two.getValue())) {
						errorList.add(new ErrorDetail("maturityDate", "30527",
								new String[] { PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
										Labels.getLabel("label_FinanceMainDialog_NextRepayPftDate.value"),
										PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(), "") },
								new String[] {}));
					}
				}

				if (!this.nextRepayCpzDate.isReadonly()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayCpzDate_two.getValue())) {
						errorList.add(new ErrorDetail("maturityDate", "30527",
								new String[] { PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
										Labels.getLabel("label_FinanceMainDialog_NextRepayCpzDate.value"),
										PennantAppUtil.formateDate(this.nextRepayCpzDate_two.getValue(), "") },
								new String[] {}));
					}
				}
			}

			// validate finance grace profit days basis
			if (!this.grcPftDaysBasis.isDisabled() && this.gb_gracePeriodDetails.isVisible()) {
				if (getComboboxValue(this.grcPftDaysBasis).equals(PennantConstants.List_Select)) {
					errorList.add(new ErrorDetail("grcPftDaysBasis", "30505", new String[] {}, new String[] {}));
				} else if (!getComboboxValue(this.grcPftDaysBasis).equals(financeType.getFinDaysCalType())) {

					errorList.add(new ErrorDetail("grcPftDaysBasis", "65003",
							new String[] { getComboboxValue(this.grcPftDaysBasis),
									getFinanceDetail().getFinScheduleData().getFinanceMain().getGrcProfitDaysBasis() },
							new String[] { getComboboxValue(this.grcPftDaysBasis) }));
				}
			}

			if (this.finRepayPftOnFrq.isChecked()) {
				String errorCode = FrequencyUtil.validateFrequencies(this.repayPftFrq.getValue(),
						this.repayFrq.getValue());
				if (StringUtils.isNotBlank(errorCode)) {
					errorList
							.add(new ErrorDetail("Frequency", "30539",
									new String[] { Labels.getLabel("label_FinanceMainDialog_RepayPftFrq.value"),
											Labels.getLabel("label_FinanceMainDialog_RepayFrq.value") },
									new String[] {}));
				}
			}

			if (getFinanceDetail().getFinScheduleData().getFinanceType().isAllowDownpayPgm()
					&& this.downPayBank.getActualValue().compareTo(BigDecimal.ZERO) <= 0) {
				errorList.add(new ErrorDetail("Frequency", "30543", new String[] {}, new String[] {}));
			}

			// BPI Validations
			if (StringUtils.isEmpty(moduleDefiner) && this.alwBpiTreatment.isChecked()
					&& !StringUtils.equals(FinanceConstants.BPI_NO, getComboboxValue(this.dftBpiTreatment))) {
				String frqBPI = "";
				Date frqDate = null;

				if (getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowGrcPeriod()) {
					frqBPI = this.gracePftFrq.getValue();
					frqDate = this.nextGrcPftDate_two.getValue();
				} else {
					frqBPI = this.repayPftFrq.getValue();
					frqDate = this.nextRepayPftDate_two.getValue();
				}

				Date bpiDate = DateUtility.getDate(DateUtility.formatUtilDate(
						FrequencyUtil.getNextDate(frqBPI, 1, this.finStartDate.getValue(),
								HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate(),
						PennantConstants.dateFormat));

				if (DateUtility.compare(bpiDate, frqDate) >= 0) {
					errorList.add(new ErrorDetail("30571", null));

				}
			}

			// Planned EMI Holiday Validations
			if (this.alwPlannedEmiHoliday.isChecked()) {
				String rpyFrq = getFinanceDetail().getFinScheduleData().getFinanceMain().getRepayFrq();
				if (!StringUtils.equals(String.valueOf(rpyFrq.charAt(0)), FrequencyCodeTypes.FRQ_MONTHLY)) {
					errorList.add(new ErrorDetail("30572", null));
				}
			}

			//Validate insurance frequency with repayments frequency ,insfrq must be after repayment frq
			List<FinInsurances> insurances = getFinanceDetail().getFinScheduleData().getFinInsuranceList();
			if (insurances != null && !insurances.isEmpty()) {
				String repayFrqDay = FrequencyUtil
						.getFrequencyDay(getFinanceDetail().getFinScheduleData().getFinanceMain().getRepayFrq());
				for (FinInsurances finInsurance : insurances) {
					if (StringUtils.isNotEmpty(finInsurance.getInsuranceFrq())) {
						String insFrqDay = FrequencyUtil.getFrequencyDay(finInsurance.getInsuranceFrq());
						if (!StringUtils.equals(repayFrqDay, insFrqDay)) {
							errorList.add(new ErrorDetail("InsuranceFrq", "30545",
									new String[] { finInsurance.getInsuranceType() }, new String[] {}));
							break;
						}
					}
				}
			}

			// Setting error list to audit header
			auditHeader.setErrorList(ErrorUtil.getErrorDetails(errorList, getUserWorkspace().getUserLanguage()));
			auditHeader = ErrorControl.showErrorDetails(mainWindow, auditHeader);
			auditHeader.getOverideCount();

			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				return true;
			} else if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}
		setOverideMap(auditHeader.getOverideMap());
		logger.debug("Entering");
		return false;
	}

	@Override
	public String getReference() {
		if (StringUtils.isEmpty(this.financeDetail.getFinScheduleData().getFinanceMain().getFinReference())) {
			return "";
		} else {
			return String.valueOf(this.financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
		}
	}

	/**
	 * Method for Fetching Account Balance
	 * 
	 * @param acId
	 * @return
	 */
	protected String getAcBalance(String acId) {
		if (StringUtils.isNotBlank(acId)) {
			return PennantAppUtil.amountFormate(getAccountInterfaceService().getAccountAvailableBal(acId),
					CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));
		} else {
			return "";
		}
	}

	public void setDisableCbFrqs(boolean isReadOnly, Combobox cbFrq1, Combobox cbFrq2, Combobox cbFrq3) {
		readOnlyComponent(isReadOnly, cbFrq1);
		readOnlyComponent(isReadOnly, cbFrq2);
		readOnlyComponent(isReadOnly, cbFrq3);
	}

	/**
	 * Get the Finance Main Details from the Screen
	 * 
	 * @return
	 */
	public FinanceMain getFinanceMain() {
		doClearMessage();

		int formatter = CurrencyUtil.getFormat(this.finCcy.getValue());
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(StringUtils.trimToEmpty(this.finReference.getValue()));
		financeMain.setCustID(this.custID.longValue());
		financeMain.setLovDescCustCIF(this.custCIF.getValue());
		financeMain.setLovDescCustShrtName(this.custShrtName.getValue());
		financeMain.setFinCcy(this.finCcy.getValue());
		financeMain.setFinBranch(this.finBranch.getValue());
		financeMain.setFinAmount(PennantAppUtil.unFormateAmount(this.finAmount.getActualValue(), formatter));
		financeMain.setFinAssetValue(PennantAppUtil.unFormateAmount(this.finAssetValue.getActualValue(), formatter));
		financeMain.setFinCurrAssetValue(
				PennantAppUtil.unFormateAmount(this.finCurrentAssetValue.getActualValue(), formatter));
		financeMain.setFinStartDate(this.finStartDate.getValue());
		financeMain.setMaturityDate(this.maturityDate.getValue());
		financeMain.setDownPayment(PennantAppUtil
				.unFormateAmount(this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue()), formatter));
		financeMain.setLovDescFinProduct(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCategory());
		financeMain.setScheduleMethod(this.cbScheduleMethod.getSelectedItem() != null
				? this.cbScheduleMethod.getSelectedItem().getValue().toString() : "");
		financeMain.setRepayProfitRate(this.repayProfitRate.getValue());
		financeMain.setRepayRateBasis(this.repayRateBasis.getSelectedItem() != null
				? this.repayRateBasis.getSelectedItem().getValue().toString() : "");
		financeMain.setRpyMinRate(this.finMinRate.getValue());
		financeMain.setRpyMaxRate(this.finMaxRate.getValue());
		financeMain.setGrcPeriodEndDate(this.gracePeriodEndDate.getValue() != null ? this.gracePeriodEndDate.getValue()
				: this.gracePeriodEndDate_two.getValue());
		if (this.allowGrace.isChecked()) {
			financeMain.setGrcSchdMthd(getComboboxValue(this.cbGrcSchdMthd));
		}
		financeMain.setAllowGrcPeriod(this.allowGrace.isChecked());
		financeMain.setFeeChargeAmt(getFinanceDetail().getFinScheduleData().getFinanceMain().getFeeChargeAmt());
		return financeMain;
	}

	public void updateFinanceMain(FinanceMain financeMain) {
		logger.debug("Entering");
		getFinanceDetail().getFinScheduleData().setFinanceMain(financeMain);
		logger.debug("Leaving");

	}

	public void setReasonDetails(ReasonHeader reasonHeader) {
		logger.debug(Literal.ENTERING);
		getFinanceDetail().setReasonHeader(reasonHeader);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Executing Eligibility Details
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public FinanceDetail onExecuteStageAccDetail()
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		buildEvent = false;

		doSetValidation();
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			if (!this.disbAcctId.isReadonly()) {
				this.disbAcctId.validateValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.repayAcctId.isReadonly()) {
				this.repayAcctId.validateValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.downPayAccount.isReadonly()) {
				this.downPayAccount.validateValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		showErrorDetails(wve, financeTypeDetailsTab);
		wve = null;
		doWriteComponentsToBean(getFinanceDetail().getFinScheduleData());
		getFinanceDetail().setModuleDefiner(
				StringUtils.isEmpty(moduleDefiner) ? FinanceConstants.FINSER_EVENT_ORG : moduleDefiner);

		logger.debug("Leaving");
		return getFinanceDetail();
	}

	/**
	 * Method for Returning Finance Amount and Currency Data for Contributor Validation
	 * 
	 * @return
	 */
	public List<Object> prepareContributor() {
		logger.debug("Entering");
		List<Object> list = new ArrayList<Object>();

		this.finAmount.setConstraint("");
		this.finAmount.setErrorMessage("");
		this.finCcy.setConstraint("");
		this.finCcy.setErrorMessage("");

		list.add(this.finAmount.getActualValue());
		list.add(this.finCcy.getValue());
		list.add(this.downPayBank.getActualValue());
		logger.debug("Leaving");
		return list;

	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceSchData
	 *            (FinScheduleData)
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterruptedException
	 */
	protected ArrayList<WrongValueException> doWriteComponentsToBean(FinScheduleData aFinanceSchData)
			throws InterruptedException, IllegalAccessException, InvocationTargetException {

		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		FinanceMain aFinanceMain = aFinanceSchData.getFinanceMain();
		int formatter = CurrencyUtil.getFormat(aFinanceMain.getFinCcy());
		ArrayList<WrongValueException> wve = new ArrayList<>();
		doClearMessage();

		boolean isOverDraft = false;

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())) {
			isOverDraft = true;
		}

		doSetValidation();
		doSetLOVValidation();

		//FinanceMain Detail Tab ---> 1. Basic Details
		if (isBranchanged) {
			aFinanceMain.setSwiftBranchCode(branchSwiftCode);
		} else {
			aFinanceMain.setSwiftBranchCode(getFinanceDetail().getCustomerDetails().getCustomer().getCustSwiftBrnCode());
		}

		try {
			aFinanceMain.setFinBranch(this.finBranch.getValue());
			aFinanceMain.setLovDescFinBranchName(this.finBranch.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		
		Date financeDate = null;

		try {
			if (isBranchanged || StringUtils.isBlank(this.finReference.getValue())) {
				this.finReference
						.setValue(String.valueOf(ReferenceGenerator.generateFinRef(aFinanceMain, financeType)));
				isBranchanged = false;
			}
			aFinanceMain.setFinReference(this.finReference.getValue());
			aFinanceSchData.setFinReference(this.finReference.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setApplicationNo(this.applicationNo.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setLovDescFinTypeName(this.lovDescFinTypeName.getValue());
			aFinanceMain.setFinType(this.finType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.isEmpty(this.finCcy.getValue())) {
				wve.add(new WrongValueException(this.finCcy, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("label_FinanceMainDialog_FinCcy.value") })));
			} else {
				aFinanceMain.setFinCcy(this.finCcy.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		/*
		 * try { if (StringUtils.isNotEmpty(financeType.getProduct())) { // calculate max allowed future Promotion Date
		 * Date prmotionEndDate = DateUtility.addDays(financeType.getEndDate(),
		 * SysParamUtil.getValueAsInt("ALWPROMO_MAX_FUTUREDAYS")); if (this.finStartDate.getValue() != null) { if
		 * (this.finStartDate.getValue().compareTo(financeType.getStartDate()) < 0 ||
		 * this.finStartDate.getValue().compareTo(prmotionEndDate) > 0) { throw new
		 * WrongValueException(this.finStartDate, Labels.getLabel( "DATE_ALLOWED_RANGE", new String[] {
		 * Labels.getLabel("label_FinStartDate"), DateUtility.formatToShortDate(financeType.getStartDate()),
		 * DateUtility.formatToShortDate(prmotionEndDate) })); } } } } catch (WrongValueException we) { wve.add(we); }
		 */

		try {
			if (getComboboxValue(this.cbScheduleMethod).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.cbScheduleMethod, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceMainDialog_ScheduleMethod.value") }));
			}
			aFinanceMain.setScheduleMethod(getComboboxValue(this.cbScheduleMethod));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (getComboboxValue(this.cbProfitDaysBasis).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.cbProfitDaysBasis, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceMainDialog_ProfitDaysBasis.value") }));
			}

			aFinanceMain.setProfitDaysBasis(getComboboxValue(this.cbProfitDaysBasis));

		} catch (WrongValueException we) {
			wve.add(we);
		}



		try {

			if (!this.custCIF.isReadonly()) {
				if (this.custID.longValue() == 0 || this.custID.longValue() == Long.MIN_VALUE) {
					throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID",
							new String[] { Labels.getLabel("label_FinanceMainDialog_CustID.value") }));
				}
			}
			aFinanceMain.setCustID(this.custID.longValue());
			aFinanceMain.setLovDescCustCIF(this.custCIF.getValue());
			aFinanceMain.setLovDescCustShrtName(this.custShrtName.getValue());
			if (StringUtils.isNotBlank(this.custCIF.getValue())) {

				try {
					if (recSave) {
						this.disbAcctId.validateValue();
						aFinanceMain.setDisbAccountId(
								PennantApplicationUtil.unFormatAccountNumber(this.disbAcctId.getValue()));
					} else {
						aFinanceMain.setDisbAccountId(
								PennantApplicationUtil.unFormatAccountNumber(this.disbAcctId.getValidatedValue()));
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					List<SecondaryAccount> secAccList = financeDetail.getFinScheduleData().getFinanceMain()
							.getSecondaryAccount();
					if (secAccList != null && !secAccList.isEmpty()) {
						for (SecondaryAccount secondaryAccount : secAccList) {
							if (StringUtils.equals(this.repayAcctId.getValue(), secondaryAccount.getAccountNumber())) {
								throw new WrongValueException(this.repayAcctId, Labels.getLabel("PAYMENTACC_EXISTS",
										new String[] { Labels.getLabel("label_FinanceMainDialog_RepayAcctId.value") }));
							}
						}
					}
					if (recSave) {
						this.repayAcctId.validateValue();
						aFinanceMain.setRepayAccountId(
								PennantApplicationUtil.unFormatAccountNumber(this.repayAcctId.getValue()));
					} else {
						aFinanceMain.setRepayAccountId(
								PennantApplicationUtil.unFormatAccountNumber(this.repayAcctId.getValidatedValue()));
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinCommitmentRef(this.commitmentRef.getValue());
			aFinanceMain.setLovDescCommitmentRefName(this.commitmentRef.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinLimitRef(this.finLimitRef.getValue());
			aFinanceMain.setLovDescLimitRefName(this.finLimitRef.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.depreciationFrq.isValidComboValue()) {
				aFinanceMain.setDepreciationFrq(
						this.depreciationFrq.getValue() == null ? "" : this.depreciationFrq.getValue());
				if (StringUtils.isNotEmpty(this.depreciationFrq.getValue())
						&& FrequencyUtil.validateFrequency(this.depreciationFrq.getValue()) == null) {
					aFinanceMain.setNextDepDate(FrequencyUtil
							.getNextDate(this.depreciationFrq.getValue(), 1, this.finStartDate.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
							.getNextFrequencyDate());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			financeDate = this.finStartDate.getValue();
			aFinanceMain.setFinStartDate(this.finStartDate.getValue());
			if (aFinanceMain.isNew()
					|| StringUtils.trimToEmpty(aFinanceMain.getRecordType()).equals(PennantConstants.RECORD_TYPE_NEW)) {
				aFinanceMain.setLastRepayDate(this.finStartDate.getValue());
				aFinanceMain.setLastRepayPftDate(this.finStartDate.getValue());
				aFinanceMain.setLastRepayRvwDate(this.finStartDate.getValue());
				aFinanceMain.setLastRepayCpzDate(this.finStartDate.getValue());
				aFinanceMain.setLastDepDate(this.finStartDate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinContractDate(this.finContractDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if (StringUtils.equals(getProductCode(), FinanceConstants.PRODUCT_SUKUKNRM)) {
				if (this.finAmount.getActualValue().longValue() % 10 != 0) {
					throw new WrongValueException(this.finAmount, Labels.getLabel("FINAMOUNT_UNITS",
							new String[] { Labels.getLabel("label_FinanceMainDialog_FinAmount.value"), "10" }));
				}
			}
			if (!isOverDraft) {
				aFinanceMain.setFinAmount(PennantAppUtil.unFormateAmount(this.finAmount.isReadonly()
						? this.finAmount.getActualValue() : this.finAmount.getValidateValue(), formatter));
				aFinanceMain.setCurDisbursementAmt(
						PennantAppUtil.unFormateAmount(this.finAmount.getActualValue(), formatter));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.defferments.isReadonly() && this.defferments.intValue() != 0
					&& (financeType.getFinMaxDifferment() < this.defferments.intValue())) {

				throw new WrongValueException(this.defferments,
						Labels.getLabel("FIELD_IS_LESSER",
								new String[] { Labels.getLabel("label_FinanceMainDialog_Defferments.value"),
										String.valueOf(financeType.getFinMaxDifferment()) }));

			}
			aFinanceMain.setDefferments(this.defferments.intValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.planDeferCount.isReadonly() && this.planDeferCount.intValue() != 0
					&& this.repayFrq.getFrqCodeCombobox().getSelectedIndex() > 0) {
				int maxPlanDeferCount = PennantAppUtil.getAlwPlanDeferCount(financeType.getFinRpyFrq().substring(0, 1),
						financeType.getPlanDeferCount(),
						this.repayFrq.getFrqCodeCombobox().getSelectedItem().getValue().toString());
				if (maxPlanDeferCount < this.planDeferCount.intValue()) {
					throw new WrongValueException(this.planDeferCount,
							Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
									new String[] { Labels.getLabel("label_FinanceMainDialog_PlanDeferCount.value"),
											String.valueOf(maxPlanDeferCount) }));
				}

			}
			aFinanceMain.setPlanDeferCount(this.planDeferCount.intValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinPurpose(this.finPurpose.getValue());
			aFinanceMain.setLovDescFinPurposeName(this.finPurpose.getDescription());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.row_securityDeposit.isVisible() && !this.securityDeposit.isReadonly()
					&& this.securityDeposit.getActualValue().compareTo(this.finAmount.getActualValue()) >= 0) {
				throw new WrongValueException(this.securityDeposit,
						Labels.getLabel("NUMBER_MAXVALUE",
								new String[] { Labels.getLabel("label_FinanceMainDialog_SecurityDeposit.value"),
										Labels.getLabel("label_FinanceMainDialog_FinAmount.value") }));
			}
			aFinanceMain.setSecurityDeposit(
					PennantAppUtil.unFormateAmount(this.securityDeposit.getActualValue(), formatter));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		aFinanceMain.setManualSchedule(this.manualSchedule.isChecked());

		// Step Finance Details
		if (this.row_stepFinance.isVisible()) {
			aFinanceMain.setStepFinance(this.stepFinance.isChecked());
			if (this.stepFinance.isChecked()) {
				try {
					aFinanceMain.setStepPolicy(this.stepPolicy.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					aFinanceMain.setStepType(this.stepType.getSelectedItem().getValue().toString());
				} catch (WrongValueException we) {
					wve.add(we);
				}
			}
			aFinanceMain.setAlwManualSteps(this.alwManualSteps.isChecked());
			try {
				aFinanceMain.setNoOfSteps(this.noOfSteps.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		try {
			aFinanceMain.setShariaStatus(getShariaStatus());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setMMAId(Long
					.valueOf(StringUtils.isBlank(this.mMAReference.getValue()) ? "0" : this.mMAReference.getValue()));
			aFinanceMain.setLovDescMMAReference(this.mMAReference.setDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.accountsOfficer.getValidatedValue();
			Object object = this.accountsOfficer.getAttribute("DealerId");
			if (object != null) {
				aFinanceMain.setAccountsOfficer(Long.parseLong(object.toString()));
			} else {
				aFinanceMain.setAccountsOfficer(0);
			}
			aFinanceMain.setLovDescAccountsOfficer(this.accountsOfficer.setDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setDsaName(this.dsaCode.getValue());
			aFinanceMain.setDsaCodeDesc(this.dsaCode.getDescription());
			Object object = this.dsaCode.getAttribute("DSAdealerID");
			if (object != null) {
				aFinanceMain.setDsaCode(object.toString());
			} else {
				aFinanceMain.setDsaCode(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setDmaName(this.dmaCode.getValue());
			aFinanceMain.setDmaCodeDesc(this.dmaCode.getDescription());
			Object object = this.dmaCode.getAttribute("DMAdealerID");
			if (object != null) {
				aFinanceMain.setDmaCode(object.toString());
			} else {
				aFinanceMain.setDmaCode(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinanceMain.setConnectorCode(this.connector.getValue());
			aFinanceMain.setConnectorDesc(this.connector.getDescription());
			Object object = this.connector.getAttribute("DealerId");
			if (object != null) {
				aFinanceMain.setConnector(Long.parseLong(object.toString()));
			} else {
				aFinanceMain.setConnector(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setReferralId(this.referralId.getValue());
			aFinanceMain.setReferralIdDesc(this.referralId.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (this.row_employeeName != null && this.row_employeeName.isVisible()) {
			try {
				aFinanceMain.setEmployeeName(this.employeeName.getValue());
				aFinanceMain.setEmployeeNameDesc(this.employeeName.getDescription());

			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		try {
			aFinanceMain.setSalesDepartment(this.salesDepartment.getValue());
			aFinanceMain.setSalesDepartmentDesc(this.salesDepartment.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		aFinanceMain.setQuickDisb(this.quickDisb.isChecked());
		//Commercial Workflow Fields Data Setting
		aFinanceMain.setTDSApplicable(this.tDSApplicable.isChecked());

		//FinanceMain Details tab ---> 2. Grace Period Details
		try {
			if (this.gracePeriodEndDate.getValue() != null) {
				this.gracePeriodEndDate_two.setValue(this.gracePeriodEndDate.getValue());
			}

			if (this.gracePeriodEndDate_two.getValue() != null) {

				aFinanceMain.setGrcPeriodEndDate(DateUtility.getDate(DateUtility
						.formatUtilDate(this.gracePeriodEndDate_two.getValue(), PennantConstants.dateFormat)));

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if (this.allowGrace.isChecked()) {

				if (this.graceTerms.intValue() < 0) {
					throw new WrongValueException(this.graceTerms, Labels.getLabel("NUMBER_NOT_NEGATIVE",
							new String[] { Labels.getLabel("label_FinanceMainDialog_GraceTerms.value") }));
				}

				if (this.graceTerms.intValue() != 0 && this.gracePeriodEndDate_two.getValue() == null) {
					this.graceTerms_Two.setValue(this.graceTerms.intValue());
				}

				if (this.graceTerms.intValue() == 0 && this.graceTerms_Two.intValue() == 0
						&& this.gracePeriodEndDate_two.getValue() != null && this.finStartDate.getValue() != null
						&& this.gracePeriodEndDate_two.getValue().compareTo(this.finStartDate.getValue()) == 0) {

					throw new WrongValueException(this.graceTerms, Labels.getLabel("MUST_BE_ENTERED",
							new String[] { Labels.getLabel("label_FinanceMainDialog_GraceTerms.value") }));

				}

				try {
					if (StringUtils.equals(CalculationConstants.RATE_BASIS_R,
							this.grcRateBasis.getSelectedItem().getValue().toString())
							&& StringUtils.isNotEmpty(financeType.getFinGrcBaseRate())) {
						if (this.finGrcMinRate.getValue() != null && this.finGrcMaxRate.getValue() != null) {
							if (finGrcMaxRate.getValue().compareTo(finGrcMinRate.getValue()) < 0) {
								throw new WrongValueException(this.finGrcMaxRate, Labels.getLabel("FIELD_IS_GREATER",
										new String[] { Labels.getLabel("label_FinanceMainDialog_FinGrcMaxRate.value"),
												Labels.getLabel("label_FinanceMainDialog_FinGrcMinRate.value") }));
							}
						}
						aFinanceMain.setGrcMinRate(this.finGrcMinRate.getValue());
						aFinanceMain.setGrcMaxRate(this.finGrcMaxRate.getValue());
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}

				if (!recSave && this.graceTerms_Two.intValue() == 0 && this.gracePeriodEndDate_two.getValue() == null) {
					throw new WrongValueException(this.graceTerms,
							Labels.getLabel("EITHER_OR",
									new String[] { Labels.getLabel("label_FinanceMainDialog_GracePeriodEndDate.value"),
											Labels.getLabel("label_FinanceMainDialog_GraceTerms.value") }));

				} else if (!recSave && this.graceTerms.intValue() > 0 && this.gracePeriodEndDate.getValue() != null
						&& this.gracePeriodEndDate_two.getValue() != null) {

					throw new WrongValueException(this.graceTerms,
							Labels.getLabel("EITHER_OR",
									new String[] { Labels.getLabel("label_FinanceMainDialog_GracePeriodEndDate.value"),
											Labels.getLabel("label_FinanceMainDialog_GraceTerms.value") }));

				} else if (this.gracePeriodEndDate.getValue() != null) {
					if (this.finStartDate.getValue().compareTo(this.gracePeriodEndDate.getValue()) > 0) {

						throw new WrongValueException(this.gracePeriodEndDate,
								Labels.getLabel("NUMBER_MINVALUE_EQ",
										new String[] {
												Labels.getLabel("label_FinanceMainDialog_GracePeriodEndDate.value"),
												Labels.getLabel("label_FinanceMainDialog_FinStartDate.value") }));
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		aFinanceMain.setAllowGrcPeriod(this.allowGrace.isChecked());

		if (this.allowGrace.isChecked()) {

			try {
				// Field is foreign key and not a mandatory value so it should
				// be either null or non empty
				if (StringUtils.isEmpty(this.graceRate.getBaseValue())) {
					aFinanceMain.setGraceBaseRate(null);
				} else {
					aFinanceMain.setGraceBaseRate(this.graceRate.getBaseValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (getComboboxValue(this.grcRateBasis).equals(PennantConstants.List_Select)) {
					throw new WrongValueException(this.grcRateBasis, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinanceMainDialog_GrcRateBasis.value") }));
				}
				aFinanceMain.setGrcRateBasis(getComboboxValue(this.grcRateBasis));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				// Field is foreign key and not a mandatory value so it should
				// be either null or non empty
				if (StringUtils.isEmpty(this.graceRate.getSpecialValue())) {
					aFinanceMain.setGraceSpecialRate(null);
				} else {
					aFinanceMain.setGraceSpecialRate(this.graceRate.getSpecialValue());
				}
				aFinanceMain.setGrcPftRate(this.graceRate.getEffRateValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (!this.graceRate.isBaseReadonly()) {
					calculateRate(this.graceRate.getBaseValue(), this.finCcy.getValue(),
							this.graceRate.getSpecialComp(), this.graceRate.getBaseComp(),
							this.graceRate.getMarginValue(), this.graceRate.getEffRateComp(), this.finGrcMinRate,
							this.finMaxRate);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}

			try {
				if (this.gracePftRate.getValue() != null && !this.gracePftRate.isReadonly()) {
					if ((this.gracePftRate.getValue().intValue() > 0)
							&& (StringUtils.isNotEmpty(this.graceRate.getBaseValue()))) {

						throw new WrongValueException(this.gracePftRate,
								Labels.getLabel("EITHER_OR",
										new String[] { Labels.getLabel("label_FinanceMainDialog_GraceBaseRate.value"),
												Labels.getLabel("label_FinanceMainDialog_GracePftRate.value") }));
					}
					aFinanceMain.setGrcPftRate(this.gracePftRate.getValue());
				} else {
					aFinanceMain.setGrcPftRate(this.gracePftRate.getValue());
				}

				if (aFinanceMain.getGrcPftRate() == null) {
					aFinanceMain.setGrcPftRate(BigDecimal.ZERO);
				}

			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aFinanceMain.setGrcMargin(this.graceRate.getMarginValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				/*
				 * to check mutually exclusive values i.e Grace base rate code and Grace profit rate
				 */
				if (this.grcAdvPftRate.getValue() != null) {
					if ((this.grcAdvPftRate.getValue().intValue() > 0)
							&& (StringUtils.isNotEmpty(this.grcAdvRate.getBaseValue()))) {
						throw new WrongValueException(this.grcAdvPftRate,
								Labels.getLabel("EITHER_OR",
										new String[] { Labels.getLabel("label_FinanceMainDialog_GrcAdvBaseRate.value"),
												Labels.getLabel("label_FinanceMainDialog_GrcAdvPftRate.value") }));
					}
					aFinanceMain.setGrcAdvPftRate(this.grcAdvPftRate.getValue());
				} else {
					aFinanceMain.setGrcAdvPftRate(BigDecimal.ZERO);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				// Field is foreign key and not a mandatory value so it should be either null or non empty
				aFinanceMain.setGrcAdvBaseRate(
						StringUtils.isEmpty(this.grcAdvRate.getBaseValue()) ? null : this.grcAdvRate.getBaseValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (StringUtils.isEmpty(this.grcAdvRate.getBaseValue()) && this.grcAdvRate.getMarginValue() != null
						&& this.grcAdvRate.getMarginValue().compareTo(BigDecimal.ZERO) > 0) {
					throw new WrongValueException(this.grcAdvRate.getMarginComp(), Labels.getLabel("FIELD_EMPTY",
							new String[] { Labels.getLabel("label_FinanceMainDialog_GrcAdvMargin.value") }));
				}
				aFinanceMain.setGrcAdvMargin(
						this.grcAdvRate.getMarginValue() == null ? BigDecimal.ZERO : this.grcAdvRate.getMarginValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (getComboboxValue(this.grcPftDaysBasis).equals(PennantConstants.List_Select)) {
					throw new WrongValueException(this.grcPftDaysBasis, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinanceMainDialog_GraceProfitDaysBasis.value") }));
				}

				aFinanceMain.setGrcProfitDaysBasis(getComboboxValue(this.grcPftDaysBasis));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (this.gracePftFrq.isValidComboValue()) {
					aFinanceMain.setGrcPftFrq(this.gracePftFrq.getValue() == null ? "" : this.gracePftFrq.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (!this.nextGrcPftDate.isReadonly() && StringUtils.isNotEmpty(this.gracePftFrq.getValue())) {
					if (this.nextGrcPftDate.getValue() != null) {
						this.nextGrcPftDate_two.setValue(this.nextGrcPftDate.getValue());
					}
					if (StringUtils.isNotEmpty(this.gracePftFrq.getValue())
							&& FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {
						aFinanceMain.setNextGrcPftDate(DateUtility.getDate(DateUtility
								.formatUtilDate(this.nextGrcPftDate_two.getValue(), PennantConstants.dateFormat)));
					}
					//Validation Against the Repay Frequency and the next Frequency Date
					if (ImplementationConstants.FRQ_DATE_VALIDATION && this.nextGrcPftDate.getValue() != null
							&& !FrequencyUtil.isFrqDate(this.gracePftFrq.getValue(), this.nextGrcPftDate.getValue())) {
						throw new WrongValueException(this.nextGrcPftDate,
								Labels.getLabel("FRQ_DATE_MISMATCH",
										new String[] { Labels.getLabel("label_FinanceMainDialog_NextGrcPftDate.value"),
												Labels.getLabel("label_FinanceMainDialog_GracePftFrq.value") }));
					}
				}

			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (this.grcPftRvwFrqRow.isVisible() && this.gracePftRvwFrq.isValidComboValue()) {
					aFinanceMain.setGrcPftRvwFrq(
							this.gracePftRvwFrq.getValue() == null ? "" : this.gracePftRvwFrq.getValue());
					aFinanceMain.setAllowGrcPftRvw(true);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (!this.nextGrcPftRvwDate.isReadonly() && StringUtils.isNotEmpty(this.gracePftRvwFrq.getValue())) {
					if (this.nextGrcPftRvwDate.getValue() != null) {
						this.nextGrcPftRvwDate_two.setValue(this.nextGrcPftRvwDate.getValue());
					}
					if (StringUtils.isNotEmpty(this.gracePftRvwFrq.getValue())
							&& FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {
						aFinanceMain.setNextGrcPftRvwDate(DateUtility.getDate(DateUtility
								.formatUtilDate(this.nextGrcPftRvwDate_two.getValue(), PennantConstants.dateFormat)));
					}
					//Validation Against the Repay Frequency and the next Frequency Date
					if (ImplementationConstants.FRQ_DATE_VALIDATION && this.nextGrcPftRvwDate.getValue() != null
							&& !FrequencyUtil.isFrqDate(this.gracePftRvwFrq.getValue(),
									this.nextGrcPftRvwDate.getValue())) {
						throw new WrongValueException(this.nextGrcPftRvwDate,
								Labels.getLabel("FRQ_DATE_MISMATCH",
										new String[] {
												Labels.getLabel("label_FinanceMainDialog_NextGrcPftRvwDate.value"),
												Labels.getLabel("label_FinanceMainDialog_GracePftRvwFrq.value") }));
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (this.grcCpzFrqRow.isVisible() && this.graceCpzFrq.isValidComboValue()) {
					aFinanceMain.setGrcCpzFrq(this.graceCpzFrq.getValue() == null ? "" : this.graceCpzFrq.getValue());
					aFinanceMain.setAllowGrcCpz(true);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (!this.nextGrcCpzDate.isReadonly() && StringUtils.isNotEmpty(this.graceCpzFrq.getValue())) {
					if (this.nextGrcCpzDate.getValue() != null) {
						this.nextGrcCpzDate_two.setValue(this.nextGrcCpzDate.getValue());
					}

					if (StringUtils.isNotEmpty(this.graceCpzFrq.getValue())
							&& FrequencyUtil.validateFrequency(this.graceCpzFrq.getValue()) == null
							&& this.nextGrcCpzDate_two.getValue() != null) {
						aFinanceMain.setNextGrcCpzDate(DateUtility.getDate(DateUtility
								.formatUtilDate(this.nextGrcCpzDate_two.getValue(), PennantConstants.dateFormat)));
					}
					//Validation Against the Repay Frequency and the next Frequency Date
					if (ImplementationConstants.FRQ_DATE_VALIDATION && this.nextGrcCpzDate.getValue() != null
							&& !FrequencyUtil.isFrqDate(this.graceCpzFrq.getValue(), this.nextGrcCpzDate.getValue())) {
						throw new WrongValueException(this.nextGrcCpzDate,
								Labels.getLabel("FRQ_DATE_MISMATCH",
										new String[] { Labels.getLabel("label_FinanceMainDialog_NextGrcCpzDate.value"),
												Labels.getLabel("label_FinanceMainDialog_GraceCpzFrq.value") }));
					}
				} else {
					aFinanceMain.setNextGrcCpzDate(this.nextGrcCpzDate_two.getValue());
				}

			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aFinanceMain.setAllowGrcRepay(this.allowGrcRepay.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (this.allowGrcRepay.isChecked()
						&& getComboboxValue(this.cbGrcSchdMthd).equals(PennantConstants.List_Select)) {
					throw new WrongValueException(this.cbGrcSchdMthd, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinanceMainDialog_GrcSchdMthd.value") }));
				}
				aFinanceMain.setGrcSchdMthd(getComboboxValue(this.cbGrcSchdMthd));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aFinanceMain.setGraceTerms(this.graceTerms_Two.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				aFinanceMain.setGrcMaxAmount(PennantAppUtil.unFormateAmount(this.grcMaxAmount.getActualValue(), formatter));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
		} else {
			aFinanceMain.setGrcCpzFrq("");
			aFinanceMain.setNextGrcCpzDate(null);
			aFinanceMain.setGrcPftFrq("");
			aFinanceMain.setNextGrcPftDate(null);
			aFinanceMain.setGrcPftRvwFrq("");
			aFinanceMain.setNextGrcPftRvwDate(null);
			aFinanceMain.setGrcRateBasis(PennantConstants.List_Select);
			if (financeDate != null) {
				this.gracePeriodEndDate.setValue(this.finStartDate.getValue());
				this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			}
			aFinanceMain.setGrcPeriodEndDate(DateUtility.getDate(
					DateUtility.formatUtilDate(this.gracePeriodEndDate_two.getValue(), PennantConstants.dateFormat)));
			aFinanceMain.setGraceTerms(0);
			aFinanceMain.setAllowGrcRepay(false);
			aFinanceMain.setGraceBaseRate(null);
			aFinanceMain.setGraceSpecialRate(null);
			aFinanceMain.setGrcMargin(BigDecimal.ZERO);
			aFinanceMain.setGrcProfitDaysBasis(null);
			aFinanceMain.setGrcAdvBaseRate(null);
			aFinanceMain.setGrcAdvMargin(BigDecimal.ZERO);
			aFinanceMain.setGrcAdvPftRate(BigDecimal.ZERO);
			aFinanceMain.setGrcPftRate(BigDecimal.ZERO);
			aFinanceMain.setGrcMaxAmount(BigDecimal.ZERO);
		}

		//FinanceMain Details tab ---> 3. Repayment Period Details

		try {
			if (this.finRepaymentAmount != null) {
				BigDecimal downpay = this.finAmount.getActualValue()
						.subtract(this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue()));
				if (this.finRepaymentAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0
						&& this.finRepaymentAmount.getActualValue().compareTo(downpay) >= 0) {
					if (financeType.isFinIsDwPayRequired()) {
						throw new WrongValueException(this.finRepaymentAmount,
								Labels.getLabel("REPAY_AMOUNT_DOWNPAY",
										new String[] {
												Labels.getLabel("label_FinanceMainDialog_FinRepaymentAmount.value"),
												Labels.getLabel("label_FinanceMainDialog_FinAmount.value") }));
					} else {
						throw new WrongValueException(this.finRepaymentAmount,
								Labels.getLabel("REPAY_AMOUNT",
										new String[] {
												Labels.getLabel("label_FinanceMainDialog_FinRepaymentAmount.value"),
												Labels.getLabel("label_FinanceMainDialog_FinAmount.value") }));
					}
				}
			}
			aFinanceMain.setReqRepayAmount(PennantAppUtil.unFormateAmount(this.finRepaymentAmount.isReadonly()
					? this.finRepaymentAmount.getActualValue() : this.finRepaymentAmount.getValidateValue(),
					formatter));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			// Field is foreign key and not a mandatory value so it should be
			// either null or non empty
			if (StringUtils.isEmpty(this.repayRate.getBaseValue())) {
				aFinanceMain.setRepayBaseRate(null);
			} else {
				aFinanceMain.setRepayBaseRate(this.repayRate.getBaseValue());
				
				BaseRate baseRate = getBaseRateService().getBaseRateByDate(aFinanceMain.getRepayBaseRate(),
						aFinanceMain.getFinCcy(), aFinanceMain.getFinStartDate());
				if(baseRate != null){
					aFinanceMain.setRepayBaseRateVal(baseRate.getBRRate());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			// Field is foreign key and not a mandatory value so it should be
			// either null or non empty
			if (StringUtils.isEmpty(this.repayRate.getSpecialValue()) || isOverDraft) {
				aFinanceMain.setRepaySpecialRate(null);
			} else {
				aFinanceMain.setRepaySpecialRate(this.repayRate.getSpecialValue());
			}
			aFinanceMain.setRepayProfitRate(this.repayProfitRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setRepayMargin(this.repayRate.getMarginValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.repayRate.isBaseReadonly()) {
				calculateRate(this.repayRate.getBaseValue(), this.finCcy.getValue(), this.repayRate.getSpecialComp(),
						this.repayRate.getBaseComp(), this.repayRate.getMarginValue(), this.repayRate.getEffRateComp(),
						this.finMinRate, this.finMaxRate);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		try {
			/*
			 * to check mutually exclusive values i.e Repay base rate code and Repay profit rate
			 */
			if (this.rpyAdvPftRate.getValue() != null) {
				if ((this.rpyAdvPftRate.getValue().intValue() > 0)
						&& (StringUtils.isNotEmpty(this.rpyAdvRate.getBaseValue()))) {
					throw new WrongValueException(this.rpyAdvPftRate,
							Labels.getLabel("EITHER_OR",
									new String[] { Labels.getLabel("label_FinanceMainDialog_RpyAdvBaseRate.value"),
											Labels.getLabel("label_FinanceMainDialog_RpyAdvPftRate.value") }));
				}
				aFinanceMain.setRpyAdvPftRate(this.rpyAdvPftRate.getValue());
			} else {
				aFinanceMain.setRpyAdvPftRate(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// Field is foreign key and not a mandatory value so it should be either null or non empty
			aFinanceMain.setRpyAdvBaseRate(
					StringUtils.isEmpty(this.rpyAdvRate.getBaseValue()) ? null : this.rpyAdvRate.getBaseValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.isEmpty(this.rpyAdvRate.getBaseValue()) && this.rpyAdvRate.getMarginValue() != null
					&& this.rpyAdvRate.getMarginValue().compareTo(BigDecimal.ZERO) > 0) {
				throw new WrongValueException(this.rpyAdvRate.getMarginComp(), Labels.getLabel("FIELD_EMPTY",
						new String[] { Labels.getLabel("label_FinanceMainDialog_RpyAdvMargin.value") }));
			}
			aFinanceMain.setRpyAdvMargin(
					this.rpyAdvRate.getMarginValue() == null ? BigDecimal.ZERO : this.rpyAdvRate.getMarginValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain
					.setSupplementRent(PennantAppUtil.unFormateAmount(this.supplementRent.getActualValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain
					.setIncreasedCost(PennantAppUtil.unFormateAmount(this.increasedCost.getActualValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinRepayPftOnFrq(this.finRepayPftOnFrq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.repayProfitRate.getValue() != null && !this.repayProfitRate.isReadonly()) {
				if ((this.repayProfitRate.getValue().intValue() > 0)
						&& (StringUtils.isNotEmpty(this.repayRate.getBaseValue()))) {
					throw new WrongValueException(this.repayProfitRate,
							Labels.getLabel("EITHER_OR",
									new String[] { Labels.getLabel("label_FinanceMainDialog_RepayBaseRate.value"),
											Labels.getLabel("label_FinanceMainDialog_ProfitRate.value") }));
				}
				aFinanceMain.setRepayProfitRate(this.repayProfitRate.getValue());
			}

			if (aFinanceMain.getRepayProfitRate() == null) {
				aFinanceMain.setRepayProfitRate(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.equals(CalculationConstants.RATE_BASIS_R,
					this.repayRateBasis.getSelectedItem().getValue().toString())
					&& StringUtils.isNotEmpty(financeType.getFinBaseRate())) {
				if (this.finMinRate.getValue() != null && this.finMaxRate.getValue() != null) {
					if (finMaxRate.getValue().compareTo(finMinRate.getValue()) < 0) {
						throw new WrongValueException(this.finGrcMaxRate,
								Labels.getLabel("FIELD_IS_GREATER",
										new String[] { Labels.getLabel("label_FinanceMainDialog_FinMaxRate.value"),
												Labels.getLabel("label_FinanceMainDialog_FinMinRate.value") }));
					}
				}
				aFinanceMain.setRpyMinRate(this.finMinRate.getValue());
				aFinanceMain.setRpyMaxRate(this.finMaxRate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.rpyPftFrqRow.isVisible()) {
				if (this.repayPftFrq.isValidComboValue()) {
					aFinanceMain.setRepayPftFrq(this.repayPftFrq.getValue() == null ? "" : this.repayPftFrq.getValue());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if (isOverDraft && !financeType.isDroplineOD()) {
				if (StringUtils.isNotEmpty(this.repayFrq.getValue())) {
					this.nextRepayDate_two.setValue(FrequencyUtil
							.getNextDate(this.repayFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
							.getNextFrequencyDate());
					this.nextRepayPftDate_two.setValue(this.nextRepayDate_two.getValue());
					this.oldVar_nextRepayDate = this.nextRepayDate_two.getValue();
					this.oldVar_nextRepayPftDate = this.nextRepayPftDate_two.getValue();
				}
				if (StringUtils.isNotEmpty(this.repayRvwFrq.getValue())) {
					this.nextRepayRvwDate_two.setValue(FrequencyUtil
							.getNextDate(this.repayRvwFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
							.getNextFrequencyDate());
					this.oldVar_nextRepayRvwDate = this.nextRepayRvwDate_two.getValue();
				}
			}

			if (this.rpyPftFrqRow.isVisible()) {
				if (!this.nextRepayPftDate.isDisabled() && StringUtils.isNotEmpty(this.repayPftFrq.getValue())) {
					if (this.nextRepayPftDate.getValue() != null) {
						this.nextRepayPftDate_two.setValue(this.nextRepayPftDate.getValue());
					}
					if (StringUtils.isNotEmpty(this.repayPftFrq.getValue())
							&& FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {
						aFinanceMain.setNextRepayPftDate(DateUtility.getDate(DateUtility
								.formatUtilDate(this.nextRepayPftDate_two.getValue(), PennantConstants.dateFormat)));
					}
					//Validation Against the Repay Frequency and the next Frequency Date
					if (ImplementationConstants.FRQ_DATE_VALIDATION && !this.nextRepayPftDate.isReadonly()
							&& this.nextRepayPftDate.getValue() != null && !FrequencyUtil
									.isFrqDate(this.repayPftFrq.getValue(), this.nextRepayPftDate.getValue())) {
						throw new WrongValueException(this.nextRepayPftDate,
								Labels.getLabel("FRQ_DATE_MISMATCH",
										new String[] {
												Labels.getLabel("label_FinanceMainDialog_NextRepayPftDate.value"),
												Labels.getLabel("label_FinanceMainDialog_RepayPftFrq.value") }));
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.repayRvwFrq.isValidComboValue()) {
				aFinanceMain.setRepayRvwFrq(this.repayRvwFrq.getValue() == null ? "" : this.repayRvwFrq.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.nextRepayRvwDate.isReadonly() && StringUtils.isNotEmpty(this.repayRvwFrq.getValue())) {
				if (this.nextRepayRvwDate.getValue() != null) {
					this.nextRepayRvwDate_two.setValue(this.nextRepayRvwDate.getValue());
				}
				if (StringUtils.isNotEmpty(this.repayRvwFrq.getValue())
						&& FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue()) == null) {
					aFinanceMain.setNextRepayRvwDate(DateUtility.getDate(DateUtility
							.formatUtilDate(this.nextRepayRvwDate_two.getValue(), PennantConstants.dateFormat)));
				}
				//Validation Against the Repay Frequency and the next Frequency Date
				if (ImplementationConstants.FRQ_DATE_VALIDATION && this.nextRepayRvwDate.getValue() != null
						&& !FrequencyUtil.isFrqDate(this.repayRvwFrq.getValue(), this.nextRepayRvwDate.getValue())) {
					throw new WrongValueException(this.nextRepayRvwDate,
							Labels.getLabel("FRQ_DATE_MISMATCH",
									new String[] { Labels.getLabel("label_FinanceMainDialog_NextRepayRvwDate.value"),
											Labels.getLabel("label_FinanceMainDialog_RepayRvwFrq.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.repayCpzFrq.isValidComboValue()) {
				aFinanceMain.setRepayCpzFrq(this.repayCpzFrq.getValue() == null ? "" : this.repayCpzFrq.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.nextRepayCpzDate.isReadonly() && StringUtils.isNotEmpty(this.repayCpzFrq.getValue())) {
				if (this.nextRepayCpzDate.getValue() != null) {
					this.nextRepayCpzDate_two.setValue(this.nextRepayCpzDate.getValue());
				}
				if (StringUtils.isNotEmpty(this.repayCpzFrq.getValue())
						&& FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue()) == null) {
					aFinanceMain.setNextRepayCpzDate(DateUtility.getDate(DateUtility
							.formatUtilDate(this.nextRepayCpzDate_two.getValue(), PennantConstants.dateFormat)));
				}
				//Validation Against the Repay Frequency and the next Frequency Date
				if (ImplementationConstants.FRQ_DATE_VALIDATION && this.nextRepayCpzDate.getValue() != null
						&& !FrequencyUtil.isFrqDate(this.repayCpzFrq.getValue(), this.nextRepayCpzDate.getValue())) {
					throw new WrongValueException(this.nextRepayCpzDate,
							Labels.getLabel("FRQ_DATE_MISMATCH",
									new String[] { Labels.getLabel("label_FinanceMainDialog_NextRepayCpzDate.value"),
											Labels.getLabel("label_FinanceMainDialog_RepayCpzFrq.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.repayFrq.isValidComboValue()) {
				aFinanceMain.setRepayFrq(this.repayFrq.getValue() == null ? "" : this.repayFrq.getValue());
			}
			if (!this.rpyPftFrqRow.isVisible()) {
				aFinanceMain.setRepayPftFrq(this.repayFrq.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.nextRepayDate.isReadonly() && StringUtils.isNotEmpty(this.repayFrq.getValue())) {
				if (this.nextRepayDate.getValue() != null) {
					this.nextRepayDate_two.setValue(this.nextRepayDate.getValue());
				}

				if (StringUtils.isNotEmpty(this.repayFrq.getValue())
						&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
					aFinanceMain.setNextRepayDate(DateUtility.getDate(DateUtility
							.formatUtilDate(this.nextRepayDate_two.getValue(), PennantConstants.dateFormat)));
				}
				//Validation Against the Repay Frequency and the next Frequency Date
				if (ImplementationConstants.FRQ_DATE_VALIDATION && this.nextRepayDate.getValue() != null
						&& !FrequencyUtil.isFrqDate(this.repayFrq.getValue(), this.nextRepayDate.getValue())) {
					throw new WrongValueException(this.nextRepayDate,
							Labels.getLabel("FRQ_DATE_MISMATCH",
									new String[] { Labels.getLabel("label_FinanceMainDialog_NextRepayDate.value"),
											Labels.getLabel("label_FinanceMainDialog_RepayFrq.value") }));
				}
			}

			if (!this.rpyPftFrqRow.isVisible() && this.rpyFrqRow.isVisible()) {
				aFinanceMain.setNextRepayPftDate(aFinanceMain.getNextRepayDate());
				this.nextRepayPftDate_two.setValue(this.nextRepayDate_two.getValue());
				this.nextRepayPftDate.setText("");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.rolloverFrq.isValidComboValue()) {
				aFinanceMain.setRolloverFrq(this.rolloverFrq.getValue() == null ? "" : this.rolloverFrq.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.nextRollOverDate.isReadonly() && StringUtils.isNotEmpty(this.rolloverFrq.getValue())) {
				if (this.nextRollOverDate.getValue() != null) {
					this.nextRollOverDate_two.setValue(this.nextRollOverDate.getValue());
				}
				if (StringUtils.isNotEmpty(this.rolloverFrq.getValue())
						&& FrequencyUtil.validateFrequency(this.rolloverFrq.getValue()) == null) {
					aFinanceMain.setNextRolloverDate(DateUtility.getDate(DateUtility
							.formatUtilDate(this.nextRollOverDate_two.getValue(), PennantConstants.dateFormat)));
				}
				//Validation Against the Repay Frequency and the next Frequency Date
				if (ImplementationConstants.FRQ_DATE_VALIDATION && this.nextRollOverDate.getValue() != null
						&& !FrequencyUtil.isFrqDate(this.rolloverFrq.getValue(), this.nextRollOverDate.getValue())) {
					throw new WrongValueException(this.nextRollOverDate,
							Labels.getLabel("FRQ_DATE_MISMATCH",
									new String[] { Labels.getLabel("label_FinanceMainDialog_NextRollOverDate.value"),
											Labels.getLabel("label_FinanceMainDialog_RolloverFrq.value") }));
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {

			if (this.maturityDate.getValue() != null && this.numberOfTerms.isReadonly()) {
				this.maturityDate_two.setValue(this.maturityDate.getValue());
			}

			//validate maturity date
			if (this.maturityDate_two.getValue() != null && !this.maturityDate.isReadonly()) {
				if (this.maturityDate_two.getValue().compareTo(this.finStartDate.getValue()) <= 0) {
					throw new WrongValueException(this.maturityDate,
							Labels.getLabel("DATE_ALLOWED_AFTER", new String[] { Labels.getLabel("label_MaturityDate"),
									Labels.getLabel("label_FinStartDate") }));
				}
			}

			if (this.numberOfTerms.intValue() != 0 && this.maturityDate_two.getValue() == null) {
				if (this.numberOfTerms.intValue() < 0) {
					this.numberOfTerms.setConstraint("NO NEGATIVE:" + Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO",
							new String[] { Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value") }));
				}
				this.numberOfTerms_two.setValue(this.numberOfTerms.intValue());
			}

			String product = StringUtils.trimToEmpty(financeType.getFinCategory());

			if (this.row_ManualSchedule.isVisible() && this.manualSchedule.isChecked()) {
				aFinanceMain.setNumberOfTerms(this.numberOfTerms.intValue());
			} else if (product.equals(FinanceConstants.PRODUCT_SUKUK)) {

				if (!recSave && this.maturityDate_two.getValue() == null && this.maturityDate.getValue() == null) {
					throw new WrongValueException(this.maturityDate, Labels.getLabel("MUST_BE_ENTERED",
							new String[] { Labels.getLabel("label_FinanceMainDialog_MaturityDate.value") }));
				}
			} else {
				if (!recSave && this.numberOfTerms_two.intValue() == 0 && this.maturityDate_two.getValue() == null
						&& !this.manualSchedule.isChecked()) {
					throw new WrongValueException(this.numberOfTerms,
							Labels.getLabel("EITHER_OR",
									new String[] { Labels.getLabel("label_FinanceMainDialog_MaturityDate.value"),
											Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value") }));

				} else if (!recSave && this.numberOfTerms.intValue() > 0 && this.maturityDate.getValue() != null
						&& this.maturityDate_two.getValue() != null) {

					if (financeType.getFinMinTerm() == 1 && financeType.getFinMaxTerm() == 1) {
						//Do Nothing
					} else {
						throw new WrongValueException(this.numberOfTerms,
								Labels.getLabel("EITHER_OR",
										new String[] { Labels.getLabel("label_FinanceMainDialog_MaturityDate.value"),
												Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value") }));
					}
				}

				int noterms = this.numberOfTerms_two.intValue();
				if ((StringUtils.isEmpty(moduleDefiner))) {
					boolean validationRequired = true;
					int minTerms = financeType.getFinMinTerm();
					int maxTerms = financeType.getFinMaxTerm();
					if (deviationExecutionCtrl != null) {
						List<DeviationHeader> list = deviationExecutionCtrl
								.getProductDeviatations(this.finType.getValue());
						if (list != null && !list.isEmpty()) {
							for (DeviationHeader deviationHeader : list) {
								if (deviationHeader.getDeviationDetails() != null
										&& !deviationHeader.getDeviationDetails().isEmpty()) {
									validationRequired = false;
									break;
								}
							}
						}
					}

					if (minTerms == 0 && maxTerms == 0) {
						validationRequired = false;
					}

					if (validationRequired) {
						if (noterms < minTerms || noterms > maxTerms) {
							throw new WrongValueException(this.numberOfTerms,
									Labels.getLabel("NUMBER_RANGE_EQ",
											new String[] {
													Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"),
													String.valueOf(minTerms), String.valueOf(maxTerms) }));
						}
					}
				}

				aFinanceMain.setNumberOfTerms(noterms);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(row_advEMITerms.isVisible()) {
				int minTerms = financeType.getAdvEMIMinTerms();
				int maxTerms = financeType.getAdvEMIMaxTerms();
				int advEMITerms = this.advEMITerms.intValue();
				int loanTerms = this.numberOfTerms_two.intValue();
				boolean validationRequired = true;

				if (minTerms == 0 && maxTerms == 0) {
					validationRequired = false;
				}

				if (validationRequired) {
					if (advEMITerms < minTerms || advEMITerms > maxTerms) {
						throw new WrongValueException(this.advEMITerms, Labels.getLabel("NUMBER_RANGE_EQ", new String[] {
								Labels.getLabel("label_FinanceMainDialog_AdvEMITerms.value"), String.valueOf(minTerms),
								String.valueOf(maxTerms) }));
					}
				}

				if (advEMITerms >= loanTerms) {
					throw new WrongValueException(this.advEMITerms, Labels.getLabel("NUMBER_MAXVALUE", new String[] {
							Labels.getLabel("label_FinanceMainDialog_AdvEMITerms.value"), String.valueOf(loanTerms)}));
				}

				aFinanceMain.setAdvEMITerms(advEMITerms);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.maturityDate_two.getValue() != null) {
				aFinanceMain.setMaturityDate(DateUtility.getDate(
						DateUtility.formatUtilDate(this.maturityDate_two.getValue(), PennantConstants.dateFormat)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.row_downPayBank.isVisible()) {
				if (this.downPayBank.getActualValue() == null) {
					this.downPayBank.setValue(BigDecimal.ZERO);
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.downPaySupl.getActualValue() == null) {
				this.downPaySupl.setValue(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if (!recSave && (!this.downPayBank.isReadonly() || !this.downPaySupl.isReadonly())) {

				this.downPayBank.clearErrorMessage();
				this.downPaySupl.clearErrorMessage();
				Clients.clearWrongValue(this.downPayBank);

				//Resetting Down payment Percentage Value
				setDownpaymentRulePercentage(false);

				BigDecimal reqDwnPay = PennantAppUtil.getPercentageValue(this.finAmount.getActualValue(),
						aFinanceMain.getMinDownPayPerc());

				BigDecimal downPayment = this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue());

				if (this.finAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0) {
					if (downPayment.compareTo(this.finAmount.getActualValue()) >= 0 && !this.downPaySupl.isReadonly()) {
						throw new WrongValueException(this.downPayBank.getErrorComp(),
								Labels.getLabel("MAND_FIELD_MIN",
										new String[] { Labels.getLabel("label_FinanceMainDialog_DownPayBS.value"),
												String.valueOf(reqDwnPay),
												String.valueOf(this.finAmount.getActualValue()) }));
					} else if (downPayment.compareTo(this.finAmount.getActualValue()) >= 0
							&& this.downPaySupl.isReadonly()) {
						throw new WrongValueException(this.downPayBank.getErrorComp(),
								Labels.getLabel("MAND_FIELD_MIN",
										new String[] { Labels.getLabel("label_FinanceMainDialog_DownPayBank.value"),
												String.valueOf(reqDwnPay),
												String.valueOf(this.finAmount.getActualValue()) }));
					}
				}

				if (downPayment.compareTo(reqDwnPay) < 0 && !this.downPaySupl.isReadonly()) {
					throw new WrongValueException(this.downPayBank.getErrorComp(),
							Labels.getLabel("PERC_MIN",
									new String[] { Labels.getLabel("label_FinanceMainDialog_DownPayBS.value"),
											PennantAppUtil.formatAmount(reqDwnPay, formatter, false) }));
				}

				if (downPayment.compareTo(reqDwnPay) < 0 && this.downPaySupl.isReadonly()) {
					throw new WrongValueException(this.downPayBank.getErrorComp(),
							Labels.getLabel("PERC_MIN",
									new String[] { Labels.getLabel("label_FinanceMainDialog_DownPayBank.value"),
											PennantAppUtil.formatAmount(reqDwnPay, formatter, false) }));
				}
			}
			aFinanceMain
					.setDownPayAccount(PennantApplicationUtil.unFormatAccountNumber(this.downPayAccount.getValue()));
			if (this.row_downPayBank.isVisible() || this.row_downPaySupl.isVisible()) {
				aFinanceMain
						.setDownPayBank(PennantAppUtil.unFormateAmount(this.downPayBank.getActualValue(), formatter));
				aFinanceMain
						.setDownPaySupl(PennantAppUtil.unFormateAmount(this.downPaySupl.getActualValue(), formatter));
				aFinanceMain.setDownPayment(PennantAppUtil.unFormateAmount(
						this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue()), formatter));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.finRepayMethod.isDisabled()
					&& getComboboxValue(this.finRepayMethod).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.finRepayMethod, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceMainDialog_FinRepayMethod.value") }));
			}

			aFinanceMain.setFinRepayMethod(getComboboxValue(this.finRepayMethod));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isOverDraft) {
				//validate Overdraft Limit with configured finmin and fin max amounts
				this.label_FinanceMainDialog_FinAssetValue
						.setValue(Labels.getLabel("label_FinanceMainDialog_ODFinAssetValue.value"));
				validateFinAssetvalue(this.finAssetValue, financeType, formatter);

				// in overDraftMaintenance if the finassetValue is less than the org_finAssetValue then validation is thrown

				if (org_finAssetValue.compareTo(BigDecimal.ZERO) > 0) {
					if (this.finAssetValue.getActualValue()
							.compareTo(PennantAppUtil.formateAmount(org_finAssetValue, formatter)) < 0) {
						throw new WrongValueException(this.finAssetValue.getCcyTextBox(),
								Labels.getLabel("NUMBER_MINVALUE_EQ",
										new String[] { Labels.getLabel("label_FinanceMainDialog_ODFinAssetValue.value"),
												PennantAppUtil.amountFormate(org_finAssetValue, formatter) }));
					}
				}
			}

			if (this.row_FinAssetValue.isVisible()) {
				//Validate if the total disbursement amount exceeds maximum disbursement Amount 
				if (!isBuildEvent() && ((StringUtils.isEmpty(moduleDefiner)
						|| StringUtils.equals(FinanceConstants.FINSER_EVENT_ADDDISB, moduleDefiner)))) {
					if (this.finCurrentAssetValue.getActualValue() != null
							&& finAssetValue.getActualValue().compareTo(BigDecimal.ZERO) > 0
							&& finCurrentAssetValue.getActualValue().compareTo(finAssetValue.getActualValue()) > 0) {
						throw new WrongValueException(finCurrentAssetValue.getCcyTextBox(),
								Labels.getLabel("NUMBER_MAXVALUE_EQ",
										new String[] { this.label_FinanceMainDialog_FinCurrentAssetValue.getValue(),
												String.valueOf(label_FinanceMainDialog_FinAssetValue.getValue()) }));
					}
				}
				aFinanceMain.setFinAssetValue(PennantAppUtil.unFormateAmount(this.finAssetValue.isReadonly()
						? this.finAssetValue.getActualValue() : this.finAssetValue.getValidateValue(), formatter));
			}
			//Validation  on finAsset And fin Current Asset value based on field visibility

			if (!isOverDraft) {
				if (financeType.isAlwMaxDisbCheckReq()) {
					//If max disbursement amount less than prinicpal amount validate the amount
					if (this.row_FinAssetValue.isVisible() && StringUtils.isEmpty(moduleDefiner)) {
						validateFinAssetvalue(this.finAssetValue, financeType, formatter);
						if (this.row_FinAssetValue.isVisible() && finAssetValue.getActualValue() != null
								&& this.finAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0
								&& finAssetValue.getActualValue().compareTo(this.finAmount.getActualValue()) < 0) {

							throw new WrongValueException(finAssetValue.getCcyTextBox(), Labels.getLabel(
									"NUMBER_MINVALUE_EQ",
									new String[] { this.label_FinanceMainDialog_FinAssetValue.getValue(), String
											.valueOf(Labels.getLabel("label_FinanceMainDialog_FinAmount.value")) }));
						}
					} else {
						if (StringUtils.isEmpty(moduleDefiner)) {
							this.label_FinanceMainDialog_FinAssetValue
									.setValue(Labels.getLabel("label_FinanceMainDialog_FinAmount.value"));
							validateFinAssetvalue(this.finAmount, financeType, formatter);
						}
					}

					aFinanceMain.setFinAssetValue(
							PennantAppUtil.unFormateAmount(this.finAssetValue.getActualValue(), formatter));

				} else {
					if (StringUtils.isEmpty(moduleDefiner)) {
						this.label_FinanceMainDialog_FinAssetValue
								.setValue(Labels.getLabel("label_FinanceMainDialog_FinAssetValue.value"));
						validateFinAssetvalue(this.finAmount, financeType, formatter);
					}
					aFinanceMain.setFinAssetValue(
							PennantAppUtil.unFormateAmount(this.finCurrentAssetValue.getActualValue(), formatter));

				}
			}

			aFinanceMain.setFinCurrAssetValue(
					PennantAppUtil.unFormateAmount(this.finCurrentAssetValue.getActualValue(), formatter));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		aFinanceMain.setPftServicingODLimit(false);
		if (isOverDraft) {

			boolean procToCalMaturity = true;
			aFinanceMain.setPftServicingODLimit(this.pftServicingODLimit.isChecked());
			try {

				if (this.odYearlyTerms.intValue() <= 0 && this.odMnthlyTerms.intValue() <= 0) {
					throw new WrongValueException(this.odYearlyTerms,
							"Either Monthly or Yearly Terms need to be entered");
				}
			} catch (WrongValueException we) {
				wve.add(we);
				procToCalMaturity = false;
			}
			try {
				int tenor = 0;

				if (StringUtils.equals(FinanceConstants.FINSER_EVENT_OVERDRAFTSCHD, moduleDefiner) || isOverDraft) {
					String frqCode = this.repayFrq.getFrqCodeValue();

					if (this.odYearlyTerms.intValue() < 1 && this.odMnthlyTerms.intValue() > 0) {
						if (FrequencyCodeTypes.FRQ_QUARTERLY.equals(frqCode) && this.odMnthlyTerms.intValue() < 4) {
							throw new WrongValueException(this.odMnthlyTerms, Labels.getLabel("label_FrqValidation",
									new String[] { String.valueOf(4), "Months" }));
						} else if (FrequencyCodeTypes.FRQ_HALF_YEARLY.equals(frqCode)
								&& this.odMnthlyTerms.intValue() < 6) {
							throw new WrongValueException(this.odMnthlyTerms, Labels.getLabel("label_FrqValidation",
									new String[] { String.valueOf(6), "Months" }));
						} else if (FrequencyCodeTypes.FRQ_YEARLY.equals(frqCode) && this.odYearlyTerms.intValue() < 1) {
							throw new WrongValueException(this.odYearlyTerms,
									Labels.getLabel("label_FrqValidation", new String[] { String.valueOf(1), "Year" }));
						}
					}

					tenor = (this.odYearlyTerms.intValue() * 12) + this.odMnthlyTerms.intValue();

					if (StringUtils.equals(FinanceConstants.FINSER_EVENT_OVERDRAFTSCHD, moduleDefiner)) {
						//Validation in OverDraft Maintenance i.e..,Tenor should be greater than the current business date
						int minNoofMonths;
						if (!financeType.isDroplineOD()) {
							minNoofMonths = DateUtility.getMonthsBetween(this.finStartDate.getValue(),
									DateUtility.getAppDate());
						} else {
							minNoofMonths = DateUtility.getMonthsBetween(this.firstDroplineDate.getValue(),
									DateUtility.getAppDate());
						}
						if (tenor < minNoofMonths) {
							throw new WrongValueException(this.odMnthlyTerms,
									Labels.getLabel("NUMBER_MINVALUE",
											new String[] { Labels.getLabel("label_FinanceMainDialog_ODTenor.value"),
													String.valueOf(minNoofMonths) + " Months" }));
						}
					}

				}
				aFinanceMain.setNumberOfTerms(tenor);
			} catch (WrongValueException we) {
				wve.add(we);
				procToCalMaturity = false;
			}

			try {
				aFinanceMain.setFinAssetValue(
						PennantAppUtil.unFormateAmount(this.finAssetValue.getActualValue(), formatter));
			} catch (WrongValueException we) {
				wve.add(we);
				procToCalMaturity = false;
			}

			try {
				if (this.row_DroplineFrq.isVisible() && this.droplineFrq.isValidComboValue()) {
					aFinanceMain.setDroplineFrq(this.droplineFrq.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
				procToCalMaturity = false;
			}

			try {
				if (this.firstDroplineDate.getValue() != null) {
					if (DateUtility.compare(this.firstDroplineDate.getValue(), this.finStartDate.getValue()) <= 0) {
						throw new WrongValueException(this.firstDroplineDate,
								Labels.getLabel("DATE_ALLOWED_AFTER",
										new String[] { Labels.getLabel("label_FinanceMainDialog_DroplineDate.value"),
												Labels.getLabel("label_FinanceMainDialog_ODStartDate.value") }));

					}

					if (this.odMaturityDate.getValue() != null && DateUtility.compare(this.firstDroplineDate.getValue(),
							this.odMaturityDate.getValue()) >= 0) {
						throw new WrongValueException(this.firstDroplineDate,
								Labels.getLabel("DATE_ALLOWED_BEFORE",
										new String[] { Labels.getLabel("label_FinanceMainDialog_DroplineDate.value"),
												Labels.getLabel("label_FinanceMainDialog_ODMaturityDate.value") }));

					}

					if (StringUtils.isNotBlank(this.droplineFrq.getValue()) && this.firstDroplineDate.getValue() != null
							&& !FrequencyUtil.isFrqDate(this.droplineFrq.getValue(),
									this.firstDroplineDate.getValue())) {
						throw new WrongValueException(this.firstDroplineDate,
								Labels.getLabel("FRQ_DATE_MISMATCH",
										new String[] { Labels.getLabel("label_FinanceMainDialog_DroplineDate.value"),
												Labels.getLabel("label_FinanceMainDialog_DroplineFrequency.value") }));
					}
					aFinanceMain.setFirstDroplineDate(this.firstDroplineDate.getValue());
				}

			} catch (WrongValueException we) {
				wve.add(we);
				procToCalMaturity = false;
			}

			if (procToCalMaturity) {
				aFinanceMain.setMaturityDate(calMaturityDate());
			}

		}

		try {
			aFinanceMain.setAlwBPI(this.alwBpiTreatment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (alwBpiTreatment.isChecked() && isValidComboValue(this.dftBpiTreatment,
					Labels.getLabel("label_FinanceMainDialog_DftBpiTreatment.value"))) {
				aFinanceMain.setBpiTreatment(getComboboxValue(this.dftBpiTreatment));
			} else {
				aFinanceMain.setBpiTreatment(FinanceConstants.BPI_NO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setPlanEMIHAlw(this.alwPlannedEmiHoliday.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.alwPlannedEmiHoliday.isChecked()) {
			try {
				if (isValidComboValue(this.planEmiMethod,
						Labels.getLabel("label_FinanceMainDialog_PlanEmiHolidayMethod.value"))) {
					aFinanceMain.setPlanEMIHMethod(getComboboxValue(this.planEmiMethod));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceMain.setPlanEMIHMaxPerYear(this.maxPlanEmiPerAnnum.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceMain.setPlanEMIHMax(this.maxPlanEmi.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceMain.setPlanEMIHLockPeriod(this.planEmiHLockPeriod.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceMain.setPlanEMICpz(this.cpzAtPlanEmi.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			aFinanceMain.setPlanEMIHMethod("");
			aFinanceMain.setPlanEMIHMaxPerYear(0);
			aFinanceMain.setPlanEMIHMax(0);
			aFinanceMain.setPlanEMIHLockPeriod(0);
			aFinanceMain.setPlanEMICpz(false);
		}

		try {
			aFinanceMain.setUnPlanEMIHLockPeriod(this.unPlannedEmiHLockPeriod.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setMaxUnplannedEmi(this.maxUnplannedEmi.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setMaxReAgeHolidays(this.maxReAgeHolidays.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setUnPlanEMICpz(this.cpzAtUnPlannedEmi.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setReAgeCpz(this.cpzAtReAge.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!this.roundingMode.isReadonly() && isValidComboValue(this.roundingMode,
					Labels.getLabel("label_FinanceMainDialog_RoundingMode.value"))) {

				aFinanceMain.setCalRoundingMode(getComboboxValue(this.roundingMode));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//FinanceMain Details Tab ---> 4. Overdue Penalty Details

		FinODPenaltyRate penaltyRate = null;
		if (this.applyODPenalty.isChecked()) {

			penaltyRate = new FinODPenaltyRate();
			try {
				penaltyRate.setApplyODPenalty(this.applyODPenalty.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				penaltyRate.setODIncGrcDays(this.oDIncGrcDays.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.applyODPenalty.isChecked()
						&& getComboboxValue(this.oDChargeType).equals(PennantConstants.List_Select)) {
					throw new WrongValueException(this.oDChargeType, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinanceMainDialog_ODChargeType.value") }));
				}
				penaltyRate.setODChargeType(getComboboxValue(this.oDChargeType));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				penaltyRate.setODGraceDays(this.oDGraceDays.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.applyODPenalty.isChecked() && !this.oDChargeCalOn.isDisabled()
						&& getComboboxValue(this.oDChargeCalOn).equals(PennantConstants.List_Select)) {
					throw new WrongValueException(this.oDChargeCalOn, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinanceMainDialog_ODChargeCalOn.value") }));
				}
				penaltyRate.setODChargeCalOn(getComboboxValue(this.oDChargeCalOn));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				//Mandatory Validation
				if (!this.oDChargeAmtOrPerc.isDisabled()) {
					if (this.oDChargeAmtOrPerc.getValue() == null
							|| this.oDChargeAmtOrPerc.getValue().compareTo(BigDecimal.ZERO) == 0) {
						throw new WrongValueException(this.oDChargeAmtOrPerc, Labels.getLabel("MUST_BE_ENTERED",
								new String[] { Labels.getLabel("FinanceMainDialog_oDChargeAmtOrPerc.value") }));
					}
					if (this.oDChargeAmtOrPerc.getValue().compareTo(BigDecimal.ZERO) < 0) {
						throw new WrongValueException(this.oDChargeAmtOrPerc, Labels.getLabel(
								"PERCENT_NOTNEGATIVE_LABEL",
								new String[] { Labels.getLabel("FinanceMainDialog_oDChargeAmtOrPerc.value"), "0" }));
					}
				}
				if (FinanceConstants.PENALTYTYPE_FLAT.equals(getComboboxValue(this.oDChargeType))
						|| FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					penaltyRate.setODChargeAmtOrPerc(
							PennantAppUtil.unFormateAmount(this.oDChargeAmtOrPerc.getValue(), formatter));
				} else if (FinanceConstants.PENALTYTYPE_PERC_ONETIME.equals(getComboboxValue(this.oDChargeType))
						|| FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(getComboboxValue(this.oDChargeType))
						|| FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					penaltyRate
							.setODChargeAmtOrPerc(PennantAppUtil.unFormateAmount(this.oDChargeAmtOrPerc.getValue(), 2));
				}

			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {

				if (this.oDAllowWaiver.isChecked()) {
					if (this.oDMaxWaiverPerc.getValue() == null
							|| this.oDMaxWaiverPerc.getValue().compareTo(BigDecimal.ZERO) == 0) {
						throw new WrongValueException(this.oDMaxWaiverPerc, Labels.getLabel("MUST_BE_ENTERED",
								new String[] { Labels.getLabel("label_FinanceTypeDialog_ODMaxWaiver.value") }));
					}
					if (this.oDMaxWaiverPerc.getValue().compareTo(BigDecimal.ZERO) < 0) {
						throw new WrongValueException(this.oDMaxWaiverPerc, Labels.getLabel("PERCENT_NOTNEGATIVE_LABEL",
								new String[] { Labels.getLabel("label_FinanceTypeDialog_ODMaxWaiver.value"), "0" }));
					}
					if (this.oDMaxWaiverPerc.getValue().compareTo(new BigDecimal(100)) > 0) {
						throw new WrongValueException(this.oDMaxWaiverPerc, Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
								new String[] { Labels.getLabel("label_FinanceTypeDialog_ODMaxWaiver.value"), "100" }));
					}
				}
				penaltyRate.setODAllowWaiver(this.oDAllowWaiver.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.oDAllowWaiver.isChecked()) {
					penaltyRate.setODMaxWaiverPerc(this.oDMaxWaiverPerc.getValue() == null ? BigDecimal.ZERO
							: this.oDMaxWaiverPerc.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		//FinanceMain Details Tab ---> 5. DDA Registration Details
		if (this.gb_ddaRequest.isVisible()) {
			try {
				aFinanceMain.setBankName(this.bankName.getValue());
				aFinanceMain.setBankNameDesc(this.bankName.getDescription());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aFinanceMain.setIban(this.iban.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceMain.setIfscCode(this.ifscCode.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (!this.accountType.isDisabled()) {
					if (getComboboxValue(this.accountType).equals(PennantConstants.List_Select) && !recSave
							&& !buildEvent) {
						throw new WrongValueException(this.accountType, Labels.getLabel("STATIC_INVALID",
								new String[] { Labels.getLabel("label_FinanceMainDialog_AccountType.value") }));
					}
					aFinanceMain.setAccountType(getComboboxValue(this.accountType));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		// ###_0.3
		//Eligibility Method
		try {
			this.eligibilityMethod.getValidatedValue();
			aFinanceMain.setLovEligibilityMethod(this.eligibilityMethod.getValue());
			Object object = this.eligibilityMethod.getAttribute("FieldCodeId");
			if (object != null) {
				aFinanceMain.setEligibilityMethod(Long.parseLong(object.toString()));
			} else {
				aFinanceMain.setEligibilityMethod(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//FinanceMain Details Tab ---> Rollover Finance Details
		if (this.gb_RolloverFinance.isVisible()) {

			RolledoverFinanceHeader rolledoverFinanceHeader = getFinanceDetail().getRolledoverFinanceHeader();
			rolledoverFinanceHeader.setFinReference(this.finReference.getValue());

			try {
				rolledoverFinanceHeader.setCustPayment(
						PennantAppUtil.unFormateAmount(this.custPaymentAmount.getActualValue(), formatter));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				rolledoverFinanceHeader
						.setPaymentAccount(PennantApplicationUtil.unFormatAccountNumber(this.custPayAccId.getValue()));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				rolledoverFinanceHeader.setLatePayAmount(
						PennantAppUtil.unFormateAmount(this.latePayAmount.getActualValue(), formatter));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {

				if (!this.latePayWaiverAmount.isDisabled() && this.latePayWaiverAmount.getActualValue()
						.compareTo(this.latePayAmount.getActualValue()) > 0) {
					throw new WrongValueException(this.latePayWaiverAmount,
							Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
									new String[] { Labels.getLabel("label_FinanceMainDialog_LatePayWaiverAmount.value"),
											latePayAmount.getActualValue().toString() }));
				}
				rolledoverFinanceHeader.setLatePayWaiverAmount(
						PennantAppUtil.unFormateAmount(this.latePayWaiverAmount.getActualValue(), formatter));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			getFinanceDetail().setRolledoverFinanceHeader(rolledoverFinanceHeader);

			//Rollover Details 
			List<RolledoverFinanceDetail> list = rolledoverFinanceHeader.getRolledoverFinanceDetails();
			for (RolledoverFinanceDetail detail : list) {
				detail.setNewFinReference(this.finReference.getValue());
				Decimalbox custPayAmount = (Decimalbox) this.listBoxRolledoverFinance
						.getFellowIfAny("custPayAmount_" + detail.getFinReference());
				detail.setCustPayment(PennantAppUtil.unFormateAmount(custPayAmount.getValue(), formatter));

				// validate Customer payment amount
				if (detail.getCustPayment().compareTo(detail.getTotalPriBal().add(detail.getTotalPftBal())) > 0) {
					throw new WrongValueException(custPayAmount,
							Labels.getLabel("NUMBER_MAXVALUE_EQ",
									new String[] { Labels.getLabel("label_RolloverFinanceMainDialog_CustPaymentAmount"),
											PennantAppUtil.amountFormate(
													detail.getTotalPriBal().add(detail.getTotalPftBal()),
													formatter) }));
				}
				// validate Finance start date with Rollover Date
				if (this.finStartDate.getValue().compareTo(detail.getRolloverDate()) < 0) {
					throw new WrongValueException(this.finStartDate,
							Labels.getLabel("DATE_ALLOWED_MINDATE_EQUAL",
									new String[] { Labels.getLabel("label_RolloverFinanceMainDialog_FinStartdate"),
											DateUtility.formatToLongDate(detail.getRolloverDate()) }));
				}
			}
		} else {
			getFinanceDetail().setRolledoverFinanceHeader(null);
		}

		if (wve.isEmpty()) {

			// Finance Overdue Details set to Penalty Rate Object FIXME:
			if (!this.buildEvent) {
				aFinanceSchData.setFinODPenaltyRate(penaltyRate);
			}

			if (this.allowGrace.isChecked()) {
				aFinanceMain.setGrcRateBasis(this.grcRateBasis.getSelectedItem().getValue().toString());

				if (StringUtils.isEmpty(aFinanceMain.getGrcCpzFrq())) {
					aFinanceMain.setAllowGrcCpz(false);
				}
			}

			aFinanceMain.setRepayRateBasis(this.repayRateBasis.getSelectedItem().getValue().toString());
			aFinanceMain.setRecalType("");
			aFinanceMain.setCalculateRepay(true);
			if (aFinanceMain.isManualSchedule()) {
				aFinanceMain.setCalculateRepay(false);
			}
			if (isFinPreApproved) {
				aFinanceMain.setRcdMaintainSts("");
			} else {
				aFinanceMain.setRcdMaintainSts(moduleDefiner);
			}

			aFinanceMain.setReqRepayAmount(BigDecimal.ZERO);
			if (this.finRepaymentAmount.getActualValue() != null) {
				if (this.finRepaymentAmount.getActualValue().compareTo(BigDecimal.ZERO) == 1) {
					aFinanceMain.setCalculateRepay(false);
					aFinanceMain.setReqRepayAmount(
							PennantAppUtil.unFormateAmount(this.finRepaymentAmount.getActualValue(), formatter));
				}
			}

			//Reset Maturity Date for maintainance purpose
			if (!buildEvent && aFinanceSchData.getFinanceScheduleDetails() != null
					&& !aFinanceSchData.getFinanceScheduleDetails().isEmpty()) {

				int size = aFinanceSchData.getFinanceScheduleDetails().size();
				// Resetting Maturity Terms & Summary details rendering incase of Reduce maturity cases
				if (!isOverDraft) {
					for (int i = size - 1; i >= 0; i--) {
						FinanceScheduleDetail curSchd = aFinanceSchData.getFinanceScheduleDetails().get(i);
						if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
								&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
							aFinanceMain.setMaturityDate(curSchd.getSchDate());
							break;
						} else if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
								&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
							aFinanceSchData.getFinanceScheduleDetails().remove(i);
						}
					}
				} else {
					aFinanceMain
							.setMaturityDate(aFinanceSchData.getFinanceScheduleDetails().get(size - 1).getSchDate());
				}

				aFinanceSchData.setFinanceScheduleDetails(sortSchdDetails(aFinanceSchData.getFinanceScheduleDetails()));
				//Reset Grace period End Date while Change Frequency Option
				if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CHGFRQ)) {
					for (int i = 0; i < aFinanceSchData.getFinanceScheduleDetails().size(); i++) {
						FinanceScheduleDetail curSchd = aFinanceSchData.getFinanceScheduleDetails().get(i);
						if (curSchd.getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_GRACE_END)) {
							aFinanceMain.setGrcPeriodEndDate(curSchd.getSchDate());
						}
					}
				}

			}

			aFinanceMain.setEqualRepay(financeType.isEqualRepayment());
			aFinanceMain.setIncreaseTerms(false);
			aFinanceMain.setRecordStatus(this.recordStatus.getValue());
			if (StringUtils.isBlank(aFinanceMain.getFinSourceID())) {
				aFinanceMain.setFinSourceID(App.CODE);
			}
			aFinanceMain.setFinIsActive(true);
			if (isFinPreApproved) {
				aFinanceMain.setFinPreApprovedRef(FinanceConstants.FINSER_EVENT_PREAPPROVAL);
			}

			//Maturity Calculation for Commercial 
			int months = DateUtility.getMonthsBetween(aFinanceMain.getFinStartDate(), aFinanceMain.getMaturityDate(),
					true);
			if (months > 0) {
				aFinanceMain.setMaturity(new BigDecimal((months / 12) + "." + (months % 12)));
			}

			aFinanceSchData.setFinanceMain(aFinanceMain);

			//Istisna Disbursement Details validations
			if (buildEvent && disbursementDetailDialogCtrl != null) {
				aFinanceSchData.setDisbursementDetails(disbursementDetailDialogCtrl.getDisbursementDetails());
			}

			// Fee Details Validations on Customer data
			if (buildEvent && customerDialogCtrl != null) {
				try {
					customerDialogCtrl.doValidateFeeDetails(custDetailTab);
				} catch (WrongValueException e) {
					throw e;
				}
			}

			boolean isIstisnaProduct = false;
			if (getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_ISTISNA)) {
				isIstisnaProduct = true;
			}

			aFinanceSchData = doWriteSchData(aFinanceSchData, isIstisnaProduct);
		}

		//FinanceMain Details Tab Validation Error Throwing
		if (!getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_SUKUK)) {
			showErrorDetails(wve, financeTypeDetailsTab);
		}

		long intiateUser = aFinanceMain.getInitiateUser();

		if (intiateUser == 0) {
			if (isFirstTask() && getUserWorkspace().getUserRoles().contains(getWorkFlow().firstTaskOwner())) {
				aFinanceMain.setInitiateUser(getUserWorkspace().getLoggedInUser().getUserId());
			}
		}

		if (aFinanceMain.getInitiateDate() == null && getUserWorkspace().getLoggedInUser().getUserId() != 0) {
			if (!recSave) {
				aFinanceMain.setInitiateDate(DateUtility.getAppDate());
			}
		}

		setCollateralRuleValues();

		if (this.collateralRuleMap.containsKey("COLLATERAL_TYPES")) {
			aFinanceMain.setCollateralType(this.collateralRuleMap.get("COLLATERAL_TYPES").toString());
		}

		if (this.collateralRuleMap.containsKey("MARKET_VALUE")) {
			aFinanceMain.setMarketValue((BigDecimal) this.collateralRuleMap.get("MARKET_VALUE"));
		}

		if (this.collateralRuleMap.containsKey("GUIDED_VALUE")) {
			aFinanceMain.setGuidedValue((BigDecimal) this.collateralRuleMap.get("GUIDED_VALUE"));
		}

		Set<String> finReferens = new HashSet<>();

		aFinanceMain.setTotalExposure(BigDecimal.ZERO);

		// Customer Exposure
		if (!CollectionUtils.isEmpty(getFinanceDetail().getCustomerDetails().getCustFinanceExposureList())) {
			for (FinanceEnquiry financeEnquiry : getFinanceDetail().getCustomerDetails().getCustFinanceExposureList()) {
				if (!finReferens.contains(financeEnquiry.getFinReference())) {
					finReferens.add(financeEnquiry.getFinReference());
					aFinanceMain.setTotalExposure(
							aFinanceMain.getTotalExposure().add(financeEnquiry.getFinCurrAssetValue()));

				}
			}
		}
		// Co Applicent Exposure
		List<FinanceExposure> exposures = new ArrayList<>();
		if (jointAccountDetailDialogCtrl != null) {
			exposures = jointAccountDetailDialogCtrl.getExposureList();
		}
		for (FinanceExposure financeExposure : exposures) {
			if (!finReferens.contains(financeExposure.getFinReference())) {
				finReferens.add(financeExposure.getFinReference());
				aFinanceMain
						.setTotalExposure(aFinanceMain.getTotalExposure().add(financeExposure.getCurrentExpoSure()));
			}
		}

		aFinanceMain.setSamplingRequired(samplingRequired.isChecked());
		aFinanceMain.setLegalRequired(legalRequired.isChecked());
		
		logger.debug(" Total Exposure for Reference " + aFinanceMain.getFinReference() + "---"
				+ aFinanceMain.getTotalExposure());

		return wve;
	}

	/*
	 * validates finAmount or FinAssetvalue based on the field visibility by Finmax and finmin Amount from loan type
	 * configuration.
	 */
	private void validateFinAssetvalue(CurrencyBox finAllowedAmt, FinanceType financeType, int formatter) {
		BigDecimal finMinAmount = PennantApplicationUtil.formateAmount(financeType.getFinMinAmount(), formatter);
		BigDecimal finMaxAmount = PennantApplicationUtil.formateAmount(financeType.getFinMaxAmount(), formatter);

		if (finAllowedAmt.getActualValue() != null && finMinAmount.compareTo(BigDecimal.ZERO) > 0
				&& finAllowedAmt.getActualValue().compareTo(finMinAmount) < 0) {
			throw new WrongValueException(finAllowedAmt.getCcyTextBox(),
					Labels.getLabel("NUMBER_MINVALUE_EQ",
							new String[] { this.label_FinanceMainDialog_FinAssetValue.getValue(),
									PennantApplicationUtil.amountFormate(financeType.getFinMinAmount(), formatter) }));
		}
		if (finAllowedAmt.getActualValue() != null && finMaxAmount.compareTo(BigDecimal.ZERO) > 0
				&& finAllowedAmt.getActualValue().compareTo(finMaxAmount) > 0) {
			throw new WrongValueException(finAllowedAmt.getCcyTextBox(),
					Labels.getLabel("NUMBER_MAXVALUE_EQ",
							new String[] { this.label_FinanceMainDialog_FinAssetValue.getValue(),
									PennantApplicationUtil.amountFormate(financeType.getFinMaxAmount(), formatter) }));
		}

	}

	private String getShariaStatus() {
		String shariaSts = "";
		String existingShariaSts = getFinanceDetail().getFinScheduleData().getFinanceMain().getShariaStatus();
		if (StringUtils.containsIgnoreCase(existingShariaSts, PennantConstants.SHARIA_STATUS_APPROVED)) {
			shariaSts = existingShariaSts;
		} else {
			if (this.shariaApprovalReq.isChecked()) {
				if (StringUtils.containsIgnoreCase(userAction.getSelectedItem().getValue().toString(), "Approve")) {
					shariaSts = PennantConstants.SHARIA_STATUS_APPROVED;
				} else if (StringUtils.containsIgnoreCase(userAction.getSelectedItem().getValue().toString(),
						"Decline")) {
					shariaSts = PennantConstants.SHARIA_STATUS_DECLINED;
				} else {
					shariaSts = PennantConstants.SHARIA_STATUS_PENDING;
				}
			} else {
				shariaSts = PennantConstants.SHARIA_STATUS_NOTREQUIRED;
			}
		}
		return shariaSts;
	}

	/**
	 * Method for Sorting Schdue details
	 * 
	 * @param financeScheduleDetail
	 * @return
	 */
	private List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

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
	 * Method to show error details if occurred
	 * 
	 **/
	protected void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			buildEvent = false;
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (comp instanceof HtmlBasedComponent) {
						Clients.scrollIntoView(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug("Leaving");
	}

	// GUI operations

	/**
	 * Method for Executing Eligibility Details
	 * 
	 * @throws Exception
	 */
	public void onExecuteAccountingDetail(Boolean onLoadProcess) throws Exception {
		logger.debug("Entering");
		buildEvent = false;

		doSetValidation();
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		boolean isOverdraft = false;

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		try {
			if (!isOverdraft) {
				this.finAmount.getValidateValue();
			} else {
				this.finAssetValue.getValidateValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.custCIF.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.finBranch.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.disbAcctId.validateValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.repayAcctId.validateValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.downPayAccount.validateValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		accountingDetailDialogCtrl.getLabel_AccountingDisbCrVal().setValue("");
		accountingDetailDialogCtrl.getLabel_AccountingDisbDrVal().setValue("");
		if (onLoadProcess) {
			showErrorDetails(wve, financeTypeDetailsTab);
		}
		wve = null;
		if (!isOverdraft) {
			if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() <= 0) {
				MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
				return;
			}
		} else {
			if (getFinanceDetail().getFinScheduleData().getOverdraftScheduleDetails().size() <= 0
					&& getFinanceDetail().getFinScheduleData().getFinanceType().isDroplineOD()) {
				MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
				return;
			}
		}

		//Finance Accounting Details Execution
		executeAccounting(onLoadProcess);
		logger.debug("Leaving");
	}

	/**
	 * Method for Executing Accounting tab Rules
	 * 
	 * @throws Exception
	 * 
	 */
	private void executeAccounting(boolean onLoadProcess) throws Exception {
		logger.debug("Entering");

		List<ReturnDataSet> accountingSetEntries = new ArrayList<ReturnDataSet>();

		FinanceProfitDetail profitDetail = null;
		if (StringUtils.isEmpty(moduleDefiner)) {
			profitDetail = new FinanceProfitDetail();
		} else {
			profitDetail = getFinanceDetailService()
					.getFinProfitDetailsById(getFinanceDetail().getFinScheduleData().getFinReference());
		}

		AEEvent aeEvent = prepareAccountingData(onLoadProcess, profitDetail);
		HashMap<String, Object> dataMap = aeEvent.getDataMap();

		prepareFeeRulesMap(aeEvent.getAeAmountCodes(), dataMap);

		//GST Added		
		String branch = getUserWorkspace().getLoggedInUser().getBranchCode();
		HashMap<String, Object> gstExecutionMap = getFinanceDetailService().prepareGstMappingDetails(getFinanceDetail(),
				branch);
		if (gstExecutionMap != null) {
			for (String key : gstExecutionMap.keySet()) {
				if (StringUtils.isNotBlank(key)) {
					dataMap.put(key, gstExecutionMap.get(key));
				}
			}
		}

		aeEvent.getAeAmountCodes().getDeclaredFieldValues(dataMap);
		aeEvent.setDataMap(dataMap);

		aeEvent = getEngineExecution().getAccEngineExecResults(aeEvent);
		accountingSetEntries.addAll(aeEvent.getReturnDataSet());

		//Disb Instruction Posting
		if (eventCode.equals(AccountEventConstants.ACCEVENT_ADDDBS)
				|| eventCode.equals(AccountEventConstants.ACCEVENT_ADDDBSF)
				|| eventCode.equals(AccountEventConstants.ACCEVENT_ADDDBSN)
				|| eventCode.equals(AccountEventConstants.ACCEVENT_ADDDBSP)) {

			accountingSetEntries.addAll(disbursementPostings.getDisbPosting(getFinanceDetail().getAdvancePaymentsList(),
					getFinanceDetail().getFinScheduleData().getFinanceMain()));

			//	prepareDisbInstructionPosting(accountingSetEntries, aeEvent);
		}

		// Vas Recording Accounting Entries
		if (StringUtils.isEmpty(moduleDefiner)) {
			if (finVasRecordingDialogCtrl != null && finVasRecordingDialogCtrl.getVasRecordings() != null
					&& !finVasRecordingDialogCtrl.getVasRecordings().isEmpty()) {
				accountingSetEntries.addAll(getFinanceDetailService().prepareVasAccounting(aeEvent,
						finVasRecordingDialogCtrl.getVasRecordings()));
			}
			accountingSetEntries.addAll(getInstallmentDueService().processbackDateInstallmentDues(getFinanceDetail(),
					profitDetail, DateUtility.getAppDate(), false, ""));
		}

		getFinanceDetail().setReturnDataSetList(accountingSetEntries);

		if (accountingDetailDialogCtrl != null) {
			accountingDetailDialogCtrl.doFillAccounting(accountingSetEntries);
		}

		logger.debug("Leaving");
	}

	private AEEvent prepareAccountingData(boolean onLoadProcess, FinanceProfitDetail profitDetail)
			throws InterruptedException, IllegalAccessException, InvocationTargetException {

		Date curBDay = DateUtility.getAppDate();

		FinScheduleData finScheduleData = getFinanceDetail().getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		if (onLoadProcess) {
			doWriteComponentsToBean(getFinanceDetail().getFinScheduleData());
		}

		if (StringUtils.isBlank(eventCode)) {
			eventCode = PennantApplicationUtil.getEventCode(finMain.getFinStartDate());
		}

		BigDecimal totalPftSchdOld = BigDecimal.ZERO;
		BigDecimal totalPftCpzOld = BigDecimal.ZERO;
		BigDecimal totalPriSchdOld = BigDecimal.ZERO;
		//For New Records Profit Details will be set inside the AEAmounts 
		FinanceProfitDetail newProfitDetail = new FinanceProfitDetail();
		if (profitDetail != null) {//FIXME
			BeanUtils.copyProperties(profitDetail, newProfitDetail);
			totalPftSchdOld = profitDetail.getTotalPftSchd();
			totalPftCpzOld = profitDetail.getTotalPftCpz();
			totalPriSchdOld = profitDetail.getTotalpriSchd();
		}

		AEEvent aeEvent = AEAmounts.procAEAmounts(finMain, finSchdDetails, profitDetail, eventCode, curBDay, curBDay);
		if (StringUtils.isNotBlank(finMain.getPromotionCode())) {
			//	aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getPromotionCode(), eventCode, FinanceConstants.MODULEID_PROMOTION));
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getFinType(), eventCode,
					FinanceConstants.MODULEID_FINTYPE));
		} else {
			aeEvent.getAcSetIDList().add(AccountingConfigCache.getAccountSetID(finMain.getFinType(), eventCode,
					FinanceConstants.MODULEID_FINTYPE));
		}

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		accrualService.calProfitDetails(finMain, finSchdDetails, newProfitDetail, curBDay);
		if (!FinanceConstants.BPI_NO.equals(finMain.getBpiTreatment())) {
			amountCodes.setBpi(finMain.getBpiAmount());
		}
		BigDecimal totalPftSchdNew = newProfitDetail.getTotalPftSchd();
		BigDecimal totalPftCpzNew = newProfitDetail.getTotalPftCpz();

		amountCodes.setPftChg(totalPftSchdNew.subtract(totalPftSchdOld));
		amountCodes.setCpzChg(totalPftCpzNew.subtract(totalPftCpzOld));

		aeEvent.setModuleDefiner(
				StringUtils.isEmpty(moduleDefiner) ? FinanceConstants.FINSER_EVENT_ORG : moduleDefiner);
		if (StringUtils.isEmpty(moduleDefiner)) {
			amountCodes.setDisburse(finMain.getFinCurrAssetValue().add(finMain.getDownPayment()));
		} else {
			amountCodes.setDisburse(newProfitDetail.getTotalpriSchd().subtract(totalPriSchdOld));
		}

		if (finMain.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(finMain.getRecordType())) {
			aeEvent.setNewRecord(true);
		}

		return aeEvent;
	}

	private void prepareFeeRulesMap(AEAmountCodes amountCodes, HashMap<String, Object> dataMap) {
		logger.debug("Entering");

		List<FinFeeDetail> finFeeDetailList = getFinanceDetail().getFinScheduleData().getFinFeeDetailList();

		if (finFeeDetailList != null) {
			FeeRule feeRule;

			BigDecimal deductFeeDisb = BigDecimal.ZERO;
			BigDecimal addFeeToFinance = BigDecimal.ZERO;
			BigDecimal paidFee = BigDecimal.ZERO;
			BigDecimal feeWaived = BigDecimal.ZERO;

			//VAS
			BigDecimal deductVasDisb = BigDecimal.ZERO;
			BigDecimal addVasToFinance = BigDecimal.ZERO;
			BigDecimal paidVasFee = BigDecimal.ZERO;
			BigDecimal vasFeeWaived = BigDecimal.ZERO;

			for (FinFeeDetail finFeeDetail : finFeeDetailList) {
				if (!finFeeDetail.isRcdVisible()) {
					continue;
				}

				feeRule = new FeeRule();

				feeRule.setFeeCode(finFeeDetail.getFeeTypeCode());
				feeRule.setFeeAmount(finFeeDetail.getActualAmount());
				feeRule.setWaiverAmount(finFeeDetail.getWaivedAmount());
				feeRule.setPaidAmount(finFeeDetail.getPaidAmount());
				feeRule.setFeeToFinance(finFeeDetail.getFeeScheduleMethod());
				feeRule.setFeeMethod(finFeeDetail.getFeeScheduleMethod());

				dataMap.put(finFeeDetail.getFeeTypeCode() + "_C", finFeeDetail.getActualAmountOriginal());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_W", finFeeDetail.getWaivedAmount());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_P", finFeeDetail.getPaidAmountOriginal());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_N", finFeeDetail.getNetAmountOriginal());

				//GST 
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_N", finFeeDetail.getNetAmount());
				//Calculated Amount 
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_C", finFeeDetail.getFinTaxDetails().getActualCGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_C", finFeeDetail.getFinTaxDetails().getActualSGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_C", finFeeDetail.getFinTaxDetails().getActualIGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_C", finFeeDetail.getFinTaxDetails().getActualUGST());

				//Paid Amount 
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_P", finFeeDetail.getFinTaxDetails().getPaidCGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_P", finFeeDetail.getFinTaxDetails().getPaidSGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_P", finFeeDetail.getFinTaxDetails().getPaidIGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_P", finFeeDetail.getFinTaxDetails().getPaidUGST());

				//Net Amount 
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_N", finFeeDetail.getFinTaxDetails().getNetCGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_N", finFeeDetail.getFinTaxDetails().getNetSGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_N", finFeeDetail.getFinTaxDetails().getNetIGST());
				dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_N", finFeeDetail.getFinTaxDetails().getNetUGST());

				if (feeRule.getFeeToFinance().equals(CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)
						|| feeRule.getFeeToFinance().equals(CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)
						|| feeRule.getFeeToFinance().equals(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)) {
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_SCH", finFeeDetail.getRemainingFeeOriginal());
					//GST
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_SCH",
							finFeeDetail.getFinTaxDetails().getRemFeeCGST());
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_SCH",
							finFeeDetail.getFinTaxDetails().getRemFeeSGST());
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_SCH",
							finFeeDetail.getFinTaxDetails().getRemFeeIGST());
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_SCH",
							finFeeDetail.getFinTaxDetails().getRemFeeUGST());
				} else {
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_SCH", 0);
					//GST
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_SCH", 0);
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_SCH", 0);
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_SCH", 0);
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_SCH", 0);
				}

				if (StringUtils.equals(feeRule.getFeeToFinance(), RuleConstants.DFT_FEE_FINANCE)) {
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_AF", finFeeDetail.getRemainingFeeOriginal());
					//GST
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_AF",
							finFeeDetail.getFinTaxDetails().getRemFeeCGST());
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_AF",
							finFeeDetail.getFinTaxDetails().getRemFeeSGST());
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_AF",
							finFeeDetail.getFinTaxDetails().getRemFeeIGST());
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_AF",
							finFeeDetail.getFinTaxDetails().getRemFeeUGST());
				} else {
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_AF", 0);
					//GST
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_CGST_AF", 0);
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_SGST_AF", 0);
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_IGST_AF", 0);
					dataMap.put(finFeeDetail.getFeeTypeCode() + "_UGST_AF", 0);
				}

				if (finFeeDetail.getFeeScheduleMethod().equals(CalculationConstants.REMFEE_PART_OF_DISBURSE)) {
					deductFeeDisb = deductFeeDisb.add(finFeeDetail.getRemainingFee());
					if (AccountEventConstants.ACCEVENT_VAS_FEE.equals(finFeeDetail.getFinEvent())) {
						deductVasDisb = deductVasDisb.add(finFeeDetail.getActualAmount());
					}
				} else if (finFeeDetail.getFeeScheduleMethod().equals(CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
					addFeeToFinance = addFeeToFinance.add(finFeeDetail.getRemainingFee());
					if (AccountEventConstants.ACCEVENT_VAS_FEE.equals(finFeeDetail.getFinEvent())) {
						addVasToFinance = addVasToFinance.add(finFeeDetail.getActualAmount());
					}
				}

				paidFee = paidFee.add(finFeeDetail.getPaidAmount());
				feeWaived = feeWaived.add(finFeeDetail.getWaivedAmount());

				if (AccountEventConstants.ACCEVENT_VAS_FEE.equals(finFeeDetail.getFinEvent())) {
					paidVasFee = paidVasFee.add(finFeeDetail.getPaidAmount());
					vasFeeWaived = vasFeeWaived.add(finFeeDetail.getWaivedAmount());
				}
			}

			amountCodes.setDeductFeeDisb(deductFeeDisb);
			amountCodes.setAddFeeToFinance(addFeeToFinance);
			amountCodes.setFeeWaived(feeWaived);
			amountCodes.setPaidFee(paidFee);

			//VAS
			amountCodes.setDeductVasDisb(deductVasDisb);
			amountCodes.setAddVasToFinance(addVasToFinance);
			amountCodes.setVasFeeWaived(vasFeeWaived);
			amountCodes.setPaidVasFee(paidVasFee);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method For Preparing Fees & Disbursement Details
	 * 
	 * @param aFinanceSchData
	 * @param isIstisnaProd
	 * @return
	 * @throws InterruptedException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private FinScheduleData doWriteSchData(FinScheduleData aFinanceSchData, boolean isIstisnaProd)
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		FinanceMain aFinanceMain = aFinanceSchData.getFinanceMain();

		Tab taxTab = getTab(AssetConstants.UNIQUE_ID_TAX);
		if (financeTaxDetailDialogCtrl != null && taxTab.isVisible()) {
			financeTaxDetailDialogCtrl.doSave_Tax(getFinanceDetail(), taxTab, recSave);
		} else {
			getFinanceDetail().setFinanceTaxDetails(null);
		}

		if (buildEvent) {

			aFinanceSchData.getFinanceMain().setFeeChargeAmt(BigDecimal.ZERO);
			aFinanceSchData.getFinanceMain().setInsuranceAmt(BigDecimal.ZERO);
			if (finFeeDetailListCtrl != null) {
				finFeeDetailListCtrl.doExecuteFeeCharges(true, aFinanceSchData);
				//Fill the Insurances listbox's data for the  amounts calculated
				finFeeDetailListCtrl.doFillFinInsurances(aFinanceSchData.getFinInsuranceList());
			}

			if (!isIstisnaProd
					&& !StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())) {

				Date curBDay = DateUtility.getAppDate();
				aFinanceSchData.getDisbursementDetails().clear();
				FinanceDisbursement disbursementDetails = new FinanceDisbursement();
				disbursementDetails.setDisbDate(aFinanceMain.getFinStartDate());
				disbursementDetails.setDisbSeq(1);
				disbursementDetails.setDisbAmount(aFinanceMain.getFinAmount());
				disbursementDetails.setDisbReqDate(curBDay);
				disbursementDetails.setFeeChargeAmt(aFinanceSchData.getFinanceMain().getFeeChargeAmt());
				disbursementDetails.setInsuranceAmt(aFinanceSchData.getFinanceMain().getInsuranceAmt());
				disbursementDetails
						.setDisbAccountId(PennantApplicationUtil.unFormatAccountNumber(this.disbAcctId.getValue()));
				aFinanceSchData.getDisbursementDetails().add(disbursementDetails);

				if (aFinanceMain.getFinAssetValue().compareTo(aFinanceMain.getFinAmount()) > 0 && (StringUtils
						.equals(FinanceConstants.PRODUCT_IJARAH, aFinanceSchData.getFinanceType().getFinCategory())
						|| StringUtils.equals(FinanceConstants.PRODUCT_FWIJARAH,
								aFinanceSchData.getFinanceType().getFinCategory()))) {
					FinanceDisbursement remAssetDisburse = new FinanceDisbursement();

					remAssetDisburse.setDisbSeq(aFinanceSchData.getDisbursementDetails().size() + 1);
					remAssetDisburse.setDisbDate(aFinanceMain.getGrcPeriodEndDate());
					remAssetDisburse
							.setDisbAmount(aFinanceMain.getFinAssetValue().subtract(aFinanceMain.getFinAmount()));
					remAssetDisburse.setDisbReqDate(curBDay);
					remAssetDisburse.setFeeChargeAmt(BigDecimal.ZERO);
					remAssetDisburse
							.setDisbAccountId(PennantApplicationUtil.unFormatAccountNumber(this.disbAcctId.getValue()));
					aFinanceSchData.getDisbursementDetails().add(remAssetDisburse);
				}
			} else {
				if (StringUtils.isEmpty(moduleDefiner)) {
					if (!aFinanceSchData.getDisbursementDetails().isEmpty()) {
						aFinanceSchData.getDisbursementDetails().get(0)
								.setFeeChargeAmt(aFinanceSchData.getFinanceMain().getFeeChargeAmt());
						aFinanceSchData.getDisbursementDetails().get(0)
								.setInsuranceAmt(aFinanceSchData.getFinanceMain().getInsuranceAmt());
					}
				}
			}
		} else {
			if (finFeeDetailListCtrl != null) {
				finFeeDetailListCtrl.doExecuteFeeCharges(true, aFinanceSchData);
				//Fill the Insurances listbox's data for the  amounts calculated
				finFeeDetailListCtrl.doFillFinInsurances(aFinanceSchData.getFinInsuranceList());
			}
		}
		if (!isIstisnaProd) {
			if (aFinanceSchData.getDisbursementDetails() != null
					&& !aFinanceSchData.getDisbursementDetails().isEmpty()) {
				aFinanceSchData.getDisbursementDetails().get(0).setDisbAccountId(aFinanceMain.getDisbAccountId());
			}
		}
		logger.debug("Leaving");
		return aFinanceSchData;
	}

	/**
	 * This method set the check list details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 * @throws Exception
	 */
	protected boolean doSave_CheckList(FinanceDetail aFinanceDetail, boolean isForAgreementGen) throws Exception {
		logger.debug("Entering ");

		setFinanceDetail(aFinanceDetail);
		boolean validationSuccess = true;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("financeDetail", getFinanceDetail());
		map.put("userAction", this.userAction.getSelectedItem().getLabel());
		if (isForAgreementGen) {
			map.put("agreement", isForAgreementGen);
		}
		try {
			financeCheckListReferenceDialogCtrl.doSetLabels(getFinBasicDetails());
			Events.sendEvent("onChkListValidation", checkListChildWindow, map);
		} catch (Exception e) {
			validationSuccess = false;
			if (e instanceof WrongValuesException) {
				throw e;
			}
		}

		Map<Long, Long> selAnsCountMap = new HashMap<Long, Long>();
		List<FinanceCheckListReference> chkList = getFinanceDetail().getFinanceCheckList();
		selAnsCountMap = getFinanceDetail().getLovDescSelAnsCountMap();

		if (chkList != null && chkList.size() >= 0) {
			getFinanceDetail().setFinanceCheckList(chkList);
			getFinanceDetail().setLovDescSelAnsCountMap(selAnsCountMap);
		}
		logger.debug("Leaving ");
		return validationSuccess;

	}

	/**
	 * Method for Setting Disbusement Account from Asset default values
	 * 
	 * @param accountNumber
	 */
	public void setDisbAccount(String accountNumber) {
		if (StringUtils.isBlank(this.disbAcctId.getValue())) {
			this.disbAcctId.setValue(accountNumber);
		}
	}

	/**
	 * Method to process and save the customer details
	 * 
	 * @param financeDetail
	 * @param validatePhone
	 * @return
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public boolean processCustomerDetails(FinanceDetail financeDetail, boolean validatePhone)
			throws ParseException, InterruptedException {
		logger.debug("Entering");
		if (customerDialogCtrl.getCustomerDetails() != null) {
			return customerDialogCtrl.doSave_CustomerDetail(financeDetail, custDetailTab, validatePhone);
		}
		if (!customerDialogCtrl.setEmpStatusOnSalCust(custDetailTab)) {
			return false;
		}
		logger.debug("Leaving");
		return true;
	}

	/**
	 * Method to validate customer details
	 * 
	 * @return
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public boolean doCustomerValidation() throws ParseException, InterruptedException {
		logger.debug("Entering");
		if (customerDialogCtrl != null) {
			return processCustomerDetails(getFinanceDetail(), false);
		}
		logger.debug("Leaving");
		return true;
	}

	/**
	 * Method to validate Extended details
	 * 
	 * @return
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public boolean doExtendedDetailsValidation() throws ParseException, InterruptedException {
		logger.debug("Entering");
		// Extended Field validations
		if (getFinanceDetail().getExtendedFieldHeader() != null) {
			getFinanceDetail().setExtendedFieldRender(extendedFieldCtrl.save(true));
		}
		logger.debug("Leaving");
		return true;
	}

	/**
	 * Method to validate PSL details
	 * 
	 * @return
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public boolean doPSLDetailsValidation() throws ParseException, InterruptedException {
		logger.debug("Entering");
		// Extended Field validations
		Tab pslDetailsTab = getTab(AssetConstants.UNIQUE_ID_PSL_DETAILS);
		if ((pslDetailsTab != null && pslDetailsTab.isVisible()) && pSLDetailDialogCtrl != null) {
			pSLDetailDialogCtrl.doSave(getFinanceDetail(), pslDetailsTab, false);
		}
		logger.debug("Leaving");
		return true;
	}

	/**
	 * Method to set user details values to asset objects
	 * 
	 * @param aFinanceDetail
	 *            (FinanceDetail)
	 ***/
	private FinanceDetail doProcess_Assets(FinanceDetail aFinanceDetail) {
		logger.debug("Entering");

		List<CollateralAssignment> collateralAssignmentList = aFinanceDetail.getCollateralAssignmentList();
		if (collateralAssignmentList != null && !collateralAssignmentList.isEmpty()) {
			for (CollateralAssignment collAssignment : collateralAssignmentList) {
				collAssignment.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				collAssignment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				collAssignment.setUserDetails(getUserWorkspace().getLoggedInUser());
				collAssignment.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			}
		}

		List<FinAssetTypes> finAssetTypesList = aFinanceDetail.getFinAssetTypesList();
		if (finAssetTypesList != null && !finAssetTypesList.isEmpty()) {
			for (FinAssetTypes finAssetTypes : finAssetTypesList) {
				finAssetTypes.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				finAssetTypes.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				finAssetTypes.setUserDetails(getUserWorkspace().getLoggedInUser());
				finAssetTypes.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			}
		}
		logger.debug("Leaving");
		return aFinanceDetail;
	}

	/**
	 * Method to store the default values if no values are entered in respective fields when validate or build schedule
	 * buttons are clicked
	 * 
	 */
	private void doStoreDefaultValues() {
		// calling method to clear the constraints
		logger.debug("Entering");

		doClearMessage();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		//FinanceMain Details Tab ---> 1. Basic Details

		if (this.finStartDate.getValue() == null) {
			this.finStartDate.setValue(DateUtility.getAppDate());
		}

		if (this.finContractDate.getValue() == null) {
			this.finContractDate.setValue(this.finStartDate.getValue());
		}

		if (StringUtils.isEmpty(this.finCcy.getValue())) {
			this.finCcy.setValue(financeType.getFinCcy(),
					CurrencyUtil.getCurrencyObject(financeType.getFinCcy()).getCcyDesc());
		}

		if (getComboboxValue(this.cbScheduleMethod).equals(PennantConstants.List_Select)) {

			fillComboBox(this.cbScheduleMethod, financeType.getFinSchdMthd(),
					PennantStaticListUtil.getScheduleMethods(), ",NO_PAY,GRCNDPAY,PFTCAP,");
		}

		if (getComboboxValue(this.cbProfitDaysBasis).equals(PennantConstants.List_Select)) {
			fillComboBox(this.cbProfitDaysBasis, financeType.getFinDaysCalType(),
					PennantStaticListUtil.getProfitDaysBasis(), "");
		}

		if (getComboboxValue(this.finRepayMethod).equals(PennantConstants.List_Select)) {
			String excldRepayMethods = "";
			if (StringUtils.equals(FinanceConstants.PRODUCT_TAWARRUQ, financeType.getFinCategory())) {
				excldRepayMethods = "," + FinanceConstants.REPAYMTH_AUTODDA + ",";
			}

			List<ValueLabel> rpyMethodList = new ArrayList<>();
			if (StringUtils.isNotEmpty(financeType.getAlwdRpyMethods())) {
				String[] rpMthds = financeType.getAlwdRpyMethods().trim().split(",");
				if (rpMthds.length > 0) {
					List<String> list = Arrays.asList(rpMthds);
					for (ValueLabel rpyMthd : PennantStaticListUtil.getRepayMethods()) {
						if (list.contains(rpyMthd.getValue().trim())) {
							rpyMethodList.add(rpyMthd);
						}
					}
				}
			}

			fillComboBox(this.finRepayMethod, financeType.getFinRepayMethod(), rpyMethodList, excldRepayMethods);

			setRepayAccMandatory();
			doCheckDDA();
		}

		//FinanceMain Details Tab ---> 2. Grace Period Details
		getFinanceDetail().getFinScheduleData().getFinanceMain().setAllowGrcPeriod(this.allowGrace.isChecked());

		if (this.graceTerms.intValue() == 0 && this.gracePeriodEndDate.getValue() == null) {
			this.graceTerms.setText("");
			if (this.graceTerms_Two.intValue() == 0) {
				this.graceTerms_Two.setText("0");
			}
		}

		// Fill grace period details if finance type allows grace
		if (getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowGrcPeriod()) {

			if (this.gracePeriodEndDate.getValue() == null && this.graceTerms_Two.intValue() == 0) {
				this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			} else if (this.gracePeriodEndDate.getValue() != null) {
				this.gracePeriodEndDate_two.setValue(this.gracePeriodEndDate.getValue());
			}

			if (financeType.isFinIsAlwGrcRepay()
					&& getComboboxValue(this.grcRateBasis).equals(PennantConstants.List_Select)) {

				fillComboBox(this.grcRateBasis, financeType.getFinGrcRateType(),
						PennantStaticListUtil.getInterestRateType(
								!getFinanceDetail().getFinScheduleData().getFinanceMain().isMigratedFinance()),
						",C,D,");
			}

			if (financeType.isFinIsAlwGrcRepay() && this.allowGrcRepay.isChecked()
					&& getComboboxValue(this.cbGrcSchdMthd).equals(PennantConstants.List_Select)) {

				fillComboBox(this.cbGrcSchdMthd, financeType.getFinGrcSchdMthd(),
						PennantStaticListUtil.getScheduleMethods(), ",EQUAL,PRI_PFT,PRI,");
			}

			if (this.graceRate.getMarginValue() == null) {
				this.graceRate.setMarginValue(financeType.getFinGrcMargin());
			}

			if (!this.graceRate.isBaseReadonly() && StringUtils.isEmpty(this.graceRate.getBaseValue())) {
				this.graceRate.setBaseValue(financeType.getFinGrcBaseRate());
			}

			if (this.grcAdvRate.getMarginValue() == null) {
				this.grcAdvRate.setMarginValue(financeType.getGrcAdvMargin());
			}

			if (this.grcAdvRate.isBaseVisible() && !this.grcAdvRate.isBaseReadonly()
					&& StringUtils.isEmpty(this.grcAdvRate.getBaseValue())) {

				this.grcAdvRate.setBaseValue(financeType.getGrcAdvBaseRate());
				this.grcAdvRate.setBaseDescription(
						financeType.getGrcAdvBaseRate() == null ? "" : financeType.getGrcAdvBaseRateDesc());
			}

			calAdvPftRate(this.grcAdvRate.getBaseValue(), this.finCcy.getValue(), this.grcAdvRate.getMarginValue(),
					BigDecimal.ZERO, BigDecimal.ZERO, this.grcAdvRate.getEffRateComp());

			if (!this.graceRate.isSpecialReadonly() && StringUtils.isEmpty(this.graceRate.getSpecialValue())) {
				this.graceRate.setSpecialValue(financeType.getFinGrcSplRate());
			}

			if (StringUtils.isNotEmpty(this.graceRate.getBaseValue())) {

				RateDetail rateDetail = RateUtil.rates(this.graceRate.getBaseValue(), this.finCcy.getValue(),
						this.graceRate.getSpecialValue(),
						this.graceRate.getMarginValue() == null ? BigDecimal.ZERO : this.graceRate.getMarginValue(),
						this.finGrcMinRate.getValue(), this.finGrcMaxRate.getValue());

				this.graceRate.setEffRateText(
						PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
			} else {

				if (this.gracePftRate.getValue() != null) {

					if (this.gracePftRate.getValue().intValue() == 0 && this.gracePftRate.getValue().precision() == 1) {
						this.graceRate.setEffRateValue(financeType.getFinGrcIntRate());
					}
				} else {
					this.graceRate.setEffRateValue(BigDecimal.ZERO);
				}
			}

			if (this.nextGrcPftDate.getValue() == null && StringUtils.isNotEmpty(this.gracePftFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {

				this.nextGrcPftDate_two.setValue(FrequencyUtil
						.getNextDate(this.gracePftFrq.getValue(), 1, this.finStartDate.getValue(),
								HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
						.getNextFrequencyDate());

			} else {
				this.nextGrcPftDate_two.setValue(this.nextGrcPftDate.getValue());
			}

			if (financeType.isFinGrcIsRvwAlw() && StringUtils.isNotEmpty(this.gracePftRvwFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {

				if (this.nextGrcPftRvwDate.getValue() == null) {
					this.nextGrcPftRvwDate_two.setValue(FrequencyUtil
							.getNextDate(this.gracePftRvwFrq.getValue(), 1, this.finStartDate.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
							.getNextFrequencyDate());
				} else {
					this.nextGrcPftRvwDate_two.setValue(this.nextGrcPftRvwDate.getValue());
				}

				if (this.nextGrcPftRvwDate.getValue() == null && this.nextGrcPftRvwDate_two.getValue() != null) {
					if (this.nextGrcPftRvwDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						this.nextGrcPftRvwDate_two.setValue(this.gracePeriodEndDate_two.getValue());
					}
				}
			}

			if (financeType.isFinGrcIsIntCpz() && StringUtils.isNotEmpty(this.graceCpzFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.graceCpzFrq.getValue()) == null) {

				if (StringUtils.isNotEmpty(this.graceCpzFrq.getValue()) && this.nextGrcCpzDate.getValue() == null
						&& this.nextGrcPftDate_two.getValue() != null) {

					this.nextGrcCpzDate_two.setValue(FrequencyUtil
							.getNextDate(this.graceCpzFrq.getValue(), 1, this.finStartDate.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
							.getNextFrequencyDate());

				} else if (this.nextGrcCpzDate.getValue() != null) {
					this.nextGrcCpzDate_two.setValue(this.nextGrcCpzDate.getValue());

				} else if (this.nextGrcCpzDate_two.getValue() == null) {
					this.nextGrcCpzDate_two.setValue(this.gracePeriodEndDate_two.getValue());
				}

				if (this.nextGrcCpzDate.getValue() == null && this.nextGrcCpzDate_two.getValue() != null) {
					if (this.nextGrcCpzDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						this.nextGrcCpzDate_two.setValue(this.gracePeriodEndDate_two.getValue());
					}
				}

			} else {
				this.nextGrcCpzDate_two.setValue(this.nextGrcCpzDate.getValue());
			}
		} else {
			this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
		}

		if (this.allowGrace.isChecked()) {
			if (this.graceTerms_Two.intValue() > 0 && this.gracePeriodEndDate.getValue() == null) {

				int chkDays = financeType.getFddLockPeriod();
				//Added Earlier for Fortnightly Frequency to Check Minimum Days. But it Effects to Monthly Frequency with Terms = 1
				/*
				 * if(this.graceTerms_Two.intValue() == 1){ chkDays = true; }
				 */

				List<Calendar> scheduleDateList = FrequencyUtil
						.getNextDate(this.gracePftFrq.getValue(), this.graceTerms_Two.intValue(),
								this.finStartDate.getValue(), HolidayHandlerTypes.MOVE_NONE, false, chkDays)
						.getScheduleList();

				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					this.gracePeriodEndDate_two.setValue(calendar.getTime());
				}
				scheduleDateList = null;

			} else if (this.graceTerms_Two.intValue() == 0
					&& (this.gracePeriodEndDate.getValue() != null || this.gracePeriodEndDate_two.getValue() != null)
					&& !this.manualSchedule.isChecked()) {

				if (this.finStartDate.getValue().compareTo(this.nextGrcPftDate_two.getValue()) == 0) {
					this.graceTerms_Two.setValue(
							FrequencyUtil.getTerms(this.gracePftFrq.getValue(), this.nextGrcPftDate_two.getValue(),
									this.gracePeriodEndDate_two.getValue(), false, false).getTerms());
				} else if (this.finStartDate.getValue().compareTo(this.nextGrcPftDate_two.getValue()) < 0) {
					this.graceTerms_Two.setValue(
							FrequencyUtil.getTerms(this.gracePftFrq.getValue(), this.nextGrcPftDate_two.getValue(),
									this.gracePeriodEndDate_two.getValue(), true, false).getTerms());
				}

				this.graceTerms.setText("");
			}

			if (this.nextGrcPftDate.getValue() == null && this.nextGrcPftDate_two.getValue() != null) {
				if (this.nextGrcPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
					this.nextGrcPftDate_two.setValue(this.gracePeriodEndDate_two.getValue());
				}
			}
		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		if (this.repayRate.getMarginValue() == null) {
			this.repayRate.setMarginValue(financeType.getFinMargin());
		}

		if (getComboboxValue(this.repayRateBasis).equals(PennantConstants.List_Select)) {
			fillComboBox(this.repayRateBasis, financeType.getFinRateType(), PennantStaticListUtil.getInterestRateType(
					!getFinanceDetail().getFinScheduleData().getFinanceMain().isMigratedFinance()), "");
		}

		if (this.rpyAdvRate.getMarginValue() == null) {
			this.rpyAdvRate.setMarginValue(financeType.getRpyAdvMargin());
		}

		if (this.rpyAdvRate.isBaseVisible() && !this.rpyAdvRate.isBaseReadonly()
				&& StringUtils.isEmpty(this.rpyAdvRate.getBaseValue())) {

			this.rpyAdvRate.setBaseValue(financeType.getRpyAdvBaseRate());
			this.rpyAdvRate.setBaseDescription(
					financeType.getRpyAdvBaseRate() == null ? "" : financeType.getRpyAdvBaseRateDesc());
		}

		calAdvPftRate(this.rpyAdvRate.getBaseValue(), this.finCcy.getValue(), this.rpyAdvRate.getMarginValue(),
				BigDecimal.ZERO, BigDecimal.ZERO, this.rpyAdvRate.getEffRateComp());

		if (CalculationConstants.RATE_BASIS_R.equals(getComboboxValue(this.repayRateBasis))) {

			if (!this.repayRate.isBaseReadonly() && StringUtils.isEmpty(this.repayRate.getBaseValue())) {
				this.repayRate.setBaseValue(financeType.getFinBaseRate());
			}

			if (!this.repayRate.isSpecialReadonly() && StringUtils.isEmpty(this.repayRate.getSpecialValue())) {
				this.repayRate.setSpecialValue(financeType.getFinSplRate());
			}

			if (StringUtils.isNotEmpty(this.repayRate.getBaseValue())) {

				RateDetail rateDetail = RateUtil.rates(this.repayRate.getBaseValue(), this.finCcy.getValue(),
						this.repayRate.getSpecialValue(),
						this.repayRate.getMarginValue() == null ? BigDecimal.ZERO : this.repayRate.getMarginValue(),
						this.finMinRate.getValue() != null && this.finMinRate.getValue().compareTo(BigDecimal.ZERO) <= 0
								? BigDecimal.ZERO : this.finMinRate.getValue(),
						this.finMaxRate.getValue() != null && this.finMaxRate.getValue().compareTo(BigDecimal.ZERO) <= 0
								? BigDecimal.ZERO : this.finMaxRate.getValue());

				if (rateDetail.getErrorDetails() == null) {
					this.repayRate.setEffRateText(
							PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
				}
			}
		}

		if (CalculationConstants.RATE_BASIS_F.equals(getComboboxValue(this.repayRateBasis))
				|| CalculationConstants.RATE_BASIS_C.equals(getComboboxValue(this.repayRateBasis))
				|| CalculationConstants.RATE_BASIS_D.equals(getComboboxValue(this.repayRateBasis))) {
			if (this.repayProfitRate.getValue() != null) {
				if (this.repayProfitRate.getValue().intValue() == 0
						&& this.repayProfitRate.getValue().precision() == 1) {
					this.repayRate.setEffRateValue(financeType.getFinIntRate());

				} else {
					this.repayRate.setEffRateValue(this.repayProfitRate.getValue() == null ? BigDecimal.ZERO
							: this.repayProfitRate.getValue());
				}
			}
		}
		doChangeTerms();
		logger.debug("Leaving");
	}

	private void doChangeTerms() {
		logger.debug("Entering");

		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		boolean singleTermFinance = false;
		if (financeType.getFinMinTerm() == 1 && financeType.getFinMaxTerm() == 1) {
			singleTermFinance = true;
		}

		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		if (!this.rpyPftFrqRow.isVisible()) {
			this.nextRepayPftDate.setText("");
		}

		if (this.maturityDate.getValue() != null && !isOverdraft) {

			this.maturityDate_two.setValue(this.maturityDate.getValue());

			if (singleTermFinance) {

				this.numberOfTerms.setValue(1);
				this.nextRepayDate.setValue(this.maturityDate.getValue());
				this.nextRepayDate_two.setValue(this.maturityDate.getValue());
				if (!financeType.isFinRepayPftOnFrq()) {
					this.nextRepayPftDate.setValue(this.maturityDate.getValue());
					this.nextRepayRvwDate.setValue(this.maturityDate.getValue());
					this.nextRepayCpzDate.setValue(this.maturityDate.getValue());
					this.nextRepayPftDate_two.setValue(this.maturityDate.getValue());
					this.nextRepayRvwDate_two.setValue(this.maturityDate.getValue());
					this.nextRepayCpzDate_two.setValue(this.maturityDate.getValue());
				}

			} else {
				if (StringUtils.isNotEmpty(this.repayFrq.getValue())
						&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
					if (this.nextRepayPftDate.getValue() != null) {
						int frqDay = Integer.parseInt(this.repayFrq.getValue().substring(3));
						int day = DateUtility.getDay(this.nextRepayPftDate.getValue());
						this.nextRepayDate_two.setValue(FrequencyUtil
								.getNextDate(this.repayFrq.getValue(), 1, this.nextRepayPftDate.getValue(),
										HolidayHandlerTypes.MOVE_NONE, day == frqDay, financeType.getFddLockPeriod())
								.getNextFrequencyDate());
					} else {
						this.nextRepayDate_two.setValue(FrequencyUtil
								.getNextDate(this.repayFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
										HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
								.getNextFrequencyDate());
					}
				}

				/*
				 * if(this.finRepayPftOnFrq.isChecked()){
				 * 
				 * Date nextPftDate = this.nextRepayPftDate.getValue(); if(nextPftDate == null){ nextPftDate =
				 * FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
				 * HolidayHandlerTypes.MOVE_NONE, false, true).getNextFrequencyDate(); }
				 * 
				 * this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayPftFrq.getValue(), nextPftDate,
				 * this.maturityDate_two.getValue(), true, true).getTerms()); }else{
				 */
				if (!this.manualSchedule.isChecked()) {
					this.numberOfTerms_two.setValue(
							FrequencyUtil.getTerms(this.repayFrq.getValue(), this.nextRepayDate_two.getValue(),
									this.maturityDate_two.getValue(), true, true).getTerms());
				}
				//}
			}
		}

		if (StringUtils.isNotEmpty(this.repayFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null && !singleTermFinance) {

			if (this.nextRepayPftDate.getValue() != null) {

				int frqDay = Integer.parseInt(this.repayFrq.getValue().substring(3));
				int day = DateUtility.getDay(this.nextRepayPftDate.getValue());
				this.nextRepayDate_two.setValue(FrequencyUtil
						.getNextDate(this.repayFrq.getValue(), 1, this.nextRepayPftDate.getValue(),
								HolidayHandlerTypes.MOVE_NONE, day == frqDay, financeType.getFddLockPeriod())
						.getNextFrequencyDate());

			} else {
				this.nextRepayDate_two.setValue(FrequencyUtil
						.getNextDate(this.repayFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
								HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
						.getNextFrequencyDate());

			}
		}

		boolean isRollover = false;
		if (StringUtils.isNotEmpty(this.rolloverFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.rolloverFrq.getValue()) == null && !singleTermFinance
				&& !this.manualSchedule.isChecked()) {
			isRollover = true;
			if (this.nextRepayPftDate.getValue() != null && this.nextRollOverDate.getValue() == null) {

				int frqDay = Integer.parseInt(this.rolloverFrq.getValue().substring(3));
				int day = DateUtility.getDay(this.nextRepayPftDate.getValue());
				this.nextRollOverDate_two.setValue(FrequencyUtil
						.getNextDate(this.rolloverFrq.getValue(), 1, this.nextRepayPftDate.getValue(),
								HolidayHandlerTypes.MOVE_NONE, day == frqDay, financeType.getFddLockPeriod())
						.getNextFrequencyDate());

				this.numberOfTerms
						.setValue(FrequencyUtil.getTerms(this.repayPftFrq.getValue(), this.nextRepayPftDate.getValue(),
								this.nextRollOverDate_two.getValue(), day == frqDay, true).getTerms());

			} else {
				if (this.nextRollOverDate.getValue() == null) {
					this.nextRollOverDate_two.setValue(FrequencyUtil
							.getNextDate(this.rolloverFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
							.getNextFrequencyDate());
				} else {
					this.nextRollOverDate_two.setValue(this.nextRollOverDate.getValue());
				}

				this.numberOfTerms.setValue(
						FrequencyUtil.getTerms(this.repayFrq.getValue(), this.gracePeriodEndDate_two.getValue(),
								this.nextRollOverDate_two.getValue(), false, true).getTerms());
			}

			this.maturityDate_two.setValue(this.nextRollOverDate_two.getValue());
		}

		if (this.numberOfTerms.intValue() == 0 && this.numberOfTerms_two.intValue() == 0
				&& this.maturityDate_two.getValue() != null && !this.manualSchedule.isChecked()) {

			/*
			 * if(this.finRepayPftOnFrq.isChecked()){
			 * 
			 * Date nextPftDate = this.nextRepayPftDate.getValue(); if(nextPftDate == null){ nextPftDate =
			 * FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
			 * HolidayHandlerTypes.MOVE_NONE, false, true).getNextFrequencyDate(); }
			 * 
			 * this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayPftFrq.getValue(), nextPftDate,
			 * this.maturityDate_two.getValue(), true, true).getTerms()); }else{
			 */
			this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayFrq.getValue(),
					this.nextRepayDate_two.getValue(), this.maturityDate_two.getValue(), true, true).getTerms());
			//}

		} else if (this.numberOfTerms.intValue() > 0) {
			this.numberOfTerms_two.setValue(this.numberOfTerms.intValue());
		}

		if (this.nextRepayDate.getValue() != null) {
			this.nextRepayDate_two.setValue(this.nextRepayDate.getValue());
		}

		if (this.numberOfTerms_two.intValue() != 0 && !singleTermFinance && !isRollover
				&& !this.manualSchedule.isChecked() && !isOverdraft) {

			List<Calendar> scheduleDateList = null;

			/*
			 * if(this.finRepayPftOnFrq.isChecked()){
			 * 
			 * Date nextPftDate = this.nextRepayPftDate.getValue(); if(nextPftDate == null){ nextPftDate =
			 * FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1,
			 * this.gracePeriodEndDate_two.getValue(),HolidayHandlerTypes.MOVE_NONE, false,
			 * true).getNextFrequencyDate(); }
			 * 
			 * scheduleDateList = FrequencyUtil.getNextDate(this.repayPftFrq.getValue(),
			 * this.numberOfTerms_two.intValue(), nextPftDate, HolidayHandlerTypes.MOVE_NONE, true,
			 * false).getScheduleList(); }else{
			 */
			scheduleDateList = FrequencyUtil
					.getNextDate(this.repayFrq.getValue(), this.numberOfTerms_two.intValue(),
							this.nextRepayDate_two.getValue(), HolidayHandlerTypes.MOVE_NONE, true, 0)
					.getScheduleList();
			//}

			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				if (this.maturityDate.getValue() == null) {
					if (DateUtility.compare(calendar.getTime(), appEndDate) > 0) {
						throw new WrongValueException(this.numberOfTerms,
								Labels.getLabel("Cal_MaturityDate_Terms",
										new String[] { Labels.getLabel("label_FinanceMainDialog_MaturityDate.value"),
												Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value") }));
					}
					this.maturityDate_two.setValue(calendar.getTime());
				}
			}
			scheduleDateList = null;
		}
		//set the defualt first dropline date based on droplinefrq
		if (isOverdraft && financeType.isDroplineOD() && this.firstDroplineDate.getValue() == null) {
			int tenor = ((this.odYearlyTerms.intValue() * 12) + this.odMnthlyTerms.intValue());
			if (tenor > 0) {
				Date nextSchdDate = DateUtility.getDate(DateUtility.formatUtilDate(
						FrequencyUtil.getNextDate(this.droplineFrq.getValue(), tenor, this.finStartDate.getValue(),
								HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate(),
						PennantConstants.dateFormat));
				this.firstDroplineDate.setValue(nextSchdDate);
			}
		}

		if (this.maturityDate_two.getValue() != null && this.nextRepayDate_two.getValue() != null
				&& this.nextRepayDate.getValue() == null) {

			if (this.maturityDate_two.getValue().before(this.nextRepayDate_two.getValue())) {
				this.nextRepayDate_two.setValue(this.maturityDate_two.getValue());
			}
		}

		if (this.numberOfTerms.intValue() == 1) {
			this.maturityDate_two.setValue(this.nextRepayDate_two.getValue());
		}

		if (this.rpyPftFrqRow.isVisible() && this.nextRepayPftDate.getValue() == null
				&& StringUtils.isNotEmpty(this.repayPftFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {
			this.nextRepayPftDate_two.setValue(FrequencyUtil
					.getNextDate(this.repayPftFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
							HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
					.getNextFrequencyDate());
		} else if (!this.rpyPftFrqRow.isVisible()) {
			this.nextRepayPftDate_two.setValue(this.nextRepayDate_two.getValue());
		}

		if (this.nextRepayPftDate.getValue() != null) {
			this.nextRepayPftDate_two.setValue(this.nextRepayPftDate.getValue());
		}

		if (this.maturityDate_two.getValue() != null && this.nextRepayPftDate_two.getValue() != null
				&& this.nextRepayPftDate.getValue() == null) {

			if (this.maturityDate_two.getValue().before(this.nextRepayPftDate_two.getValue())) {
				this.nextRepayPftDate_two.setValue(this.maturityDate_two.getValue());
			}
		}

		if (this.nextRepayRvwDate.getValue() == null && StringUtils.isNotEmpty(this.repayRvwFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue()) == null) {
			this.nextRepayRvwDate_two.setValue(FrequencyUtil
					.getNextDate(this.repayRvwFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
							HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
					.getNextFrequencyDate());
		}

		if (this.nextRepayRvwDate.getValue() != null) {
			this.nextRepayRvwDate_two.setValue(this.nextRepayRvwDate.getValue());
		}

		if (this.maturityDate_two.getValue() != null && this.nextRepayRvwDate_two.getValue() != null
				&& this.nextRepayRvwDate.getValue() == null) {
			if (this.maturityDate_two.getValue().before(this.nextRepayRvwDate_two.getValue())) {
				this.nextRepayRvwDate_two.setValue(this.maturityDate_two.getValue());
			}
		}

		if (this.nextRepayCpzDate.getValue() == null && StringUtils.isNotEmpty(this.repayCpzFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue()) == null) {
			this.nextRepayCpzDate_two.setValue(FrequencyUtil
					.getNextDate(this.repayCpzFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
							HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
					.getNextFrequencyDate());
		}

		if (this.nextRepayCpzDate.getValue() != null) {
			this.nextRepayCpzDate_two.setValue(this.nextRepayCpzDate.getValue());
		}

		if (this.maturityDate_two.getValue() != null && this.nextRepayCpzDate_two.getValue() != null
				&& this.nextRepayCpzDate.getValue() == null) {

			if (this.maturityDate_two.getValue().before(this.nextRepayCpzDate_two.getValue())) {
				this.nextRepayCpzDate_two.setValue(this.maturityDate_two.getValue());
			}
		}

		if (this.numberOfTerms.intValue() == 0 && this.numberOfTerms_two.intValue() == 0
				&& this.maturityDate_two.getValue() != null && !this.manualSchedule.isChecked()) {

			this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayFrq.getValue(),
					this.nextRepayDate_two.getValue(), this.maturityDate_two.getValue(), true, true).getTerms());
		}

		if (this.repayFrq.getFrqCodeCombobox().getSelectedIndex() > 0) {
			int count = PennantAppUtil.getDefermentCount(this.numberOfTerms_two.intValue(),
					this.planDeferCount.intValue(),
					this.repayFrq.getFrqCodeCombobox().getSelectedItem().getValue().toString());
			if (count > 0) {
				this.defferments.setValue(count);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	protected void doEdit() {
		logger.debug("Entering");
		FinScheduleData finScheduleData = getFinanceDetail().getFinScheduleData();
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		boolean isOverDraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			isOverDraft = true;
		}
		//FinanceMain Details Tab ---> 1. Basic Details
		readOnlyComponent(isReadOnly("FinanceMainDialog_finPurpose"), this.finPurpose);
		if (financeMain.isNewRecord()) {
			doEditGenFinRef(financeType);
			readOnlyComponent(isReadOnly("FinanceMainDialog_finBranch"), this.finBranch);
		} else {
			this.finReference.setReadonly(true);
			readOnlyComponent(true, this.finBranch);
		}

		this.viewCustInfo.setVisible(false);
		this.btnSearchFinType.setDisabled(true);
		readOnlyComponent(isReadOnly("FinanceMainDialog_finCcy"), this.finCcy);
		readOnlyComponent(isReadOnly("FinanceMainDialog_profitDaysBasis"), this.cbProfitDaysBasis);
		this.finBranch.setMandatoryStyle(false);
		readOnlyComponent(isReadOnly("FinanceMainDialog_custID"), this.custCIF);
		readOnlyComponent(isReadOnly("FinanceMainDialog_finRemarks"), this.finRemarks);
		readOnlyComponent(isReadOnly("FinanceMainDialog_finStartDate"), this.finStartDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_finContractDate"), this.finContractDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_eligibilityMethod"), this.eligibilityMethod);

		//Finance Amount
		if (getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_ISTISNA) || isOverDraft) {
			this.finAmount.setReadonly(true);
		} else {
			readOnlyComponent(isReadOnly("FinanceMainDialog_finAmount"), this.finAmount);
		}

		//Rollover Details
		doEditRollover(finScheduleData);

		//FinAsset Value
		/*
		 * if (financeType.isFinIsAlwMD()) { readOnlyComponent(isReadOnly("FinanceMainDialog_finAssetValue"),
		 * this.finAssetValue); this.row_FinAssetValue.setVisible(true); } else { this.finAssetValue.setReadonly(true);
		 * }
		 */

		if (StringUtils.equals(financeType.getProductCategory(), FinanceConstants.PRODUCT_ODFACILITY)) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_finAssetValue"), this.finAssetValue);
		}

		this.btnSearchCustCIF.setVisible(!isReadOnly("FinanceMainDialog_custID"));

		//FIXME: getMinDownPayPerc was >=0 changed to 0. Confirm. Temporary fix 
		if (financeType.isFinIsDwPayRequired() && financeMain.getMinDownPayPerc().compareTo(BigDecimal.ZERO) >= 0) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_downPayment"), this.downPayBank);
			readOnlyComponent(isReadOnly("FinanceMainDialog_downPaySupl"), this.downPaySupl);
			readOnlyComponent(isReadOnly("FinanceMainDialog_downPaymentAcc"), this.downPayAccount);
		} else {
			this.downPayBank.setReadonly(true);
			this.downPaySupl.setReadonly(true);
			this.downPayAccount.setReadonly(true);
		}

		readOnlyComponent(isReadOnly("FinanceMainDialog_defferments"), this.defferments);

		if (financeType.isAlwPlanDeferment()) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_planDeferCount"), this.planDeferCount);
		} else {
			this.planDeferCount.setReadonly(true);
		}

		//FIXME: PV_Based on Disbursement changes
		readOnlyComponent(isReadOnly("FinanceMainDialog_disbAcctId"), this.disbAcctId);

		if (StringUtils.isEmpty(financeMain.getFinRepayMethod())) {
			this.repayAcctId.setReadonly(true);
		} else if (StringUtils.equals(financeMain.getFinRepayMethod(), FinanceConstants.REPAYMTH_AUTO)) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_repayAcctId"), this.repayAcctId);
		} else {
			this.repayAcctId.setReadonly(false);
		}

		//Commitment Details
		doEditCommitment(finScheduleData);

		//Client Specific
		doEditClient(finScheduleData);

		readOnlyComponent(isReadOnly("FinanceMainDialog_accountsOfficer"), this.accountsOfficer);
		readOnlyComponent(isReadOnly("FinanceMainDialog_dsaCode"), this.dsaCode);

		//TDS Applicable
		if (!financeType.isTDSApplicable()) {
			this.tDSApplicable.setDisabled(true);
		} else {
			readOnlyComponent(isReadOnly("FinanceMainDialog_tDSApplicable"), this.tDSApplicable);
		}

		readOnlyComponent(true, this.flagDetails);
		this.btnFlagDetails.setVisible(!isReadOnly("FinanceMainDialog_flagDetails"));

		readOnlyComponent(isReadOnly("FinanceMainDialog_applicationNo"), this.applicationNo);
		readOnlyComponent(isReadOnly("FinanceMainDialog_referralId"), this.referralId);
		if (this.row_employeeName != null && this.row_employeeName.isVisible()) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_employeeName"), this.employeeName);
		}
		readOnlyComponent(isReadOnly("FinanceMainDialog_dmaCode"), this.dmaCode);
		readOnlyComponent(isReadOnly("FinanceMainDialog_connector"), this.connector);
		readOnlyComponent(isReadOnly("FinanceMainDialog_salesDepartment"), this.salesDepartment);

		//FIXME: AlloW QUick Disbursement to be added in RMTFinanceTypes also. Explained to Chaitanya and Siva
		if (ImplementationConstants.ALLOW_QUICK_DISB) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_quickDisb"), this.quickDisb);
			this.quickDisb.setVisible(false);
		} else {
			this.quickDisb.setDisabled(true);
		}

		if (!financeType.isFinDepreciationReq()) {
			this.depreciationFrq.setDisabled(true);
		} else {
			readOnlyComponent(isReadOnly("FinanceMainDialog_depreciationFrq"), this.depreciationFrq);
		}

		//Product Category Specific
		if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_CONVENTIONAL)) {
			doEditConventional(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_ODFACILITY)) {
			doEditODFacility(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_DISCOUNT)) {
			doEditDiscount(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_MURABAHA)) {
			doEditMurabaha(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_IJARAH)) {
			doEditIjarah(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_FWIJARAH)) {
			doEditForwardIharah(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_TAWARRUQ)) {
			doEditTawarruq(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_MUDARABA)) {
			doEditMudaraba(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_MUSHARAKA)) {
			doEditMusharaka(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_WAKALA)) {
			doEditWakala(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_QARDHASSAN)) {
			doEditQardHassan(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_MUSAWAMA)) {
			doEditMusawama(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_SUKUKNRM)) {
			doEditSukukNormal(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_ISTNORM)) {
			doEditIstNorm(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_STRUCTMUR)) {
			doEditStructMur(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_ISTISNA)) {
			doEditIstisna(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_SUKUK)) {
			doEditSukuk(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_SALAM)) {
			doEditSalam(finScheduleData);
		}

		//Step Loans
		doEditStep(finScheduleData);

		//Implementation Type
		doEditImplementationType(finScheduleData);

		//Implementation Country
		doEditImplementationCountry(finScheduleData);

		//Manual Schedule
		doEditManualSchedule(finScheduleData);

		if (ImplementationConstants.CAPTURE_APPLICATION_NUMBER) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_applicationNo"), this.applicationNo);
		} else {
			this.applicationNo.setReadonly(true);
		}

		//FinanceMain Details Tab ---> 2. Grace Period Details
		doEditGrace(finScheduleData);

		//FinanceMain Details Tab ---> 3. Repayment Period Details
		readOnlyComponent(isReadOnly("FinanceMainDialog_repayRateBasis"), this.repayRateBasis);
		readOnlyComponent(isReadOnly("FinanceMainDialog_numberOfTerms"), this.numberOfTerms);
		this.repayRate.setBaseReadonly(isReadOnly("FinanceMainDialog_repayBaseRate"));
		this.repayRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_repaySpecialRate"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_repayMargin"), this.repayRate.getMarginComp());
		readOnlyComponent(isReadOnly("FinanceMainDialog_repayBaseRate"), this.finMinRate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_repayBaseRate"), this.finMaxRate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_scheduleMethod"), this.cbScheduleMethod);
		readOnlyComponent(isReadOnly("FinanceMainDialog_repayFrq"), this.repayFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayDate"), this.nextRepayDate);

		//Drop Line
		if (isOverDraft) {
			if (financeType.isDroplineOD()) {
				readOnlyComponent(isReadOnly("FinanceMainDialog_droplineFrq"), this.droplineFrq);
				readOnlyComponent(isReadOnly("FinanceMainDialog_firstDroplineDate"), this.firstDroplineDate);
			} else {
				this.droplineFrq.setDisabled(true);
				this.firstDroplineDate.setReadonly(true);
			}

			readOnlyComponent(isReadOnly("FinanceMainDialog_pftServicingODLimit"), this.pftServicingODLimit);
			readOnlyComponent(isReadOnly("FinanceMainDialog_odYearlyTerms"), this.odYearlyTerms);
			readOnlyComponent(isReadOnly("FinanceMainDialog_odYearlyTerms"), this.odMnthlyTerms);
		} else {
			this.droplineFrq.setDisabled(true);
			this.firstDroplineDate.setReadonly(true);
			this.pftServicingODLimit.setDisabled(true);
			this.odYearlyTerms.setReadonly(true);
			this.odMnthlyTerms.setReadonly(true);
		}

		if (financeType.isRollOverFinance()) {
			this.rolloverFrqRow.setVisible(true);
			readOnlyComponent(isReadOnly("FinanceMainDialog_rolloverFrq"), this.rolloverFrq);
			readOnlyComponent(isReadOnly("FinanceMainDialog_nextRolloverDate"), this.nextRollOverDate);
			readOnlyComponent(isReadOnly("FinanceMainDialog_latePayWaiverAmount"), this.latePayWaiverAmount);
			readOnlyComponent(isReadOnly("FinanceMainDialog_custPayAccId"), this.custPayAccId);

			this.row_MaturityDate.setVisible(false);
			this.finRepaymentAmount.setMandatory(true);
			readOnlyComponent(true, this.cbScheduleMethod);
			readOnlyComponent(true, this.finCcy);
			if (this.moduleDefiner.equals(FinanceConstants.FINSER_EVENT_ROLLOVER)) {
				this.finAmount.setReadonly(true);
			}
		} else {
			this.rolloverFrqRow.setVisible(false);
			this.rolloverFrq.setDisabled(true);
			readOnlyComponent(true, this.nextRollOverDate);
		}

		readOnlyComponent(isReadOnly("FinanceMainDialog_repayPftFrq"), this.repayPftFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayPftDate"), this.nextRepayPftDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_repayRvwFrq"), this.repayRvwFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayRvwDate"), this.nextRepayRvwDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_repayCpzFrq"), this.repayCpzFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayCpzDate"), this.nextRepayCpzDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_finRepayPftOnFrq"), this.finRepayPftOnFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_finRepaymentAmount"), this.finRepaymentAmount);

		readOnlyComponent(isReadOnly("FinanceMainDialog_maturityDate"), this.maturityDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_finRepayMethod"), this.finRepayMethod);

		readOnlyComponent(isReadOnly("FinanceMainDialog_RpyAdvBaseRate"), this.rpyAdvRate.getBaseComp());
		readOnlyComponent(isReadOnly("FinanceMainDialog_RpyAdvMargin"), this.rpyAdvRate.getMarginComp());
		readOnlyComponent(isReadOnly("FinanceMainDialog_RpyAdvPftRate"), this.rpyAdvPftRate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_SupplementRent"), this.supplementRent);
		readOnlyComponent(isReadOnly("FinanceMainDialog_IncreasedCost"), this.increasedCost);

		readOnlyComponent(isReadOnly("FinanceMainDialog_AlwBpiTreatment"), this.alwBpiTreatment);
		readOnlyComponent(isReadOnly("FinanceMainDialog_DftBpiTreatment"), this.dftBpiTreatment);
		readOnlyComponent(isReadOnly("FinanceMainDialog_AlwPlannedEmiHoliday"), this.alwPlannedEmiHoliday);
		readOnlyComponent(isReadOnly("FinanceMainDialog_PlanEmiMethod"), this.planEmiMethod);
		readOnlyComponent(isReadOnly("FinanceMainDialog_MaxPlanEmiPerAnnum"), this.maxPlanEmiPerAnnum);
		readOnlyComponent(isReadOnly("FinanceMainDialog_MaxPlanEmi"), this.maxPlanEmi);
		readOnlyComponent(isReadOnly("FinanceMainDialog_PlanEmiHLockPeriod"), this.planEmiHLockPeriod);
		readOnlyComponent(isReadOnly("FinanceMainDialog_CpzAtPlanEmi"), this.cpzAtPlanEmi);
		readOnlyComponent(isReadOnly("FinanceMainDialog_UnPlannedEmiHLockPeriod"), this.unPlannedEmiHLockPeriod);
		readOnlyComponent(isReadOnly("FinanceMainDialog_MaxUnplannedEmi"), this.maxUnplannedEmi);
		readOnlyComponent(isReadOnly("FinanceMainDialog_MaxReAgeHolidays"), this.maxReAgeHolidays);
		readOnlyComponent(isReadOnly("FinanceMainDialog_CpzAtUnPlannedEmi"), this.cpzAtUnPlannedEmi);
		readOnlyComponent(isReadOnly("FinanceMainDialog_CpzAtReAge"), this.cpzAtReAge);
		readOnlyComponent(isReadOnly("FinanceMainDialog_RoundingMode"), this.roundingMode);
		readOnlyComponent(isReadOnly("FinanceMainDialog_AdvEMITerms"), this.advEMITerms);

		//FinanceMain Details Tab ---> 4. Overdue Penalty Details
		readOnlyComponent(isReadOnly("FinanceMainDialog_applyODPenalty"), this.applyODPenalty);

		this.custCIF.setReadonly(true);
		this.btnSearchCustCIF.setVisible(false);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (getFinanceDetail().isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_New();
			btnCancel.setVisible(true);
		}

		/**
		 * Enabling the sampling required flag for the role who is having the sampling required
		 */
		if (financeDetail.isSamplingInitiator()) {
			samplingRequiredRow.setVisible(true);
		} else {
			samplingRequiredRow.setVisible(false);
		}
		
		/**
		 * Disabling sampling required filed when sampling already available
		 */
		if (financeMain.isSamplingRequired()
				&& samplingService.isExist(getFinanceDetail().getFinScheduleData().getFinReference(), "_view")) {
			samplingRequired.setDisabled(true);
		}
		/**
		 * Enabling the Legal required flag for the role who is having the Legal required
		 */
		if (financeDetail.isLegalInitiator()) {
			legalRequiredRow.setVisible(true);
		} else {
			legalRequiredRow.setVisible(false);
		}
		
		logger.debug("Leaving");
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT GENERATE REFRENCE
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditGenFinRef(FinanceType financeType) {
		this.finReference.setReadonly(false);
		this.btnCancel.setVisible(false);

		if (financeType.isFinIsGenRef()) {
			this.space_finReference.setSclass("");
			this.finReference.setReadonly(true);
		} else {
			this.space_finReference.setSclass(PennantConstants.mandateSclass);
		}
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT ROLLOVER
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditRollover(FinScheduleData finScheduleData) {

		if (!finScheduleData.getFinanceType().isRollOverFinance()) {
			this.row_RolloverPayment.setVisible(false);
			this.custPaymentAmount.setReadonly(true);
			this.custPayAccId.setReadonly(true);
			return;
		}

		//FIXME: Is there any right code available for fields?
		this.row_RolloverPayment.setVisible(true);
		this.custPaymentAmount.setReadonly(false);
		this.custPayAccId.setReadonly(false);

		//FIXME: PV_Even now it is not set to false. If it is correct then remove else
		//Visibility also need to made it here?
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT COMMITMENT
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditCommitment(FinScheduleData finScheduleData) {

		if (finScheduleData.getFinanceType().isFinCommitmentReq()) {
			this.commitmentRef.setReadonly(true);
			return;
		}

		readOnlyComponent(isReadOnly("FinanceMainDialog_commitmentRef"), this.commitmentRef);

	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT MUDARABA
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditMudaraba(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT SALAM
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditSalam(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT ISTISNA
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditIstisna(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT MUSHARAKA
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditMusharaka(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT IJARAH
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditIjarah(FinScheduleData finScheduleData) {
		if (finScheduleData.getFinanceType().isAlwAdvanceRent()) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_securityDeposit"), this.securityDeposit);
			this.row_securityDeposit.setVisible(true);
		}
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT MURABAHA
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditMurabaha(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT SUKUK
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditSukuk(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT SUKUK NORMAL
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditSukukNormal(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT TAWARRUQ
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditTawarruq(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT WAKALA
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditWakala(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT ISTNORM
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditIstNorm(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT MUSAWAMA
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditMusawama(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT CONVENTIONAL
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditConventional(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT QARDHASSAN
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditQardHassan(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT STRUCTURED MURABAHA
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditStructMur(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT FORWARD IJARAH
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditForwardIharah(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT ODFACILITY
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditODFacility(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT DISCOUNTING
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditDiscount(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT STEP LOANS
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditStep(FinScheduleData finScheduleData) {
		boolean isStepFinance = finScheduleData.getFinanceMain().isStepFinance();
		boolean isAlwManualSteps = finScheduleData.getFinanceMain().isAlwManualSteps();

		if (finScheduleData.getFinanceMain().isNewRecord() || StringUtils
				.equals(finScheduleData.getFinanceMain().getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			isStepFinance = finScheduleData.getFinanceType().isStepFinance();
			isAlwManualSteps = finScheduleData.getFinanceType().isAlwManualSteps();
		}

		//FIXME (KS) TO be fixed for record type empty.logic needs to be relooked in case of orgination and servicing are different
		//		if (!StringUtils.equals(finScheduleData.getFinanceMain().getRecordType(), PennantConstants.RECORD_TYPE_NEW)
		//				|| (finScheduleData.getFinanceMain().isNewRecord() && StringUtils.equals(finScheduleData
		//						.getFinanceMain().getRecordType(), PennantConstants.RECORD_TYPE_NEW))) {
		//			isStepFinance = finScheduleData.getFinanceMain().isStepFinance();
		//			isAlwManualSteps = finScheduleData.getFinanceMain().isAlwManualSteps();
		//		}

		if (isStepFinance) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_stepFinance"), this.stepFinance);
			readOnlyComponent(isReadOnly("FinanceMainDialog_stepPolicy"), this.stepPolicy);
			row_stepFinance.setVisible(true);

			if (isAlwManualSteps) {
				readOnlyComponent(isReadOnly("FinanceMainDialog_alwManualSteps"), this.alwManualSteps);
				readOnlyComponent(isReadOnly("FinanceMainDialog_noOfSteps"), this.noOfSteps);
				readOnlyComponent(isReadOnly("FinanceMainDialog_stepType"), this.stepType);
				row_manualSteps.setVisible(true);
				row_stepType.setVisible(true);
			} else {
				this.alwManualSteps.setDisabled(true);
				this.noOfSteps.setDisabled(true);
				this.stepType.setDisabled(true);
				row_manualSteps.setVisible(false);
				row_stepType.setVisible(false);
			}
		} else {
			this.stepFinance.setDisabled(true);
			this.stepPolicy.setReadonly(true);
			this.alwManualSteps.setDisabled(true);
			this.noOfSteps.setDisabled(true);
			this.stepType.setDisabled(true);
			row_stepFinance.setVisible(false);
			row_manualSteps.setVisible(false);
			row_stepType.setVisible(false);
		}
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT IMPLEMENTATION TYPE
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditImplementationType(FinScheduleData finScheduleData) {
		if (ImplementationConstants.IMPLEMENTATION_CONVENTIONAL) {
			this.row_shariaApproval.setVisible(false);
		} else {
			this.row_shariaApproval.setVisible(!isReadOnly("FinanceMainDialog_ShariaStatus"));
		}
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT IMPLEMENTATION COUNTRY
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditImplementationCountry(FinScheduleData finScheduleData) {
		String AppCountry = (String) SysParamUtil.getValue(PennantConstants.DEFAULT_COUNTRY);

		if (StringUtils.equals(AppCountry, "IN")) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_bankName"), this.bankName);
			readOnlyComponent(isReadOnly("FinanceMainDialog_iban"), this.iban);
			readOnlyComponent(isReadOnly("FinanceMainDialog_ifscCode"), this.ifscCode);
			readOnlyComponent(isReadOnly("FinanceMainDialog_accountType"), this.accountType);
			gb_ddaRequest.setVisible(true);
		}
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT MANUAL SCHEDULE
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditManualSchedule(FinScheduleData finScheduleData) {
		boolean isManualSchedule = finScheduleData.getFinanceType().isManualSchedule();

		if (!StringUtils.equals(finScheduleData.getFinanceMain().getRecordType(), PennantConstants.RECORD_TYPE_NEW)
				|| (finScheduleData.getFinanceMain().isNewRecord() && StringUtils
						.equals(finScheduleData.getFinanceMain().getRecordType(), PennantConstants.RECORD_TYPE_NEW))) {
			isManualSchedule = finScheduleData.getFinanceMain().isManualSchedule();
		}

		if (isManualSchedule) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_manualSchedule"), this.manualSchedule);
			row_ManualSchedule.setVisible(true);
		} else {
			this.manualSchedule.setDisabled(true);
			row_ManualSchedule.setVisible(false);
		}

	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT CLIENT
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditClient(FinScheduleData finScheduleData) {

		this.finLimitRef.setReadonly(true);
		this.mMAReference.setReadonly(true);
		//this.finPurpose.setReadonly(true);

		//AHB: External Limits and Commitments
		if (StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_AHB)
				&& finScheduleData.getFinanceType().isLimitRequired()) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_commitmentRef"), this.finLimitRef);
			readOnlyComponent(isReadOnly("FinanceMainDialog_mMAReference"), this.mMAReference);
		}

		//AIB: Finance Purpose from Subsector
		if (StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_AIB)) {
			//FIXME: PV_Revisit (Code Related to Asset)
			this.finPurpose.setReadonly(true);
		}

	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT GRACE
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditGrace(FinScheduleData finScheduleData) {

		boolean isAllowGrace = finScheduleData.getFinanceMain().isAllowGrcPeriod();
		if (finScheduleData.getFinanceMain().isNewRecord() || StringUtils
				.equals(finScheduleData.getFinanceMain().getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			isAllowGrace = finScheduleData.getFinanceType().isFInIsAlwGrace();
		}
		//FIXME (KS) TO be fixed for record type empty.logic needs to be relooked in case of orgination and servicing are different
		//		if (!StringUtils.equals(finScheduleData.getFinanceMain().getRecordType(), PennantConstants.RECORD_TYPE_NEW)
		//				|| (finScheduleData.getFinanceMain().isNewRecord() && StringUtils.equals(finScheduleData
		//						.getFinanceMain().getRecordType(), PennantConstants.RECORD_TYPE_NEW))) {
		//			isAllowGrace = finScheduleData.getFinanceMain().isAllowGrcPeriod();		}

		if (isAllowGrace) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_allowGrace"), this.allowGrace);
		} else {
			this.allowGrace.setDisabled(true);
		}

		if (!isAllowGrace) {
			this.grcRateBasis.setReadonly(true);
			this.gracePeriodEndDate.setReadonly(true);
			this.cbGrcSchdMthd.setReadonly(true);
			this.allowGrcRepay.setDisabled(true);
			this.graceRate.setBaseReadonly(true);
			this.graceRate.setSpecialReadonly(true);
			this.graceRate.setMarginReadonly(true);
			this.gracePftRate.setReadonly(true);
			this.finGrcMinRate.setReadonly(true);
			this.finGrcMaxRate.setReadonly(true);
			this.grcPftDaysBasis.setReadonly(true);
			this.gracePftFrq.setDisabled(true);
			this.nextGrcPftDate.setReadonly(true);
			this.gracePftRvwFrq.setDisabled(true);
			this.nextGrcPftRvwDate.setReadonly(true);
			this.graceCpzFrq.setDisabled(true);
			this.nextGrcCpzDate.setReadonly(true);
			this.graceTerms.setReadonly(true);

			//Additional Code to handle Structured Murabaha. Not required but kept it becuase group is made not visible
			this.grcAdvRate.getBaseComp().setReadonly(true);
			this.grcAdvRate.getMarginComp().setReadonly(true);
			this.grcAdvPftRate.setReadonly(true);
			this.grcMaxAmount.setReadonly(true);
			gb_gracePeriodDetails.setVisible(false);

			logger.debug("Leaving");
			return;
		}

		readOnlyComponent(isReadOnly("FinanceMainDialog_allowGrace"), this.allowGrace);
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceRateBasis"), this.grcRateBasis);
		readOnlyComponent(isReadOnly("FinanceMainDialog_gracePeriodEndDate"), this.gracePeriodEndDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_grcSchdMthd"), this.cbGrcSchdMthd);
		readOnlyComponent(isReadOnly("FinanceMainDialog_allowGrcRepay"), this.allowGrcRepay);

		//FIXME: Should we give access rights to individual components OR main componet OR base component is enough
		/*
		 * readOnlyComponent(isReadOnly("FinanceMainDialog_graceBaseRate"), this.graceRate.getBaseComp());
		 * readOnlyComponent(isReadOnly("FinanceMainDialog_graceSpecialRate"), this.graceRate.getSpecialComp());
		 * readOnlyComponent(isReadOnly("FinanceMainDialog_grcMargin"), this.graceRate.getMarginComp());
		 */
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceBaseRate"), this.graceRate.getBaseComp());
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceBaseRate"), this.graceRate.getSpecialComp());
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceBaseRate"), this.graceRate.getMarginComp());
		readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);

		readOnlyComponent(isReadOnly("FinanceMainDialog_graceBaseRate"), this.finGrcMinRate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceBaseRate"), this.finGrcMaxRate);

		//FIXME: We are not giving grace interest days seprately
		//readOnlyComponent(isReadOnly("FinanceMainDialog_grcPftDaysBasis"), this.grcPftDaysBasis);

		readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftFrq"), this.gracePftFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftDate"), this.nextGrcPftDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRvwFrq"), this.gracePftRvwFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"), this.nextGrcPftRvwDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceCpzFrq"), this.graceCpzFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcCpzDate"), this.nextGrcCpzDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceTerms"), this.graceTerms);
		readOnlyComponent(isReadOnly("FinanceMainDialog_GrcAdvBaseRate"), this.grcAdvRate.getBaseComp());
		readOnlyComponent(isReadOnly("FinanceMainDialog_GrcAdvMargin"), this.grcAdvRate.getMarginComp());
		readOnlyComponent(isReadOnly("FinanceMainDialog_GrcAdvPftRate"), this.grcAdvPftRate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_GrcMaxAmount"), this.grcMaxAmount);
		

		logger.debug("Leaving");
	}

	/**
	 * Method to clear error messages.
	 */
	protected void doClearMessage() {
		logger.debug("Entering");
		for (Component component : this.window.getFellows()) {
			if (component instanceof InputElement) {
				((InputElement) component).setErrorMessage("");
			} else if (component instanceof ExtendedCombobox) {
				((ExtendedCombobox) component).setErrorMessage("");
			} else if (component instanceof CurrencyBox) {
				((CurrencyBox) component).setErrorMessage("");
			} else if (component instanceof AccountSelectionBox) {
				((AccountSelectionBox) component).setErrorMessage("");
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	protected void doReadOnly() {
		logger.debug("Entering");

		//FinanceMain Details Tab ---> 1. Basic Details
		this.custCIF.setDisabled(true);
		this.btnSearchCustCIF.setVisible(false);
		this.finReference.setReadonly(true);
		this.btnSearchFinType.setDisabled(true);
		this.finRemarks.setReadonly(true);
		this.finCcy.setReadonly(true);
		readOnlyComponent(true, this.cbProfitDaysBasis);
		readOnlyComponent(true, this.finStartDate);
		readOnlyComponent(true, this.finContractDate);
		this.finAmount.setReadonly(true);
		this.finAssetValue.setReadonly(true);
		this.downPayBank.setReadonly(true);
		this.downPaySupl.setReadonly(true);
		this.downPayAccount.setReadonly(true);
		this.defferments.setReadonly(true);
		this.planDeferCount.setReadonly(true);
		this.finBranch.setReadonly(true);
		this.finBranch.setMandatoryStyle(false);
		this.disbAcctId.setReadonly(true);
		this.repayAcctId.setReadonly(true);
		this.finPurpose.setReadonly(true);
		this.finPurpose.setMandatoryStyle(false);
		this.commitmentRef.setReadonly(true);
		this.commitmentRef.setMandatoryStyle(false);
		readOnlyComponent(true, this.finLimitRef);
		readOnlyComponent(true, this.mMAReference);
		this.custCIF.setReadonly(true);
		this.accountsOfficer.setReadonly(true);
		this.securityDeposit.setReadonly(true);
		this.dsaCode.setReadonly(true);
		this.tDSApplicable.setDisabled(true);
		if (this.row_employeeName != null && this.row_employeeName.isVisible()) {
			this.employeeName.setReadonly(true);
		}
		this.referralId.setReadonly(true);
		this.dmaCode.setReadonly(true);
		this.connector.setReadonly(true);
		this.salesDepartment.setReadonly(true);
		this.quickDisb.setDisabled(true);
		this.row_shariaApproval.setVisible(false);

		readOnlyComponent(true, this.bankName);
		readOnlyComponent(true, this.iban);
		readOnlyComponent(true, this.ifscCode);
		readOnlyComponent(true, this.accountType);

		// Step Finance Fields
		this.stepFinance.setDisabled(true);
		this.stepPolicy.setReadonly(true);
		this.alwManualSteps.setDisabled(true);
		this.noOfSteps.setDisabled(true);
		this.stepType.setDisabled(true);
		this.applicationNo.setReadonly(true);

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.allowGrace.setDisabled(true);
		readOnlyComponent(true, this.gracePeriodEndDate);
		readOnlyComponent(true, this.grcRateBasis);
		readOnlyComponent(true, this.cbGrcSchdMthd);
		readOnlyComponent(true, this.allowGrcRepay);
		this.graceRate.setBaseReadonly(true);
		this.graceRate.setSpecialReadonly(true);
		this.graceRate.setMarginReadonly(true);
		this.gracePftRate.setReadonly(true);
		this.graceRate.setMarginReadonly(true);
		this.grcPftDaysBasis.setDisabled(true);
		readOnlyComponent(true, this.nextGrcPftDate);
		readOnlyComponent(true, this.nextGrcPftRvwDate);
		readOnlyComponent(true, this.nextGrcCpzDate);
		this.gracePftFrq.setDisabled(true);
		this.gracePftRvwFrq.setDisabled(true);
		this.graceCpzFrq.setDisabled(true);
		this.graceTerms.setReadonly(true);
		readOnlyComponent(true, this.grcAdvRate.getBaseComp());
		readOnlyComponent(true, this.grcAdvRate.getMarginComp());
		readOnlyComponent(true, this.grcAdvPftRate);
		readOnlyComponent(true, this.grcMaxAmount);

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		this.numberOfTerms.setReadonly(true);
		readOnlyComponent(true, this.repayRateBasis);
		this.repayRate.setBaseReadonly(true);
		this.repayRate.setSpecialReadonly(true);
		this.finRepaymentAmount.setReadonly(true);
		readOnlyComponent(true, this.repayProfitRate);
		readOnlyComponent(true, this.repayRate.getMarginComp());
		readOnlyComponent(true, this.cbScheduleMethod);
		readOnlyComponent(true, this.nextRepayDate);
		readOnlyComponent(true, this.nextRollOverDate);
		readOnlyComponent(true, this.nextRepayPftDate);
		readOnlyComponent(true, this.nextRepayRvwDate);
		readOnlyComponent(true, this.nextRepayCpzDate);
		readOnlyComponent(true, this.maturityDate);
		readOnlyComponent(true, this.odMaturityDate);
		readOnlyComponent(true, this.finRepayMethod);
		this.repayFrq.setDisabled(true);
		this.rolloverFrq.setDisabled(true);
		this.repayPftFrq.setDisabled(true);
		this.repayRvwFrq.setDisabled(true);
		this.repayCpzFrq.setDisabled(true);
		readOnlyComponent(true, this.finRepayPftOnFrq);
		readOnlyComponent(true, this.rpyAdvRate.getBaseComp());
		readOnlyComponent(true, this.rpyAdvRate.getMarginComp());
		readOnlyComponent(true, this.rpyAdvPftRate);
		readOnlyComponent(true, this.supplementRent);
		readOnlyComponent(true, this.increasedCost);
		this.droplineFrq.setDisabled(true);
		this.odYearlyTerms.setReadonly(true);
		readOnlyComponent(true, this.firstDroplineDate);
		readOnlyComponent(true, this.pftServicingODLimit);
		this.maxPlanEmi.setReadonly(true);
		this.planEmiHLockPeriod.setReadonly(true);
		this.maxUnplannedEmi.setReadonly(true);
		this.cpzAtUnPlannedEmi.setDisabled(true);
		this.cpzAtPlanEmi.setDisabled(true);
		this.maxReAgeHolidays.setReadonly(true);
		this.unPlannedEmiHLockPeriod.setReadonly(true);
		this.cpzAtReAge.setDisabled(true);
		readOnlyComponent(true, this.roundingMode);

		//FinanceMain Details Tab ---> 4. Overdue Penalty Details
		readOnlyComponent(true, this.applyODPenalty);
		readOnlyComponent(true, this.oDIncGrcDays);
		readOnlyComponent(true, this.oDChargeType);
		readOnlyComponent(true, this.oDGraceDays);
		readOnlyComponent(true, this.oDChargeCalOn);
		readOnlyComponent(true, this.oDChargeAmtOrPerc);
		readOnlyComponent(true, this.oDAllowWaiver);
		readOnlyComponent(true, this.oDMaxWaiverPerc);

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * when clicks on button "SearchFinCcy"
	 * 
	 * @param event
	 */
	public void onFulfill$finCcy(Event event) {
		logger.debug("Entering " + event.toString());

		this.finCcy.setConstraint("");
		Object dataObject = finCcy.getObject();
		if (dataObject instanceof String) {
			this.finCcy.setValue(dataObject.toString());
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {

				this.disbAcctId.setValue("");
				this.repayAcctId.setValue("");
				this.finCcy.setValue(details.getCcyCode(), details.getCcyDesc());

				// To Format Amount based on the currency
				getFinanceDetail().getFinScheduleData().getFinanceMain().setFinCcy(details.getCcyCode());

				this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.finAssetValue.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.finRepaymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.downPayBank.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.downPaySupl.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.downPaySupl.setScale(details.getCcyEditField());
				try {
					if (childWindowDialogCtrl.getClass().getField("ccyFormatter") != null) {
						childWindowDialogCtrl.getClass().getField("ccyFormatter").setInt(childWindowDialogCtrl,
								details.getCcyEditField());

						if (childWindowDialogCtrl.getClass().getMethod("doSetFieldProperties") != null) {
							childWindowDialogCtrl.getClass().getMethod("doSetFieldProperties")
									.invoke(childWindowDialogCtrl);
						}
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				if (StringUtils.isEmpty(moduleDefiner)) {
					getFinanceDetail().getFinScheduleData().getFinanceMain().setFinAmount(
							PennantAppUtil.unFormateAmount(this.finAmount.getActualValue(), details.getCcyEditField()));
				}
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "finLimitRef"
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InterfaceException
	 */
	public void onFulfill$finLimitRef(Event event) throws InterruptedException, InterfaceException {
		logger.debug("Entering " + event.toString());
		this.mMAReference.setValue("");
		if (!StringUtils.isBlank(getFinanceDetail().getCustomerDetails().getCustomer().getCustCoreBank())) {
			try {
				if (!StringUtils.isBlank(this.finLimitRef.getValue())) {
					//fetch Limit Details from ACP Interface
					processLimitData();

				} else {
					this.facilityAmount.setValue(BigDecimal.ZERO);
					this.facilityUtilizedAmount.setValue(BigDecimal.ZERO);
					this.facilityAvaliableAmount.setValue(BigDecimal.ZERO);
					this.facilityExpiryDate.setValue(null);
					this.facilityBaseRate.setValue(BigDecimal.ZERO);
					this.facilityMarginRate.setValue(BigDecimal.ZERO);
					this.facilityNotes.setValue("");
					this.mMAReference.setValue("", "");
				}
			} catch (InterfaceException e) {
				MessageUtil.showError(e);
			}

		}
		doSetMMAProperties();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "CommitmentRef"
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InterfaceException
	 */
	public void onFulfill$commitmentRef(Event event) throws InterruptedException, InterfaceException {
		logger.debug("Entering " + event.toString());

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("custID", this.custID.longValue(), Filter.OP_EQUAL);

		Object dataObject = commitmentRef.getObject();

		if (dataObject instanceof String) {
			this.commitmentRef.setValue(dataObject.toString(), "");
			commitment = null;
		} else {
			Commitment details = (Commitment) dataObject;
			commitment = details;
			if (details != null) {
				this.commitmentRef.setValue(details.getCmtReference(), details.getCmtTitle());
				this.availCommitAmount = details.getCmtAvailable();
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "mMAReference"
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InterfaceException
	 */
	public void onFulfill$mMAReference(Event event) throws InterruptedException, InterfaceException {
		logger.debug("Entering " + event.toString());

		Object dataObject = mMAReference.getObject();
		if (dataObject instanceof String) {
			this.mMAReference.setValue("0");
			this.mMAReference.setDescription("");
			getFinanceMain().setAgreeName("");
		} else {
			if (dataObject != null) {
				MMAgreement details = (MMAgreement) dataObject;
				if (details != null) {
					this.mMAReference.setValue(String.valueOf(details.getMMAId()));
					this.mMAReference.setDescription(details.getMMAReference());
					getFinanceDetail().getFinScheduleData().getFinanceMain().setAgreeName(details.getAgreeName());
				}
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Filling FOlReference Based on finLimitRef field.
	 */
	private void doSetMMAProperties() {
		logger.debug("Entering");
		Filter filter1[] = new Filter[1];
		filter1[0] = new Filter("FOlReference", this.finLimitRef.getValue(), Filter.OP_EQUAL);
		this.mMAReference.setFilters(filter1);
		logger.debug("Leaving");
	}

	/**
	 * Method for Filling Commitment data using Interface call or Existing Data
	 * 
	 * @throws InterfaceException
	 */
	protected void processLimitData() throws InterfaceException {
		logger.debug("Entering");
		LimitDetail limitDetail = getLimitCheckDetails().getLimitDetails(this.finLimitRef.getValue(),
				this.finBranch.getValue());
		if (limitDetail != null) {
			//save the limitDetails
			getLimitCheckDetails().saveOrUpdate(limitDetail);
		}
		//Setting Commitment Details parameters
		doSetFacilityDetails(limitDetail);

		logger.debug("Leaving");
	}

	private void doSetFacilityDetails(LimitDetail limitDetail) {
		logger.debug("Entering");
		if (limitDetail != null) {
			this.facilityAmount.setValue(limitDetail.getApprovedLimit());
			this.facilityUtilizedAmount.setValue(limitDetail.getReservedAmt());
			this.facilityAvaliableAmount.setValue(limitDetail.getAvailableAmt());
			this.facilityExpiryDate.setValue(limitDetail.getLimitExpiryDate());
			this.facilityBaseRate.setValue(BigDecimal.ZERO);
			this.facilityMarginRate.setValue(limitDetail.getMargin());
			this.facilityNotes.setValue(limitDetail.getNotes());
		} else {
			this.facilityAmount.setValue(BigDecimal.ZERO);
			this.facilityUtilizedAmount.setValue(BigDecimal.ZERO);
			this.facilityAvaliableAmount.setValue(BigDecimal.ZERO);
			this.facilityExpiryDate.setValue(null);
			this.facilityBaseRate.setValue(BigDecimal.ZERO);
			this.facilityMarginRate.setValue(BigDecimal.ZERO);
			this.facilityNotes.setValue("");
		}
		logger.debug("Leaving");
	}

	private void checkLimitDetailsForSingleLimit() {
		logger.debug("Entering");
		if (StringUtils.isEmpty(this.finLimitRef.getValue())) {
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			JdbcSearchObject<LimitDetail> searchObject = new JdbcSearchObject<LimitDetail>(LimitDetail.class);
			searchObject.addTabelName("LimitAPIDetails");
			searchObject.addFilter(new Filter("CustomerReference",
					getFinanceDetail().getCustomerDetails().getCustomer().getCustCIF(), Filter.OP_EQUAL));
			List<LimitDetail> limitDetail = pagedListService.getBySearchObject(searchObject);

			if (limitDetail != null && !limitDetail.isEmpty() && limitDetail.size() == 1) {
				this.finLimitRef.setValue(limitDetail.get(0).getLimitRef());
				this.finLimitRef.setDescription(limitDetail.get(0).getLimitDesc());
				if (!StringUtils.isBlank(this.finLimitRef.getValue())) {
					doSetFacilityDetails(limitDetail.get(0));
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Set Mandatory On DownPay Account Based on Down payment Amount
	 * 
	 * @param event
	 */
	public void onFulfill$downPayBank(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		this.downPayBank.clearErrorMessage();
		Clients.clearWrongValue(this.downPayBank);
		setDownpayAmount();
		setDownPayAcMand();

		//Contributor Details Resetting List Data
		if (contributorDetailsDialogCtrl != null) {
			contributorDetailsDialogCtrl.doResetContributorDetails();
			BigDecimal downPayment = this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue());
			contributorDetailsDialogCtrl.doSetFinAmount(this.finAmount.getActualValue(), downPayment);
		}
		onChangeFinAndDownpayAmount();
		setDownPayPercentage();
		setNetFinanceAmount(false);
		logger.debug("Leaving " + event.toString());
	}

	private void onChangeFinAndDownpayAmount() {
		if (manualSchedule.isChecked() && getManualScheduleDetailDialogCtrl() != null) {

			if (getManualScheduleDetailDialogCtrl().resetFinDisbursement(getFinanceMain())) {
				return;
			}
		}
	}

	public void onFulfill$downPaySupl(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		this.downPaySupl.clearErrorMessage();
		if (this.downPaySupl.getActualValue().compareTo(BigDecimal.ZERO) > 0
				&& this.finAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0) {
			if (this.finAmount.getActualValue().compareTo(this.downPaySupl.getActualValue()) <= 0) {
				setDownPayPercentage();
				setNetFinanceAmount(false);
				return;
			}
		}
		onChangeFinAndDownpayAmount();

		setDownpayAmount();
		setDownPayAcMand();
		setDownPayPercentage();
		setNetFinanceAmount(false);

		//Contributor Details Resetting List Data
		if (contributorDetailsDialogCtrl != null) {
			contributorDetailsDialogCtrl.doResetContributorDetails();
			BigDecimal downPayment = this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue());
			contributorDetailsDialogCtrl.doSetFinAmount(this.finAmount.getActualValue(), downPayment);
		}
		logger.debug("Leaving " + event.toString());
	}

	protected void setDownPayPercentage() {
		logger.debug("Entering");

		BigDecimal downPayAmount = BigDecimal.ZERO;
		BigDecimal finAmount = this.finAmount.getActualValue() == null ? BigDecimal.ZERO
				: this.finAmount.getActualValue();
		if (finAmount.compareTo(BigDecimal.ZERO) == 0) {
			downPayAmount = BigDecimal.ZERO;
		} else {
			BigDecimal downPayBank = this.downPayBank.getActualValue();
			BigDecimal downPaySup = this.downPaySupl.getActualValue();
			BigDecimal downPayValue = downPayBank.add(downPaySup);
			//formula
			downPayAmount = downPayValue.multiply(new BigDecimal(100)).divide(finAmount, RoundingMode.HALF_DOWN);
		}

		this.downPayPercentage
				.setValue(Labels.getLabel("label_Percent", new String[] { String.valueOf(downPayAmount) }));
		logger.debug("Leaving");
	}

	/**
	 * Executes the down payment rule from finance type and set the minimum down payment percentage required for finance
	 * 
	 */
	public void setDownpaymentRulePercentage(boolean isLoadProcess) {
		logger.debug("Entering");
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		if (financeType.getDownPayRule() != 0 && financeType.getDownPayRule() != Long.MIN_VALUE
				&& StringUtils.isNotEmpty(financeType.getDownPayRuleDesc())) {

			CustomerEligibilityCheck customerEligibilityCheck = prepareCustElgDetail(isLoadProcess)
					.getCustomerEligibilityCheck();
			String sqlRule = getRuleService().getAmountRule(financeType.getDownPayRuleDesc(),
					RuleConstants.MODULE_DOWNPAYRULE, RuleConstants.EVENT_DOWNPAYRULE);
			BigDecimal downpayPercentage = BigDecimal.ZERO;
			if (StringUtils.isNotEmpty(sqlRule)) {
				HashMap<String, Object> fieldsAndValues = customerEligibilityCheck.getDeclaredFieldValues();
				downpayPercentage = (BigDecimal) getRuleExecutionUtil().executeRule(sqlRule, fieldsAndValues,
						finCcy.getValue(), RuleReturnType.DECIMAL);
			}
			getFinanceDetail().getFinScheduleData().getFinanceMain().setMinDownPayPerc(downpayPercentage);
		} else {
			getFinanceDetail().getFinScheduleData().getFinanceMain().setMinDownPayPerc(BigDecimal.ZERO);
		}
		logger.debug("Leaving");
	}

	/*
	 * setting the Grace rate or grace margin based on the rule execution on the pricing policy desc in financetype.
	 */
	public void setGrcPolicyRate(boolean isLoadProcess, boolean isNewRecord) {
		logger.debug("Entering");

		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		if (StringUtils.isNotEmpty(financeType.getGrcPricingMethodDesc())) {
			//getting the rule based on the pricing method des
			Rule rule = getRuleService().getRuleById(financeType.getRpyPricingMethodDesc(),
					RuleConstants.MODULE_RATERULE, RuleConstants.EVENT_RATERULE);

			if (rule != null) {
				boolean isAllowDeviation = rule.isAllowDeviation();
				//for new record getting the actual rate based on the rule
				if (isNewRecord) {
					String sqlRule = rule.getSQLRule();
					BigDecimal actRate = BigDecimal.ZERO;

					if (StringUtils.isNotBlank(sqlRule)) {
						CustomerEligibilityCheck customerEligibilityCheck = prepareCustElgDetail(isLoadProcess)
								.getCustomerEligibilityCheck();
						HashMap<String, Object> fieldsAndValues = customerEligibilityCheck.getDeclaredFieldValues();
						actRate = (BigDecimal) getRuleExecutionUtil().executeRule(sqlRule, fieldsAndValues,
								finCcy.getValue(), RuleReturnType.DECIMAL);
					}

					if (this.allowGrace.isChecked()) {
						if (this.grcBaseRateRow.isVisible()) {
							this.graceRate.setMarginValue(actRate);
						} else {
							this.gracePftRate.setValue(actRate);
							this.graceRate.setEffRateValue(actRate);
						}
					}
				}

				//if Deviation is allowed then editable based on rights or else they are not editable
				if (isAllowDeviation) {
					readOnlyComponent(isReadOnly("FinanceMainDialog_graceBaseRate"), this.graceRate.getMarginComp());
					readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
				} else {
					if (this.grcBaseRateRow.isVisible()) {
						this.graceRate.getMarginComp().setReadonly(true);
					} else {
						this.gracePftRate.setReadonly(true);
					}
				}
			}
		}

		logger.debug("Leaving");
	}

	/*
	 * setting the repay Actual rate or repay margin based on the rule execution on the pricing policy desc in
	 * financetype.
	 */
	public void setPolicyRate(boolean isLoadProcess, boolean isnewRecord) {
		logger.debug("Entering");

		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		if (StringUtils.isNotEmpty(financeType.getRpyPricingMethodDesc())) {
			//getting the rule based on the pricing method des
			Rule rule = getRuleService().getRuleById(financeType.getRpyPricingMethodDesc(),
					RuleConstants.MODULE_RATERULE, RuleConstants.EVENT_RATERULE);

			if (rule != null) {

				boolean isAllowDeviation = rule.isAllowDeviation();

				//for new record getting the actual rate based on the rule
				if (isnewRecord) {
					String sqlRule = rule.getSQLRule();
					BigDecimal actRate = BigDecimal.ZERO;

					if (StringUtils.isNotBlank(sqlRule)) {
						CustomerEligibilityCheck customerEligibilityCheck = prepareCustElgDetail(isLoadProcess)
								.getCustomerEligibilityCheck();
						HashMap<String, Object> fieldsAndValues = customerEligibilityCheck.getDeclaredFieldValues();
						actRate = (BigDecimal) getRuleExecutionUtil().executeRule(sqlRule, fieldsAndValues,
								finCcy.getValue(), RuleReturnType.DECIMAL);
					}

					if (StringUtils.isNotEmpty(repayRate.getBaseValue())) {
						this.repayRate.setMarginValue(actRate);
					} else {
						this.repayProfitRate.setValue(actRate);
						this.repayRate.setEffRateValue(actRate);
					}
				}
				//if deviation is not allowed the rate should not be editable
				if (isAllowDeviation) {
					readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);
					readOnlyComponent(isReadOnly("FinanceMainDialog_repayMargin"), this.repayRate.getMarginComp());
				} else {
					if (this.repayRate.getBaseComp().isVisible()) {
						this.repayRate.getMarginComp().setReadonly(true);
					} else {
						this.repayProfitRate.setReadonly(true);
					}
				}
			}
		}
		logger.debug("Leaving");
	}

	public void setNetFinanceAmount(boolean isDataRender) {
		logger.debug("Entering");

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		BigDecimal feeChargeAmount = BigDecimal.ZERO;
		BigDecimal finAmount = this.finAmount.getActualValue() == null ? BigDecimal.ZERO
				: this.finAmount.getActualValue();

		// Fee calculation for Add to Disbursement
		List<FinFeeDetail> finFeeDetails = getFinanceDetail().getFinScheduleData().getFinFeeDetailList();
		if (finFeeDetails != null && !finFeeDetails.isEmpty()) {
			for (FinFeeDetail feeDetail : finFeeDetails) {
				if (StringUtils.equals(feeDetail.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
					feeChargeAmount = feeChargeAmount.add(feeDetail.getActualAmount()
							.subtract(feeDetail.getWaivedAmount()).subtract(feeDetail.getPaidAmount()));
				}
			}
		}

		feeChargeAmount = PennantApplicationUtil.formateAmount(feeChargeAmount, formatter);
		BigDecimal netFinanceVal = finAmount
				.subtract(this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue()))
				.add(feeChargeAmount);
		if (netFinanceVal.compareTo(BigDecimal.ZERO) < 0) {
			netFinanceVal = BigDecimal.ZERO;
		}

		String netFinAmt = PennantApplicationUtil
				.amountFormate(PennantApplicationUtil.unFormateAmount(netFinanceVal, formatter), formatter);
		if (finAmount != null && finAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (ImplementationConstants.ADD_FEEINFTV_ONCALC) {
				this.netFinAmount.setValue(netFinAmt + " ("
						+ ((netFinanceVal.multiply(new BigDecimal(100))).divide(finAmount, 2, RoundingMode.HALF_DOWN))
						+ "%)");
			} else {
				this.netFinAmount.setValue(
						netFinAmt + " (" + (((netFinanceVal.subtract(feeChargeAmount)).multiply(new BigDecimal(100)))
								.divide(finAmount, 2, RoundingMode.HALF_DOWN)) + "%)");
			}
		} else {
			this.netFinAmount.setValue("");
		}
		logger.debug("Leaving");
	}

	private void setDownpayAmount() {
		logger.debug("Entering");
		this.downPayBank.clearErrorMessage();
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		BigDecimal reqDwnPay = BigDecimal.ZERO;
		if (getFinanceDetail().getFinScheduleData().getFinanceType().isAllowDownpayPgm()) {
			if (this.finAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0) {
				reqDwnPay = PennantAppUtil.getPercentageValue(
						PennantAppUtil.unFormateAmount(this.finAmount.getActualValue(), formatter),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getMinDownPayPerc());
				if (this.downPaySupl.getActualValue() != null
						&& this.downPaySupl.getActualValue().compareTo(BigDecimal.ZERO) > 0) {
					reqDwnPay = reqDwnPay
							.subtract(PennantAppUtil.unFormateAmount(this.downPaySupl.getActualValue(), formatter));
					if (reqDwnPay.compareTo(BigDecimal.ZERO) < 0) {
						reqDwnPay = BigDecimal.ZERO;
					}
				}
				this.downPayBank.setValue(PennantAppUtil.formateAmount(reqDwnPay, formatter));
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for getting Discrepancies based on Finance Amount
	 */
	public void onFulfill$finAmount(Event event) {
		logger.debug("Entering " + event.toString());
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
		financeMain.setFinAmount(PennantAppUtil.unFormateAmount(this.finAmount.getActualValue(), formatter));

		if (financeType.isAllowDownpayPgm()) {
			setDownpaymentRulePercentage(false);
			if (this.finAmount.getActualValue() != null
					&& this.finAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0) {
				this.downPayBank.setValue(PennantAppUtil.formateAmount(
						PennantAppUtil.getPercentageValue(
								PennantAppUtil.unFormateAmount(this.finAmount.getActualValue(), formatter),
								getFinanceDetail().getFinScheduleData().getFinanceMain().getMinDownPayPerc()),
						formatter));

				this.downPayBank.setValue(PennantAppUtil.formateAmount(PennantAppUtil.getPercentageValue(
						PennantAppUtil.unFormateAmount(this.finAmount.getActualValue(), formatter),
						financeMain.getMinDownPayPerc()), formatter));
			} else {
				this.downPayBank.setValue(BigDecimal.ZERO);
			}

			// Getting Downpayment Account For DPSP Support program
			this.downPayAccount.setValue(SysParamUtil.getValueAsString("AHB_DOWNPAY_AC"));
		}

		onChangeFinAndDownpayAmount();
		setDownpayAmount();

		//Contributor Details Resetting List Data
		if (contributorDetailsDialogCtrl != null) {
			contributorDetailsDialogCtrl.doResetContributorDetails();
			BigDecimal downPayment = this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue());
			contributorDetailsDialogCtrl.doSetFinAmount(this.finAmount.getActualValue(), downPayment);
		}
		setDownPayPercentage();
		setNetFinanceAmount(false);

		if (collateralHeaderDialogCtrl != null) {

			BigDecimal UtilizedAmt = BigDecimal.ZERO;
			if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(financeType.getFinLTVCheck())) {
				UtilizedAmt = PennantAppUtil
						.unFormateAmount((this.finAssetValue.getActualValue().compareTo(BigDecimal.ZERO) > 0
								? this.finAssetValue.getActualValue() : this.finAmount.getActualValue())
										.subtract(this.downPayBank.getActualValue()).subtract(
												this.downPaySupl.getActualValue()),
								formatter)
						.add(financeMain.getFeeChargeAmt()).add(financeMain.getInsuranceAmt());
			} else {
				UtilizedAmt = PennantAppUtil
						.unFormateAmount(this.finAmount.getActualValue().subtract(this.downPayBank.getActualValue())
								.subtract(this.downPaySupl.getActualValue()), formatter)
						.add(financeMain.getFeeChargeAmt()).add(financeMain.getInsuranceAmt());
			}
			collateralHeaderDialogCtrl.updateShortfall(UtilizedAmt);

		}

		logger.debug("Leaving " + event.toString());
	}

	/*
	 * Validation in overdraft Maintenance if the curfinasset value is less than the finasset
	 */
	public void onFulfill$finAssetValue(Event event) {
		logger.debug("Entering");
		
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
		if (StringUtils.equals(FinanceConstants.FINSER_EVENT_OVERDRAFTSCHD, moduleDefiner)) {
			org_finAssetValue = getFinanceDetailService().getFinAssetValue(finReference.getValue());
		}
		if (collateralHeaderDialogCtrl != null) {
			BigDecimal UtilizedAmt  = BigDecimal.ZERO;
			if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(financeType.getFinLTVCheck())) {
				UtilizedAmt = PennantAppUtil
						.unFormateAmount((this.finAssetValue.getActualValue().compareTo(BigDecimal.ZERO) > 0
								? this.finAssetValue.getActualValue() : this.finAmount.getActualValue())
										.subtract(this.downPayBank.getActualValue()).subtract(
												this.downPaySupl.getActualValue()), formatter)
						.add(financeMain.getFeeChargeAmt()).add(financeMain.getInsuranceAmt());
			} else {
				UtilizedAmt = PennantAppUtil
						.unFormateAmount(this.finAmount.getActualValue().subtract(this.downPayBank.getActualValue())
								.subtract(this.downPaySupl.getActualValue()), formatter)
						.add(financeMain.getFeeChargeAmt()).add(financeMain.getInsuranceAmt());
			}
			collateralHeaderDialogCtrl.updateShortfall(UtilizedAmt);
		}
		logger.debug("Leaving");
	}

	/***
	 * Method to get the FinAsset Value by calling to the financemaindaoimpl
	 */
	public void getfinassetcheck(boolean isvalidCheck) {
		logger.debug("Entering");
		if (isvalidCheck) {
			int format = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
			if (StringUtils.equals(FinanceConstants.FINSER_EVENT_OVERDRAFTSCHD, moduleDefiner)) {
				BigDecimal minFinAssetValue = getFinanceDetailService().getFinAssetValue(finReference.getValue());
				if (this.finAssetValue.getActualValue().compareTo(minFinAssetValue) < 0) {
					MessageUtil.showError(Labels.getLabel("NUMBER_MINVALUE_EQ",
							new String[] { Labels.getLabel("label_FinanceMainDialog_ODFinAssetValue.value"),
									PennantAppUtil.amountFormate(minFinAssetValue, format) }));
				} else {
					return;
				}
			}
			logger.debug("Leaving");
		}
	}

	/**
	 * Validation check for Commitment For Available Amount and Expiry Date Check
	 * 
	 * @param aFinanceDetail
	 * @return
	 * @throws InterruptedException
	 */
	private boolean doValidateCommitment(FinanceDetail aFinanceDetail) throws InterruptedException {
		logger.debug("Entering");

		FinanceMain finMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		FinanceType finType = aFinanceDetail.getFinScheduleData().getFinanceType();

		if (StringUtils.isEmpty(moduleDefiner)) {

			if (StringUtils.isNotBlank(finMain.getFinCommitmentRef())) {

				if (commitment == null) {
					commitment = getCommitmentService().getApprovedCommitmentById(finMain.getFinCommitmentRef());
				}
				//Commitment Stop draw down when rate Out of rage:
				BigDecimal effRate = finMain.getEffectiveRateOfReturn() == null ? BigDecimal.ZERO
						: finMain.getEffectiveRateOfReturn();
				if (BigDecimal.ZERO.compareTo(new BigDecimal(
						PennantApplicationUtil.formatRate(commitment.getCmtPftRateMin().doubleValue(), 9))) != 0
						&& BigDecimal.ZERO.compareTo(new BigDecimal(PennantApplicationUtil
								.formatRate(commitment.getCmtPftRateMax().doubleValue(), 9))) != 0) {

					if (commitment.isCmtStopRateRange() && (effRate
							.compareTo(new BigDecimal(PennantApplicationUtil
									.formatRate(commitment.getCmtPftRateMin().doubleValue(), 9))) < 0
							|| effRate.compareTo(new BigDecimal(PennantApplicationUtil
									.formatRate(commitment.getCmtPftRateMax().doubleValue(), 9))) > 0)) {
						MessageUtil.showError(Labels.getLabel("label_Finance_CommitRateOutOfRange",
								new String[] { String.valueOf(commitment.getCmtPftRateMin()),
										String.valueOf(commitment.getCmtPftRateMax()) }));
						return false;
					}
				}

				//Commitment Expire date should be greater than finance start data
				if (commitment.getCmtExpDate().compareTo(finMain.getFinStartDate()) < 0) {
					MessageUtil.showError(Labels.getLabel("label_Finance_CommitExpiryDateCheck",
							new String[] { DateUtility.formatToLongDate(commitment.getCmtExpDate()) }));
					return false;
				}

				//MultiBranch Utilization
				if (!commitment.isMultiBranch() && !finMain.getFinBranch().equals(commitment.getCmtBranch())) {
					MessageUtil.showError(Labels.getLabel("label_Finance_MultiBranchCheck",
							new String[] { commitment.getCmtBranch() }));
					return false;
				}

				//Shared Commitment Amount Check
				if (!commitment.isSharedCmt() && commitment.getCmtUtilizedAmount().compareTo(BigDecimal.ZERO) > 0) {
					MessageUtil.showError(Labels.getLabel("label_Finance_MultiFinanceCheck"));
					return false;
				}

				BigDecimal finAmtCmtCcy = CalculationUtil.getConvertedAmount(finMain.getFinCcy(),
						commitment.getCmtCcy(), finMain.getFinAmount().subtract(
								finMain.getDownPayment() == null ? BigDecimal.ZERO : finMain.getDownPayment()));

				if (!recSave && commitment.getCmtAvailable().compareTo(finAmtCmtCcy) < 0) {
					if (finType.isFinCommitmentOvrride()) {
						final String msg = Labels.getLabel("message_AvailAmt_Commitment_Required_Override_YesNo");

						if (MessageUtil.confirm(msg, MessageUtil.CANCEL | MessageUtil.OVERIDE) == MessageUtil.CANCEL) {
							return false;
						}
					} else {
						MessageUtil.showError(Labels.getLabel("label_Finance_CommitAmtCheck"));
						return false;
					}
				}
			} else if (!this.commitmentRef.isReadonly() && this.commitmentRef.isMandatory()) {

				if (finType.isFinCommitmentOvrride()) {
					final String msg = Labels.getLabel("message_Commitment_Required_Override_YesNo");

					if (MessageUtil.confirm(msg, MessageUtil.CANCEL | MessageUtil.OVERIDE) == MessageUtil.CANCEL) {
						return false;
					}
				} else {
					return false;
				}

			}
		}
		logger.debug("Leaving");
		return true;
	}

	/**
	 * Method for Checking Recommendation for Mandatory
	 * 
	 * @return
	 * @throws InterruptedException
	 */
	protected boolean doValidateRecommendation() throws InterruptedException {
		logger.debug("Entering");
		boolean isRecommendEntered = true;
		logger.debug("Leaving");
		return isRecommendEntered;
	}

	/**
	 * Method for Setting Mandatory Check to Repay Account ID based on Repay Method
	 * 
	 * @param event
	 */
	public void onChange$finRepayMethod(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		setRepayAccMandatory();
		String repymethod = "";
		if (this.finRepayMethod.getSelectedItem() != null && this.finRepayMethod.getSelectedItem().getValue() != null) {
			repymethod = this.finRepayMethod.getSelectedItem().getValue().toString();
		}

		if (getMandateDialogCtrl() != null) {
			getMandateDialogCtrl().checkTabDisplay(repymethod, true);
		}

		if (getChequeDetailDialogCtrl() != null) {
			getChequeDetailDialogCtrl().checkTabDisplay(this.financeDetail, repymethod, true);
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.financeDetail.getFinScheduleData().getFinanceMain());
	}

	private void setRepayAccMandatory() {
		if (this.finRepayMethod.getSelectedIndex() != 0) {
			String repayMthd = StringUtils.trimToEmpty(this.finRepayMethod.getSelectedItem().getValue().toString());
			if (StringUtils.equals(FinanceConstants.PRODUCT_MUDARABA,
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinCategory())) {
				if (repayMthd.equals(FinanceConstants.REPAYMTH_AUTODDA)
						|| repayMthd.equals(FinanceConstants.REPAYMTH_MANUAL)) {
					this.repayAcctId.setMandatoryStyle(false);
				} else {
					this.repayAcctId.setMandatoryStyle(!isReadOnly("FinanceMainDialog_ManRepayAcctId"));
				}
			} else if (repayMthd.equals(FinanceConstants.REPAYMTH_AUTO)
					|| repayMthd.equals(FinanceConstants.REPAYMTH_AUTODDA)) {
				this.repayAcctId.setMandatoryStyle(!isReadOnly("FinanceMainDialog_ManRepayAcctId"));
			} else if (repayMthd.equals(FinanceConstants.REPAYMTH_MANUAL)) {
				this.repayAcctId.setMandatoryStyle(false);
			}
		}
	}

	private void setDownpayPgmDeails(boolean isNewRecord) {
		if (getFinanceDetail().getFinScheduleData().getFinanceType().isAllowDownpayPgm()) {
			fillComboBox(this.repayRateBasis, CalculationConstants.RATE_BASIS_C,
					PennantStaticListUtil.getInterestRateType(true), "");

			this.repayRateBasis.setDisabled(true);
			this.repayRate.setBaseConstraint("");
			this.repayRate.setSpecialConstraint("");
			this.repayRate.getEffRateComp().setConstraint("");
			this.repayRate.setSpecialReadonly(true);

			this.repayRate.setBaseValue("");
			this.repayRate.setBaseDescription("");
			this.repayRate.setSpecialValue("");
			this.repayRate.setSpecialDescription("");

			this.repayRate.setBaseReadonly(true);
			this.repayRate.setSpecialReadonly(true);
			this.row_FinRepRates.setVisible(false);
			this.repayBaseRateRow.setVisible(true);

			readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);
			if (isNewRecord && !getFinanceDetail().getFinScheduleData().getFinanceMain().isLovDescIsSchdGenerated()) {
				this.repayRate.setEffRateText("0.00");
				this.repayProfitRate.setText("0.00");
			}
		}
	}

	protected void refreshList() {
		JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceMainListCtrl().getSearchObj();
		getFinanceMainListCtrl().pagingFinanceMainList.setActivePage(0);
		getFinanceMainListCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceMainListCtrl().listBoxFinanceMain != null) {
			getFinanceMainListCtrl().listBoxFinanceMain.getListModel();
		}
	}

	protected void refreshMaintainList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceSelectCtrl().getSearchObj(true);
		getFinanceSelectCtrl().getPagingFinanceList().setActivePage(0);
		getFinanceSelectCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceSelectCtrl().getListBoxFinance() != null) {
			getFinanceSelectCtrl().getListBoxFinance().getListModel();
		}
	}

	@Override
	public void closeDialog() {

		//Closing Check List Details Window
		if (getFinanceCheckListReferenceDialogCtrl() != null) {
			getFinanceCheckListReferenceDialogCtrl().closeDialog();
		}

		//Closing Customer Details Window
		if (getContributorDetailsDialogCtrl() != null) {
			getContributorDetailsDialogCtrl().closeDialog();
		}

		//Closing Customer Details Window
		if (getCustomerDialogCtrl() != null) {
			getCustomerDialogCtrl().closeDialog();
		}

		//Closing Etihad Credit Bureau Details Window
		if (getEtihadCreditBureauDetailDialogCtrl() != null) {
			getEtihadCreditBureauDetailDialogCtrl().closeDialog();
		}

		//Closing Bundled Products Details Window
		if (getBundledProductsDetailDialogCtrl() != null) {
			getBundledProductsDetailDialogCtrl().closeDialog();
		}

		//Closing Asset Evaluation Details Window
		if (getFinAssetEvaluationDialogCtrl() != null) {
			getFinAssetEvaluationDialogCtrl().closeDialog();
		}

		//Closing Advance Payment Details Window
		if (getFinAdvancePaymentsListCtrl() != null) {
			getFinAdvancePaymentsListCtrl().closeDialog();
		}

		//Closing Finance Fee Details Window
		if (getFinFeeDetailListCtrl() != null) {
			getFinFeeDetailListCtrl().closeDialog();
		}

		//Closing Covenant Type Details Window
		if (getFinCovenantTypeListCtrl() != null) {
			getFinCovenantTypeListCtrl().closeDialog();
		}

		//Closing Collateral Assingments Details Window
		if (collateralHeaderDialogCtrl != null) {
			collateralHeaderDialogCtrl.closeDialog();
		}

		//Closing Collateral Details Window
		if (finCollateralHeaderDialogCtrl != null) {
			finCollateralHeaderDialogCtrl.closeDialog();
		}

		//Closing Mandate Details Window
		if (mandateDialogCtrl != null) {
			mandateDialogCtrl.closeDialog();
		}

		super.closeDialog();
	}

	public void onChange$custCIF(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		this.custCIF.clearErrorMessage();
		Customer customer = (Customer) PennantAppUtil.getCustomerObject(this.custCIF.getValue(), null);
		if (customer == null) {
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID",
					new String[] { Labels.getLabel("label_EligibilityCheck_CustCIF.value") }));
		} else {
			doSetCustomer(customer, null);
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onClick$viewCustInfo(Event event) {
		try {
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("custid", this.custID.longValue());
			map.put("custCIF", this.custCIF.getValue());
			map.put("custShrtName", this.custShrtName.getValue());
			map.put("finFormatter",
					CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));
			map.put("finReference", this.finReference.getValue());
			map.put("finance", true);
			if (StringUtils.equals(finDivision, FinanceConstants.FIN_DIVISION_RETAIL)) {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/FinCustomerDetailsEnq.zul",
						getMainWindow(), map);
			} else {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Enquiry/CustomerSummary.zul",
						getMainWindow(), map);
			}
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
		}
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug("Entering");
		this.custCIF.clearErrorMessage();
		setCustomerData((Customer) nCustomer);
		this.custCIFSearchObject = newSearchObject;
		logger.debug("Leaving ");
	}

	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		doSearchCustomerCIF();
		logger.debug("Leaving " + event.toString());
	}

	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Finance Schedule Detail For Maintenance purpose
	 */
	public void reRenderScheduleList(FinScheduleData aFinSchData) {
		logger.debug("Entering");
		if (scheduleDetailDialogCtrl != null) {
			scheduleDetailDialogCtrl.doFillScheduleList(aFinSchData);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Reset Schedule Details after Schedule Calculation
	 */
	public void resetScheduleTerms(FinScheduleData scheduleData) {
		BigDecimal utilizedAmt = BigDecimal.ZERO;
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		getFinanceDetail().setFinScheduleData(scheduleData);

		//Setting Total Disbursements as of Date

		for (FinanceDisbursement curDisb : getFinanceDetail().getFinScheduleData().getDisbursementDetails()) {
			if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus())) {
				continue;
			}
			utilizedAmt = utilizedAmt.add(curDisb.getDisbAmount());
		}
		utilizedAmt = utilizedAmt.subtract(PennantAppUtil.unFormateAmount(
				this.downPayBank.getActualValue().subtract(this.downPaySupl.getActualValue()), formatter));
		this.finCurrentAssetValue.setValue(PennantAppUtil.formateAmount(utilizedAmt, formatter));
	}

	/**
	 * Method for Resetting Data after Recalculate of Planned EMI Holidays
	 * 
	 * @param planEMIMonths
	 * @param planEMIDates
	 */
	public void resetPlanEMIH(List<Integer> planEMIMonths, List<Date> planEMIDates) {
		this.oldVar_planEMIMonths = planEMIMonths;
		this.oldVar_planEMIDates = planEMIDates;
	}

	/**
	 * Method for Preparation of Eligibility Data
	 * 
	 * @param detail
	 * @return
	 */
	public FinanceDetail prepareCustElgDetail(Boolean isLoadProcess) {
		logger.debug("Entering");

		FinanceDetail detail = getFinanceDetail();

		//Stop Resetting data multiple times on Load Processing on Record or Double click the record
		if (isLoadProcess && getFinanceDetail().getCustomerEligibilityCheck() != null) {
			return detail;
		}

		FinanceMain financeMain = detail.getFinScheduleData().getFinanceMain();
		Customer customer = detail.getCustomerDetails().getCustomer();
		//Current Finance Monthly Installment Calculation
		BigDecimal totalRepayAmount = financeMain.getTotalRepayAmt();
		BigDecimal curFinRepayAmt = BigDecimal.ZERO;
		int installmentMnts = DateUtility.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate(),
				false);
		if (installmentMnts > 0) {
			curFinRepayAmt = totalRepayAmount.divide(new BigDecimal(installmentMnts), 0, RoundingMode.HALF_DOWN);
		}
		int months = DateUtility.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate());

		//Customer Data Fetching
		if (customer == null) {
			customer = getCustomerService().getCustomerById(financeMain.getCustID());
		}

		//Get Customer Employee Designation
		String custEmpDesg = "";
		String custEmpSector = "";
		String custEmpAlocType = "";
		String custOtherIncome = "";
		BigDecimal custOtherIncomeAmt = BigDecimal.ZERO;
		String custNationality = "";
		String custEmpSts = "";
		String custSector = "";
		String custCtgCode = "";
		String custEmpType = "";
		BigDecimal custYearOfExp = BigDecimal.ZERO;
		if (detail.getCustomerDetails() != null) {
			if (detail.getCustomerDetails().getCustEmployeeDetail() != null) {
				custEmpDesg = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustEmployeeDetail().getEmpDesg());
				custEmpSector = StringUtils
						.trimToEmpty(detail.getCustomerDetails().getCustEmployeeDetail().getEmpSector());
				custEmpAlocType = StringUtils
						.trimToEmpty(detail.getCustomerDetails().getCustEmployeeDetail().getEmpAlocType());
				custOtherIncome = StringUtils
						.trimToEmpty(detail.getCustomerDetails().getCustEmployeeDetail().getOtherIncome());
				custOtherIncomeAmt = detail.getCustomerDetails().getCustEmployeeDetail().getAdditionalIncome();
				int custMonthsofExp = DateUtility
						.getMonthsBetween(detail.getCustomerDetails().getCustEmployeeDetail().getEmpFrom(), appDate);
				custYearOfExp = BigDecimal.valueOf(custMonthsofExp).divide(BigDecimal.valueOf(12), 2,
						RoundingMode.CEILING);
			}
			if (ImplementationConstants.ALLOW_MULTIPLE_EMPLOYMENTS
					&& detail.getCustomerDetails().getEmploymentDetailsList() != null
					&& !detail.getCustomerDetails().getEmploymentDetailsList().isEmpty()) {
				Date custEmpFromDate = null;
				Date custEmpToDate = null;
				boolean isCurrentEmp = false;
				for (CustomerEmploymentDetail custEmpDetail : detail.getCustomerDetails().getEmploymentDetailsList()) {
					if (custEmpDetail.isCurrentEmployer()) {
						isCurrentEmp = true;
						custEmpDesg = custEmpDetail.getCustEmpDesg();
						custEmpFromDate = custEmpDetail.getCustEmpFrom();
						custEmpType = custEmpDetail.getCustEmpType();
					} else {
						if (custEmpFromDate == null) {
							custEmpFromDate = custEmpDetail.getCustEmpFrom();
						} else {
							if (custEmpDetail.getCustEmpFrom() != null
									&& custEmpDetail.getCustEmpFrom().compareTo(custEmpFromDate) < 0) {
								custEmpFromDate = custEmpDetail.getCustEmpFrom();
							}
						}
						if (!isCurrentEmp) {
							if (custEmpToDate == null) {
								custEmpToDate = custEmpDetail.getCustEmpTo();
								custEmpDesg = custEmpDetail.getCustEmpDesg();
							} else {
								if (custEmpDetail.getCustEmpTo() != null
										&& custEmpDetail.getCustEmpTo().compareTo(custEmpToDate) > 0) {
									custEmpToDate = custEmpDetail.getCustEmpTo();
									custEmpDesg = custEmpDetail.getCustEmpDesg();
								}
							}
						}
					}
					if (custEmpFromDate != null) {
						int custMonthsofExp = DateUtility.getMonthsBetween(custEmpFromDate, appDate);
						custYearOfExp = BigDecimal.valueOf(custMonthsofExp).divide(BigDecimal.valueOf(12), 2,
								RoundingMode.CEILING);
					}
				}
			}
			custNationality = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustomer().getCustNationality());
			custEmpSts = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustomer().getCustEmpSts());
			custSector = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustomer().getCustSector());
			custCtgCode = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustomer().getCustCtgCode());
		}

		// Set Customer Data to check the eligibility

		if (jointAccountDetailDialogCtrl != null) {
			if (jointAccountDetailDialogCtrl.getJountAccountDetailList() != null
					&& jointAccountDetailDialogCtrl.getJountAccountDetailList().size() > 0) {
				jointAccountDetailDialogCtrl.doSave_JointAccountDetail(detail);
			}
		}

		detail.setCustomerEligibilityCheck(getFinanceDetailService().getCustEligibilityDetail(customer,
				detail.getFinScheduleData().getFinanceType().getFinCategory(), financeMain.getFinReference(),
				financeMain.getFinCcy(), curFinRepayAmt, months, null, detail.getJountAccountDetailList()));

		detail.getCustomerEligibilityCheck().setReqFinAmount(financeMain.getFinAmount());
		detail.getCustomerEligibilityCheck()
				.setDisbursedAmount(financeMain.getFinAmount().subtract(financeMain.getDownPayment()));
		detail.getCustomerEligibilityCheck().setReqFinType(financeMain.getFinType());

		//detail.getCustomerEligibilityCheck().setFinProfitRate(financeMain.getEffectiveRateOfReturn());
		if (StringUtils.isNotEmpty(this.repayRate.getBaseValue())) {
			detail.getCustomerEligibilityCheck().setFinProfitRate(this.repayRate.getEffRateComp().getValue());
		} else {
			detail.getCustomerEligibilityCheck().setFinProfitRate(this.repayProfitRate.getValue());
		}

		detail.getCustomerEligibilityCheck().setDownpayBank(financeMain.getDownPayBank());
		detail.getCustomerEligibilityCheck().setDownpaySupl(financeMain.getDownPaySupl());
		detail.getCustomerEligibilityCheck().setStepFinance(financeMain.isStepFinance());
		detail.getCustomerEligibilityCheck().setFinRepayMethod(financeMain.getFinRepayMethod());
		detail.getCustomerEligibilityCheck()
				.setAlwDPSP(detail.getFinScheduleData().getFinanceType().isAllowDownpayPgm());
		detail.getCustomerEligibilityCheck().setAlwPlannedDefer(financeMain.getPlanDeferCount() > 0 ? true : false);
		detail.getCustomerEligibilityCheck().setInstallmentAmount(financeMain.getFirstRepay());
		detail.getCustomerEligibilityCheck().setSalariedCustomer(customer.isSalariedCustomer());
		detail.getCustomerEligibilityCheck().setCustTotalIncome(customer.getCustTotalIncome());
		detail.getCustomerEligibilityCheck().setCustOtherIncome(custOtherIncome);
		detail.getCustomerEligibilityCheck().setCustOtherIncomeAmt(custOtherIncomeAmt);
		detail.getCustomerEligibilityCheck().setCustEmpDesg(custEmpDesg);
		detail.getCustomerEligibilityCheck().setCustEmpType(custEmpType);
		detail.getCustomerEligibilityCheck().setCustEmpSector(custEmpSector);
		detail.getCustomerEligibilityCheck().setCustEmpAloc(custEmpAlocType);
		detail.getCustomerEligibilityCheck().setCustNationality(custNationality);
		detail.getCustomerEligibilityCheck().setCustEmpSts(custEmpSts);
		detail.getCustomerEligibilityCheck().setCustYearOfExp(custYearOfExp);
		detail.getCustomerEligibilityCheck().setCustSector(custSector);
		detail.getCustomerEligibilityCheck().setCustCtgCode(custCtgCode);
		detail.getCustomerEligibilityCheck().setGraceTenure(
				DateUtility.getYearsBetween(financeMain.getFinStartDate(), financeMain.getGrcPeriodEndDate()));

		detail.getCustomerEligibilityCheck().setReqFinCcy(financeMain.getFinCcy());
		detail.getCustomerEligibilityCheck().setNoOfTerms(financeMain.getNumberOfTerms());

		if (detail.getCustomerDetails().getCustEmployeeDetail() != null) {
			detail.getCustomerEligibilityCheck()
					.setCustMonthlyIncome(PennantAppUtil.formateAmount(
							detail.getCustomerDetails().getCustEmployeeDetail().getMonthlyIncome(), CurrencyUtil
									.getFormat(detail.getCustomerDetails().getCustomer().getCustBaseCcy())));

			detail.getCustomerEligibilityCheck()
					.setCustEmpName(String.valueOf(detail.getCustomerDetails().getCustEmployeeDetail().getEmpName()));

		}

		detail.getCustomerEligibilityCheck().setReqFinPurpose(financeMain.getFinPurpose());
		financeMain.setCustDSR(detail.getCustomerEligibilityCheck().getDSCR());
		detail.getCustomerEligibilityCheck().setAgreeName(financeMain.getAgreeName());
		// ###_0.3
		detail.getCustomerEligibilityCheck().setEligibilityMethod(financeMain.getLovEligibilityMethod());
		detail.getFinScheduleData().setFinanceMain(financeMain);

		// Customer Extended Value
		if (detail.getCustomerDetails() != null && detail.getCustomerDetails().getExtendedFieldHeader() != null
				&& detail.getCustomerDetails().getExtendedFieldHeader().getExtendedFieldDetails() != null
				&& detail.getCustomerDetails().getExtendedFieldRender().getMapValues() != null) {
			for (ExtendedFieldDetail fieldDetail : detail.getCustomerDetails().getExtendedFieldHeader()
					.getExtendedFieldDetails()) {
				if (fieldDetail.isAllowInRule()) {
					Object value = detail.getCustomerDetails().getExtendedFieldRender().getMapValues()
							.get(fieldDetail.getFieldName());
					value = getRuleValue(value, fieldDetail.getFieldType(),
							detail.getCustomerDetails().getCustomer().getCustBaseCcy());
					detail.getCustomerEligibilityCheck().addExtendedField(fieldDetail.getLovDescModuleName() + "_"
							+ fieldDetail.getLovDescSubModuleName() + "_" + fieldDetail.getFieldName(), value);
				}
			}

		}

		// Loan Extended Value  
		if (detail.getExtendedFieldHeader() != null && detail.getExtendedFieldHeader().getExtendedFieldDetails() != null
				&& detail.getExtendedFieldRender().getMapValues() != null) {

			for (ExtendedFieldDetail fieldDetail : detail.getExtendedFieldHeader().getExtendedFieldDetails()) {
				if (fieldDetail.isAllowInRule()) {
					Object value = detail.getExtendedFieldRender().getMapValues().get(fieldDetail.getFieldName());
					value = getRuleValue(value, fieldDetail.getFieldType(),
							detail.getCustomerDetails().getCustomer().getCustBaseCcy());
					detail.getCustomerEligibilityCheck().addExtendedField(fieldDetail.getLovDescModuleName() + "_"
							+ fieldDetail.getLovDescSubModuleName() + "_" + fieldDetail.getFieldName(), value);
				}
			}
		}
		// Loan purpose value
		String finPurpose = " ";
		if (StringUtils.isNotBlank(financeMain.getFinPurpose())) {
			finPurpose = financeMain.getFinPurpose();
		}
		detail.getCustomerEligibilityCheck().addExtendedField("finPurpose", this.finPurpose.getValue());

		// ### 08-05-2018 - End- Development Item 81

		// ### 10-05-2018 - Start- Development Item 82
		if (jointAccountDetailDialogCtrl != null) {
			detail.getCustomerEligibilityCheck().setExtendedFields(jointAccountDetailDialogCtrl.getRules());
		} else {
			detail.getCustomerEligibilityCheck().addExtendedField("Co_Applicants_Count", 0);
			detail.getCustomerEligibilityCheck().addExtendedField("Guarantors_Bank_CustomerCount", 0);
			detail.getCustomerEligibilityCheck().addExtendedField("Guarantors_Other_CustomerCount", 0);
			detail.getCustomerEligibilityCheck().addExtendedField("Guarantors_Total_Count", 0);
			detail.getCustomerEligibilityCheck().addExtendedField("Total_Co_Applicants_Income", 0);
			detail.getCustomerEligibilityCheck().addExtendedField("Total_Co_Applicants_Expense", 0);
			detail.getCustomerEligibilityCheck().addExtendedField("Co_Applicants_Obligation_Internal", BigDecimal.ZERO);
			detail.getCustomerEligibilityCheck().addExtendedField("Co_Applicants_Obligation_External", BigDecimal.ZERO);
		}

		if (collateralHeaderDialogCtrl != null) {
			detail.getCustomerEligibilityCheck().setExtendedFields(collateralHeaderDialogCtrl.getRules());
		} else {
			detail.getCustomerEligibilityCheck().addExtendedField("Collaterals_Total_Assigned", 0);
			detail.getCustomerEligibilityCheck().addExtendedField("Collaterals_Total_UN_Assigned", 0);
			detail.getCustomerEligibilityCheck().addExtendedField("Collateral_Bank_Valuation", 0);
			detail.getCustomerEligibilityCheck().addExtendedField("Collateral_Average_LTV", 0);

		}
		// ### 10-05-2018 - End - Development Item 82
		setCollateralRuleValues();
		detail.getCustomerEligibilityCheck().setExtendedFields(collateralRuleMap);

		int maturityAge = 0;
		if (customer.getCustDOB() != null && maturityDate_two.getValue() != null) {
			maturityAge = DateUtility.getYearsBetween(customer.getCustDOB(), maturityDate_two.getValue());
		}

		BigDecimal iIRVAlue = BigDecimal.ZERO;
		iIRVAlue.setScale(6);
		if (detail.getCustomerEligibilityCheck().getInstallmentAmount().compareTo(BigDecimal.ZERO) != 0
				&& customer.getCustTotalIncome().compareTo(BigDecimal.ZERO) != 0) {
			iIRVAlue = detail.getCustomerEligibilityCheck().getInstallmentAmount();
			iIRVAlue = iIRVAlue.divide(customer.getCustTotalIncome(), 6, RoundingMode.HALF_EVEN);
			iIRVAlue = iIRVAlue.multiply(new BigDecimal(100));
		}

		detail.getCustomerEligibilityCheck().addExtendedField("IIR_RATIO", iIRVAlue);

		detail.getCustomerEligibilityCheck().getExtendedValue("COLLATERAL_TYPES");
		detail.getCustomerEligibilityCheck().addExtendedField("maturityAge", maturityAge);
		if(BigDecimal.ZERO.compareTo(this.finAssetValue.getActualValue())==0){
			detail.getCustomerEligibilityCheck().setCurrentAssetValue(this.finAmount.getActualValue());
		}else{
			detail.getCustomerEligibilityCheck().setCurrentAssetValue(this.finAssetValue.getActualValue());	
		}
		detail.getCustomerEligibilityCheck().addExtendedField("Customer_Margin", customer.isMarginDeviation());
		detail.getCustomerEligibilityCheck().addExtendedField("CUSTOMER_MARGIN_DEVIATION",
				customer.isMarginDeviation());

		// Customer Oblication 
		BigDecimal internal_Obligation = new BigDecimal(0);
		BigDecimal external_Obligation = new BigDecimal(0);

		if (CollectionUtils.isNotEmpty(detail.getCustomerDetails().getCustFinanceExposureList())) {
			for (FinanceEnquiry enquiry : detail.getCustomerDetails().getCustFinanceExposureList()) {
				internal_Obligation = internal_Obligation.add(enquiry.getMaxInstAmount());
			}
		}

		if (CollectionUtils.isNotEmpty(detail.getCustomerDetails().getCustomerExtLiabilityList())) {
			for (CustomerExtLiability liability : detail.getCustomerDetails().getCustomerExtLiabilityList()) {
				external_Obligation = external_Obligation.add(liability.getInstalmentAmount());
			}
		}

		detail.getCustomerEligibilityCheck().addExtendedField("Customer_Obligation_Internal", internal_Obligation);
		detail.getCustomerEligibilityCheck().addExtendedField("Customer_Obligation_External", external_Obligation);
	
		if(pSLDetailDialogCtrl!=null){
			detail.getCustomerEligibilityCheck().setExtendedFields(pSLDetailDialogCtrl.getRules());
		}else{
			if(detail.getPslDetail()!=null){
				detail.getCustomerEligibilityCheck().addExtendedField("ASL_ELIGABLE_AMOUNT", detail.getPslDetail().getEligibleAmount());	
			}else{
				detail.getCustomerEligibilityCheck().addExtendedField("ASL_ELIGABLE_AMOUNT", 0);
			}
		}

		// FOIR  (Total Obligation /Total Income)*100
		BigDecimal foir= BigDecimal.ZERO;
		BigDecimal total = internal_Obligation.add(external_Obligation);
		
		
		if(total.compareTo(BigDecimal.ZERO)!=0 && customer.getCustTotalIncome().compareTo(BigDecimal.ZERO)!=0 ){
			foir= total.divide(customer.getCustTotalIncome(),4,RoundingMode.HALF_UP).multiply(new BigDecimal(100));
		}
			
		detail.getCustomerEligibilityCheck().addExtendedField("FOIR_Ratio", foir);
		
		//Corporate Financial Input Data to set Eligibility Rules
		Set<Long> custIds = new HashSet<>();
		if(!detail.getJountAccountDetailList().isEmpty()){
			for (JointAccountDetail accountDetail : detail.getJountAccountDetailList()) {
				custIds.add(accountDetail.getCustID());
			}
		}
		int noOfYears = SysParamUtil.getValueAsInt("NO_OF_YEARS_TOSHOW");
		long custId = customer.getCustID();
		//Get max audit year from Corporate Financial Input Data master 
		String maxAuditYear = getCreditApplicationReviewService().getMaxAuditYearByCustomerId(custId, "_VIEW");
		int toYear = Integer.parseInt(maxAuditYear);
		//Customers data fetching and MaxAudit year is greater than zero
		Map<String,String> dataMap = new HashMap<>();
		if(toYear > 0){
			//set Default values for extension finance fields
			setExtValuesMap();
			dataMap = creditReviewSummaryData.setDataMap(custId, custIds, toYear, noOfYears, customer.getCustCtgCode(), true, true, extValuesMap, 
					creditApplicationReviewService.getCreditRevCategoryByCreditRevCode(this.custCtgCode));
		}
		detail.setDataMap(dataMap);
		
		setFinanceDetail(detail);
		logger.debug("Leaving");
		return getFinanceDetail();
	}

	/**
	 * Prepare credit review details map for agreements
	 * @param detail
	 */
	private void setCreditRevDetails(FinanceDetail detail){
		//Corporate Financial Input Data to set Eligibility Rules
		Set<Long> custIds = new HashSet<>();
		if(!detail.getJountAccountDetailList().isEmpty()){
			for (JointAccountDetail accountDetail : detail.getJountAccountDetailList()) {
				custIds.add(accountDetail.getCustID());
			}
		}
		int noOfYears = SysParamUtil.getValueAsInt("NO_OF_YEARS_TOSHOW");
		long custId = detail.getCustomerDetails().getCustomer().getCustID();
		//Get max audit year from Corporate Financial Input Data master 
		String maxAuditYear = getCreditApplicationReviewService().getMaxAuditYearByCustomerId(custId, "_VIEW");
		int toYear = Integer.parseInt(maxAuditYear);
		//Customers data fetching and MaxAudit year is greater than zero
		Map<String,String> dataMap = new HashMap<>();
		if(toYear > 0){
			//set Default values for extension finance fields
			setExtValuesMap();
			dataMap = creditReviewSummaryData.setDataMap(custId, custIds, toYear, noOfYears, detail.getCustomerDetails().getCustomer().getCustCtgCode(), true, true, extValuesMap, 
					creditApplicationReviewService.getCreditRevCategoryByCreditRevCode(this.custCtgCode));
		}
		detail.setDataMap(dataMap);
	}
	
	private Map<String, String> setExtValuesMap() {
		extValuesMap.put("EXT_CREDITTRANNO", "0");
		extValuesMap.put("EXT_CREDITTRANAMT", "0");
		extValuesMap.put("EXT_CREDITTRANAVG", "0");
		extValuesMap.put("EXT_DEBITTRANNO", "0");
		extValuesMap.put("EXT_DEBITTRANAMT", "0");
		extValuesMap.put("EXT_CASHDEPOSITNO", "0");
		extValuesMap.put("EXT_CASHDEPOSITAMT", "0");
		extValuesMap.put("EXT_CASHWITHDRAWALNO", "0");
		extValuesMap.put("EXT_CASHWITHDRAWALAMT", "0");
		extValuesMap.put("EXT_CHQDEPOSITNO", "0");
		extValuesMap.put("EXT_CHQDEPOSITAMT", "0");
		extValuesMap.put("EXT_CHQISSUEN", "0");
		extValuesMap.put("EXT_CHQISSUEAMT", "0");
		extValuesMap.put("EXT_INWARDCHQBOUNCENO", "0");
		extValuesMap.put("EXT_OUTWARDCHQBOUNCENO", "0");
		extValuesMap.put("EXT_EODBALAVG", "0");
		extValuesMap.put("EXT_EODBALMAX", "0");
		extValuesMap.put("EXT_EODBALMIN", "0");
		extValuesMap.put("EXT_SUMOFEMI", "0");
		extValuesMap.put("EXT_NUMBEROFTERMS", "0");
		extValuesMap.put("EXT_CHQISSUENO", "0");
		extValuesMap.put("EXT_OBLIGATION", "0");
		extValuesMap.put("EXT_REPAYPROFITRATE", "0");
		extValuesMap.put("EXT_ROUNDINGTARGET", "0");
		extValuesMap.put("EXT_FINASSETVALUE", "0");
		extValuesMap.put("EXT_FINAMOUNT", "0");
		extValuesMap.put("EXT_FIRSTREPAY", "0");
		return extValuesMap;
	}
	
	private Object getRuleValue(Object value, String fieldType, String currency) {

		if (value == null) {
			switch (ExtendedFieldConstants.FieldType.valueOf(fieldType)) {
			case BOOLEAN:
				return false;
			case INT:
			case LONG:
				return "0";
			case DATE:
			case DATETIME:
			case TIME:
				return null;
			case ACTRATE:
			case DECIMAL:
			case PERCENTAGE:
			case BASERATE:
			case CURRENCY:
				return BigDecimal.ZERO;
			default:
				value = "";
			}
		} else if (StringUtils.equals("CURRENCY", fieldType)) {
			value = PennantAppUtil.formateAmount((BigDecimal) value, CurrencyUtil.getFormat(currency));
		}
		return value;
	}

	private void setCollateralRuleValues() {
		collateralRuleMap.clear();

		BigDecimal guidedValue = BigDecimal.ZERO;
		BigDecimal marketValue = BigDecimal.ZERO;
		BigDecimal unitPrice = BigDecimal.ZERO;
		BigDecimal marketValue_Consider = BigDecimal.ZERO;
		BigDecimal propertyLTV = BigDecimal.ZERO;
		StringBuilder collateralType = new StringBuilder("");

		if (collateralHeaderDialogCtrl != null
				&& CollectionUtils.isNotEmpty(collateralHeaderDialogCtrl.getCollateralAssignments())) {
			for (CollateralAssignment assignment : collateralHeaderDialogCtrl.getCollateralAssignments()) {
				CollateralSetup setup = getCollateralSetupService()
						.getCollateralSetupByRef(assignment.getCollateralRef(), curNextRoleCode, isEnquiry);

				if (setup != null && setup.getCollateralStructure() != null) {

					for (ExtendedFieldDetail fieldDetail : setup.getCollateralStructure().getExtendedFieldHeader()
							.getExtendedFieldDetails()) {
						if (fieldDetail.isAllowInRule()) {
							String key = fieldDetail.getLovDescModuleName() + "_"
									+ fieldDetail.getLovDescSubModuleName() + "_" + fieldDetail.getFieldName();

							if (CollectionUtils.isNotEmpty(setup.getExtendedFieldRenderList())) {
								for (ExtendedFieldRender extendedFieldRender : setup.getExtendedFieldRenderList()) {
									// FIXME currently it  is available for the first REcord only 
									if (!collateralRuleMap.containsKey(key)) {
										Object value = extendedFieldRender.getMapValues()
												.get(fieldDetail.getFieldName());
										value = getRuleValue(value, fieldDetail.getFieldType(),
												setup.getCollateralCcy());
										collateralRuleMap.put(key, value);
									}
								}
							}
						}
					}
					if (CollectionUtils.isNotEmpty(setup.getExtendedFieldRenderList())) {
						ExtendedFieldRender extendedFieldRender = setup.getExtendedFieldRenderList().get(0);
						if (!collateralRuleMap.containsKey("GLNVAL")) {
							guidedValue = decimalValue(extendedFieldRender.getMapValues().get("GLNVAL"),
									setup.getCollateralCcy());
						}
						if (!collateralRuleMap.containsKey("MKTVAL")) {
							marketValue = decimalValue(extendedFieldRender.getMapValues().get("MKTVAL"),
									setup.getCollateralCcy());
						}
						if (!collateralRuleMap.containsKey("UNITPRICE")) {
							unitPrice = decimalValue(extendedFieldRender.getMapValues().get("UNITPRICE"),
									setup.getCollateralCcy());
						}

						String type = (String) extendedFieldRender.getMapValues().get("COLLATERALTYPE");
						if (type != null && !StringUtils.contains(collateralType.toString(), type)) {
							collateralType.append(type);
							collateralType.append(",");
						}
					}
				}
			}
		}

		collateralRuleMap.put("COLLATERAL_TYPES", collateralType.toString());
		collateralRuleMap.put("MARKET_VALUE", marketValue);
		collateralRuleMap.put("GUIDED_VALUE", guidedValue);

		// (Guided Value/MarketValue)/100
		if (marketValue.compareTo(BigDecimal.ZERO) != 0  && guidedValue.compareTo(BigDecimal.ZERO) != 0 ) {
			marketValue_Consider = marketValue.divide(guidedValue, 6, RoundingMode.HALF_UP);
			//marketValue_Consider  = marketValue_Consider.multiply(new BigDecimal(100)); 
		}
		collateralRuleMap.put("MRKVALUE_CONSIDER", marketValue_Consider);

		// PROP_LTV Property LTV	LoanAmount/UNITPRICE

		if (unitPrice.compareTo(BigDecimal.ZERO) != 0 && getFinanceDetail().getFinScheduleData().getFinanceMain()
				.getFinAmount().compareTo(BigDecimal.ZERO) != 0) {
			propertyLTV = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAssetValue().divide(unitPrice,
					6, RoundingMode.HALF_UP);
		}
		collateralRuleMap.put("PROP_LTV", propertyLTV);

		List<ExtendedFieldDetail> extendedFieldDetails = PennantAppUtil.getCollateralExtendedFieldForRules();
		for (ExtendedFieldDetail fieldDetail : extendedFieldDetails) {
			String key = fieldDetail.getLovDescModuleName() + "_" + fieldDetail.getLovDescSubModuleName() + "_"
					+ fieldDetail.getFieldName();
			if (!collateralRuleMap.containsKey(key)) {
				Object value = getRuleValue(null, fieldDetail.getFieldType(), "INR");
				collateralRuleMap.put(key, value);
			}
		}
	}

	private BigDecimal decimalValue(Object mapvalue, String currency) {
		BigDecimal decimal = BigDecimal.ZERO;

		try {
			if (mapvalue instanceof BigDecimal) {
				decimal = (BigDecimal) mapvalue;
				decimal = PennantAppUtil.formateAmount(decimal, CurrencyUtil.getFormat(currency));
			}
		} catch (Exception e) {
			logger.equals(e);
		}

		return decimal;
	}

	public String getCodeValue(String fieldCodeId) {
		if (!StringUtils.equals(fieldCodeId, PennantConstants.List_Select)) {
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

			JdbcSearchObject<LovFieldDetail> searchObject = new JdbcSearchObject<LovFieldDetail>(LovFieldDetail.class);
			searchObject.addSort("FieldCodeValue", false);
			searchObject.addFilterIn("FieldCodeId", fieldCodeId, false);

			return pagedListService.getBySearchObject(searchObject).get(0).getFieldCodeValue();
		}
		return "";

	}

	/**
	 * Method for Reset Customer Data
	 */
	private void setCustomerData(Customer customer) {
		logger.debug("Entering");

		this.custID.setValue(customer.getCustID());
		this.custCIF.setValue(customer.getCustCIF());
		this.custShrtName.setValue(customer.getCustShrtName());
		this.disbAcctId.setCustCIF(customer.getCustCIF());
		this.repayAcctId.setCustCIF(customer.getCustCIF());
		this.downPayAccount.setCustCIF(customer.getCustCIF());
		this.disbAcctId.setValue("");
		this.repayAcctId.setValue("");
		this.downPayAccount.setValue("");
		this.finBranch.setValue(customer.getCustDftBranch());
		this.finBranch.setDescription(customer.getLovDescCustDftBranchName());
		this.disbAcctId.setBranchCode(customer.getCustDftBranch());
		this.repayAcctId.setBranchCode(customer.getCustDftBranch());
		this.downPayAccount.setBranchCode(customer.getCustDftBranch());

		this.commitmentRef.setValue("");
		this.commitmentRef.setDescription("");
		custCtgCode = customer.getCustCtgCode();

		FinanceDetail financeDetail = getFinanceDetail();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		financeMain.setFinBranch(customer.getCustDftBranch());

		financeMain.setCustID(customer.getCustID());
		setFinanceDetail(getFinanceDetailService().fetchFinCustDetails(financeDetail, custCtgCode,
				financeMain.getFinType(), getRole(),
				StringUtils.isEmpty(moduleDefiner) ? FinanceConstants.FINSER_EVENT_ORG : moduleDefiner));

		financeMain.setLovDescCustFName(StringUtils.trimToEmpty(customer.getCustFName()));
		financeMain.setLovDescCustLName(StringUtils.trimToEmpty(customer.getCustLName()));
		financeMain.setLovDescCustCIF(StringUtils.trimToEmpty(customer.getCustCIF()));

		//Preparation of Customer Eligibility Data
		financeDetail.getFinScheduleData().setFinanceMain(financeMain);
		prepareCustElgDetail(true);

		// Execute Eligibility Rule and Display Result
		if (eligibilityDetailDialogCtrl != null) {
			eligibilityDetailDialogCtrl.doFillFinEligibilityDetails(financeDetail.getFinElgRuleList());
		} else {
			appendEligibilityDetailTab(false);
		}

		//Scoring Detail Tab
		financeMain.setLovDescCustCtgCode(custCtgCode);
		appendFinScoringDetailTab(false);

		//Agreement Details Tab
		setAgreementDetailTab(getMainWindow());

		// Fill Check List Details based on Rule Execution if Rule Exist
		appendCheckListDetailTab(financeDetail, false);

		//Show Accounting Tab Details Based upon Role Condition using Work flow
		if ("Accounting".equals(getTaskTabs(getTaskId(getRole())))) {

			// Finance Accounting Posting Details
			if (accountingDetailDialogCtrl != null) {

				List<TransactionEntry> entryList = new ArrayList<>();
				if (StringUtils.isNotBlank(financeMain.getPromotionCode())) {
					entryList.addAll(AccountingConfigCache.getTransactionEntry(AccountingConfigCache.getAccountSetID(
							financeMain.getPromotionCode(), eventCode, FinanceConstants.MODULEID_PROMOTION)));
				} else {
					entryList.addAll(AccountingConfigCache.getTransactionEntry(AccountingConfigCache
							.getAccountSetID(financeMain.getFinType(), eventCode, FinanceConstants.MODULEID_FINTYPE)));
				}

				accountingDetailDialogCtrl.doFillAccounting(entryList);
				if (StringUtils.isNotBlank(this.commitmentRef.getValue())) {
					accountingDetailDialogCtrl.doFillCmtAccounting(financeDetail.getCmtFinanceEntries(), 0);
				}
			} else {
				setAccountingDetailTab(getMainWindow());
			}
		}

		//Document Details
		if (documentDetailDialogCtrl != null) {
			documentDetailDialogCtrl.doFillDocumentDetails(financeDetail.getDocumentDetailsList());
		}

		//Finance Stage Accounting Posting Details
		appendStageAccountingDetailsTab(false);

		//Credit Review Details
		if (StringUtils.equals(isCreditRevTabReq, PennantConstants.YES)) {

			if (customer.getCustID() != 0 && !StringUtils.equals(PennantConstants.PFF_CUSTCTG_INDIV,
					financeDetail.getCustomerDetails().getCustomer().getCustCtgCode())) {
				appendCreditReviewDetailTab(false);
			}
		}

		//Query Management Tab
		if (isTabVisible(StageTabConstants.QueryMangement)) {
			appendQueryMangementTab(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method is to fetch EID Number and calling it from DocumentTypeSelectDialogCtrl and DocumentDetailDialogCtrl
	 * controllers when document type is 01.
	 * 
	 */
	public String getCustomerIDNumber(String idType) {
		String idNumber = "";
		if (customerDialogCtrl != null) {
			idNumber = customerDialogCtrl.getCustIDNumber(idType);
		}
		return idNumber;
	}

	public void validateAssetValue() throws ParseException, InterruptedException {
		logger.debug("Entering");
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		this.finAmount.setErrorMessage("");
		if (!this.finAmount.isReadonly()) {
			this.finAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_FinanceMainDialog_FinAmount.value"), 0, true, false));
		}
		try {
			this.finAmount.getValidateValue();
		} catch (WrongValueException we) {
			wve.add(we);
			showErrorDetails(wve, this.financeTypeDetailsTab);
			throw we;
		}
		this.finAmount.setConstraint("");

	}

	/**
	 * To pass Data For Agreement Child Windows Used in reflection
	 * 
	 * @return
	 * @throws Exception
	 */
	public FinanceDetail getAgrFinanceDetails() throws Exception {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		Cloner cloner = new Cloner();
		aFinanceDetail = cloner.deepClone(getFinanceDetail());

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		doWriteComponentsToBean(aFinanceDetail.getFinScheduleData());

		//Schedule details Tab Validation
		if (this.btnBuildSchedule.isVisible() && isSchdlRegenerate()) {
			MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
			return null;
		}

		if (this.btnBuildSchedule.isVisible()
				&& aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() <= 0) {
			MessageUtil.showError(Labels.getLabel("label_Finance_GenSchedule"));
			return null;
		}

		// Customer Details Tab ---> Customer Details 
		if (customerDialogCtrl != null) {
			processCustomerDetails(aFinanceDetail, false);
		}

		//Finance Scoring Details Tab  --- > Scoring Module Details Check
		//Check if any overrides exits then the overridden score count is same or not
		if (scoringDetailDialogCtrl != null) {
			if (scoringDetailDialogCtrl.isScoreExecuted()) {
				if (!scoringDetailDialogCtrl.isSufficientScore()) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Insufficient_Score"));
					return null;
				}
			} else {
				MessageUtil.showError(Labels.getLabel("label_Finance_Verify_Score"));
				return null;
			}
		}

		//Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aFinanceDetail, true);
			if (!validationSuccess) {
				return null;
			}
		} else {
			aFinanceDetail.setFinanceCheckList(null);
		}

		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

		//Finance Eligibility Details Tab
		if (eligibilityDetailDialogCtrl != null) {
			aFinanceDetail = eligibilityDetailDialogCtrl.doSave_EligibilityList(aFinanceDetail);
		}

		//Finance Scoring Details Tab
		if (scoringDetailDialogCtrl != null) {
			scoringDetailDialogCtrl.doSave_ScoreDetail(aFinanceDetail);
		} else {
			aFinanceDetail.setFinScoreHeaderList(null);
		}

		// Guaranteer Details Tab ---> Guaranteer Details 
		if (jointAccountDetailDialogCtrl != null) {
			if (jointAccountDetailDialogCtrl.getGuarantorDetailList() != null
					&& jointAccountDetailDialogCtrl.getGuarantorDetailList().size() > 0) {
				jointAccountDetailDialogCtrl.doSave_GuarantorDetail(aFinanceDetail);
			}
			if (jointAccountDetailDialogCtrl.getJountAccountDetailList() != null
					&& jointAccountDetailDialogCtrl.getJountAccountDetailList().size() > 0) {
				jointAccountDetailDialogCtrl.doSave_JointAccountDetail(aFinanceDetail);
			}
		} else {
			aFinanceDetail.setJountAccountDetailList(null);
			aFinanceDetail.setGurantorsDetailList(null);
		}
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);

		if (finAdvancePaymentsListCtrl != null) {
			finAdvancePaymentsListCtrl.doSave_AdvencePaymentDetail(aFinanceDetail);
		}
		logger.debug("Leaving");
		return aFinanceDetail;
	}

	private void setDownPayAcMand() {
		if (this.downPayBank.getActualValue() != null
				&& this.downPayBank.getActualValue().compareTo(BigDecimal.ZERO) > 0) {
			this.downPayAccount.setMandatoryStyle(!isReadOnly("FinanceMainDialog_MandownPaymentAcc"));
		} else {
			this.downPayAccount.setMandatoryStyle(false);
		}
	}

	// ******************************************************//
	// ***************Overdue Penalty Details****************//
	// ******************************************************//

	public void onCheck$applyODPenalty(Event event) {
		logger.debug("Entering" + event.toString());
		onCheckODPenalty(true);
		logger.debug("Leaving" + event.toString());
	}

	protected void onCheckODPenalty(boolean checkAction) {
		logger.debug("Entering");

		int format = CurrencyUtil.getFormat(getFinanceMain().getFinCcy());
		FinODPenaltyRate penaltyRate = getFinanceDetail().getFinScheduleData().getFinODPenaltyRate();
		this.space_oDChargeCalOn.setSclass(PennantConstants.mandateSclass);
		this.space_oDChargeAmtOrPerc.setSclass(PennantConstants.mandateSclass);
		this.space_oDMaxWaiverPerc.setSclass(PennantConstants.mandateSclass);
		this.space_oDChargeType.setSclass(PennantConstants.mandateSclass);

		if (this.applyODPenalty.isChecked()) {

			readOnlyComponent(isReadOnly("FinanceMainDialog_oDIncGrcDays"), this.oDIncGrcDays);
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeType"), this.oDChargeType);
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeCalOn"), this.oDChargeCalOn);
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDAllowWaiver"), this.oDAllowWaiver);
			this.oDGraceDays.setReadonly(isReadOnly("FinanceMainDialog_oDGraceDays"));

			if (checkAction) {
				readOnlyComponent(true, this.oDChargeAmtOrPerc);
				readOnlyComponent(true, this.oDMaxWaiverPerc);
				readOnlyComponent(true, this.oDChargeCalOn);
				readOnlyComponent(true, this.oDIncGrcDays);
				this.space_oDChargeCalOn.setSclass("");
				checkAction = false;
			} else {
				onChangeODChargeType(false);
				onCheckODWaiver(false);
			}

		} else {
			readOnlyComponent(true, this.oDIncGrcDays);
			readOnlyComponent(true, this.oDChargeType);
			readOnlyComponent(true, this.oDGraceDays);
			readOnlyComponent(true, this.oDChargeCalOn);
			readOnlyComponent(true, this.oDChargeAmtOrPerc);
			readOnlyComponent(true, this.oDAllowWaiver);
			readOnlyComponent(true, this.oDMaxWaiverPerc);

			checkAction = true;
		}

		if (checkAction) {
			this.oDIncGrcDays.setChecked(false);
			this.oDChargeType.setSelectedIndex(0);
			this.oDChargeCalOn.setSelectedIndex(0);
			this.oDGraceDays.setValue(0);
			this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
			this.oDAllowWaiver.setChecked(false);
			this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
		} else {
			this.oDIncGrcDays.setChecked(penaltyRate.isODIncGrcDays());
			fillComboBox(this.oDChargeType, penaltyRate.getODChargeType(), PennantStaticListUtil.getODCChargeType(),
					"");
			fillComboBox(this.oDChargeCalOn, penaltyRate.getODChargeCalOn(), PennantStaticListUtil.getODCCalculatedOn(),
					"");
			this.oDGraceDays.setValue(penaltyRate.getODGraceDays());
			this.oDChargeAmtOrPerc.setValue(PennantAppUtil.formateAmount(penaltyRate.getODChargeAmtOrPerc(), format));
			this.oDAllowWaiver.setChecked(penaltyRate.isODAllowWaiver());
			this.oDMaxWaiverPerc.setValue(penaltyRate.getODMaxWaiverPerc());
		}

		if (!this.applyODPenalty.isChecked()) {
			this.space_oDChargeCalOn.setSclass("");
			this.space_oDChargeAmtOrPerc.setSclass("");
			this.space_oDMaxWaiverPerc.setSclass("");
			this.space_oDChargeType.setSclass("");
		}
		logger.debug("Leaving");
	}

	/*
	 * public void onCheck$oDIncGrcDays(Event event){ logger.debug("Entering" + event.toString());
	 * if(this.oDIncGrcDays.isChecked()){ readOnlyComponent(isReadOnly("FinanceMainDialog_oDGraceDays"),
	 * this.oDGraceDays); }else{ readOnlyComponent(true, this.oDGraceDays); } logger.debug("Leaving" +
	 * event.toString()); }
	 */

	public void onChange$oDChargeType(Event event) {
		logger.debug("Entering" + event.toString());
		onChangeODChargeType(true);
		logger.debug("Leaving" + event.toString());
	}

	private void onChangeODChargeType(boolean changeAction) {
		logger.debug("Entering");

		if (changeAction) {
			this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
		}

		readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeCalOn"), this.oDChargeCalOn);
		this.space_oDChargeAmtOrPerc.setSclass(PennantConstants.mandateSclass);
		this.space_oDChargeCalOn.setSclass(PennantConstants.mandateSclass);
		if (getComboboxValue(this.oDChargeType).equals(PennantConstants.List_Select)) {
			readOnlyComponent(true, this.oDChargeAmtOrPerc);
			this.space_oDChargeAmtOrPerc.setSclass("");
		} else if (getComboboxValue(this.oDChargeType).equals(FinanceConstants.PENALTYTYPE_FLAT)
				|| getComboboxValue(this.oDChargeType).equals(FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH)) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeAmtOrPerc"), this.oDChargeAmtOrPerc);
			//this.oDChargeAmtOrPerc.setMaxlength(15);
			this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(
					CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy())));
			Clients.clearWrongValue(this.oDChargeCalOn);
			readOnlyComponent(true, this.oDChargeCalOn);
			this.space_oDChargeCalOn.setSclass("");
			if (changeAction) {
				this.oDChargeCalOn.setSelectedIndex(0);
			}
		} else {
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeAmtOrPerc"), this.oDChargeAmtOrPerc);
			this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(2));
		}

		if (!getComboboxValue(this.oDChargeType).equals(FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS)) {
			readOnlyComponent(true, this.oDIncGrcDays);
			if (changeAction) {
				this.oDIncGrcDays.setChecked(false);
			}
		} else {
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDIncGrcDays"), this.oDIncGrcDays);
			this.oDIncGrcDays.setChecked(true);
		}
		logger.debug("Leaving");
	}

	public void onCheck$oDAllowWaiver(Event event) {
		logger.debug("Entering" + event.toString());
		onCheckODWaiver(true);
		logger.debug("Leaving" + event.toString());
	}

	private void onCheckODWaiver(boolean checkAction) {
		logger.debug("Entering");

		if (checkAction) {
			this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
		}
		if (this.oDAllowWaiver.isChecked()) {
			this.space_oDMaxWaiverPerc.setSclass(PennantConstants.mandateSclass);
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDMaxWaiverPerc"), this.oDMaxWaiverPerc);
		} else {
			readOnlyComponent(true, this.oDMaxWaiverPerc);
			this.space_oDMaxWaiverPerc.setSclass("");
		}
		logger.debug("Leaving");
	}

	/**
	 * when the "validate" button is clicked. <br>
	 * Stores the default values, sets the validation and validates the given finance details.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnValidate(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		buildEvent = false;
		validate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "buildSchedule" button is clicked. <br>
	 * Stores the default values, sets the validation, validates the given finance details, builds the schedule.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnBuildSchedule(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		this.buildEvent = true;

		//Setting the Empstatus Based on the Salaried customer
		if (customerDialogCtrl != null) {
			if (!customerDialogCtrl.setEmpStatusOnSalCust(custDetailTab)) {
				return;
			}
		}

		if (validate() != null) {
			this.buildEvent = false;

			if (manualSchedule.isChecked()) {
				if (getManualScheduleDetailDialogCtrl() != null) {

					getFinanceDetail().setFinScheduleData(getManualScheduleDetailDialogCtrl()
							.doPrepareSchdData(getFinanceDetail().getFinScheduleData(), true));

					if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 2) {

						getFinanceDetail().setFinScheduleData(
								ScheduleCalculator.getCalSchd(getFinanceDetail().getFinScheduleData(), null));
						getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
						getFinanceDetail().getFinScheduleData().setSchduleGenerated(true);

						//Fill Finance Schedule details List data into ListBox
						if (getManualScheduleDetailDialogCtrl() != null) {
							appendScheduleDetailTab(false, false);
							getManualScheduleDetailDialogCtrl().setSchRebuildReq(false);
						}
					}
					return;
				}
			} else if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
					getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())
					&& (StringUtils
							.isNotEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain().getDroplineFrq())
							|| StringUtils.isNotEmpty(moduleDefiner))) {

				//Overdraft Schedule Maintenance
				FinScheduleData scheduleData = null;
				if (StringUtils.equals(FinanceConstants.FINSER_EVENT_OVERDRAFTSCHD, moduleDefiner)) {
					scheduleData = rebuildODSchd(getFinanceDetail().getFinScheduleData());
				} else {

					if (getFinanceDetail().getFinScheduleData().getOverdraftScheduleDetails() != null) {
						getFinanceDetail().getFinScheduleData().getOverdraftScheduleDetails().clear();
					}

					//To Rebuild the overdraft if any fields are changed
					getFinanceDetail().getFinScheduleData().getFinanceMain().setEventFromDate(
							getFinanceDetail().getFinScheduleData().getFinanceMain().getFinStartDate());

					scheduleData = ScheduleCalculator.buildODSchedule(getFinanceDetail().getFinScheduleData());
				}

				// Show Error Details in Schedule Calculation
				if (scheduleData.getErrorDetails() != null && !scheduleData.getErrorDetails().isEmpty()) {
					MessageUtil.showError(scheduleData.getErrorDetails().get(0).getError());
					return;
				}

				getFinanceDetail().setFinScheduleData(scheduleData);
				getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
				if (getOverdraftScheduleDetailDialogCtrl() != null) {
					getOverdraftScheduleDetailDialogCtrl().doFillScheduleList(getFinanceDetail().getFinScheduleData());
				} else {
					appendScheduleDetailTab(false, false);
				}

			} else {

				//Setting Finance Step Policy Details to Finance Schedule Data Object
				if (stepDetailDialogCtrl != null) {
					validFinScheduleData.setStepPolicyDetails(stepDetailDialogCtrl.getFinStepPoliciesList());
					this.oldVar_finStepPolicyList = stepDetailDialogCtrl.getFinStepPoliciesList();
				}

				//Setting Finance Step Policy Details to Finance Schedule Data Object
				if (getFinFeeDetailListCtrl() != null) {
					this.oldVar_finInsuranceList = getFinFeeDetailListCtrl().getFinInsuranceList();
				}
				
				//Prepare Finance Schedule Generator Details List
				if (!moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CHGGRCEND)) {
					getFinanceDetail().getFinScheduleData().setRepayInstructions(new ArrayList<RepayInstruction>());
					getFinanceDetail().getFinScheduleData().setPlanEMIHmonths(new ArrayList<Integer>());
					getFinanceDetail().getFinScheduleData().setPlanEMIHDates(new ArrayList<Date>());
					getFinanceDetail().setFinScheduleData(ScheduleGenerator.getNewSchd(validFinScheduleData));
				}

				// Show Error Details in Schedule Generation
				if (getFinanceDetail().getFinScheduleData().getErrorDetails() != null
						&& !getFinanceDetail().getFinScheduleData().getErrorDetails().isEmpty()) {
					MessageUtil.showError(getFinanceDetail().getFinScheduleData().getErrorDetails().get(0).getError());
					getFinanceDetail().getFinScheduleData().getErrorDetails().clear();
					return;
				}

				getFinanceDetail().getFinScheduleData().getFinanceMain().setScheduleMaintained(false);
				getFinanceDetail().getFinScheduleData().getFinanceMain().setMigratedFinance(false);
				getFinanceDetail().getFinScheduleData().getFinanceMain().setScheduleRegenerated(false);

				//Build Finance Schedule Details List
				if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() != 0) {

					if (moduleDefiner.equals(FinanceConstants.FINSER_EVENT_CHGGRCEND)) {
						validFinScheduleData.getFinanceMain().setEventFromDate(org_grcPeriodEndDate);
						getFinanceDetail().getFinScheduleData().getFinanceMain().setDevFinCalReq(false);
						getFinanceDetail().setFinScheduleData(
								ScheduleCalculator.changeGraceEnd(getFinanceDetail().getFinScheduleData()));

						// Plan EMI Holidays Resetting after Change Grace Period End Date
						if (getFinanceDetail().getFinScheduleData().getFinanceMain().isPlanEMIHAlw()) {
							getFinanceDetail().getFinScheduleData().getFinanceMain().setEventFromDate(
									getFinanceDetail().getFinScheduleData().getFinanceMain().getRecalFromDate());
							getFinanceDetail().getFinScheduleData().getFinanceMain().setEventToDate(
									getFinanceDetail().getFinScheduleData().getFinanceMain().getMaturityDate());
							getFinanceDetail().getFinScheduleData().getFinanceMain().setRecalFromDate(
									getFinanceDetail().getFinScheduleData().getFinanceMain().getRecalFromDate());
							getFinanceDetail().getFinScheduleData().getFinanceMain().setRecalToDate(
									getFinanceDetail().getFinScheduleData().getFinanceMain().getMaturityDate());
							getFinanceDetail().getFinScheduleData().getFinanceMain().setRecalSchdMethod(
									getFinanceDetail().getFinScheduleData().getFinanceMain().getScheduleMethod());

							getFinanceDetail().getFinScheduleData().getFinanceMain().setEqualRepay(true);
							getFinanceDetail().getFinScheduleData().getFinanceMain().setCalculateRepay(true);

							if (StringUtils.equals(
									getFinanceDetail().getFinScheduleData().getFinanceMain().getPlanEMIHMethod(),
									FinanceConstants.PLANEMIHMETHOD_FRQ)) {
								getFinanceDetail().setFinScheduleData(
										ScheduleCalculator.getFrqEMIHoliday(getFinanceDetail().getFinScheduleData()));
							} else {
								getFinanceDetail().setFinScheduleData(
										ScheduleCalculator.getAdhocEMIHoliday(getFinanceDetail().getFinScheduleData()));
							}
						}

					} else {
						getFinanceDetail().setFinScheduleData(
								ScheduleCalculator.getCalSchd(getFinanceDetail().getFinScheduleData(), null));
					}

					// Show Error Details in Schedule Calculation
					if (getFinanceDetail().getFinScheduleData().getErrorDetails() != null
							&& !getFinanceDetail().getFinScheduleData().getErrorDetails().isEmpty()) {
						MessageUtil.showError(getFinanceDetail().getFinScheduleData().getErrorDetails().get(0));
						getFinanceDetail().getFinScheduleData().getErrorDetails().clear();
						return;
					}

					getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
					getFinanceDetail().getFinScheduleData().setSchduleGenerated(true);

					//Preparation of Customer Eligibility Data
					prepareCustElgDetail(false);

					//Fill Finance Schedule details List data into ListBox
					if (getScheduleDetailDialogCtrl() != null) {
						getScheduleDetailDialogCtrl().doFillScheduleList(getFinanceDetail().getFinScheduleData());
						getScheduleDetailDialogCtrl().setPlanEMIHDateList(new ArrayList<Date>());
					} else {
						appendScheduleDetailTab(false, false);
					}
				}
				//Calculating the Net FinanceAmount After Schedule is Built
				setNetFinanceAmount(false);

			}

			if (StringUtils.equals(moduleDefiner, FinanceConstants.FINSER_EVENT_OVERDRAFTSCHD)) {
				getFinanceDetail().getFinScheduleData().getFinanceMain().setScheduleRegenerated(true);
			}

			//Schedule tab Selection After Schedule Re-modified
			Tab tab = getTab(AssetConstants.UNIQUE_ID_SCHEDULE);
			if (tab != null) {
				tab.setSelected(true);
				tab.setVisible(true);
			}

			if (stepDetailDialogCtrl != null) {
				stepDetailDialogCtrl.doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
			}

			if (financeCheckListReferenceDialogCtrl != null) {
				financeCheckListReferenceDialogCtrl.doWriteBeanToComponents(getFinanceDetail().getCheckList(),
						getFinanceDetail().getFinanceCheckList(), true);
			}

			//Deviation calculations
			if (StringUtils.isEmpty(moduleDefiner)) {
				deviationExecutionCtrl.checkProductDeviations(getFinanceDetail());
				deviationExecutionCtrl.checkFeeDeviations(getFinanceDetail());
			}
		}
		if (lVerificationCtrl != null) {
			lVerificationCtrl.setFinanceDetail(getFinanceDetail());
		}

		Tab pdcTab = getTab(AssetConstants.UNIQUE_ID_CHEQUE);
		if (chequeDetailDialogCtrl != null && pdcTab.isVisible()) {
			chequeDetailDialogCtrl
					.setUpdatedFinanceSchedules(getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails());
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to Rebuild the Schedule and recalculate the Overdraft Schedule Details
	 * 
	 * @throws InterruptedException
	 */
	private FinScheduleData rebuildODSchd(FinScheduleData finScheduleData) throws InterruptedException {
		logger.debug("Entering");

		// Validate Limit Increases after New Maturity Date
		List<OverdraftScheduleDetail> odSchdList = finScheduleData.getOverdraftScheduleDetails();
		for (int i = 0; i < odSchdList.size(); i++) {
			OverdraftScheduleDetail curODSchd = odSchdList.get(i);
			if (DateUtility.compare(curODSchd.getDroplineDate(),
					finScheduleData.getFinanceMain().getMaturityDate()) >= 0) {
				if (curODSchd.getLimitIncreaseAmt().compareTo(BigDecimal.ZERO) > 0) {
					finScheduleData.setErrorDetail(new ErrorDetail("30575", new String[] {}));
					break;
				}
			}
		}

		//If any errors , on Limit Increase validation
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("leaving");
			return finScheduleData;
		}

		// Overdraft Schedule Recalculation
		finScheduleData.getFinanceMain().setEventFromDate(appDate);
		finScheduleData = ScheduleCalculator.buildODSchedule(finScheduleData);

		//If any Errors on Overdraft Schedule build
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug("leaving");
			return finScheduleData;
		}

		// To Recalculate the Schedule based on new Parameters
		finScheduleData = getReScheduleService().doResetOverdraftSchd(finScheduleData);

		logger.debug("leaving");
		return finScheduleData;
	}

	/**
	 * Method to validate given details
	 * 
	 * @throws InterruptedException
	 * @return validfinanceDetail
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws ParseException
	 */
	private FinanceDetail validate()
			throws InterruptedException, IllegalAccessException, InvocationTargetException, ParseException {
		logger.debug("Entering");

		recSave = false;

		if (isSchdlRegenerate()) {
			getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(false);
		}

		doStoreDefaultValues();
		doCheckFeeReExecution();
		doStoreDftSchdValues();

		validFinScheduleData = new FinScheduleData();
		BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData(), validFinScheduleData);
		getFinanceDetail().setFinScheduleData(validFinScheduleData);
		doWriteComponentsToBean(validFinScheduleData);

		if (manualSchedule.isChecked()) {
			if (getManualScheduleDetailDialogCtrl() == null) {
				appendScheduleDetailTab(false, false);
				if (isBuildEvent()
						&& (tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_SCHEDULE))) != null) {
					Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_SCHEDULE));
					tab.setSelected(true);
					MessageUtil.showError(Labels.getLabel("label_Endbalvalid"));
					return financeDetail;
				}
			} else if (isBuildEvent() && getManualScheduleDetailDialogCtrl() != null) {
				boolean isEndingBal = getManualScheduleDetailDialogCtrl().doCheckEndingBal();
				if (!isEndingBal
						&& (tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_SCHEDULE))) != null) {
					Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_SCHEDULE));
					if (!tab.isVisible()) {
						tab.setVisible(true);
						tab.setSelected(true);
					}
					MessageUtil.showError(Labels.getLabel("label_Endbalvalid"));
					return financeDetail;
				}
				onChangeFinAndDownpayAmount();
			}
		}

		if (getProductCode().equalsIgnoreCase(FinanceConstants.PRODUCT_ISTISNA)) {

			if (disbursementDetailDialogCtrl != null
					&& disbursementDetailDialogCtrl.getDisbursementDetails().size() == 0) {
				Tab tab = getTab(AssetConstants.UNIQUE_ID_DISBURSMENT);
				if (tab != null) {
					tab.setSelected(true);
				}
				MessageUtil.showError("Billing & Advance Details must be Added.");
				return null;
			} else {

				boolean disbVaidated = false;
				for (FinanceDisbursement finDisb : disbursementDetailDialogCtrl.getDisbursementDetails()) {
					if (finDisb.getDisbDate().compareTo(this.finStartDate.getValue()) >= 0) {
						if ("A".equals(StringUtils.trimToEmpty(finDisb.getDisbType()))
								|| "B".equals(StringUtils.trimToEmpty(finDisb.getDisbType()))) {
							if (finDisb.getDisbAmount().compareTo(BigDecimal.ZERO) > 0) {
								disbVaidated = true;
							}
						}
					} else {
						disbVaidated = false;
						break;
					}
				}

				if (!disbVaidated) {
					MessageUtil.showError(Labels.getLabel("label_IstisnaFinanceMainDialog_ValidateAsset.value"));
					return null;
				}
			}

		}

		if (doValidation(getAuditHeader(getFinanceDetail(), ""))) {

			//Contributor Details Checking for RIA Accounting
			if (isRIAExist && contributorDetailsDialogCtrl != null) {
				contributorDetailsDialogCtrl.doSaveContributorsDetail(getFinanceDetail(),
						getTab(AssetConstants.UNIQUE_ID_CONTRIBUTOR));
			}
			validFinScheduleData.setErrorDetails(new ArrayList<ErrorDetail>());

			if (this.manualSchedule.isChecked()) {
				//Schedule tab Selection After Schedule Re-modified
				Tab tab = getTab(AssetConstants.UNIQUE_ID_SCHEDULE);
				if (tab != null) {
					tab.setSelected(true);
				}
			}

			if (lVerificationCtrl != null) {
				lVerificationCtrl.setFinanceDetail(getFinanceDetail());
			}

			logger.debug("Leaving");
			return getFinanceDetail();
		}

		logger.debug("Leaving");
		return null;
	}

	/*
	 * Set the Maturitydate on change of the tenor, droplinedate and droplinefrequency
	 */
	public void onChange$odMnthlyTerms(Event event) {
		logger.debug("Entering" + event.toString());
		calMaturityDate();
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$odYearlyTerms(Event event) {
		logger.debug("Entering" + event.toString());
		calMaturityDate();
		logger.debug("Leaving" + event.toString());
	}

	/*
	 * method to show pop up where user adds account and priority
	 */
	public void onClick$btnAddSecondaryAccounts(Event event) {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financemainBaseCtrl", this);
		map.put("financeDetail", this.financeDetail);
		map.put("repayAccountId", this.repayAcctId.getValue());
		map.put("isViewAllowed", getUserWorkspace().isAllowed("FinanceMainDialog_btnAddSecondaryAccounts"));
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AddSecondaryAccountDialog.zul", null, map);

		logger.debug("Leaving");

	}

	/**
	 * Fetches data for Bundled Products and renders it
	 * 
	 */
	protected void renderBundledProducts() {
		logger.debug("Entering");
		try {
			HashMap<String, Object> map = getDefaultArguments();
			map.put("tab", getTab(AssetConstants.UNIQUE_ID_BUNDLEDPRODUCTS));
			map.put("finBasicDetails", getFinBasicDetails());
			bundledProductsWindow = Executions.createComponents(
					"/WEB-INF/pages/Finance/BundledProducts/BundledProductsDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_BUNDLEDPRODUCTS), map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getFinBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, this.finType.getValue());
		arrayList.add(1, this.finCcy.getValue());
		if (this.cbScheduleMethod.getSelectedIndex() > 0) {
			arrayList.add(2, this.cbScheduleMethod.getSelectedItem().getLabel());
		} else {
			arrayList.add(2, "");
		}
		arrayList.add(3, this.finReference.getValue());
		arrayList.add(4, this.cbProfitDaysBasis.getSelectedItem().getLabel());
		arrayList.add(5, this.gracePeriodEndDate_two.getValue());
		arrayList.add(6, this.allowGrace.isChecked());
		if (StringUtils.isNotEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, getFinanceDetail().getFinScheduleData().getFinanceType().getFinCategory());
		String custShrtName = "";
		if (getFinanceDetail().getCustomerDetails() != null
				&& getFinanceDetail().getCustomerDetails().getCustomer() != null) {
			custShrtName = getFinanceDetail().getCustomerDetails().getCustomer().getCustShrtName();
		}
		if (customerDialogCtrl != null) {
			try {
				custShrtName = (String) this.customerDialogCtrl.getClass().getMethod("getCustomerShortName")
						.invoke(customerDialogCtrl);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				logger.error("Exception: ", e);
			}
		}
		arrayList.add(9, custShrtName);
		arrayList.add(10, getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord());
		arrayList.add(11, moduleDefiner);
		return arrayList;
	}

	// To set the NumberofTerms and MaturitDate in Manual Schedule 
	public void doSetfinTerms(Integer noOfTerms, Integer graceTerms) {
		logger.debug("Entering");

		this.graceTerms_Two.setValue(graceTerms);
		this.numberOfTerms.setValue(noOfTerms);
		this.numberOfTerms_two.setValue(noOfTerms);

		logger.debug("Leaving");
	}

	public String getUserRole() {
		return getRole();
	}

	public String getUserID() {
		return String.valueOf(getUserWorkspace().getUserDetails().getUserId());
	}

	// WorkFlow Components

	protected void doLoadWorkFlow(FinanceMain financeMain)
			throws FileNotFoundException, XMLStreamException, UnsupportedEncodingException, FactoryConfigurationError {
		logger.debug("Entering");
		String roleCode = null;
		if (StringUtils.isEmpty(moduleDefiner) && !financeMain.isNewRecord()
				&& StringUtils.trimToEmpty(financeMain.getNextTaskId()).contains(";")) {
			roleCode = getFinanceDetailService().getUserRoleCodeByRefernce(
					getUserWorkspace().getUserDetails().getUserId(), financeMain.getFinReference(),
					getUserWorkspace().getUserRoles());
		}

		if (null == roleCode) {
			doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());
		} else {
			doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), null, roleCode);
		}
		logger.debug("Entering");
	}

	/**
	 * Method for calculation of maturity Date based on Dropline frequency & Tenor mentioned.
	 */

	private Date calMaturityDate() {

		String frq = "";
		if (this.finStartDate.getValue() == null) {
			return null;
		}

		if (this.odYearlyTerms.intValue() <= SysParamUtil.getValueAsInt("MAX_FIN_YEARS")
				&& this.odMnthlyTerms.intValue() <= 11) {
			int tenorMonths = (12 * this.odYearlyTerms.intValue()) + (this.odMnthlyTerms.intValue());
			if (tenorMonths <= 0) {
				return null;
			}

			frq = "M00" + StringUtils.leftPad(String.valueOf(DateUtility.getDay(this.finStartDate.getValue())), 2, "0");
			List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(frq, tenorMonths, this.finStartDate.getValue(),
					HolidayHandlerTypes.MOVE_NONE, false, 0).getScheduleList();

			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				readOnlyComponent(true, this.odMaturityDate);
				this.odMaturityDate.setValue(calendar.getTime());
				this.odMaturityDate.setFormat(DateFormat.SHORT_DATE.getPattern());
			}
		}
		return odMaturityDate.getValue();
	}

	/**
	 * Method for fetching Currency format of selected currency
	 * 
	 * @return
	 */
	public int getCcyFormat() {
		return CurrencyUtil.getFormat(this.finCcy.getValue());
	}

	/**
	 * Get Audit Header Details
	 */
	protected AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
				afinanceDetail.getUserDetails(), getOverideMap());
	}

	/**
	 * Method for fetching Customer Basic Details for Document Details processing
	 * 
	 * @return
	 */
	public List<Object> getCustomerBasicDetails() {

		List<Object> custBasicDetails = null;
		if (financeDetail.getCustomerDetails() != null && financeDetail.getCustomerDetails().getCustomer() != null) {
			custBasicDetails = new ArrayList<>();
			custBasicDetails.add(financeDetail.getCustomerDetails().getCustomer().getCustID());
			custBasicDetails.add(financeDetail.getCustomerDetails().getCustomer().getCustCIF());
			custBasicDetails.add(financeDetail.getCustomerDetails().getCustomer().getCustShrtName());
		}
		return custBasicDetails;
	}

	private void setComboboxSelectedItem(Combobox combobox, String selectedValue) {
		List<Comboitem> comboitems = combobox.getItems();
		for (Comboitem comboitem : comboitems) {
			if (StringUtils.equals(comboitem.getValue().toString(), selectedValue)) {
				combobox.setSelectedItem(comboitem);
				break;
			}
		}
	}

	private void checkQDPProcess(FinanceDetail financeDetail) {
		List<FinAdvancePayments> list = financeDetail.getAdvancePaymentsList();
		if (list != null && !list.isEmpty()) {
			for (FinAdvancePayments finAdvancePayments : list) {
				if (finAdvancePayments.ispOIssued()
						&& !StringUtils.equals(finAdvancePayments.getStatus(), DisbursementConstants.STATUS_CANCEL)) {
					this.quickDisb.setDisabled(true);
					break;
				}
			}
		}
	}

	private void allowQDPBuild(FinanceDetail financeDetail) {
		FinanceMain finmain = financeDetail.getFinScheduleData().getFinanceMain();
		if (finmain.isQuickDisb()) {
			readOnlyComponent(
					isReadOnly("FinanceMainDialog_finStartDate_QDP") && isReadOnly("FinanceMainDialog_finStartDate"),
					this.finStartDate);
			this.btnBuildSchedule.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnBuildSchd_QDP")
					|| getUserWorkspace().isAllowed("button_FinanceMainDialog_btnBuildSchd"));

			readOnlyComponent(false, this.nextRepayPftDate);
			readOnlyComponent(false, this.nextRepayRvwDate);
			readOnlyComponent(false, this.nextRepayDate);
			readOnlyComponent(false, this.repayPftFrq);
			readOnlyComponent(false, this.repayRvwFrq);
			readOnlyComponent(false, this.repayFrq);

			if (StringUtils.equals(PennantConstants.RCD_STATUS_SUBMITTED, finmain.getRecordStatus())) {
				this.nextRepayPftDate.setText("");
				this.nextRepayRvwDate.setText("");
				this.nextRepayDate.setText("");
			}
			gb_repaymentDetails.setVisible(true);
			this.rpyPftFrqRow.setVisible(true);
			this.rpyRvwFrqRow.setVisible(true);
			this.rpyFrqRow.setVisible(true);
		}
	}

	public List<DocumentDetails> getDocumentDetails() {
		if (getDocumentDetailDialogCtrl() != null) {
			return getDocumentDetailDialogCtrl().getDocumentDetailsList();
		}
		return null;
	}

	public void onClickExtbtnEXTRACT() {
		logger.debug("Entering");
		try {
			Map<String, Object> pdfExtractFields = getPdfParserCaller()
					.callDocumentParser(customerDialogCtrl.getCustomerDocumentDetailList());
			extendedFieldCtrl.fillcomponentData(pdfExtractFields, getPdfExtTabPanelId(), false);
		} catch (Exception e) {
			{
				if (e.getLocalizedMessage() != null) {
					MessageUtil.showError(e.getLocalizedMessage());
				} else {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void processSave() throws InterruptedException, Exception {
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		String prevRecordStatus = financeMain.getRecordStatus();
		String recordStatus = userAction.getSelectedItem().getValue();
		if (!PennantConstants.RCD_STATUS_REJECTED.equals(prevRecordStatus)
				&& (PennantConstants.RCD_STATUS_REJECTED.equals(recordStatus)
						|| PennantConstants.RCD_STATUS_CANCELLED.equals(recordStatus))
				&& StringUtils.isEmpty(moduleDefiner)) {
			boolean allow = DisbursementInstCtrl.allowReject(getFinanceDetail().getAdvancePaymentsList());
			if (!allow) {
				MessageUtil.showMessage(Labels.getLabel("label_Finance_QuickDisb_Cancelled"));
				return;
			}
		}

		Long captureReasone = null;
		String taskId = getTaskId(getRole());
		financeMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());
		captureReasone = getWorkFlow().getReasonTypeToCapture(taskId, financeMain);

		if (captureReasone != null && captureReasone.intValue() != 0) {
			doFillReasons(captureReasone.intValue());
		} else {
			doSave();
		}
	}

	public void doFillReasons(int reason) throws InterruptedException {
		logger.debug("Entering");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("reason", reason);
		try {
			Executions.createComponents("/WEB-INF/pages/ReasonDetail/ReasonDetails.zul", getMainWindow(), map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public ScheduleDetailDialogCtrl getScheduleDetailDialogCtrl() {
		return scheduleDetailDialogCtrl;
	}

	public void setScheduleDetailDialogCtrl(ScheduleDetailDialogCtrl scheduleDetailDialogCtrl) {
		this.scheduleDetailDialogCtrl = scheduleDetailDialogCtrl;
	}

	public EligibilityDetailDialogCtrl getEligibilityDetailDialogCtrl() {
		return eligibilityDetailDialogCtrl;
	}

	public void setEligibilityDetailDialogCtrl(EligibilityDetailDialogCtrl eligibilityDetailDialogCtrl) {
		this.eligibilityDetailDialogCtrl = eligibilityDetailDialogCtrl;
	}

	public DocumentDetailDialogCtrl getDocumentDetailDialogCtrl() {
		return documentDetailDialogCtrl;
	}

	public void setDocumentDetailDialogCtrl(DocumentDetailDialogCtrl documentDetailDialogCtrl) {
		this.documentDetailDialogCtrl = documentDetailDialogCtrl;
		this.documentDetailDialogCtrl.setRcuVerificationDialogCtrl(rcuVerificationDialogCtrl);
		this.documentDetailDialogCtrl.setlVerificationCtrl(lVerificationCtrl);
	}

	public AccountingDetailDialogCtrl getAccountingDetailDialogCtrl() {
		return accountingDetailDialogCtrl;
	}

	public void setAccountingDetailDialogCtrl(AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}

	public StageAccountingDetailDialogCtrl getStageAccountingDetailDialogCtrl() {
		return stageAccountingDetailDialogCtrl;
	}

	public void setStageAccountingDetailDialogCtrl(StageAccountingDetailDialogCtrl stageAccountingDetailDialogCtrl) {
		this.stageAccountingDetailDialogCtrl = stageAccountingDetailDialogCtrl;
	}

	public ContributorDetailsDialogCtrl getContributorDetailsDialogCtrl() {
		return contributorDetailsDialogCtrl;
	}

	public void setContributorDetailsDialogCtrl(ContributorDetailsDialogCtrl contributorDetailsDialogCtrl) {
		this.contributorDetailsDialogCtrl = contributorDetailsDialogCtrl;
	}

	public JointAccountDetailDialogCtrl getJointAccountDetailDialogCtrl() {
		return jointAccountDetailDialogCtrl;
	}

	public void setJointAccountDetailDialogCtrl(JointAccountDetailDialogCtrl jointAccountDetailDialogCtrl) {
		this.jointAccountDetailDialogCtrl = jointAccountDetailDialogCtrl;
		this.jointAccountDetailDialogCtrl.setFieldVerificationDialogCtrl(fieldVerificationDialogCtrl);
	}

	public AgreementDetailDialogCtrl getAgreementDetailDialogCtrl() {
		return agreementDetailDialogCtrl;
	}

	public void setAgreementDetailDialogCtrl(AgreementDetailDialogCtrl agreementDetailDialogCtrl) {
		this.agreementDetailDialogCtrl = agreementDetailDialogCtrl;
	}

	public ScoringDetailDialogCtrl getScoringDetailDialogCtrl() {
		return scoringDetailDialogCtrl;
	}

	public void setScoringDetailDialogCtrl(ScoringDetailDialogCtrl scoringDetailDialogCtrl) {
		this.scoringDetailDialogCtrl = scoringDetailDialogCtrl;
	}

	public FinanceCheckListReferenceDialogCtrl getFinanceCheckListReferenceDialogCtrl() {
		return financeCheckListReferenceDialogCtrl;
	}

	public void setFinanceCheckListReferenceDialogCtrl(
			FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl) {
		this.financeCheckListReferenceDialogCtrl = financeCheckListReferenceDialogCtrl;
	}

	public void setChildWindowDialogCtrl(Object childWindowDialogCtrl) {
		this.childWindowDialogCtrl = childWindowDialogCtrl;
	}

	public Object getChildWindowDialogCtrl() {
		return childWindowDialogCtrl;
	}

	public StepDetailDialogCtrl getStepDetailDialogCtrl() {
		return stepDetailDialogCtrl;
	}

	public void setStepDetailDialogCtrl(StepDetailDialogCtrl stepDetailDialogCtrl) {
		this.stepDetailDialogCtrl = stepDetailDialogCtrl;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

	public DisbursementDetailDialogCtrl getDisbursementDetailDialogCtrl() {
		return disbursementDetailDialogCtrl;
	}

	public void setDisbursementDetailDialogCtrl(DisbursementDetailDialogCtrl disbursementDetailDialogCtrl) {
		this.disbursementDetailDialogCtrl = disbursementDetailDialogCtrl;
	}

	public DeviationDetailDialogCtrl getDeviationDetailDialogCtrl() {
		return deviationDetailDialogCtrl;
	}

	public void setDeviationDetailDialogCtrl(DeviationDetailDialogCtrl deviationDetailDialogCtrl) {
		this.deviationDetailDialogCtrl = deviationDetailDialogCtrl;
	}

	public MandateDialogCtrl getMandateDialogCtrl() {
		return mandateDialogCtrl;
	}

	public void setMandateDialogCtrl(MandateDialogCtrl mandateDialogCtrl) {
		this.mandateDialogCtrl = mandateDialogCtrl;
	}

	public EtihadCreditBureauDetailDialogCtrl getEtihadCreditBureauDetailDialogCtrl() {
		return etihadCreditBureauDetailDialogCtrl;
	}

	public void setEtihadCreditBureauDetailDialogCtrl(
			EtihadCreditBureauDetailDialogCtrl etihadCreditBureauDetailDialogCtrl) {
		this.etihadCreditBureauDetailDialogCtrl = etihadCreditBureauDetailDialogCtrl;
	}

	public BundledProductsDetailDialogCtrl getBundledProductsDetailDialogCtrl() {
		return bundledProductsDetailDialogCtrl;
	}

	public void setBundledProductsDetailDialogCtrl(BundledProductsDetailDialogCtrl bundledProductsDetailDialogCtrl) {
		this.bundledProductsDetailDialogCtrl = bundledProductsDetailDialogCtrl;
	}

	public FinAssetEvaluationDialogCtrl getFinAssetEvaluationDialogCtrl() {
		return finAssetEvaluationDialogCtrl;
	}

	public void setFinAssetEvaluationDialogCtrl(FinAssetEvaluationDialogCtrl finAssetEvaluationDialogCtrl) {
		this.finAssetEvaluationDialogCtrl = finAssetEvaluationDialogCtrl;
	}

	public FinAdvancePaymentsListCtrl getFinAdvancePaymentsListCtrl() {
		return finAdvancePaymentsListCtrl;
	}

	public void setFinAdvancePaymentsListCtrl(FinAdvancePaymentsListCtrl finAdvancePaymentsListCtrl) {
		this.finAdvancePaymentsListCtrl = finAdvancePaymentsListCtrl;
	}

	public FinFeeDetailListCtrl getFinFeeDetailListCtrl() {
		return finFeeDetailListCtrl;
	}

	public void setFinFeeDetailListCtrl(FinFeeDetailListCtrl finFeeDetailListCtrl) {
		this.finFeeDetailListCtrl = finFeeDetailListCtrl;
	}

	public FinCovenantTypeListCtrl getFinCovenantTypeListCtrl() {
		return finCovenantTypeListCtrl;
	}

	public void setFinCovenantTypeListCtrl(FinCovenantTypeListCtrl finCovenantTypeListCtrl) {
		this.finCovenantTypeListCtrl = finCovenantTypeListCtrl;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public boolean isAssetDataChanged() {
		return assetDataChanged;
	}

	public void setAssetDataChanged(Boolean assetDataChanged) {
		this.assetDataChanged = assetDataChanged;
	}

	public boolean isFinPurposeDataChanged() {
		return finPurposeDataChanged;
	}

	public void setFinPurposeDataChanged(Boolean finPurposeDataChanged) {
		this.finPurposeDataChanged = finPurposeDataChanged;
	}

	public AccountsService getAccountsService() {
		return accountsService;
	}

	public void setAccountsService(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}

	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}

	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}

	public FinanceMainListCtrl getFinanceMainListCtrl() {
		return financeMainListCtrl;
	}

	public void setFinanceMainListCtrl(FinanceMainListCtrl financeMainListCtrl) {
		this.financeMainListCtrl = financeMainListCtrl;
	}

	public FinanceSelectCtrl getFinanceSelectCtrl() {
		return financeSelectCtrl;
	}

	public void setFinanceSelectCtrl(FinanceSelectCtrl financeSelectCtrl) {
		this.financeSelectCtrl = financeSelectCtrl;
	}

	public void setAdvancePaymentsList(List<FinAdvancePayments> advancePaymentDetails) {
		getFinanceDetail().setAdvancePaymentsList(advancePaymentDetails);
	}

	public void setCovenantTypeList(List<FinCovenantType> covenantTypeDetails) {
		getFinanceDetail().setCovenantTypeList(covenantTypeDetails);
	}

	public void setCollateralAssignmentList(List<CollateralAssignment> collateralAssignments) {
		getFinanceDetail().setCollateralAssignmentList(collateralAssignments);
	}

	public CommitmentService getCommitmentService() {
		return commitmentService;
	}

	public void setCommitmentService(CommitmentService commitmentService) {
		this.commitmentService = commitmentService;
	}

	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}

	public MailUtil getMailUtil() {
		return mailUtil;
	}

	public SMSUtil getSmsUtil() {
		return smsUtil;
	}

	public void setSmsUtil(SMSUtil smsUtil) {
		this.smsUtil = smsUtil;
	}

	public boolean isExtMailService() {
		return extMailService;
	}

	public void setExtMailService(boolean extMailService) {
		this.extMailService = extMailService;
	}

	public boolean isExtSMSService() {
		return extSMSService;
	}

	public void setExtSMSService(boolean extSMSService) {
		this.extSMSService = extSMSService;
	}

	public Window getMainWindow() {
		return mainWindow;
	}

	public void setMainWindow(Window mainWindow) {
		this.mainWindow = mainWindow;
	}

	public Label getLabel_FinanceMainDialog_FinType() {
		return label_FinanceMainDialog_FinType;
	}

	public void setLabel_FinanceMainDialog_FinType(Label label_FinanceMainDialog_FinType) {
		this.label_FinanceMainDialog_FinType = label_FinanceMainDialog_FinType;
	}

	public Label getLabel_FinanceMainDialog_ScheduleMethod() {
		return label_FinanceMainDialog_ScheduleMethod;
	}

	public void setLabel_FinanceMainDialog_ScheduleMethod(Label label_FinanceMainDialog_ScheduleMethod) {
		this.label_FinanceMainDialog_ScheduleMethod = label_FinanceMainDialog_ScheduleMethod;
	}

	public Label getLabel_FinanceMainDialog_FinRepayPftOnFrq() {
		return label_FinanceMainDialog_FinRepayPftOnFrq;
	}

	public void setLabel_FinanceMainDialog_FinRepayPftOnFrq(Label labelFinanceMainDialogFinRepayPftOnFrq) {
		this.label_FinanceMainDialog_FinRepayPftOnFrq = labelFinanceMainDialogFinRepayPftOnFrq;
	}

	public Label getLabel_FinanceMainDialog_CommitRef() {
		return label_FinanceMainDialog_CommitRef;
	}

	public void setLabel_FinanceMainDialog_CommitRef(Label labelFinanceMainDialogCommitRef) {
		this.label_FinanceMainDialog_CommitRef = labelFinanceMainDialogCommitRef;
	}

	public Label getLabel_FinanceMainDialog_DepriFrq() {
		return label_FinanceMainDialog_DepriFrq;
	}

	public void setLabel_FinanceMainDialog_DepriFrq(Label labelFinanceMainDialogDepriFrq) {
		this.label_FinanceMainDialog_DepriFrq = labelFinanceMainDialogDepriFrq;
	}

	public Label getLabel_FinanceMainDialog_PlanDeferCount() {
		return label_FinanceMainDialog_PlanDeferCount;
	}

	public void setLabel_FinanceMainDialog_PlanDeferCount(Label labelFinanceMainDialogPlanDeferCount) {
		this.label_FinanceMainDialog_PlanDeferCount = labelFinanceMainDialogPlanDeferCount;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public Label getLabel_FinanceMainDialog_AlwGrace() {
		return label_FinanceMainDialog_AlwGrace;
	}

	public void setLabel_FinanceMainDialog_AlwGrace(Label labelFinanceMainDialogAlwGrace) {
		this.label_FinanceMainDialog_AlwGrace = labelFinanceMainDialogAlwGrace;
	}

	public Label getLabel_FinanceMainDialog_StepPolicy() {
		return label_FinanceMainDialog_StepPolicy;
	}

	public void setLabel_FinanceMainDialog_StepPolicy(Label labelFinanceMainDialogStepPolicy) {
		this.label_FinanceMainDialog_StepPolicy = labelFinanceMainDialogStepPolicy;
	}

	public Label getLabel_FinanceMainDialog_numberOfSteps() {
		return label_FinanceMainDialog_numberOfSteps;
	}

	public void setLabel_FinanceMainDialog_numberOfSteps(Label labelFinanceMainDialogNumberOfSteps) {
		this.label_FinanceMainDialog_numberOfSteps = labelFinanceMainDialogNumberOfSteps;
	}

	public Label getLabel_FinanceMainDialog_PromoProduct() {
		return label_FinanceMainDialog_PromoProduct;
	}

	public void setLabel_FinanceMainDialog_PromoProduct(Label label_FinanceMainDialog_PromoProduct) {
		this.label_FinanceMainDialog_PromoProduct = label_FinanceMainDialog_PromoProduct;
	}

	public boolean isRecommendEntered() {
		return recommendEntered;
	}

	public void setRecommendEntered(boolean recommendEntered) {
		this.recommendEntered = recommendEntered;
	}

	public StepPolicyService getStepPolicyService() {
		return stepPolicyService;
	}

	public void setStepPolicyService(StepPolicyService stepPolicyService) {
		this.stepPolicyService = stepPolicyService;
	}

	public FinanceReferenceDetailService getFinanceReferenceDetailService() {
		return financeReferenceDetailService;
	}

	public void setFinanceReferenceDetailService(FinanceReferenceDetailService financeReferenceDetailService) {
		this.financeReferenceDetailService = financeReferenceDetailService;
	}

	public String getNextUserId() {
		return nextUserId;
	}

	public void setNextUserId(String nextUserId) {
		this.nextUserId = nextUserId;
	}

	//used in reflection method should not be removed
	public DeviationExecutionCtrl getDeviationExecutionCtrl() {
		return deviationExecutionCtrl;
	}

	public void setFinCollateralHeaderDialogCtrl(FinCollateralHeaderDialogCtrl finCollateralHeaderDialogCtrl) {
		this.finCollateralHeaderDialogCtrl = finCollateralHeaderDialogCtrl;
	}

	public void setAgreementFieldsDetailDialogCtrl(AgreementFieldsDetailDialogCtrl agreementFieldsDetailDialogCtrl) {
		this.agreementFieldsDetailDialogCtrl = agreementFieldsDetailDialogCtrl;
	}

	public void setAgreementGeneration(AgreementGeneration agreementGeneration) {
		this.agreementGeneration = agreementGeneration;
	}

	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}

	public RuleService getRuleService() {
		return ruleService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public void setChildWindow(Window childWindow) {
		this.childWindow = childWindow;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public void setCustomerInterfaceService(CustomerInterfaceService customerInterfaceService) {
		this.customerInterfaceService = customerInterfaceService;
	}

	public CustomerInterfaceService getCustomerInterfaceService() {
		return customerInterfaceService;
	}

	public LimitCheckDetails getLimitCheckDetails() {
		return limitCheckDetails;
	}

	public void setLimitCheckDetails(LimitCheckDetails limitCheckDetails) {
		this.limitCheckDetails = limitCheckDetails;
	}

	public DDAInterfaceService getDdaInterfaceService() {
		return ddaInterfaceService;
	}

	public void setDdaInterfaceService(DDAInterfaceService ddaInterfaceService) {
		this.ddaInterfaceService = ddaInterfaceService;
	}

	public DDAProcessService getDdaProcessService() {
		return ddaProcessService;
	}

	public void setDdaProcessService(DDAProcessService ddaProcessService) {
		this.ddaProcessService = ddaProcessService;
	}

	public DDAControllerService getDdaControllerService() {
		return ddaControllerService;
	}

	public void setDdaControllerService(DDAControllerService ddaControllerService) {
		this.ddaControllerService = ddaControllerService;
	}

	public NorkamCheckService getNorkamCheckService() {
		return norkamCheckService;
	}

	public void setNorkamCheckService(NorkamCheckService norkamCheckService) {
		this.norkamCheckService = norkamCheckService;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public boolean isFinPreApproved() {
		return isFinPreApproved;
	}

	public void setFinPreApproved(boolean isFinPreApproved) {
		this.isFinPreApproved = isFinPreApproved;
	}

	public DedupParmService getDedupParmService() {
		return dedupParmService;
	}

	public void setDedupParmService(DedupParmService dedupParmService) {
		this.dedupParmService = dedupParmService;
	}

	public FinanceMainExtService getFinanceMainExtService() {
		return financeMainExtService;
	}

	public void setFinanceMainExtService(FinanceMainExtService financeMainExtService) {
		this.financeMainExtService = financeMainExtService;
	}

	public CollateralMarkProcess getCollateralMarkProcess() {
		return collateralMarkProcess;
	}

	public void setCollateralMarkProcess(CollateralMarkProcess collateralMarkProcess) {
		this.collateralMarkProcess = collateralMarkProcess;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public void setEventManager(EventManager eventManager) {
		this.eventManager = eventManager;
	}

	public boolean isBuildEvent() {
		return buildEvent;
	}

	public void setBuildEvent(boolean buildEvent) {
		this.buildEvent = buildEvent;
	}

	public LimitManagement getLimitManagement() {
		return limitManagement;
	}

	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	public void setCollateralHeaderDialogCtrl(CollateralHeaderDialogCtrl collateralHeaderDialogCtrl) {
		this.collateralHeaderDialogCtrl = collateralHeaderDialogCtrl;
		this.collateralHeaderDialogCtrl.setTVerificationDialogCtrl(tVerificationDialogCtrl);
		this.collateralHeaderDialogCtrl.setRcuVerificationDialogCtrl(rcuVerificationDialogCtrl);
		this.collateralHeaderDialogCtrl.setlVerificationCtrl(lVerificationCtrl);
	}

	public FinSamplingDialogCtrl getFinSamplingDialogCtrl() {
		return finSamplingDialogCtrl;
	}

	public void setFinSamplingDialogCtrl(FinSamplingDialogCtrl finSamplingDialogCtrl) {
		this.finSamplingDialogCtrl = finSamplingDialogCtrl;
	}

	public Label getLabel_FinanceMainDialog_TDSApplicable() {
		return label_FinanceMainDialog_TDSApplicable;
	}

	public void setLabel_FinanceMainDialog_TDSApplicable(Label label_FinanceMainDialog_TDSApplicable) {
		this.label_FinanceMainDialog_TDSApplicable = label_FinanceMainDialog_TDSApplicable;
	}

	public Label getLabel_FinanceMainDialog_FinLimitRef() {
		return label_FinanceMainDialog_FinLimitRef;
	}

	public void setLabel_FinanceMainDialog_FinLimitRef(Label label_FinanceMainDialog_FinLimitRef) {
		this.label_FinanceMainDialog_FinLimitRef = label_FinanceMainDialog_FinLimitRef;
	}

	public NotificationsService getNotificationsService() {
		return notificationsService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public void setDedupValidation(DedupValidation dedupValidation) {
		this.dedupValidation = dedupValidation;
	}

	public ManualScheduleDetailDialogCtrl getManualScheduleDetailDialogCtrl() {
		return manualScheduleDetailDialogCtrl;
	}

	public void setManualScheduleDetailDialogCtrl(ManualScheduleDetailDialogCtrl manualScheduleDetailDialogCtrl) {
		this.manualScheduleDetailDialogCtrl = manualScheduleDetailDialogCtrl;
	}

	public OverdraftScheduleDetailDialogCtrl getOverdraftScheduleDetailDialogCtrl() {
		return overdraftScheduleDetailDialogCtrl;
	}

	public void setOverdraftScheduleDetailDialogCtrl(
			OverdraftScheduleDetailDialogCtrl overdraftScheduleDetailDialogCtrl) {
		this.overdraftScheduleDetailDialogCtrl = overdraftScheduleDetailDialogCtrl;
	}

	public List<FinFlagsDetail> getFinFlagsDetailList() {
		return finFlagsDetailList;
	}

	public void setFinFlagsDetailList(List<FinFlagsDetail> finFlagsDetailList) {
		this.finFlagsDetailList = finFlagsDetailList;
	}

	public List<ExtendedFieldRender> getExtendedFieldRenderList() {
		return extendedFieldRenderList;
	}

	public void setExtendedFieldRenderList(List<ExtendedFieldRender> extendedFieldRenderList) {
		this.extendedFieldRenderList = extendedFieldRenderList;
	}

	public void setFinAssetTypesList(List<FinAssetTypes> finAssetTypes) {
		getFinanceDetail().setFinAssetTypesList(finAssetTypes);
	}

	public Label getLabel_FinanceMainDialog_PlanEmiHolidayMethod() {
		return label_FinanceMainDialog_PlanEmiHolidayMethod;
	}

	public void setLabel_FinanceMainDialog_PlanEmiHolidayMethod(Label label_FinanceMainDialog_PlanEmiHolidayMethod) {
		this.label_FinanceMainDialog_PlanEmiHolidayMethod = label_FinanceMainDialog_PlanEmiHolidayMethod;
	}

	public FinVasRecordingDialogCtrl getFinVasRecordingDialogCtrl() {
		return finVasRecordingDialogCtrl;
	}

	public void setFinVasRecordingDialogCtrl(FinVasRecordingDialogCtrl finVasRecordingDialogCtrl) {
		this.finVasRecordingDialogCtrl = finVasRecordingDialogCtrl;
	}

	public Label getLabel_FinanceMainDialog_CustPayAccId() {
		return label_FinanceMainDialog_CustPayAccId;
	}

	public void setLabel_FinanceMainDialog_CustPayAccId(Label label_FinanceMainDialog_CustPayAccId) {
		this.label_FinanceMainDialog_CustPayAccId = label_FinanceMainDialog_CustPayAccId;
	}

	public Label getLabel_FinanceMainDialog_DisbAcctId() {
		return label_FinanceMainDialog_DisbAcctId;
	}

	public void setLabel_FinanceMainDialog_DisbAcctId(Label label_FinanceMainDialog_DisbAcctId) {
		this.label_FinanceMainDialog_DisbAcctId = label_FinanceMainDialog_DisbAcctId;
	}

	public Label getLabel_FinanceMainDialog_RepayAcctId() {
		return label_FinanceMainDialog_RepayAcctId;
	}

	public void setLabel_FinanceMainDialog_RepayAcctId(Label label_FinanceMainDialog_RepayAcctId) {
		this.label_FinanceMainDialog_RepayAcctId = label_FinanceMainDialog_RepayAcctId;
	}

	public Label getLabel_FinanceMainDialog_SecondaryAccount() {
		return label_FinanceMainDialog_SecondaryAccount;
	}

	public void setLabel_FinanceMainDialog_SecondaryAccount(Label label_FinanceMainDialog_SecondaryAccount) {
		this.label_FinanceMainDialog_SecondaryAccount = label_FinanceMainDialog_SecondaryAccount;
	}

	public Label getLabel_FinanceMainDialog_DownPayAccount() {
		return label_FinanceMainDialog_DownPayAccount;
	}

	public void setLabel_FinanceMainDialog_DownPayAccount(Label label_FinanceMainDialog_DownPayAccount) {
		this.label_FinanceMainDialog_DownPayAccount = label_FinanceMainDialog_DownPayAccount;
	}

	public Label getLabel_FinanceMainDialog_SalesDepartment() {
		return label_FinanceMainDialog_SalesDepartment;
	}

	public void setLabel_FinanceMainDialog_SalesDepartment(Label label_FinanceMainDialog_SalesDepartment) {
		this.label_FinanceMainDialog_SalesDepartment = label_FinanceMainDialog_SalesDepartment;
	}

	public ReScheduleService getReScheduleService() {
		return reScheduleService;
	}

	public void setReScheduleService(ReScheduleService reScheduleService) {
		this.reScheduleService = reScheduleService;
	}

	public AccrualService getAccrualService() {
		return accrualService;
	}

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	public DisbursementPostings getDisbursementPostings() {
		return disbursementPostings;
	}

	public void setDisbursementPostings(DisbursementPostings disbursementPostings) {
		this.disbursementPostings = disbursementPostings;
	}

	public FinanceTaxDetailDialogCtrl getFinanceTaxDetailDialogCtrl() {
		return financeTaxDetailDialogCtrl;
	}

	public void setFinanceTaxDetailDialogCtrl(FinanceTaxDetailDialogCtrl financeTaxDetailDialogCtrl) {
		this.financeTaxDetailDialogCtrl = financeTaxDetailDialogCtrl;
	}

	public InstallmentDueService getInstallmentDueService() {
		return installmentDueService;
	}

	public void setInstallmentDueService(InstallmentDueService installmentDueService) {
		this.installmentDueService = installmentDueService;
	}

	public ShortMessageService getShortMessageService() {
		return shortMessageService;
	}

	public void setShortMessageService(ShortMessageService shortMessageService) {
		this.shortMessageService = shortMessageService;
	}

	public MailTemplateService getMailTemplateService() {
		return mailTemplateService;
	}

	public void setMailTemplateService(MailTemplateService mailTemplateService) {
		this.mailTemplateService = mailTemplateService;
	}

	public PdfParserCaller getPdfParserCaller() {
		return pdfParserCaller;
	}

	public void setPdfParserCaller(PdfParserCaller pdfParserCaller) {
		this.pdfParserCaller = pdfParserCaller;
	}

	public String getPdfExtTabPanelId() {
		return pdfExtTabPanelId;
	}

	public void setPdfExtTabPanelId(String pdfExtTabPanelId) {
		this.pdfExtTabPanelId = pdfExtTabPanelId;
	}

	public VehicleDealerService getVehicleDealerService() {
		return vehicleDealerService;
	}

	public void setVehicleDealerService(VehicleDealerService vehicleDealerService) {
		this.vehicleDealerService = vehicleDealerService;
	}

	public ChequeDetailDialogCtrl getChequeDetailDialogCtrl() {
		return chequeDetailDialogCtrl;
	}

	public void setChequeDetailDialogCtrl(ChequeDetailDialogCtrl chequeDetailDialogCtrl) {
		this.chequeDetailDialogCtrl = chequeDetailDialogCtrl;
	}

	public FieldVerificationDialogCtrl getFieldVerificationDialogCtrl() {
		return fieldVerificationDialogCtrl;
	}

	public void setFieldVerificationDialogCtrl(FieldVerificationDialogCtrl fieldVerificationDialogCtrl) {
		this.fieldVerificationDialogCtrl = fieldVerificationDialogCtrl;
		this.customerDialogCtrl.setFieldVerificationDialogCtrl(fieldVerificationDialogCtrl);
	}

	public TVerificationDialogCtrl gettVerificationDialogCtrl() {
		return tVerificationDialogCtrl;
	}

	public void settVerificationDialogCtrl(TVerificationDialogCtrl tVerificationDialogCtrl) {
		this.tVerificationDialogCtrl = tVerificationDialogCtrl;
	}

	public LVerificationCtrl getLVerificationCtrl() {
		return lVerificationCtrl;
	}

	public void setLVerificationCtrl(LVerificationCtrl lVerificationCtrl) {
		this.lVerificationCtrl = lVerificationCtrl;
		this.customerDialogCtrl.setlVerificationCtrl(lVerificationCtrl);
	}

	public RCUVerificationDialogCtrl getRcuVerificationDialogCtrl() {
		return rcuVerificationDialogCtrl;
	}

	public void setRcuVerificationDialogCtrl(RCUVerificationDialogCtrl rcuVerificationDialogCtrl) {
		this.rcuVerificationDialogCtrl = rcuVerificationDialogCtrl;
		this.customerDialogCtrl.setRcuVerificationDialogCtrl(rcuVerificationDialogCtrl);
	}

	/**
	 * Method for Rendering FIV Initiation Data in finance
	 */
	protected void appendFIInitiationTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (!getFinanceDetail().isFiInitTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_FIINITIATION) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_FIINITIATION, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_FIINITIATION);
		}
		if (getFinanceDetail().isFiInitTab() && !onLoadProcess) {
			final HashMap<String, Object> map = getDefaultArguments();
			if (financeDetail.getFiVerification() == null) {
				financeDetail.setFiVerification(new Verification());
			}
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("financeDetail", financeDetail);
			map.put("InitType", true);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/FIInitiation.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_FIINITIATION), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering FIV Approval Data in finance
	 */
	protected void appendFIApprovalTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (!getFinanceDetail().isFiApprovalTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_FIAPPROVAL) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_FIAPPROVAL, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_FIAPPROVAL);
		}
		if (getFinanceDetail().isFiApprovalTab() && !onLoadProcess) {
			final HashMap<String, Object> map = getDefaultArguments();
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("financeDetail", financeDetail);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/FIApproval.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_FIAPPROVAL), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering TV Initiation Data in finance
	 */
	protected void appendTVInitiationTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (!getFinanceDetail().isTvInitTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_TVINITIATION) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_TVINITIATION, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_TVINITIATION);
		}
		if (getFinanceDetail().isTvInitTab() && !onLoadProcess) {
			final HashMap<String, Object> map = getDefaultArguments();
			if (financeDetail.getTvVerification() == null) {
				financeDetail.setTvVerification(new Verification());
			}
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("verification", financeDetail.getTvVerification());
			map.put("financeDetail", financeDetail);

			map.put("InitType", true);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/TVInitiation.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_TVINITIATION), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering TV Approval Data in finance
	 */
	protected void appendTVApprovalTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (!getFinanceDetail().isTvApprovalTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_TVAPPROVAL) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_TVAPPROVAL, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_TVAPPROVAL);
		}
		if (getFinanceDetail().isTvApprovalTab() && !onLoadProcess) {
			final HashMap<String, Object> map = getDefaultArguments();
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("verification", financeDetail.getTvVerification());
			map.put("financeDetail", financeDetail);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/TVApproval.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_TVAPPROVAL), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering LV Initiation Data in finance
	 */
	protected void appendLVInitiationTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (!getFinanceDetail().isLvInitTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_LVINITIATION) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_LVINITIATION, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_LVINITIATION);
		}
		if (getFinanceDetail().isLvInitTab() && !onLoadProcess) {
			final HashMap<String, Object> map = getDefaultArguments();
			if (financeDetail.getLvVerification() == null) {
				financeDetail.setLvVerification(new Verification());
			}
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("verification", financeDetail.getLvVerification());
			map.put("financeDetail", financeDetail);
			map.put("InitType", true);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/LVInitiation.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_LVINITIATION), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering LV Approval Data in finance
	 */
	protected void appendLVApprovalTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (!getFinanceDetail().isLvApprovalTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_LVAPPROVAL) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_LVAPPROVAL, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_LVAPPROVAL);
		}
		if (getFinanceDetail().isLvApprovalTab() && !onLoadProcess) {
			final HashMap<String, Object> map = getDefaultArguments();
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("verification", financeDetail.getLvVerification());
			map.put("financeDetail", financeDetail);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/LVApproval.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_LVAPPROVAL), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering RCU Initiation Data in finance
	 */
	protected void appendRCUInitiationTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (!getFinanceDetail().isRcuInitTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_RCUINITIATION) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_RCUINITIATION, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_RCUINITIATION);
		}
		if (getFinanceDetail().isRcuInitTab() && !onLoadProcess) {
			final HashMap<String, Object> map = getDefaultArguments();
			if (financeDetail.getRcuVerification() == null) {
				financeDetail.setRcuVerification(new Verification());
			}
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("financeDetail", financeDetail);
			map.put("InitType", true);

			Tabpanel tabpanel = getTabpanel(AssetConstants.UNIQUE_ID_RCUINITIATION);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/RCUInitiation.zul", tabpanel,
					map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering RCU Approval Data in finance
	 */
	protected void appendRCUApprovalTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (!getFinanceDetail().isRcuApprovalTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_RCUAPPROVAL) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_RCUAPPROVAL, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_RCUAPPROVAL);
		}
		if (getFinanceDetail().isRcuApprovalTab() && !onLoadProcess) {
			final HashMap<String, Object> map = getDefaultArguments();
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("financeDetail", financeDetail);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/RCUApproval.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_RCUAPPROVAL), map);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onChange$enquiryCombobox(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		String enquiryType = this.enquiryCombobox.getSelectedItem().getValue();
		Map<String, Object> map = getDefaultArguments();
		map.put("finHeaderList", getFinBasicDetails());
		map.put("financeDetail", financeDetail);
		map.put("financeMainBaseCtrl", this);
		map.put("enquiryModule", true);
		map.put("enuiryCombobox", enquiryCombobox);
		if (enquiryType.equals("1")) {
			Executions.createComponents("/WEB-INF/pages/Verification/FieldInvestigation/VerificationEnquiryDialog.zul",
					null, map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Sampling Approval Data in finance
	 */
	protected void appendSamplingApprovalTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (!getFinanceDetail().isSamplingApprover()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_SAMPLINGAPPROVAL) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_SAMPLINGAPPROVAL, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_SAMPLINGAPPROVAL);
		}
		if (getFinanceDetail().isSamplingApprover() && !onLoadProcess) {
			final HashMap<String, Object> map = getDefaultArguments();
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("financeDetail", financeDetail);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Sampling/FinSamplingDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_SAMPLINGAPPROVAL), map);
		}
		logger.debug(Literal.LEAVING);
	}
	
	// tasks #503 Auto Generation of Agreements
	private DocumentDetails autoGenerateAgreement(FinanceReferenceDetail frefdata ,FinanceDetail financeDetail,AgreementDefinition agreementDefinition) throws Exception 
	{
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
				engine.mergeFields(getAgreementGeneration().getAggrementData(financeDetail, frefdata.getLovDescAggImage(),
						getUserWorkspace().getUserDetails()));
				getAgreementGeneration().setExtendedMasterDescription(financeDetail, engine);
				getAgreementGeneration().setFeeDetails(financeDetail, engine);
				
				//if (agreementDefinition.isAutoDownload()) {
					if (StringUtils.equals(agreementDefinition.getAggtype(), PennantConstants.DOC_TYPE_PDF)) {
						reportName = finReference + "_" + aggName + PennantConstants.DOC_TYPE_PDF_EXT;
						//engine.showDocument(this.window_documentDetailDialog, reportName, SaveFormat.PDF);
					} else {
						reportName = finReference + "_" + aggName + PennantConstants.DOC_TYPE_WORD_EXT;
						//engine.showDocument(this.window_documentDetailDialog, reportName, SaveFormat.DOCX);
					}
				//}
				DocumentDetails exstDetails = null;
				if (financeDetail.getDocumentDetailsList().size() > 0)
				exstDetails = getExistDocDetails(financeDetail.getDocumentDetailsList(), reportName);
				if (exstDetails != null) {
					exstDetails.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					if (PennantConstants.DOC_TYPE_PDF.equals(agreementDefinition.getAggtype())){
						exstDetails.setDocImage(engine.getDocumentInByteArray(
								templateName.concat(PennantConstants.DOC_TYPE_PDF_EXT), SaveFormat.PDF));
					}else{
						exstDetails.setDocImage(engine.getDocumentInByteArray(
								templateName.concat(PennantConstants.DOC_TYPE_DOCX), SaveFormat.DOCX));
					}
					
					return exstDetails;
				}

				details.setDocCategory(agreementDefinition.getDocType());
				if (PennantConstants.WORFLOW_MODULE_FINANCE.equals(agreementDefinition.getModuleName())) {
					details.setDocModule("Finance");
				} else {
					details.setDocModule(agreementDefinition.getModuleName());
				}
				details.setReferenceId(finReference);
				details.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				if (PennantConstants.DOC_TYPE_PDF.equals(agreementDefinition.getAggtype())){
					details.setDocImage(engine.getDocumentInByteArray(
							templateName.concat(PennantConstants.DOC_TYPE_PDF_EXT), SaveFormat.PDF));
				}
				else{
					details.setDocImage(engine.getDocumentInByteArray(
							templateName.concat(PennantConstants.DOC_TYPE_DOCX), SaveFormat.DOCX));
				}
				details.setDoctype(agreementDefinition.getDocType());
				//details.setDoctype(agreementDefinition.getAggtype());
				details.setDocName(reportName);
				details.setDocReceivedDate(frefdata.getLastMntOn());
				details.setVersion(1);
				details.setFinEvent(frefdata.getFinEvent());
				details.setCategoryCode(agreementDefinition.getModuleName());
				details.setLastMntOn(DateUtility.getTimestamp(DateUtility.getAppDate()));
				details.setFinEvent(FinanceConstants.FINSER_EVENT_ORG);
				details.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				engine.close();
				engine = null;

			}
			} catch (Exception e) {
				if (e instanceof IllegalArgumentException && (e.getMessage().equals("Document site does not exist.")
						|| e.getMessage().equals("Template site does not exist.")
						|| e.getMessage().equals("Template does not exist."))) {
					AppException exception = new AppException("Template does not exists.Please configure Template.");
					MessageUtil.showError(exception);
				} else {
					MessageUtil.showError(e);
				}
			}
	
			logger.debug(Literal.LEAVING);
		return details;

	}
		
	
	
	private DocumentDetails getExistDocDetails(List<DocumentDetails> exstDoclst, String docName){
		
		for(DocumentDetails docDetails : exstDoclst){
			if(docName.equalsIgnoreCase(docDetails.getDocName())){
				return docDetails;
			}
				
		}
		return null;
	}

	

	public List<String> getAssignCollateralRef() {
		return assignCollateralRef;
	}

	public CollateralSetupService getCollateralSetupService() {
		return collateralSetupService;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

	public PSLDetailDialogCtrl getpSLDetailDialogCtrl() {
		return pSLDetailDialogCtrl;
	}

	public void setpSLDetailDialogCtrl(PSLDetailDialogCtrl pSLDetailDialogCtrl) {
		this.pSLDetailDialogCtrl = pSLDetailDialogCtrl;
	}

	public LegalDetailService getLegalDetailService() {
		return legalDetailService;
	}

	public void setLegalDetailService(LegalDetailService legalDetailService) {
		this.legalDetailService = legalDetailService;
	}

	public BaseRateService getBaseRateService() {
		return baseRateService;
	}

	public void setBaseRateService(BaseRateService baseRateService) {
		this.baseRateService = baseRateService;
	}
	public AgreementGeneration getAgreementGeneration() {
		return agreementGeneration;
	}
	

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}
	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}


	public void setDocumentDetailsList(List<DocumentDetails> documentDetailsList) {
		this.documentDetailsList = documentDetailsList;
	}

	public List<DocumentDetails> getDocumentDetailsList() {
		return documentDetailsList;
	}

	public void setAgreementDefinitionService(
			AgreementDefinitionService agreementDefinitionService) {
		this.agreementDefinitionService = agreementDefinitionService;
	}

	public AgreementDefinitionService getAgreementDefinitionService() {
		return this.agreementDefinitionService;
	}

	public CreditReviewSummaryData getCreditReviewSummaryData() {
		return creditReviewSummaryData;
	}

	public void setCreditReviewSummaryData(CreditReviewSummaryData creditReviewSummaryData) {
		this.creditReviewSummaryData = creditReviewSummaryData;
	}

	public CreditApplicationReviewService getCreditApplicationReviewService() {
		return creditApplicationReviewService;
	}

	public void setCreditApplicationReviewService(CreditApplicationReviewService creditApplicationReviewService) {
		this.creditApplicationReviewService = creditApplicationReviewService;
	}

}